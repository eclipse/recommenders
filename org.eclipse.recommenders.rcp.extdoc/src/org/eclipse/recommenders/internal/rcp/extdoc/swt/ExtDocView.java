/**
 * Copyright (c) 2011 Stefan Henss.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Stefan Henss - initial API and implementation.
 */
package org.eclipse.recommenders.internal.rcp.extdoc.swt;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jdt.ui.actions.OpenAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.recommenders.commons.selection.IJavaElementSelection;
import org.eclipse.recommenders.internal.rcp.extdoc.ProviderStore;
import org.eclipse.recommenders.rcp.extdoc.ExtDocPlugin;
import org.eclipse.recommenders.rcp.extdoc.IProvider;
import org.eclipse.recommenders.rcp.extdoc.SwtFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.internal.WorkbenchWindow;
import org.eclipse.ui.part.ViewPart;

import com.google.inject.Inject;

public class ExtDocView extends ViewPart {

    static final int HEAD_LABEL_HEIGHT = 20;
    private static final String SASH_POSITION_KEY = "extDocSashPosition";
    private static boolean linkingEnabled = true;

    private final ProviderStore providerStore;

    private ProvidersComposite providersComposite;
    private ProvidersTable table;
    private CLabel selectionLabel;
    private JavaElementLabelProvider labelProvider;

    @Inject
    ExtDocView(final ProviderStore providerStore) {
        this.providerStore = providerStore;
        initializeLabelProvider();
    }

    private void initializeLabelProvider() {
        labelProvider = new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_QUALIFIED
                | JavaElementLabelProvider.SHOW_OVERLAY_ICONS | JavaElementLabelProvider.SHOW_RETURN_TYPE
                | JavaElementLabelProvider.SHOW_PARAMETERS);
    }

    @Override
    public final void createPartControl(final Composite parent) {
        createSash(parent);
        addProviders();
        fillActionBars();
    }

    private void createSash(final Composite parent) {
        final SashForm sashForm = new SashForm(parent, SWT.SMOOTH);
        sashForm.setLayout(new FillLayout());
        createLeftSashSide(sashForm);
        createRightSashSide(sashForm);
        handleSashWeights(sashForm);
    }

    private void createLeftSashSide(final SashForm sashForm) {
        table = new ProvidersTable(sashForm, providerStore);
    }

    private void createRightSashSide(final SashForm sashForm) {
        final Composite container = SwtFactory.createGridComposite(sashForm, 1, 0, 0, 0, 0);
        createSelectionLabel(container);
        providersComposite = new ProvidersComposite(container, true);
    }

    private void handleSashWeights(final SashForm sashForm) {
        final int sashWeight = ExtDocPlugin.getPreferences().getInt(SASH_POSITION_KEY, 150);
        sashForm.setWeights(new int[] { sashWeight, 1000 - sashWeight });
        sashForm.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(final DisposeEvent event) {
                ExtDocPlugin.getPreferences().putInt(SASH_POSITION_KEY, sashForm.getWeights()[0]);
            }
        });
    }

    private void createSelectionLabel(final Composite container) {
        selectionLabel = new CLabel(container, SWT.NONE);
        final GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gridData.heightHint = HEAD_LABEL_HEIGHT;
        selectionLabel.setLayoutData(gridData);
        selectionLabel.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));
    }

    private void addProviders() {
        final WorkbenchWindow window = (WorkbenchWindow) getViewSite().getWorkbenchWindow();
        for (final IProvider provider : providerStore.getProviders()) {
            final Composite composite = providersComposite.addProvider(provider, window);
            table.addProvider(composite, provider.getProviderName(), provider.getIcon(), true);
        }
    }

    private void fillActionBars() {
        final IViewSite viewSite = getViewSite();
        if (viewSite != null) {
            final IToolBarManager toolbar = viewSite.getActionBars().getToolBarManager();
            toolbar.removeAll();
            toolbar.add(new OpenInputAction());
            toolbar.add(new LinkWithEditorAction());
        }
    }

    public final boolean selectionChanged(final IJavaElementSelection selection) {
        if (selection != null && table != null) {
            table.setContext(selection);
            updateProviders(selection);
            providersComposite.scrollToTop();
            updateSelectionLabel(selection.getJavaElement());
            return true;
        }
        return false;
    }

    private void updateProviders(final IJavaElementSelection selection) {
        ProviderUpdateJob.cancelActiveJobs();
        for (final TableItem item : table.getItems()) {
            if (item.getChecked()) {
                final ProviderUpdateJob job = new ProviderUpdateJob(table, item, selection);
                job.setSystem(true);
                job.schedule();
            }
        }
    }

    private void updateSelectionLabel(final IJavaElement javaElement) {
        selectionLabel.setText(labelProvider.getText(javaElement));
        selectionLabel.setImage(labelProvider.getImage(javaElement));
        selectionLabel.getParent().layout();
    }

    @Override
    public final void setFocus() {
        providersComposite.setFocus();
    }

    public final boolean isLinkingEnabled() {
        return linkingEnabled;
    }

    private final class OpenInputAction extends AbstractAction {

        OpenInputAction() {
            super("Link with Selection", "lcl16/goto_input.png", SWT.TOGGLE);
        }

        @Override
        public void run() {
            final IJavaElement inputElement = table.getLastSelection().getJavaElement();
            new OpenAction(getViewSite()).run(new Object[] { inputElement });
        }
    }

    private static final class LinkWithEditorAction extends AbstractAction {

        LinkWithEditorAction() {
            super("Link with Selection", "lcl16/link.gif", SWT.TOGGLE);
            setChecked(true);
        }

        @Override
        public void run() {
            linkingEnabled = !linkingEnabled;
        }
    }
}
