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
import static org.eclipse.recommenders.internal.stacktraces.rcp.Constants.*;

import java.text.MessageFormat;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.recommenders.internal.stacktraces.rcp.model.ModelPackage;
import org.eclipse.recommenders.internal.stacktraces.rcp.model.SendAction;
import org.eclipse.recommenders.internal.stacktraces.rcp.model.Settings;
import org.eclipse.recommenders.rcp.utils.PreferencesHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PreferencesUtil;

public class ConfigurationDialog extends TitleAreaDialog {

    /**
     * Return code to indicate a cancel using the esc-button.
     */
    public static final int ESC_CANCEL = 42 + 42;
    public static final ImageDescriptor TITLE_IMAGE_DESC = ImageDescriptor.createFromFile(ConfigurationDialog.class,
            "/icons/wizban/stackframes_wiz.gif"); //$NON-NLS-1$
    private ComboViewer actionComboViewer;
    private Text emailText;
    private Text nameText;
    private Button anonymizeStacktracesButton;
    private Button clearMessagesButton;

    private Settings settings;

    public ConfigurationDialog(Shell parentShell, final Settings settings) {
        super(parentShell);
        this.settings = settings;
        setHelpAvailable(false);
    }

    @Override
    public void create() {
        super.create();
        getShell().addListener(SWT.Traverse, new Listener() {
            @Override
            public void handleEvent(Event e) {
                if (e.detail == SWT.TRAVERSE_ESCAPE) {
                    e.doit = false;
                    setReturnCode(ESC_CANCEL);
                    close();
                }
            }
        });
        setTitle("Do you want to enable Error Reporting?");
        setMessage(Messages.CONFIGURATIONDIALOG_PLEASE_TAKE_MOMENT_TO_CONFIGURE, IMessageProvider.INFORMATION);
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        overrideButtonTexts();
    }

