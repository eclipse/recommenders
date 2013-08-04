/**
 * Copyright (c) 2013 Timur Achmetow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Timur Achmetow - Initial API and implementation
 */
package org.eclipse.recommenders.stats.rcp.ui.views;

import static com.google.common.base.Predicates.not;
import static java.lang.Math.round;
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.apache.commons.math.stat.StatUtils.mean;
import static org.apache.commons.math.stat.StatUtils.sum;
import static org.eclipse.recommenders.stats.rcp.ui.util.TableSorters.setCompletionTypeSorter;
import static org.eclipse.recommenders.stats.rcp.ui.util.TableSorters.setCountSorter;
import static org.eclipse.recommenders.stats.rcp.ui.util.TableSorters.setLastUsedSorter;
import static org.eclipse.recommenders.stats.rcp.ui.util.TableSorters.setTypeSorter;
import static org.eclipse.recommenders.stats.rcp.ui.util.TableSorters.setUsedCompletionSorter;
import static org.eclipse.recommenders.stats.rcp.ui.util.TableViewerFactory.createColumn;
import static org.eclipse.recommenders.stats.rcp.ui.util.TableViewerFactory.createTableColumnLayout;
import static org.eclipse.recommenders.stats.rcp.ui.util.TableViewerFactory.createTableViewer;
import static org.eclipse.recommenders.stats.rcp.ui.util.TableViewerFactory.createWrapperComposite;
import static org.eclipse.recommenders.utils.Checks.cast;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.primitives.ArrayDoubleList;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.recommenders.stats.rcp.events.CompletionEvent;
import org.eclipse.recommenders.stats.rcp.events.StatisticsSessionProcessor;
import org.eclipse.recommenders.stats.rcp.events.CompletionEvent.ProposalKind;
import org.eclipse.recommenders.stats.rcp.interfaces.IDeveloperActivityPage;
import org.eclipse.recommenders.stats.rcp.ui.util.DateFormatter;
import org.eclipse.recommenders.stats.rcp.ui.util.TableViewerFactory;
import org.eclipse.recommenders.utils.Bags;
import org.eclipse.recommenders.utils.names.ITypeName;
import org.eclipse.recommenders.utils.names.Names;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.google.common.base.Charsets;
import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;
import com.google.common.io.Files;
import com.google.gson.Gson;

public class StatisticsView implements IDeveloperActivityPage {
    private static long MAX_TIME_IN_COMPLETION = TimeUnit.MINUTES.toMillis(2);
    private Collection<CompletionEvent> okayEvents;
    private Collection<CompletionEvent> appliedEvents;
    private Collection<CompletionEvent> abortedEvents;
    private Composite container;
    private Composite parent;

    private class BuggyEventsPredicate implements Predicate<CompletionEvent> {
        @Override
        public boolean apply(CompletionEvent input) {
            return input.numberOfProposals < 1 || input.sessionEnded < input.sessionStarted;
        }
    }

    private class HasAppliedProposalPredicate implements Predicate<CompletionEvent> {
        @Override
        public boolean apply(CompletionEvent e) {
            return e.applied != null;
        }
    }

    public StatisticsView() {
        loadEvents();
    }

