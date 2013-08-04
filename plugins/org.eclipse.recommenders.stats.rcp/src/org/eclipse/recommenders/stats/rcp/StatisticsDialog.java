package org.eclipse.recommenders.stats.rcp;

import static com.google.common.base.Predicates.not;
import static java.lang.Math.round;
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.*;
import static org.apache.commons.math.stat.StatUtils.*;
import static org.eclipse.recommenders.stats.rcp.TableSorters.*;
import static org.eclipse.recommenders.stats.rcp.TableViewerFactory.*;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.primitives.ArrayDoubleList;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.recommenders.stats.rcp.CompletionEvent.ProposalKind;
import org.eclipse.recommenders.utils.Bags;
import org.eclipse.recommenders.utils.names.ITypeName;
import org.eclipse.recommenders.utils.names.Names;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import com.google.common.base.Charsets;
import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;
import com.google.common.io.Files;
import com.google.gson.Gson;

public class StatisticsDialog extends TitleAreaDialog {

    private static final long MAX_TIME_IN_COMPLETION = TimeUnit.MINUTES.toMillis(2);

    private Collection<CompletionEvent> okayEvents;
    private Collection<CompletionEvent> appliedEvents;
    private Collection<CompletionEvent> abortedEvents;

    private Composite container;

    private final class BuggyEventsPredicate implements Predicate<CompletionEvent> {
        @Override
        public boolean apply(final CompletionEvent input) {
            return input.numberOfProposals < 1 || input.sessionEnded < input.sessionStarted;
        }
    }

    private final class HasAppliedProposalPredicate implements Predicate<CompletionEvent> {
        @Override
        public boolean apply(final CompletionEvent e) {
            return e.applied != null;
        }
    }

    public StatisticsDialog(final Shell parentShell) {
        super(parentShell);
        setHelpAvailable(false);
        loadEvents();
    }

    public StatisticsDialog() {
        super(null);
    }

    @Override
    protected Control createContents(final Composite parent) {
        super.createContents(parent);
        getShell().setText("Developer Activity Report");
        getShell().setSize(550, 800);
        setMessage(getDescriptionText(), IMessageProvider.INFORMATION);
        return parent;
    }

    @Override
    protected Control createDialogArea(final Composite parent) {
        parent.setLayout(new GridLayout());

        final TabFolder folder = new TabFolder(parent, SWT.NONE);
        folder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        final TabItem statsTab = new TabItem(folder, SWT.NONE);
        statsTab.setText("Content Assist");

        final Composite firstComp = new Composite(folder, SWT.NONE);
        firstComp.setLayout(new GridLayout());

        final TabItem commandsTab = new TabItem(folder, SWT.NONE);
        commandsTab.setText("Commands");

        createWidgets(firstComp);
        appendNumberOfCompletionEvents();
        appendNumberOfKeystrokesSaved();
        appendTimeSpent();

        final SashForm sashForm = new SashForm(firstComp, SWT.VERTICAL);
        final GridData data = new GridData(GridData.FILL_BOTH);
        sashForm.setLayoutData(data);

        showCompletionKindInViewer(sashForm);
        showReceiverTypeInViewer(sashForm);

        sashForm.setWeights(new int[] { 50, 50 });

        statsTab.setControl(firstComp);
        final Composite secondComp = new TriggeredCommandsTab().createContent(folder);
        commandsTab.setControl(secondComp);

        return parent;
    }

    private void createWidgets(final Composite parent) {
        container = new Composite(parent, SWT.NONE);
        GridLayout grid = new GridLayout(2, false);
        grid.marginHeight = 5;
        grid.horizontalSpacing = 0;
        grid.verticalSpacing = 0;
        container.setLayout(grid);
    }

    private void appendNumberOfCompletionEvents() {
        int total = 0;
        for (final CompletionEvent e : okayEvents) {
            total += e.numberOfProposals;
        }
        final int completedInPercent = calculatePercentData(appliedEvents);
        new Label(container, SWT.NONE).setText("Number of times code completion triggered: ");
        createLabelWithColor(MessageFormat.format("{0}", okayEvents.size()));
        final int abortedInPercent = calculatePercentData(abortedEvents);

        new Label(container, SWT.NONE).setText("Number of concluded completions: ");
        createLabelWithColor(MessageFormat.format("{0} ({1}%)", appliedEvents.size(), completedInPercent));

        new Label(container, SWT.NONE).setText("Number of aborted completions: ");
        createLabelWithColor(MessageFormat.format("{0} ({1}%)", abortedEvents.size(), abortedInPercent));

        new Label(container, SWT.NONE).setText("Number of proposals offered by code completion: ");
        createLabelWithColor(MessageFormat.format("{0}", total));
    }

