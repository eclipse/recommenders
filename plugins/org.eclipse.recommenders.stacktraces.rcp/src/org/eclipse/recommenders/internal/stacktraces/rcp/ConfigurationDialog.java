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

import static org.eclipse.emf.databinding.EMFProperties.value;
import static org.eclipse.jface.databinding.swt.WidgetProperties.*;
import static org.eclipse.jface.fieldassist.FieldDecorationRegistry.DEC_INFORMATION;
import static org.eclipse.recommenders.internal.stacktraces.rcp.Constants.*;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.recommenders.internal.stacktraces.rcp.model.ModelPackage;
import org.eclipse.recommenders.internal.stacktraces.rcp.model.SendAction;
import org.eclipse.recommenders.internal.stacktraces.rcp.model.Settings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ConfigurationDialog extends TitleAreaDialog {

    public static ImageDescriptor TITLE_IMAGE_DESC = ImageDescriptor.createFromFile(ConfigurationDialog.class,
            "/icons/wizban/stackframes_wiz.gif");
    private ComboViewer actionComboViewer;
    private Text emailText;
    private Text nameText;
    private Button anonymizeStacktracesButton;
    private Button clearMessagesButton;

    private Settings settings;

    public ConfigurationDialog(Shell parentShell, Settings settings) {
        super(parentShell);
        this.settings = settings;
        setHelpAvailable(false);

    }

    @Override
    public void create() {
        super.create();
        setTitle("Error Reporting started for the first time.");
        setMessage(" Please take a moment for configuration. ", IMessageProvider.INFORMATION);
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        overrideButtonTexts();
    }

    private void overrideButtonTexts() {
        Button ok = getButton(IDialogConstants.OK_ID);
        ok.setText("Activate Error Reporting");
        setButtonLayoutData(ok);

        Button cancel = getButton(IDialogConstants.CANCEL_ID);
        cancel.setText("Disable Error Reporting");
        setButtonLayoutData(cancel);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite area = (Composite) super.createDialogArea(parent);
        setTitleImage(TITLE_IMAGE_DESC.createImage());
        Composite container = new Composite(area, SWT.NONE);
        container.setLayout(new GridLayout());

        GridLayoutFactory layoutFactory = GridLayoutFactory.fillDefaults().margins(5, 5).numColumns(2);
        GridDataFactory dataFactory = GridDataFactory.fillDefaults().grab(true, false);

        Label text = new Label(container, SWT.WRAP);
        text.setText("Error Reporting is an automated service to analyze logged errors in Eclipse and track them on the Eclipse bugtracker.\n"
                + "You can help to improve the quality of the Eclipse Platform and installed plugins by using the reporting system.\n"
                + "All settings can be changed in the preferences at any time.");

        horizontalSeparator(container);

        Composite actionComposite = createActionComposite(container, layoutFactory, dataFactory);
        dataFactory.applyTo(actionComposite);

        horizontalSeparator(container);

        Group personalGroup = createPersonalGroup(container, layoutFactory, dataFactory);
        dataFactory.applyTo(personalGroup);

        horizontalSeparator(container);

        Group makeAnonymousGroup = makeAnonymousGroup(container, layoutFactory, dataFactory);
        dataFactory.applyTo(makeAnonymousGroup);

        Composite linksComposite = createLinksComposite(container, layoutFactory, dataFactory);
        dataFactory.grab(true, true).align(SWT.FILL, SWT.FILL).applyTo(linksComposite);

        createDataBindingContext();
        return container;
    }

    private void horizontalSeparator(Composite container) {
        GridDataFactory.fillDefaults().grab(true, false).applyTo(new Label(container, SWT.HORIZONTAL | SWT.SEPARATOR));
    }

    private Composite createActionComposite(Composite container, GridLayoutFactory layoutFactory,
            GridDataFactory dataFactory) {
        Composite actionComposite = new Composite(container, SWT.NONE);
        layoutFactory.applyTo(actionComposite);
        new Label(actionComposite, SWT.NONE).setText(Messages.FIELD_LABEL_ACTION);
        actionComboViewer = new ComboViewer(actionComposite, SWT.READ_ONLY);
        actionComboViewer.setContentProvider(ArrayContentProvider.getInstance());
        actionComboViewer.setInput(new SendAction[] { SendAction.ASK, SendAction.SILENT });
        actionComboViewer.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                SendAction mode = (SendAction) element;
                switch (mode) {
                case ASK:
                    return Messages.PREFERENCEPAGE_ASK_LABEL;
                case SILENT:
                    return Messages.FIELD_LABEL_ACTION_REPORT_ALWAYS;
                default:
                    return super.getText(element);
                }
            }
        });
        dataFactory.applyTo(actionComboViewer.getControl());
        actionComboViewer.setSelection(new StructuredSelection(settings.getAction()));
        return actionComposite;
    }

    private Group createPersonalGroup(Composite container, GridLayoutFactory layoutFactory, GridDataFactory dataFactory) {
        Group personalGroup = new Group(container, SWT.SHADOW_ETCHED_IN | SWT.SHADOW_ETCHED_OUT | SWT.SHADOW_IN
                | SWT.SHADOW_OUT);
        personalGroup.setText(Messages.SETTINGSPAGE_GROUPLABEL_PERSONAL);
        layoutFactory.numColumns(2).applyTo(personalGroup);
        FieldDecoration infoDecoration = FieldDecorationRegistry.getDefault().getFieldDecoration(DEC_INFORMATION);
        new Label(personalGroup, SWT.NONE).setText(Messages.FIELD_LABEL_NAME);
        nameText = new Text(personalGroup, SWT.BORDER);
        nameText.setMessage(Messages.FIELD_MESSAGE_NAME);
        dataFactory.applyTo(nameText);
        ControlDecoration dec = new ControlDecoration(nameText, SWT.TOP | SWT.LEFT);
        dec.setImage(infoDecoration.getImage());
        dec.setDescriptionText(Messages.FIELD_DESC_NAME);
        new Label(personalGroup, SWT.NONE).setText(Messages.FIELD_LABEL_EMAIL);
        emailText = new Text(personalGroup, SWT.BORDER);
        emailText.setMessage(Messages.FIELD_MESSAGE_EMAIL);
        dataFactory.applyTo(emailText);
        ControlDecoration dec2 = new ControlDecoration(emailText, SWT.TOP | SWT.LEFT);
        dec2.setImage(infoDecoration.getImage());
        dec2.setDescriptionText(Messages.FIELD_DESC_EMAIL);
        return personalGroup;
    }

    private Group makeAnonymousGroup(Composite container, GridLayoutFactory layoutFactory, GridDataFactory dataFactory) {
        Group makeAnonymousGroup = new Group(container, SWT.SHADOW_ETCHED_IN | SWT.SHADOW_ETCHED_OUT | SWT.SHADOW_IN
                | SWT.SHADOW_OUT);
        layoutFactory.numColumns(1).applyTo(makeAnonymousGroup);
        makeAnonymousGroup.setText("Make anonymous");
        anonymizeStacktracesButton = new Button(makeAnonymousGroup, SWT.CHECK);
        anonymizeStacktracesButton.setText(Messages.FIELD_LABEL_STACKTRACES);
        dataFactory.applyTo(anonymizeStacktracesButton);
        DefaultToolTip anonymizeStacktracesToolTip = new DefaultToolTip(anonymizeStacktracesButton);
        anonymizeStacktracesToolTip.setText(Messages.TOOLTIP_MAKE_STACKTRACE_ANONYMOUS);
        clearMessagesButton = new Button(makeAnonymousGroup, SWT.CHECK);
        clearMessagesButton.setText(Messages.FIELD_LABEL_MESSAGES);
        dataFactory.applyTo(clearMessagesButton);
        DefaultToolTip clearMessagesToolTip = new DefaultToolTip(clearMessagesButton);
        clearMessagesToolTip.setText(Messages.TOOLTIP_MAKE_MESSAGES_ANONYMOUS);
        return makeAnonymousGroup;
    }

    private Composite createLinksComposite(Composite container, GridLayoutFactory layoutFactory,
            GridDataFactory dataFactory) {
        Composite linksComposite = new Composite(container, SWT.NONE);
        layoutFactory.numColumns(2).applyTo(linksComposite);
        Link learnMoreLink = new Link(linksComposite, SWT.NONE);
        dataFactory.align(SWT.BEGINNING, SWT.END).applyTo(learnMoreLink);
        learnMoreLink.setText(Messages.LINK_LEARN_MORE);
        learnMoreLink.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Browsers.openInExternalBrowser(HELP_URL);
            }
        });
        Link feedbackLink = new Link(linksComposite, SWT.NONE);
        dataFactory.align(SWT.END, SWT.END).applyTo(feedbackLink);
        feedbackLink.setText(Messages.LINK_PROVIDE_FEEDBACK);
        feedbackLink.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Browsers.openInExternalBrowser(FEEDBACK_FORM_URL);
            }
        });
        return linksComposite;
    }

    private DataBindingContext createDataBindingContext() {
        DataBindingContext context = new DataBindingContext();

        ModelPackage pkg = ModelPackage.eINSTANCE;

        IObservableValue ovTxtName = text(SWT.Modify).observe(nameText);
        IObservableValue ovSetName = value(pkg.getSettings_Name()).observe(settings);
        context.bindValue(ovTxtName, ovSetName, null, null);

        IObservableValue ovTxtEmail = text(SWT.Modify).observe(emailText);
        IObservableValue ovSetEmail = value(pkg.getSettings_Email()).observe(settings);
        context.bindValue(ovTxtEmail, ovSetEmail, null, null);

        IObservableValue ovBtnAnonSt = selection().observe(anonymizeStacktracesButton);
        IObservableValue ovSetAnonSt = value(pkg.getSettings_AnonymizeStrackTraceElements()).observe(settings);
        context.bindValue(ovBtnAnonSt, ovSetAnonSt, null, null);

        IObservableValue ovBtnAnonMsg = selection().observe(clearMessagesButton);
        IObservableValue ovSetAnonMsg = value(pkg.getSettings_AnonymizeMessages()).observe(settings);
        context.bindValue(ovBtnAnonMsg, ovSetAnonMsg, null, null);

        IObservableValue ovVwrAction = ViewersObservables.observeSinglePostSelection(actionComboViewer);
        IObservableValue ovSetAction = value(pkg.getSettings_Action()).observe(settings);
        context.bindValue(ovVwrAction, ovSetAction, null, null);

        return context;
    }

}
