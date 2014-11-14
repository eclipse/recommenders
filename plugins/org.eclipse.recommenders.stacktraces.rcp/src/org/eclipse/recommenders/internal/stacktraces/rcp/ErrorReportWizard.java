/**
 * Copyright (c) 2014 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 *    Daniel Haftstein - added support for multiple stacktraces
 */
package org.eclipse.recommenders.internal.stacktraces.rcp;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.recommenders.internal.stacktraces.rcp.model.ErrorReport;
import org.eclipse.recommenders.internal.stacktraces.rcp.model.ErrorReports;
import org.eclipse.recommenders.internal.stacktraces.rcp.model.Settings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ExpandEvent;
import org.eclipse.swt.events.ExpandListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class ErrorReportWizard extends MessageDialog {

    public static ImageDescriptor TITLE_IMAGE_DESC = ImageDescriptor.createFromFile(ErrorReportWizard.class,
            "/icons/wizban/stackframes_wiz.gif");
    private static final Image ERROR_ICON = PlatformUI.getWorkbench().getSharedImages()
            .getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
    private Settings settings;
    private TableViewer tableViewer;
    private StyledText messageText;
    private IObservableList errors;
    private ErrorReport activeSelection;

    private StyledText commentText;

    public ErrorReportWizard(Shell parentShell, Settings settings, IObservableList errors) {
        super(new Shell(parentShell), "We noticed an error", TITLE_IMAGE_DESC.createImage(),
                "We noticed an error. Do you want to report?", MessageDialog.ERROR, new String[] { "Send", "Cancel" },
                0);
        setShellStyle(SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MAX | SWT.APPLICATION_MODAL);
        this.settings = settings;
        this.errors = errors;
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setLayout(new GridLayout(1, false));
    }

    @Override
    public Composite createCustomArea(final Composite container) {
        Composite area = new Composite(container, SWT.NONE);
        GridLayoutFactory.fillDefaults().applyTo(area);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(area);

        final ExpandBar advancedView = new ExpandBar(area, SWT.NONE);
        GridDataFactory.fillDefaults().applyTo(advancedView);
        final Composite advancedComposite = createAdvancedComposite(advancedView);
        advancedComposite.setLayout(new FillLayout());
        GridDataFactory.fillDefaults().applyTo(advancedComposite);
        final ExpandItem item1 = new ExpandItem(advancedView, SWT.NONE, 0);
        item1.setText("Advanced view");
        final int height = advancedComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
        item1.setHeight(height);
        item1.setControl(advancedComposite);
        advancedView.addExpandListener(new ExpandListener() {
            @Override
            public void itemExpanded(ExpandEvent e) {
                if (e.item instanceof ExpandItem) {
                    GridData data2 = (GridData) advancedComposite.getLayoutData();
                    data2.heightHint = 400;
                    final Shell shell = advancedView.getShell();
                    Point size = shell.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
                    shell.setSize(size);
                    shell.layout(true, true);

                }
            }

            @Override
            public void itemCollapsed(ExpandEvent e) {
                if (e.item instanceof ExpandItem) {
                    GridData data2 = (GridData) advancedComposite.getLayoutData();
                    data2.heightHint = SWT.DEFAULT;
                    Shell shell = advancedView.getShell();
                    Point size = shell.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
                    shell.setSize(size);
                    shell.layout(true, true);
                }
            }
        });
        return area;
    }

    private Composite createAdvancedComposite(Composite container) {
        SashForm composite = new SashForm(container, SWT.HORIZONTAL);
        composite.setLayout(new FillLayout());

        // left
        createTableComposite(composite);

        // right
        SashForm right = new SashForm(composite, SWT.VERTICAL);

        // right upper
        createCommentComposite(right);

        // right lower
        createMessageComposite(right);
        right.setWeights(new int[] { 15, 85 });
        composite.setWeights(new int[] { 20, 80 });

        return composite;
    }

    private Composite createTableComposite(Composite container) {
        Composite tableComposite = new Composite(container, SWT.NONE);
        tableViewer = new TableViewer(tableComposite, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION
                | SWT.BORDER);
        TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
        column.setLabelProvider(new ColumnLabelProvider() {

            @Override
            public String getText(Object element) {
                ErrorReport event = (ErrorReport) element;
                return event.getStatus().getMessage();
            }

            @Override
            public Image getImage(Object element) {
                return ERROR_ICON;
            }
        });
        TableColumnLayout tableColumnLayout = new TableColumnLayout();
        tableColumnLayout.setColumnData(column.getColumn(), new ColumnWeightData(100));
        tableComposite.setLayout(tableColumnLayout);
        tableViewer.setContentProvider(new ObservableListContentProvider());
        tableViewer.setInput(errors);
        tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                if (!event.getSelection().isEmpty()) {
                    IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
                    activeSelection = (ErrorReport) selection.getFirstElement();
                    ErrorReport copy = ErrorReports.copy(activeSelection);
                    copy.setName(settings.getName());
                    copy.setEmail(settings.getEmail());
                    messageText.setText(ErrorReports.prettyPrint(copy, settings));
                    String comment = activeSelection.getComment();
                    commentText.setText(comment == null ? "" : comment);
                }
            }

        });
        return tableComposite;
    }

    private Composite createMessageComposite(Composite container) {
        Composite messageComposite = new Composite(container, SWT.NONE);
        GridLayoutFactory.fillDefaults().applyTo(messageComposite);
        Label messageLabel = new Label(messageComposite, SWT.FILL);
        messageLabel.setText(Messages.PREVIEWPAGE_LABEL_MESSAGE);
        GridDataFactory.fillDefaults().applyTo(messageLabel);
        messageText = new StyledText(messageComposite, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER);
        messageText.setEditable(false);
        messageText.setMargins(5, messageText.getTopMargin(), messageText.getRightMargin(),
                messageText.getBottomMargin());
        messageText.setFont(JFaceResources.getFont(JFaceResources.TEXT_FONT));
        messageText.setForeground(new Color(container.getDisplay(), 80, 80, 80));
        GridDataFactory.fillDefaults().minSize(150, 1).hint(300, 300).grab(true, true).applyTo(messageText);
        return messageComposite;
    }

    private Composite createCommentComposite(Composite container) {
        Composite commentComposite = new Composite(container, SWT.FILL);
        GridLayoutFactory.fillDefaults().applyTo(commentComposite);
        Label commentLabel = new Label(commentComposite, SWT.NONE);
        commentLabel.setText(Messages.PREVIEWPAGE_LABEL_COMMENT);
        GridDataFactory.fillDefaults().applyTo(commentLabel);
        commentText = new StyledText(commentComposite, SWT.V_SCROLL | SWT.BORDER | SWT.WRAP);
        commentText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                if (activeSelection != null) {
                    activeSelection.setComment(commentText.getText());
                }
            }
        });
        GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, 75).applyTo(commentText);
        return commentComposite;
    }

    @Override
    public void create() {
        super.create();
        if (!errors.isEmpty()) {
            StructuredSelection selection = new StructuredSelection(tableViewer.getElementAt(0));
            tableViewer.setSelection(selection, true);
        }
    }
}