    private void overrideButtonTexts() {
        Button ok = getButton(IDialogConstants.OK_ID);
        ok.setText(Messages.CONFIGURATIONDIALOG_ENABLE);
        setButtonLayoutData(ok);

        Button cancel = getButton(IDialogConstants.CANCEL_ID);
        cancel.setText(Messages.CONFIGURATIONDIALOG_DISABLE);
        setButtonLayoutData(cancel);
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(Messages.CONFIGURATIONDIALOG_REPORTING_STARTED_FIRST_TIME);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite area = (Composite) super.createDialogArea(parent);
        setTitleImage(TITLE_IMAGE_DESC.createImage());
        Composite container = new Composite(area, SWT.NONE);
        container.setLayout(new GridLayout());

        GridLayoutFactory layoutFactory = GridLayoutFactory.fillDefaults().margins(10, 10).numColumns(2);
        GridDataFactory dataFactory = GridDataFactory.fillDefaults().grab(true, false);

        Label label = new Label(container, SWT.WRAP);
        label.setText(Messages.CONFIGURATIONDIALOG_INFO);
        dataFactory.applyTo(label);

        Link link = new Link(container, SWT.NONE);
        final String linkToPreferencePage = PreferencesHelper.createLinkLabelToPreferencePage(PREF_PAGE_ID);
        link.setText(MessageFormat.format(Messages.CONFIGURATIONDIALOG_PREFERENCE_PAGE_LINK, linkToPreferencePage));
        link.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(null, PREF_PAGE_ID, null, null);
                dialog.open();
            }
        });

        dataFactory.applyTo(link);

        layoutFactory.margins(0, 0);
        Composite actionComposite = createActionComposite(container, layoutFactory, dataFactory);
        dataFactory.applyTo(actionComposite);

        Group personalGroup = createPersonalGroup(container, layoutFactory, dataFactory);
        dataFactory.applyTo(personalGroup);

        Group makeAnonymousGroup = makeAnonymousGroup(container, layoutFactory, dataFactory);
        dataFactory.applyTo(makeAnonymousGroup);

        Composite linksComposite = createLinksComposite(container, layoutFactory, dataFactory);
        dataFactory.grab(true, true).align(SWT.FILL, SWT.FILL).applyTo(linksComposite);

        createDataBindingContext();
        return container;
    }

    private Composite createActionComposite(Composite container, GridLayoutFactory layoutFactory,
            GridDataFactory dataFactory) {
        Composite actionComposite = new Composite(container, SWT.NONE);
        layoutFactory.applyTo(actionComposite);
        Label actionLabel = new Label(actionComposite, SWT.NONE);
        actionLabel.setText(Messages.FIELD_LABEL_ACTION);
        String actionTooltip = Messages.CONFIGURATIONDIALOG_ACTION_TOOLTIP;
        actionLabel.setToolTipText(actionTooltip);
        actionComboViewer = new ComboViewer(actionComposite, SWT.READ_ONLY);
        actionComboViewer.setContentProvider(ArrayContentProvider.getInstance());
        actionComboViewer.setInput(new SendAction[] { SendAction.ASK, SendAction.SILENT });
        actionComboViewer.getControl().setToolTipText(actionTooltip);
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
        Label nameLabel = new Label(personalGroup, SWT.NONE);
        nameLabel.setText(Messages.FIELD_LABEL_NAME);
        String nameTooltip = Messages.FIELD_MESSAGE_NAME;
        nameLabel.setToolTipText(nameTooltip);
        nameText = new Text(personalGroup, SWT.BORDER);
        nameText.setMessage(nameTooltip);
        nameText.setToolTipText(nameTooltip);
        dataFactory.applyTo(nameText);

        Label emailLabel = new Label(personalGroup, SWT.NONE);
        emailLabel.setText(Messages.FIELD_LABEL_EMAIL);
        String emailTooltip = Messages.FIELD_MESSAGE_EMAIL + Messages.FIELD_DESC_EMAIL;
        emailLabel.setToolTipText(emailTooltip);
        emailText = new Text(personalGroup, SWT.BORDER);
        emailText.setMessage(Messages.FIELD_MESSAGE_EMAIL);
        emailText.setToolTipText(emailTooltip);
        dataFactory.applyTo(emailText);
        return personalGroup;
    }

    private Group makeAnonymousGroup(Composite container, GridLayoutFactory layoutFactory, GridDataFactory dataFactory) {
        Group makeAnonymousGroup = new Group(container, SWT.SHADOW_ETCHED_IN | SWT.SHADOW_ETCHED_OUT | SWT.SHADOW_IN
                | SWT.SHADOW_OUT);
        makeAnonymousGroup.setLayout(new RowLayout(SWT.VERTICAL));
        makeAnonymousGroup.setText(Messages.CONFIGURATIONDIALOG_ANONYMIZATION);
        anonymizeStacktracesButton = new Button(makeAnonymousGroup, SWT.CHECK);
        anonymizeStacktracesButton.setText(Messages.FIELD_LABEL_ANONYMIZE_STACKTRACES);
        addToolTip(anonymizeStacktracesButton, Messages.TOOLTIP_MAKE_STACKTRACE_ANONYMOUS);

        clearMessagesButton = new Button(makeAnonymousGroup, SWT.CHECK);
        clearMessagesButton.setText(Messages.FIELD_LABEL_ANONYMIZE_MESSAGES);
        addToolTip(clearMessagesButton, Messages.TOOLTIP_MAKE_MESSAGES_ANONYMOUS);
        return makeAnonymousGroup;
    }

    private void addToolTip(Control control, String message) {
        DefaultToolTip toolTip = new DefaultToolTip(control);
        toolTip.setText(message);
    }

    private Composite createLinksComposite(Composite container, GridLayoutFactory layoutFactory,
            GridDataFactory dataFactory) {
        Composite linksComposite = new Composite(container, SWT.NONE);
        layoutFactory.numColumns(2).applyTo(linksComposite);
        Link learnMoreLink = new Link(linksComposite, SWT.NONE);
        dataFactory.grab(true, false).align(SWT.BEGINNING, SWT.END).applyTo(learnMoreLink);
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