    @Override
    public void createContent(Composite detailCmp) {
        parent = TableViewerFactory.createWrapperComposite(detailCmp);
        createWidgets(parent);
        createNumberOfCompletionEvents();
        createNumberOfKeystrokesSaved();
        createTimeSpent();

        SashForm sashForm = new SashForm(parent, SWT.VERTICAL);
        sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));

        createViewerForCompletion(sashForm);
        createViewerForReceiverType(sashForm);
        sashForm.setWeights(new int[] { 50, 50 });
    }

    @Override
    public Composite getComposite() {
        return parent;
    }

    private void createWidgets(Composite parent) {
        container = new Composite(parent, SWT.NONE);
        GridLayout grid = new GridLayout(2, false);
        grid.marginHeight = 5;
        grid.horizontalSpacing = 0;
        grid.verticalSpacing = 0;
        container.setLayout(grid);
    }

    private void createNumberOfCompletionEvents() {
        int total = 0;
        for (CompletionEvent e : okayEvents) {
            total += e.numberOfProposals;
        }
        int completedInPercent = calculatePercentData(appliedEvents);
        new Label(container, SWT.NONE).setText("Number of times code completion triggered: ");
        createLabelWithColor(MessageFormat.format("{0}", okayEvents.size()));
        int abortedInPercent = calculatePercentData(abortedEvents);

        new Label(container, SWT.NONE).setText("Number of concluded completions: ");
        createLabelWithColor(MessageFormat.format("{0} ({1}%)", appliedEvents.size(), completedInPercent));

        new Label(container, SWT.NONE).setText("Number of aborted completions: ");
        createLabelWithColor(MessageFormat.format("{0} ({1}%)", abortedEvents.size(), abortedInPercent));

        new Label(container, SWT.NONE).setText("Number of proposals offered by code completion: ");
        createLabelWithColor(MessageFormat.format("{0}", total));
    }

    private void createNumberOfKeystrokesSaved() {
        ArrayDoubleList strokes = new ArrayDoubleList();
        for (CompletionEvent e : appliedEvents) {
            int prefix = e.prefix == null ? 0 : e.prefix.length();
            int completionLength = e.completion == null ? 0 : e.completion.length();
            int saved = Math.max(0, completionLength - prefix);
            strokes.add(saved);
        }

        Double total = sum(strokes.toArray());

        new Label(container, SWT.NONE).setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
        createFilledLabel("Keystrokes saved by using code completion");

        new Label(container, SWT.NONE).setText("Total number: ");
        createLabelWithColor(MessageFormat.format("{0}", total.toString()));

        Double mean = mean(strokes.toArray());
        new Label(container, SWT.NONE).setText("Average number:");
        createLabelWithColor(MessageFormat.format("{0}", mean.toString()));
    }

    private void createTimeSpent() {
        ArrayDoubleList spentApplied = computeTimeSpentInCompletion(appliedEvents);
        long totalApplied = round(sum(spentApplied.toArray()));
        long meanApplied = round(mean(spentApplied.toArray()));

        ArrayDoubleList spentAborted = computeTimeSpentInCompletion(abortedEvents);
        long totalAborted = round(sum(spentAborted.toArray()));
        long meanAborted = round(mean(spentAborted.toArray()));

        new Label(container, SWT.NONE).setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
        createFilledLabel("Total Time spent in completion window on");

        new Label(container, SWT.NONE).setText("Concluded sessions:");
        createLabelWithColor(MessageFormat.format("{0}", toTimeString(totalApplied)));

        new Label(container, SWT.NONE).setText("Aborted sessions:");
        createLabelWithColor(MessageFormat.format("{0}", toTimeString(totalAborted)));

        new Label(container, SWT.NONE).setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
        createFilledLabel("Average time spent in completion window per");

        new Label(container, SWT.NONE).setText("Concluded sessions:");
        createLabelWithColor(MessageFormat.format("{0}", meanApplied));

        new Label(container, SWT.NONE).setText("Aborted sessions:");
        createLabelWithColor(MessageFormat.format("{0}", meanAborted));
    }

    private void createViewerForCompletion(Composite parent) {
        Multiset<ProposalKind> proposalKindBag = HashMultiset.create();
        final Multimap<ProposalKind, CompletionEvent> multiMap = ArrayListMultimap.create();

        for (final ProposalKind kind : ProposalKind.values()) {
            Collection<CompletionEvent> byKind = Collections2.filter(okayEvents, new Predicate<CompletionEvent>() {
                @Override
                public boolean apply(CompletionEvent input) {
                    if (kind == input.applied) {
                        if (!multiMap.containsEntry(kind, input)) {
                            multiMap.put(kind, input);
                        }
                        return true;
                    }
                    return false;
                }
            });
            if (byKind.size() > 0) {
                proposalKindBag.add(kind, byKind.size());
            }
        }

        Composite newComp = createWrapperComposite(parent);
        new Label(newComp, SWT.NONE).setText("Most frequently selected completion types were:");
        Composite comp = new Composite(newComp, SWT.NONE);
        TableColumnLayout layout = createTableColumnLayout(comp);

        TableViewer viewer = createTableViewer(comp);
        TableViewerColumn completionTypeColumn = createColumn("Completion Type", viewer, 150, layout, 50);
        setCompletionTypeSorter(viewer, completionTypeColumn);
        TableViewerColumn usedCompletionColumn = createColumn("Used", viewer, 60, layout, 15);
        setUsedCompletionSorter(viewer, usedCompletionColumn, multiMap);
        TableViewerColumn lastUsedColumn = createColumn("Last used", viewer, 110, layout, 35);
        setLastUsedSorter(viewer, lastUsedColumn, multiMap);
        usedCompletionColumn.getColumn().getParent().setSortColumn(usedCompletionColumn.getColumn());
        usedCompletionColumn.getColumn().getParent().setSortDirection(SWT.DOWN);

        TableViewerFactory.addMenu(viewer);
        viewer.setContentProvider(new ArrayContentProvider());
        viewer.setLabelProvider(new ProposalLabelProvider(multiMap));
        viewer.setInput(Bags.topUsingCount(proposalKindBag, 30));
    }

    private void createViewerForReceiverType(Composite parent) {
        Multiset<ITypeName> b = HashMultiset.create();
        for (CompletionEvent e : okayEvents) {
            if (e.receiverType == null) {
                continue;
            }
            b.add(e.receiverType);
        }

        Composite newComp = createWrapperComposite(parent);
        new Label(newComp, SWT.NONE)
                .setText("Code completion was triggered most frequently on variables of these types:");

        Composite comp = new Composite(newComp, SWT.NONE);
        TableColumnLayout layout = createTableColumnLayout(comp);

        TableViewer viewer = createTableViewer(comp);
        TableViewerColumn typeColumn = createColumn("Type", viewer, 450, layout, 60);
        setTypeSorter(viewer, typeColumn);
        TableViewerColumn countColumn = createColumn("Count", viewer, 100, layout, 30);
        setCountSorter(viewer, countColumn, b);
        countColumn.getColumn().getParent().setSortColumn(countColumn.getColumn());
        countColumn.getColumn().getParent().setSortDirection(SWT.DOWN);

        TableViewerFactory.addMenu(viewer);
        viewer.setContentProvider(new ArrayContentProvider());
        viewer.setLabelProvider(new TypeNameLabelProvider());
        viewer.setInput(Bags.topUsingCount(b, 30));
    }

    private void loadEvents() {
        File log = StatisticsSessionProcessor.getCompletionLogLocation();
        LinkedList<CompletionEvent> events = Lists.newLinkedList();
        if (log.exists()) {
            Gson gson = StatisticsSessionProcessor.getCompletionLogSerializer();
            try {
                for (String json : Files.readLines(log, Charsets.UTF_8)) {
                    CompletionEvent event = gson.fromJson(json, CompletionEvent.class);
                    events.add(event);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        okayEvents = Collections2.filter(events, not(new BuggyEventsPredicate()));
        appliedEvents = Collections2.filter(okayEvents, new HasAppliedProposalPredicate());
        abortedEvents = Collections2.filter(okayEvents, not(new HasAppliedProposalPredicate()));
    }

    private void createLabelWithColor(String text) {
        Label label = new Label(container, SWT.NONE);
        label.setText(text);
        label.setForeground(JFaceResources.getColorRegistry().get(JFacePreferences.COUNTER_COLOR));
    }

    private int calculatePercentData(Collection<CompletionEvent> list) {
        if (okayEvents.size() == 0) {
            return okayEvents.size();
        }
        double division = list.size() / (double) okayEvents.size() * 100;
        return (int) Math.round(division);
    }

    private void createFilledLabel(String text) {
        Label label2 = new Label(container, SWT.NONE);
        label2.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
        label2.setText(text);
    }

    private String toTimeString(long time) {
        return format("%d min, %d sec", MILLISECONDS.toMinutes(time),
                MILLISECONDS.toSeconds(time) - MINUTES.toSeconds(MILLISECONDS.toMinutes(time)));
    }

    private ArrayDoubleList computeTimeSpentInCompletion(Collection<CompletionEvent> events) {
        ArrayDoubleList spent = new ArrayDoubleList();
        for (CompletionEvent e : events) {
            long ms = e.sessionEnded - e.sessionStarted;
            if (ms > MAX_TIME_IN_COMPLETION) {
                ms = MAX_TIME_IN_COMPLETION;
            }
            spent.add(ms);
        }
        return spent;
    }

    public Collection<CompletionEvent> getOkayEvents() {
        return okayEvents;
    }

    public class ProposalLabelProvider extends CellLabelProvider {
        private Multimap<ProposalKind, CompletionEvent> eventsByKind;

        public ProposalLabelProvider(Multimap<ProposalKind, CompletionEvent> multiMap) {
            eventsByKind = multiMap;
        }

        @Override
        public void update(ViewerCell cell) {
            String cellText = null;
            Entry<ProposalKind> e = cast(cell.getElement());
            ProposalKind kind = e.getElement();
            int count = e.getCount();

            switch (cell.getColumnIndex()) {
            case 0:
                cellText = kind.toString().toLowerCase().replace('_', ' ');
                break;
            case 1:
                cellText = Integer.toString(count);
                break;
            case 2:
                Date past = new Date(getLastSessionStartedFor(kind));
                cellText = new DateFormatter().formatUnit(past, new Date());
                break;
            }

            if (cellText != null) {
                cell.setText(cellText);
            }
        }

        public Long getLastSessionStartedFor(ProposalKind proposal) {
            Collection<CompletionEvent> collection = eventsByKind.get(proposal);
            TreeSet<Long> sessionSet = new TreeSet<Long>();
            for (CompletionEvent completionEvent : collection) {
                sessionSet.add(completionEvent.sessionEnded);
            }
            return sessionSet.last();
        }
    }

    public class TypeNameLabelProvider extends CellLabelProvider {

        @Override
        public void update(ViewerCell cell) {
            String cellText = null;
            Entry<ITypeName> e = cast(cell.getElement());

            switch (cell.getColumnIndex()) {
            case 0:
                cellText = Names.vm2srcQualifiedType(e.getElement());
                break;
            case 1:
                cellText = Integer.toString(e.getCount());
                break;
            }

            if (cellText != null) {
                cell.setText(cellText);
            }
        }
    }
}
