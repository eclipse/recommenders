/**
 * Copyright (c) 2013 Madhuranga Lakjeewa.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Madhuranga Lakjeewa - initial API and implementation.
 *    Olav Lenz - introduce ISnippetRepositoryConfiguration.
 *    Olav Lenz - add wizard support for creating snippet repositories.
 */
package org.eclipse.recommenders.internal.snipmatch.rcp;

<<<<<<< HEAD   (ed8faa [snipmatch] Bug 438745: Double click support for pref page a)
import static org.eclipse.recommenders.utils.Checks.cast;
=======
import static org.eclipse.recommenders.internal.snipmatch.rcp.Constants.*;
>>>>>>> BRANCH (21be1e [releng] 2.1.2-SNAPSHOT)

<<<<<<< HEAD   (ed8faa [snipmatch] Bug 438745: Double click support for pref page a)
import java.util.Collection;
import java.util.List;
=======
import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
>>>>>>> BRANCH (21be1e [releng] 2.1.2-SNAPSHOT)

import org.eclipse.core.runtime.preferences.InstanceScope;
<<<<<<< HEAD   (ed8faa [snipmatch] Bug 438745: Double click support for pref page a)
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.FieldEditor;
=======
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
>>>>>>> BRANCH (21be1e [releng] 2.1.2-SNAPSHOT)
import org.eclipse.jface.preference.FieldEditorPreferencePage;
<<<<<<< HEAD   (ed8faa [snipmatch] Bug 438745: Double click support for pref page a)
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.recommenders.internal.snipmatch.rcp.Repositories.SnippetRepositoryConfigurationChangedEvent;
import org.eclipse.recommenders.snipmatch.model.snipmatchmodel.EclipseGitSnippetRepositoryConfiguration;
import org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnippetRepositoryConfiguration;
import org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnippetRepositoryConfigurations;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
=======
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.recommenders.injection.InjectionService;
import org.eclipse.recommenders.snipmatch.Snippet;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
>>>>>>> BRANCH (21be1e [releng] 2.1.2-SNAPSHOT)
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

<<<<<<< HEAD   (ed8faa [snipmatch] Bug 438745: Double click support for pref page a)
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
=======
import com.google.common.eventbus.EventBus;
>>>>>>> BRANCH (21be1e [releng] 2.1.2-SNAPSHOT)

public class SnipmatchPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

<<<<<<< HEAD   (ed8faa [snipmatch] Bug 438745: Double click support for pref page a)
    private EventBus bus;
    private SnippetRepositoryConfigurations configuration;
    private boolean dirty;
=======
    private EventBus bus = InjectionService.getInstance().requestInstance(EventBus.class);

    private final UriInputValidator uriValidator = new UriInputValidator();
    private final BranchInputValidator branchValidator = new BranchInputValidator();
    private final FieldDecoration errorDecoration;

    private StringFieldEditor snippetsRepoFetchUrlField;
    private StringFieldEditor snippetsRepoPushUrlField;
    private StringFieldEditor snippetsRepoPushBranchField;
    private ControlDecoration snippetsRepoFetchUrlDecoration;
    private ControlDecoration snippetsRepoPushUrlDecoration;
    private ControlDecoration snippetsRepoPushBranchDecoration;
>>>>>>> BRANCH (21be1e [releng] 2.1.2-SNAPSHOT)

    @Inject
    public SnipmatchPreferencePage(EventBus bus, SnippetRepositoryConfigurations configuration) {
        super(GRID);
        setDescription(Messages.PREFPAGE_DESCRIPTION);
<<<<<<< HEAD   (ed8faa [snipmatch] Bug 438745: Double click support for pref page a)
        this.bus = bus;
        this.configuration = configuration;
    }

    @Override
    public void createFieldEditors() {
        ConfigurationEditor configurationEditor = new ConfigurationEditor("", //$NON-NLS-1$
                Messages.PREFPAGE_LABEL_REMOTE_SNIPPETS_REPOSITORY, getFieldEditorParent());
        addField(configurationEditor);
        dirty = false;
=======
        errorDecoration = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);
