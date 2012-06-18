/**
 * Copyright (c) 2012 Massimo Zugno
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.completion.rcp.internal.assignments.handler;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.recommenders.completion.rcp.assignments.AssignmentExpressionAnalyzer;
import org.eclipse.recommenders.completion.rcp.internal.assignments.visitor.AssignmentUsageCollector;
import org.eclipse.recommenders.utils.rcp.JdtUtils;
import org.eclipse.recommenders.utils.rcp.internal.RecommendersUtilsPlugin;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ListDialog;

import com.google.common.base.Optional;
import com.google.common.base.Strings;

/**
 * @author Massimo Zugno
 */
public class AnalyzeAssignmentsHandler extends AbstractHandler {

    private static final String PATTERN = "{0} : {1} ({2}%)";

    private String qualifiedName;

    private HashMap<String, Integer> assignments;

    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException {
        qualifiedName = getSelectedTypeQualifiedName();
        if (!Strings.isNullOrEmpty(qualifiedName)) {
            assignments = new HashMap<String, Integer>();
            Job job = new Job("Analyze Assignments: " + qualifiedName) {
                @Override
                public IStatus run(IProgressMonitor monitor) {
                    monitor.beginTask("Analyzing", 100);
                    // collect the Expressions
                    AssignmentExpressionAnalyzer analyzer = new AssignmentExpressionAnalyzer(qualifiedName);
                    List<Expression> expressions = analyzer.analyze(monitor);
                    monitor.worked(50);
                    // collect usage statistics
                    AssignmentUsageCollector collector = new AssignmentUsageCollector(expressions);
                    assignments.putAll(collector.getAssignments());
                    monitor.worked(100);
                    monitor.done();
                    return Status.OK_STATUS;
                }
            };
            job.addJobChangeListener(new JobChangeAdapter() {
                @Override
                public void done(IJobChangeEvent event) {
                    displayResults();
                }
            });
            job.setUser(true);
            job.schedule();
        }
        return null;
    }

    /**
     * 
     * @return
     */
    private String getSelectedTypeQualifiedName() {
        IEditorPart editor = null;
        try {
            editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
        } catch (Exception e) {
            RecommendersUtilsPlugin.logError(e, e.getMessage());
        }
        if (editor != null) {
            Optional<ITypeRoot> o = JdtUtils.findTypeRoot(editor);
            if (o.isPresent()) {
                ITypeRoot typeRoot = o.get();
                ISelection selection = editor.getSite().getSelectionProvider().getSelection();
                if (selection instanceof ITextSelection) {
                    ITextSelection textSelection = (ITextSelection) selection;
                    return getTypeQualifiedName(typeRoot, textSelection);
                }
            }
        }
        return null;
    }

    /**
     * Retrieve the fully qualified name of the (possible) {@link IType} selected on the active java editor.
     * 
     * @param typeRoot
     * @param textSelection
     * @return
     */
    private String getTypeQualifiedName(ITypeRoot typeRoot, ITextSelection textSelection) {
        try {
            IJavaElement[] selectedElements = typeRoot.codeSelect(textSelection.getOffset(), textSelection.getLength());
            if (selectedElements.length > 0) {
                IJavaElement javaElement = selectedElements[0];
                return getTypeQualifiedName(javaElement);
            }
        } catch (JavaModelException e) {
            RecommendersUtilsPlugin.logError(e, e.getMessage());
        }
        return null;
    }

    /**
     * @param javaElement
     * @return
     * @throws JavaModelException
     */
    private String getTypeQualifiedName(IJavaElement javaElement) throws JavaModelException {
        if (javaElement != null) {
            if (javaElement instanceof IType) {
                IType javaType = (IType) javaElement;
                return javaType.getFullyQualifiedName();
            } else if (javaElement instanceof IMethod) {
                IMethod javaMethod = (IMethod) javaElement;
                return getTypeQualifiedName(javaMethod.getDeclaringType());
            }
        }
        return null;
    }

    /**
     * Sort the entries by number of occurrences and display statistics results in a {@link ListDialog}.
     */
    private void displayResults() {
        final List<String> input = new ArrayList<String>();

        int size = computeOccurrencesSize(assignments);
        // add summary element
        input.add(MessageFormat.format(PATTERN, "Total", size, 100));
        input.add("------------------------");
        // sort entries by map value (assignment occurrences) in desc order
        List<Entry<String, Integer>> entries = sortByValue(assignments);
        for (Entry<String, Integer> entry : entries) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            Double perc = 100 * value / (double) size;
            input.add(MessageFormat.format(PATTERN, key, value, perc));
        }

        final Display display = PlatformUI.getWorkbench().getDisplay();
        display.asyncExec(new Runnable() {

            @Override
            public void run() {
                final Shell shell = display.getActiveShell();
                ListDialog dialog = new ListDialog(shell);
                dialog.setTitle("Assignment statistics");
                dialog.setMessage(qualifiedName);
                dialog.setContentProvider(new ArrayContentProvider());
                dialog.setLabelProvider(new LabelProvider());
                dialog.setInput(input);
                dialog.open();
            }
        });
    }

    /**
     * @param entries
     * @return
     */
    private int computeOccurrencesSize(Map<String, Integer> entries) {
        int size = 0;
        Collection<Integer> values = entries.values();
        for (Integer value : values) {
            size = size + value;
        }
        return size;
    }

    /**
     * Returns a descendant-ordered list of {@link Entry}s: the order is computed with priority to the map value, if two
     * values are equal then perform a comparison on the keys.
     * 
     * @param source
     *            the source map
     * @return the value-ordered list of map entries
     */
    private static <K extends Comparable<K>, V extends Comparable<V>> List<Entry<K, V>> sortByValue(Map<K, V> source) {
        List<Entry<K, V>> entries = new ArrayList<Map.Entry<K, V>>(source.entrySet());
        Comparator<Entry<K, V>> comparator = new Comparator<Entry<K, V>>() {
            public int compare(Entry<K, V> e1, Entry<K, V> e2) {
                V v1 = e1.getValue();
                V v2 = e2.getValue();
                int vc = v1.compareTo(v2);
                if (vc == 0) {
                    vc = e1.getKey().compareTo(e2.getKey());
                }
                return vc;
            }
        };
        Collections.sort(entries, Collections.reverseOrder(comparator));
        return entries;
    }

}
