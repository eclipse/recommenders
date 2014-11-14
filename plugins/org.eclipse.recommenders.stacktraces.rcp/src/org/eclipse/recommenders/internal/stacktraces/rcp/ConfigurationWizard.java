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
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.recommenders.internal.stacktraces.rcp.model.ModelPackage;
import org.eclipse.recommenders.internal.stacktraces.rcp.model.SendAction;
import org.eclipse.recommenders.internal.stacktraces.rcp.model.Settings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;

public class ConfigurationWizard extends Wizard {

    private static class ConfigurationPage extends WizardPage {
        private ComboViewer actionComboViewer;
        private Text emailText;
        private Text nameText;
        private Button anonymizeStacktracesButton;
        private Button clearMessagesButton;
        private Settings settings;

        protected ConfigurationPage(Settings settings) {
            super(ConfigurationPage.class.getName());
            this.settings = settings;
        }

        @Override
        public void createControl(Composite parent) {
            setTitle("Please configure the initial settings of Error Reporting.");
            setDescription("Error Reporting is an automated service to publish logged errors to the Eclipse bugtracker.\n"
                    + "All settings can be changed in the preferences at any time.");
            Composite container = new Composite(parent, SWT.NONE);
            container.setLayout(new GridLayout());

            GridLayoutFactory layoutFactory = GridLayoutFactory.fillDefaults().margins(5, 5).numColumns(2);
            GridDataFactory dataFactory = GridDataFactory.fillDefaults().grab(true, false);
            Composite actionComposite = new Composite(container, SWT.NONE);
            layoutFactory.applyTo(actionComposite);
            dataFactory.applyTo(actionComposite);
            {
                new Label(actionComposite, SWT.NONE).setText(Messages.FIELD_LABEL_ACTION);
                actionComboViewer = new ComboViewer(actionComposite, SWT.READ_ONLY);
                actionComboViewer.setContentProvider(ArrayContentProvider.getInstance());
                actionComboViewer.setInput(new SendAction[] { SendAction.ASK, SendAction.SILENT, SendAction.IGNORE });
                actionComboViewer.setLabelProvider(new LabelProvider() {
                    @Override
                    public String getText(Object element) {
                        SendAction mode = (SendAction) element;
                        switch (mode) {
                        case ASK:
                            return Messages.PREFERENCEPAGE_ASK_LABEL + " (recommended)";
                        case IGNORE:
                            return Messages.FIELD_LABEL_ACTION_REPORT_NEVER;
                        case SILENT:
                            return Messages.FIELD_LABEL_ACTION_REPORT_ALWAYS;
                        default:
                            return super.getText(element);
                        }
                    }
                });
                dataFactory.applyTo(actionComboViewer.getControl());
                actionComboViewer.setSelection(new StructuredSelection(settings.getAction()));
            }

            Group personalGroup = new Group(container, SWT.SHADOW_ETCHED_IN | SWT.SHADOW_ETCHED_OUT | SWT.SHADOW_IN
                    | SWT.SHADOW_OUT);
            personalGroup.setText(Messages.SETTINGSPAGE_GROUPLABEL_PERSONAL);
            layoutFactory.numColumns(2).applyTo(personalGroup);
            dataFactory.applyTo(personalGroup);
            FieldDecoration infoDecoration = FieldDecorationRegistry.getDefault().getFieldDecoration(DEC_INFORMATION);
            {
                new Label(personalGroup, SWT.NONE).setText(Messages.FIELD_LABEL_NAME);
                nameText = new Text(personalGroup, SWT.BORDER);
                nameText.setMessage(Messages.FIELD_MESSAGE_NAME);
                dataFactory.applyTo(nameText);
                ControlDecoration dec = new ControlDecoration(nameText, SWT.TOP | SWT.LEFT);
                dec.setImage(infoDecoration.getImage());
                dec.setDescriptionText(Messages.FIELD_DESC_NAME);
            }
            {
                new Label(personalGroup, SWT.NONE).setText(Messages.FIELD_LABEL_EMAIL);
                emailText = new Text(personalGroup, SWT.BORDER);
                emailText.setMessage(Messages.FIELD_MESSAGE_EMAIL);
                dataFactory.applyTo(emailText);
                ControlDecoration dec = new ControlDecoration(emailText, SWT.TOP | SWT.LEFT);
                dec.setImage(infoDecoration.getImage());
                dec.setDescriptionText(Messages.FIELD_DESC_EMAIL);
            }

            Group anonymizeGroup = new Group(container, SWT.SHADOW_ETCHED_IN | SWT.SHADOW_ETCHED_OUT | SWT.SHADOW_IN
                    | SWT.SHADOW_OUT);
            anonymizeGroup.setText("Make anonymous");
            layoutFactory.numColumns(1).applyTo(anonymizeGroup);
            dataFactory.applyTo(anonymizeGroup);
            {
                anonymizeStacktracesButton = new Button(anonymizeGroup, SWT.CHECK);
                anonymizeStacktracesButton.setText(Messages.FIELD_LABEL_STACKTRACES);
                dataFactory.applyTo(anonymizeStacktracesButton);
                DefaultToolTip anonymizeStacktracesToolTip = new DefaultToolTip(anonymizeStacktracesButton);
                anonymizeStacktracesToolTip.setText(Messages.TOOLTIP_MAKE_STACKTRACE_ANONYMOUS);
                clearMessagesButton = new Button(anonymizeGroup, SWT.CHECK);
                clearMessagesButton.setText(Messages.FIELD_LABEL_MESSAGES);
                dataFactory.applyTo(clearMessagesButton);
                DefaultToolTip clearMessagesToolTip = new DefaultToolTip(clearMessagesButton);
                clearMessagesToolTip.setText(Messages.TOOLTIP_MAKE_MESSAGES_ANONYMOUS);
            }
            {
                Composite linksComposite = new Composite(container, SWT.NONE);
                layoutFactory.numColumns(2).applyTo(linksComposite);
                dataFactory.grab(true, true).align(SWT.FILL, SWT.FILL).applyTo(linksComposite);
                {
                    Link learnMoreLink = new Link(linksComposite, SWT.NONE);
                    dataFactory.align(SWT.BEGINNING, SWT.END).applyTo(learnMoreLink);
                    learnMoreLink.setText(Messages.LINK_LEARN_MORE);
                    learnMoreLink.addSelectionListener(new SelectionAdapter() {
                        @Override
                        public void widgetSelected(SelectionEvent e) {
                            Browsers.openInExternalBrowser(HELP_URL);
                        }
                    });
                }

                {
                    Link feedbackLink = new Link(linksComposite, SWT.NONE);
                    dataFactory.align(SWT.END, SWT.END).applyTo(feedbackLink);
                    feedbackLink.setText(Messages.LINK_PROVIDE_FEEDBACK);
                    feedbackLink.addSelectionListener(new SelectionAdapter() {
                        @Override
                        public void widgetSelected(SelectionEvent e) {
                            Browsers.openInExternalBrowser(FEEDBACK_FORM_URL);
                        }
                    });
                }

            }
            setControl(container);
            createDataBindingContext();
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
            // IObservableValue ovPause = value(pkg.getSettings_PausePeriodStart()).observe(settings);
            context.bindValue(ovVwrAction, ovSetAction, null, null);
            // context.bindValue(ovVwrAction, ovPause, new UpdateValueStrategy() {
            //
            // @Override
            // public Object convert(Object value) {
            // if (value == SendAction.PAUSE_DAY) {
            // return System.currentTimeMillis();
            // } else {
            // return 0;
            // }
            // }
            //
            // }, null);

            return context;
        }

        @Override
        public void performHelp() {
            Browsers.openInExternalBrowser(HELP_URL);
        }
    }

    public static ImageDescriptor TITLE_IMAGE_DESC = ImageDescriptor.createFromFile(ConfigurationWizard.class,
            "/icons/wizban/stackframes_wiz.gif");

    private Settings settings;
    private ConfigurationPage page;

    public ConfigurationWizard(Settings settings) {
        this.settings = settings;
        page = new ConfigurationPage(settings);
        setHelpAvailable(true);
    }

    @Override
    public void addPages() {
        setWindowTitle("Error Reporting Configuration");
        setDefaultPageImageDescriptor(TITLE_IMAGE_DESC);
        addPage(page);
    }

    @Override
    public boolean performFinish() {
        PreferenceInitializer.saveSettings(settings);
        return true;
    }

    @Override
    public boolean performCancel() {
        settings.setAction(SendAction.IGNORE);
        PreferenceInitializer.saveSettings(settings);
        return true;
    }

}