    private void createLabelWithColor(String text) {
        Label label = new Label(container, SWT.NONE);
        label.setText(text);
        label.setForeground(JFaceResources.getColorRegistry().get(JFacePreferences.COUNTER_COLOR));
    }

    private int calculatePercentData(final Collection<CompletionEvent> list) {
        if (okayEvents.size() == 0) {
            return okayEvents.size();
        }
        final double division = list.size() / (double) okayEvents.size() * 100;
        return (int) Math.round(division);
    }

    private void appendNumberOfKeystrokesSaved() {
        final ArrayDoubleList strokes = new ArrayDoubleList();
        for (final CompletionEvent e : appliedEvents) {
            final int prefix = e.prefix == null ? 0 : e.prefix.length();
            final int completionLength = e.completion == null ? 0 : e.completion.length();
            final int saved = Math.max(0, completionLength - prefix);
            strokes.add(saved);
        }

        final double total = sum(strokes.toArray());

        new Label(container, SWT.NONE).setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
        createFilledLabel("Keystrokes saved by using code completion");

        new Label(container, SWT.NONE).setText("Total number: ");
        createLabelWithColor(MessageFormat.format("{0}", total));

        final double mean = mean(strokes.toArray());
        new Label(container, SWT.NONE).setText("Average number:");
        createLabelWithColor(MessageFormat.format("{0}", mean));
    }