>>>>>>> BRANCH (21be1e [releng] 2.1.2-SNAPSHOT)
    }

    @Override
    public void init(IWorkbench workbench) {
        ScopedPreferenceStore store = new ScopedPreferenceStore(InstanceScope.INSTANCE, Constants.BUNDLE_ID);
        setPreferenceStore(store);
    }

<<<<<<< HEAD   (ed8faa [snipmatch] Bug 438745: Double click support for pref page a)
    private final class ConfigurationEditor extends FieldEditor {

        private CheckboxTableViewer tableViewer;

        private Composite buttonBox;
        private Button newButton;
        private Button editButton;
        private Button removeButton;

        private ConfigurationEditor(String name, String labelText, Composite parent) {
            super(name, labelText, parent);
        }

=======
    @Override
    public void createFieldEditors() {
        Composite parent = getFieldEditorParent();
        GridLayoutFactory.swtDefaults().margins(0, 0).applyTo(parent);

        addFetchGroup(parent);
        addPushGroup(parent);
    }

    private void addFetchGroup(Composite parent) {
        Group fetchGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
        fetchGroup.setText(Messages.GROUP_FETCH_SETTINGS);
        GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(fetchGroup);

        snippetsRepoFetchUrlField = new StringFieldEditor(PREF_SNIPPETS_REPO_FETCH_URL,
                Messages.PREFPAGE_LABEL_SNIPPETS_REPO_FETCH_URL, fetchGroup) {
            @Override
            protected boolean doCheckState() {
                String errorMessage = uriValidator.isValid(getStringValue());
                snippetsRepoFetchUrlDecoration.setDescriptionText(errorMessage);
                if (errorMessage == null) {
                    snippetsRepoFetchUrlDecoration.hide();
                } else {
                    snippetsRepoFetchUrlDecoration.show();
                }
                return errorMessage == null;
            }

            @Override
            protected void adjustForNumColumns(int numColumns) {
                ((GridData) getTextControl().getLayoutData()).horizontalSpan = numColumns - 2;
            }
        };

        snippetsRepoFetchUrlDecoration = new ControlDecoration(snippetsRepoFetchUrlField.getTextControl(fetchGroup),
                SWT.LEFT | SWT.TOP);
        snippetsRepoFetchUrlDecoration.setImage(errorDecoration.getImage());

        updateMargins(fetchGroup);
        addField(snippetsRepoFetchUrlField);
    }

    private void addPushGroup(Composite parent) {
        Group pushGroup = new Group(parent, SWT.SHADOW_ETCHED_IN);
        pushGroup.setText(Messages.GROUP_PUSH_SETTINGS);
        GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(pushGroup);

        snippetsRepoPushUrlField = new StringFieldEditor(PREF_SNIPPETS_REPO_PUSH_URL,
                Messages.PREFPAGE_LABEL_SNIPPETS_REPO_PUSH_URL, pushGroup) {

            @Override
            protected boolean doCheckState() {
                String errorMessage = uriValidator.isValid(getStringValue());
                snippetsRepoPushUrlDecoration.setDescriptionText(errorMessage);
                if (errorMessage == null) {
                    snippetsRepoPushUrlDecoration.hide();
                } else {
                    snippetsRepoPushUrlDecoration.show();
                }
                return errorMessage == null;
            }
        };

        snippetsRepoPushUrlDecoration = new ControlDecoration(snippetsRepoPushUrlField.getTextControl(pushGroup),
                SWT.LEFT | SWT.TOP);
        snippetsRepoPushUrlDecoration.setImage(errorDecoration.getImage());

        addField(snippetsRepoPushUrlField);

        Label pushBranchDescription = new Label(pushGroup, SWT.WRAP);
        pushBranchDescription.setText(MessageFormat.format(Messages.PREFPAGE_LABEL_SNIPPETS_PUSH_SETTINGS_DESCRIPTION,
                Snippet.FORMAT_VERSION));
        GridDataFactory.fillDefaults().span(3, 1).hint(300, SWT.DEFAULT).grab(true, false)
        .align(SWT.FILL, SWT.BEGINNING).applyTo(pushBranchDescription);

        snippetsRepoPushBranchField = new StringFieldEditorWithPrefix(PREF_SNIPPETS_REPO_PUSH_BRANCH,
                Messages.PREFPAGE_LABEL_SNIPPETS_REPO_PUSH_BRANCH, pushGroup);

        snippetsRepoPushBranchDecoration = new ControlDecoration(snippetsRepoPushBranchField.getTextControl(pushGroup),
                SWT.LEFT | SWT.TOP);
        snippetsRepoPushBranchDecoration.setImage(errorDecoration.getImage());

        addField(snippetsRepoPushBranchField);
        updateMargins(pushGroup);
    }

    private void updateMargins(Group group) {
        GridLayout layout = (GridLayout) group.getLayout();
        layout.marginWidth = 5;
        layout.marginHeight = 5;
    }

    @Override
    public boolean performOk() {
        boolean dirty = isDirty();
        boolean ok = super.performOk();
        if (ok && dirty) {
            bus.post(new EclipseGitSnippetRepository.SnippetRepositoryConfigurationChangedEvent());
        }
        return ok;
    }

    private boolean isDirty() {
        String oldRepoFetchUrl = getPreferenceStore().getString(PREF_SNIPPETS_REPO_FETCH_URL);
        if (!snippetsRepoFetchUrlField.getStringValue().equals(oldRepoFetchUrl)) {
            return true;
        }
        String oldRepoPushUrl = getPreferenceStore().getString(PREF_SNIPPETS_REPO_PUSH_URL);
        if (!snippetsRepoPushUrlField.getStringValue().equals(oldRepoPushUrl)) {
            return true;
        }
        String oldRepoPushBranch = getPreferenceStore().getString(PREF_SNIPPETS_REPO_PUSH_BRANCH);
        if (!snippetsRepoPushBranchField.getStringValue().equals(oldRepoPushBranch)) {
            return true;
        }
        return false;
    }

    private final class StringFieldEditorWithPrefix extends StringFieldEditor {

        private Label prefixLabel;

        private StringFieldEditorWithPrefix(String name, String labelText, Composite parent) {
            super(name, labelText, parent);
        }

        @Override
        protected void adjustForNumColumns(int numColumns) {
            ((GridData) getTextControl().getLayoutData()).horizontalSpan = numColumns - 2;
        }

        @Override
        public int getNumberOfControls() {
            return 3;
        }

        @Override
        protected void doFillIntoGrid(Composite parent, int numColumns) {
            super.doFillIntoGrid(parent, numColumns - 1);
            prefixLabel = getPrefixControl(parent);
            GridData gd = new GridData();
            gd.horizontalAlignment = GridData.FILL;
            gd.widthHint = prefixLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x;
            prefixLabel.setLayoutData(gd);
        }

        private Label getPrefixControl(Composite parent) {
            if (prefixLabel == null) {
                prefixLabel = new Label(parent, SWT.NONE);
                prefixLabel.setText('/' + Snippet.FORMAT_VERSION);
                prefixLabel.addDisposeListener(new DisposeListener() {

                    @Override
                    public void widgetDisposed(DisposeEvent e) {
                        prefixLabel = null;
                    }
                });
            }
            return prefixLabel;
        }

        @Override
        protected boolean doCheckState() {
            String message = branchValidator.isValid(getStringValue());
            snippetsRepoPushBranchDecoration.setDescriptionText(message);
            if (message == null) {
                snippetsRepoPushBranchDecoration.hide();
            } else {
                snippetsRepoPushBranchDecoration.show();
            }
            return message == null;
        }
    }

    private final class UriInputValidator implements IInputValidator {
>>>>>>> BRANCH (21be1e [releng] 2.1.2-SNAPSHOT)
        @Override
<<<<<<< HEAD   (ed8faa [snipmatch] Bug 438745: Double click support for pref page a)
        protected void adjustForNumColumns(int numColumns) {
        }

        @Override
        protected void doFillIntoGrid(Composite parent, int numColumns) {
            Control control = getLabelControl(parent);
            GridDataFactory.swtDefaults().span(numColumns, 1).applyTo(control);

            tableViewer = getTableControl(parent);
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).span(numColumns - 1, 1).grab(true, true)
            .applyTo(tableViewer.getTable());
            tableViewer.getTable().addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    if (e.detail == SWT.CHECK) {
                        dirty = true;
                    }
                    updateButtonStatus();
                }

                @Override
                public void widgetDefaultSelected(SelectionEvent e) {
                    SnippetRepositoryConfiguration selectedConfiguration = getSelectedConfiguration();
                    if (selectedConfiguration != null) {
                        editConfiguration(selectedConfiguration);
                        updateButtonStatus();
                    }
                }
            });

            buttonBox = getButtonControl(parent);
            updateButtonStatus();
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).applyTo(buttonBox);
        }

        private void updateButtonStatus() {
            boolean selected = tableViewer.getTable().getSelectionIndex() != -1;
            boolean editableType = getSelectedConfiguration() instanceof EclipseGitSnippetRepositoryConfiguration;
            editButton.setEnabled(selected && editableType);
            removeButton.setEnabled(selected);
        }

        private Composite getButtonControl(Composite parent) {
            Composite box = new Composite(parent, SWT.NONE);
            GridLayoutFactory.fillDefaults().applyTo(box);

            newButton = createButton(box, Messages.PREFPAGE_BUTTON_NEW);
            newButton.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    addNewConfiguration();
                    updateButtonStatus();
                }

            });

            editButton = createButton(box, Messages.PREFPAGE_BUTTON_EDIT);
            editButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    editConfiguration(getSelectedConfiguration());
                    updateButtonStatus();
                }

            });

            editButton.setEnabled(false);

            removeButton = createButton(box, Messages.PREFPAGE_BUTTON_REMOVE);
            removeButton.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    removeConfiguration(getSelectedConfiguration());
                    updateButtonStatus();
                }

            });

            return box;
        }

        private SnippetRepositoryConfiguration getSelectedConfiguration() {
            List<SnippetRepositoryConfiguration> tableInput = getTableInput();
            int index = tableViewer.getTable().getSelectionIndex();
            if (index != -1) {
                return tableInput.get(index);
            }
            return null;
        }

        protected void removeConfiguration(SnippetRepositoryConfiguration configuration) {
            List<SnippetRepositoryConfiguration> configurations = getTableInput();
            configurations.remove(configuration);
            updateTableContent(configurations);
            dirty = true;
        }

        protected void editConfiguration(SnippetRepositoryConfiguration oldConfiguration) {
            List<WizardDescriptor> suitableWizardDescriptors = WizardDescriptors.filterApplicableWizardDescriptors(
                    WizardDescriptors.loadAvailableWizards(), oldConfiguration);
            if (!suitableWizardDescriptors.isEmpty()) {

                AbstractSnippetRepositoryWizard wizard;
                if (suitableWizardDescriptors.size() == 1) {
                    wizard = Iterables.getOnlyElement(suitableWizardDescriptors).getWizard();
                    wizard.setConfiguration(oldConfiguration);
                } else {
                    wizard = new SnippetRepositoryTypeSelectionWizard(oldConfiguration);
                }

                WizardDialog dialog = new WizardDialog(this.getPage().getShell(), wizard);
                if (dialog.open() == Window.OK) {
                    List<SnippetRepositoryConfiguration> configurations = getTableInput();
                    configurations.add(configurations.indexOf(oldConfiguration), wizard.getConfiguration());
                    configurations.remove(oldConfiguration);
                    updateTableContent(configurations);
                    dirty = true;
                }
=======
        public String isValid(String newText) {
            if (newText.isEmpty()) {
                return Messages.PREFPAGE_ERROR_INVALID_BRANCH_PREFIX_FORMAT;
            }
            try {
                new URI(newText);
                return null;
            } catch (URISyntaxException e) {
                return e.getMessage();
>>>>>>> BRANCH (21be1e [releng] 2.1.2-SNAPSHOT)
            }
        }

        private List<SnippetRepositoryConfiguration> getTableInput() {
            List<SnippetRepositoryConfiguration> configurations = cast(tableViewer.getInput());
            if (configurations == null) {
                return Lists.newArrayList();
            }
            return Lists.newArrayList(configurations);
        }

        protected void addNewConfiguration() {
            List<WizardDescriptor> availableWizards = WizardDescriptors.loadAvailableWizards();
            if (!availableWizards.isEmpty()) {
                SnippetRepositoryTypeSelectionWizard newWizard = new SnippetRepositoryTypeSelectionWizard();
                WizardDialog dialog = new WizardDialog(this.getPage().getShell(), newWizard);
                if (dialog.open() == Window.OK) {
                    List<SnippetRepositoryConfiguration> configurations = getTableInput();
                    configurations.add(newWizard.getConfiguration());
                    updateTableContent(configurations);
                    dirty = true;
                }
            }

        }

        private Button createButton(Composite box, String text) {
            Button button = new Button(box, SWT.PUSH);
            button.setText(text);

            int widthHint = Math.max(convertHorizontalDLUsToPixels(button, IDialogConstants.BUTTON_WIDTH),
                    button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);

            GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).hint(widthHint, SWT.DEFAULT).applyTo(button);

            return button;
        }

        private CheckboxTableViewer getTableControl(Composite parent) {
            final CheckboxTableViewer tableViewer = CheckboxTableViewer.newCheckList(parent, SWT.BORDER
                    | SWT.FULL_SELECTION);

            tableViewer.setLabelProvider(new ColumnLabelProvider() {

                @Override
                public String getText(Object element) {
                    SnippetRepositoryConfiguration config = cast(element);
                    return config.getName();
                }

                @Override
                public String getToolTipText(Object element) {
                    SnippetRepositoryConfiguration config = cast(element);
                    return config.getDescription();
                }
            });
            ColumnViewerToolTipSupport.enableFor(tableViewer);
            tableViewer.setContentProvider(new ArrayContentProvider() {
                @Override
                public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
                    super.inputChanged(viewer, oldInput, newInput);
                }
            });
            return tableViewer;
        }

        @Override
        protected void doLoad() {
            updateTableContent(Lists.newArrayList(configuration.getRepos()));
        }

        public void updateTableContent(List<SnippetRepositoryConfiguration> configurations) {
            final List<SnippetRepositoryConfiguration> oldConfigurations = getTableInput();
            Collection<SnippetRepositoryConfiguration> checkedConfigurations = Collections2.filter(configurations,
                    new Predicate<SnippetRepositoryConfiguration>() {

                @Override
                public boolean apply(SnippetRepositoryConfiguration input) {
                    if (oldConfigurations != null && oldConfigurations.contains(input)) {
                        return tableViewer.getChecked(input);
                    }
                    return input.isEnabled();
                }

            });

            tableViewer.setInput(configurations);
            tableViewer.setCheckedElements(checkedConfigurations.toArray());
        }

        @Override
        public void loadDefault() {
            super.loadDefault();
            setPresentsDefaultValue(false);
        }

        @Override
        protected void doLoadDefault() {
            updateTableContent(RepositoryConfigurations.fetchDefaultConfigurations());
            dirty = true;
        }

        @Override
        protected void doStore() {
            if (!dirty) {
                return;
            }
            List<SnippetRepositoryConfiguration> oldconfigs = getTableInput();
            List<SnippetRepositoryConfiguration> newConfigs = Lists.newArrayList();
            for (SnippetRepositoryConfiguration config : oldconfigs) {
                config.setEnabled(tableViewer.getChecked(config));
                newConfigs.add(config);
            }

            configuration.getRepos().clear();
            configuration.getRepos().addAll(newConfigs);

            RepositoryConfigurations.storeConfigurations(configuration);
            bus.post(new SnippetRepositoryConfigurationChangedEvent());
            dirty = false;
        }

        @Override
        public int getNumberOfControls() {
            return 2;
        }
    }

}