    private void appendTimeSpent() {
        final ArrayDoubleList spentApplied = computeTimeSpentInCompletion(appliedEvents);
        final long totalApplied = round(sum(spentApplied.toArray()));
        final long meanApplied = round(mean(spentApplied.toArray()));

        final ArrayDoubleList spentAborted = computeTimeSpentInCompletion(abortedEvents);
        final long totalAborted = round(sum(spentAborted.toArray()));
        final long meanAborted = round(mean(spentAborted.toArray()));

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

    private void createFilledLabel(String text) {
        Label label2 = new Label(container, SWT.NONE);
        label2.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
        label2.setText(text);
    }

    private String toTimeString(final long time) {
        return format("%d min, %d sec", MILLISECONDS.toMinutes(time),
                MILLISECONDS.toSeconds(time) - MINUTES.toSeconds(MILLISECONDS.toMinutes(time)));
    }

    private ArrayDoubleList computeTimeSpentInCompletion(final Collection<CompletionEvent> events) {
        final ArrayDoubleList spent = new ArrayDoubleList();
        for (final CompletionEvent e : events) {
            long ms = e.sessionEnded - e.sessionStarted;
            if (ms > MAX_TIME_IN_COMPLETION) {
                ms = MAX_TIME_IN_COMPLETION;
            }
            spent.add(ms);
        }
        return spent;
    }

    private void showCompletionKindInViewer(final Composite parent) {
        final Multiset<ProposalKind> proposalKindBag = HashMultiset.create();
        final Multimap<ProposalKind, CompletionEvent> multiMap = ArrayListMultimap.create();

        for (final ProposalKind kind : ProposalKind.values()) {
            final Collection<CompletionEvent> byKind = Collections2.filter(okayEvents,
                    new Predicate<CompletionEvent>() {
                        @Override
                        public boolean apply(final CompletionEvent input) {
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

        final Composite newComp = createWrapperComposite(parent);
        new Label(newComp, SWT.NONE).setText("Most frequently selected completion types were:");
        final Composite comp = new Composite(newComp, SWT.NONE);
        final TableColumnLayout layout = createTableColumnLayout(comp);

        final TableViewer viewer = createTableViewer(comp);
        final TableViewerColumn completionTypeColumn = createColumn("Completion Type", viewer, 150, layout, 50);
        setCompletionTypeSorter(viewer, completionTypeColumn);
        final TableViewerColumn usedCompletionColumn = createColumn("Used", viewer, 60, layout, 15);
        setUsedCompletionSorter(viewer, usedCompletionColumn, multiMap);
        final TableViewerColumn lastUsedColumn = createColumn("Last used", viewer, 110, layout, 35);
        setLastUsedSorter(viewer, lastUsedColumn, multiMap);
        usedCompletionColumn.getColumn().getParent().setSortColumn(usedCompletionColumn.getColumn());
        usedCompletionColumn.getColumn().getParent().setSortDirection(SWT.DOWN);

        viewer.setContentProvider(new ArrayContentProvider());
        viewer.setLabelProvider(new ProposalLabelProvider(multiMap));
        viewer.setInput(Bags.topUsingCount(proposalKindBag, 30));
    }

    private void showReceiverTypeInViewer(final Composite parent) {
        final Multiset<ITypeName> b = HashMultiset.create();
        for (final CompletionEvent e : okayEvents) {
            if (e.receiverType == null) {
                continue;
            }
            b.add(e.receiverType);
        }

        final Composite newComp = createWrapperComposite(parent);
        new Label(newComp, SWT.NONE)
                .setText("Code completion was triggered most frequently on variables of these types:");

        final Composite comp = new Composite(newComp, SWT.NONE);
        final TableColumnLayout layout = createTableColumnLayout(comp);

        final TableViewer viewer = createTableViewer(comp);
        final TableViewerColumn typeColumn = createColumn("Type", viewer, 450, layout, 60);
        setTypeSorter(viewer, typeColumn);
        final TableViewerColumn countColumn = createColumn("Count", viewer, 100, layout, 30);
        setCountSorter(viewer, countColumn, b);
        countColumn.getColumn().getParent().setSortColumn(countColumn.getColumn());
        countColumn.getColumn().getParent().setSortDirection(SWT.DOWN);

        viewer.setContentProvider(new ArrayContentProvider());
        viewer.setLabelProvider(new TypeNameLabelProvider(b));
        viewer.setInput(Bags.topUsingCount(b, 30));
    }

    private Composite createWrapperComposite(final Composite parent) {
        final Composite newComp = new Composite(parent, SWT.NONE);
        newComp.setLayout(new GridLayout());
        newComp.setLayoutData(new GridData(GridData.FILL_BOTH));
        return newComp;
    }

    @Override
    protected final void createButtonsForButtonBar(final Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
    }

    @Override
    protected boolean isResizable() {
        return true;
    }

    private String getDescriptionText() {
        String date = "the beginning of recording";
        if (okayEvents.size() > 0) {
            final Date start = new Date(Iterables.getFirst(okayEvents, null).sessionStarted);
            date = format("%tF", start);
        }
        return "Here is a summary of your activities since " + date;
    }

    private void loadEvents() {
        final File log = StatisticsSessionProcessor.getCompletionLogLocation();
        final LinkedList<CompletionEvent> events = Lists.newLinkedList();
        if (log.exists()) {
            final Gson gson = StatisticsSessionProcessor.getCompletionLogSerializer();
            try {
                for (final String json : Files.readLines(log, Charsets.UTF_8)) {
                    final CompletionEvent event = gson.fromJson(json, CompletionEvent.class);
                    events.add(event);
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        okayEvents = Collections2.filter(events, not(new BuggyEventsPredicate()));
        appliedEvents = Collections2.filter(okayEvents, new HasAppliedProposalPredicate());
        abortedEvents = Collections2.filter(okayEvents, not(new HasAppliedProposalPredicate()));
    }

    public class TypeNameLabelProvider extends CellLabelProvider {
        private final Multiset<ITypeName> bag;

        public TypeNameLabelProvider(final Multiset<ITypeName> b) {
            super();
            bag = b;
        }

        @Override
        public void update(final ViewerCell cell) {
            String cellText = null;
            Entry<ITypeName> e = (Entry<ITypeName>) cell.getElement();

            switch (cell.getColumnIndex()) {
            case 0:
                cellText = Names.vm2srcQualifiedType(e.getElement());
                break;
            case 1:
                cellText = Integer.toString(bag.count(e.getCount()));
                break;
            }

            if (cellText != null) {
                cell.setText(cellText);
            }
        }
    }

    public class ProposalLabelProvider extends CellLabelProvider {
        private final Multimap<ProposalKind, CompletionEvent> multiMap;

        public ProposalLabelProvider(final Multimap<ProposalKind, CompletionEvent> multiMap) {
            super();
            this.multiMap = multiMap;
        }

        @Override
        public void update(final ViewerCell cell) {
            String cellText = null;
            final ProposalKind proposal = (ProposalKind) cell.getElement();

            switch (cell.getColumnIndex()) {
            case 0:
                cellText = proposal.toString().toLowerCase().replace('_', ' ');
                break;
            case 1:
                cellText = Integer.toString(multiMap.get(proposal).size());
                break;
            case 2:
                final Date past = new Date(getLastSessionStartedFor(proposal));
                cellText = new DateFormatter().formatUnit(past, new Date());
                break;
            }

            if (cellText != null) {
                cell.setText(cellText);
            }
        }

        public Long getLastSessionStartedFor(final ProposalKind proposal) {
            final Collection<CompletionEvent> collection = multiMap.get(proposal);
            final TreeSet<Long> sessionSet = new TreeSet<Long>();
            for (final CompletionEvent completionEvent : collection) {
                sessionSet.add(completionEvent.sessionEnded);
            }
            return sessionSet.last();
        }
    }
}
