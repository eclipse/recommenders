/**
 * Copyright (c) 2010, 2013 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Stefan Prisca - initial API and implementation
 *      Marcel Bruch - changed to use jface databinding
 *      Olav Lenz - clean up metadata page.
 */
package org.eclipse.recommenders.internal.snipmatch.rcp.editors;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.eclipse.recommenders.internal.snipmatch.rcp.Constants.HELP_URL;
import static org.eclipse.recommenders.snipmatch.Location.*;
import static org.eclipse.recommenders.utils.Checks.cast;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.Parameterization;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.value.ComputedValue;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.property.INativePropertyListener;
import org.eclipse.core.databinding.property.ISimplePropertyListener;
import org.eclipse.core.databinding.property.value.SimpleValueProperty;
import org.eclipse.core.internal.databinding.property.value.SelfValueProperty;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ViewerProperties;
import org.eclipse.jface.databinding.viewers.ViewerSupport;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.recommenders.coordinates.ProjectCoordinate;
import org.eclipse.recommenders.injection.InjectionService;
import org.eclipse.recommenders.internal.models.rcp.ProjectCoordinateSelectionDialog;
import org.eclipse.recommenders.internal.snipmatch.rcp.SnippetsView;
import org.eclipse.recommenders.internal.snipmatch.rcp.l10n.LogMessages;
import org.eclipse.recommenders.internal.snipmatch.rcp.l10n.Messages;
import org.eclipse.recommenders.rcp.SharedImages;
import org.eclipse.recommenders.rcp.utils.DatabindingConverters.EnumToBooleanConverter;
import org.eclipse.recommenders.rcp.utils.DatabindingConverters.StringToUuidConverter;
import org.eclipse.recommenders.rcp.utils.ObjectToBooleanConverter;
import org.eclipse.recommenders.snipmatch.ISnippet;
import org.eclipse.recommenders.snipmatch.Location;
import org.eclipse.recommenders.snipmatch.Snippet;
import org.eclipse.recommenders.snipmatch.rcp.SnippetEditorInput;
import org.eclipse.recommenders.utils.Logs;
import org.eclipse.recommenders.utils.rcp.Browsers;
import org.eclipse.recommenders.utils.rcp.Selections;
import org.eclipse.recommenders.utils.rcp.preferences.AbstractLinkContributionPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.handlers.IHandlerService;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@SuppressWarnings("restriction")
public class SnippetMetadataPage extends FormPage {

    private static final String EXTENSION_DISCOVERY_COMMAND_ID = "org.eclipse.recommenders.rcp.commands.extensionDiscovery"; //$NON-NLS-1$

    private final class ContentsPartDirtyListener implements IChangeListener {

        @Override
        public void handleChange(ChangeEvent event) {
            contentsPart.markDirty();
        }
    }

    private static final String SNIPMATCH_P2_DISCOVERY_URL = "http://download.eclipse.org/recommenders/discovery/2.0/snipmatch/directory.xml"; //$NON-NLS-1$

    private static final Location[] SNIPMATCH_LOCATIONS = { FILE, JAVA_FILE, JAVA, JAVA_STATEMENTS, JAVA_TYPE_MEMBERS,
            JAVADOC };
    public static final String TEXT_SNIPPETNAME = "org.eclipse.recommenders.snipmatch.rcp.snippetmetadatapage.snippetname"; //$NON-NLS-1$

    private ISnippet snippet;

    private AbstractFormPart contentsPart;

    private Text txtName;
    private Text txtDescription;
    private ComboViewer comboLocation;
    private Text txtUuid;

    private ListViewer listViewerFilenameRestrictions;
    private ListViewer listViewerExtraSearchTerms;
    private ListViewer listViewerTags;
    private ListViewer listViewerDependencies;

    private Composite filenameRestrictionsButtonContainer;
    private Composite extraSearchTermsButtonContainer;
    private Composite tagsButtonContainer;
    private Composite dependenciesButtonContainer;

    private Button btnAddFilenameRestriction;
    private Button btnRemoveFilenameRestriction;
    private Button btnRemoveExtraSearchTerm;
    private Button btnRemoveTag;
    private Button btnRemoveDependency;

    private IObservableSet modelSnippetDependencies;
    private IObservableList modelSnippetExtraSearchTerms;
    private IObservableList modelSnippetFilenameRestrictions;
    private IObservableList modelSnippetTags;
    private DataBindingContext context;

    private final Image errorDecorationImage = FieldDecorationRegistry.getDefault()
            .getFieldDecoration(FieldDecorationRegistry.DEC_ERROR).getImage();
    private final Image infoDecorationImage = FieldDecorationRegistry.getDefault()
            .getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION).getImage();

    public SnippetMetadataPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
    }

    @Override
    protected void createFormContent(IManagedForm managedForm) {
        FormToolkit toolkit = managedForm.getToolkit();
        ScrolledForm form = managedForm.getForm();

        createHeader(form);

        Composite body = form.getBody();
        toolkit.decorateFormHeading(form.getForm());
        toolkit.paintBordersFor(body);
        managedForm.getForm().getBody().setLayout(new GridLayout(3, false));

        contentsPart = new AbstractFormPart() {

            @Override
            public void initialize(IManagedForm managedForm) {
                super.initialize(managedForm);

                int horizontalIndent = errorDecorationImage.getBounds().width + 2;

                createLabel(managedForm, Messages.EDITOR_LABEL_SNIPPET_NAME);
                txtName = createTextField(managedForm, snippet.getName(), Messages.EDITOR_TEXT_MESSAGE_SNIPPET_NAME,
                        horizontalIndent);
                txtName.setData(SnippetsView.SWT_ID, TEXT_SNIPPETNAME);
                final ControlDecoration nameDecoration = createDecoration(txtName,
                        Messages.ERROR_SNIPPET_NAME_CANNOT_BE_EMPTY, errorDecorationImage, SWT.LEFT);
                txtName.addModifyListener(new ModifyListener() {
                    @Override
                    public void modifyText(ModifyEvent event) {
                        if (isNullOrEmpty(txtName.getText())) {
                            nameDecoration.show();
                        } else {
                            nameDecoration.hide();
                        }
                    }
                });

                createLabel(managedForm, Messages.EDITOR_LABEL_SNIPPET_DESCRIPTION);
                txtDescription = createTextField(managedForm, snippet.getDescription(),
                        Messages.EDITOR_TEXT_MESSAGE_SNIPPET_DESCRIPTION, horizontalIndent);

                createLabel(managedForm, Messages.EDITOR_LABEL_SNIPPET_LOCATION);
                comboLocation = new ComboViewer(managedForm.getForm().getBody(), SWT.DROP_DOWN | SWT.READ_ONLY);

                managedForm.getToolkit().adapt(comboLocation.getCombo(), true, true);
                comboLocation.setContentProvider(ArrayContentProvider.getInstance());
                comboLocation.setInput(SNIPMATCH_LOCATIONS);
                comboLocation.setLabelProvider(new LabelProvider() {
                    @Override
                    public String getText(Object element) {
                        if (element instanceof Location) {
                            Location location = (Location) element;
                            switch (location) {
                            case FILE:
                                return Messages.SNIPMATCH_LOCATION_FILE;
                            case JAVA_FILE:
                                return Messages.SNIPMATCH_LOCATION_JAVA_FILE;
                            case JAVA:
                                return Messages.SNIPMATCH_LOCATION_JAVA;
                            case JAVA_STATEMENTS:
                                return Messages.SNIPMATCH_LOCATION_JAVA_STATEMENTS;
                            case JAVA_TYPE_MEMBERS:
                                return Messages.SNIPMATCH_LOCATION_JAVA_MEMBERS;
                            case JAVADOC:
                                return Messages.SNIPMATCH_LOCATION_JAVADOC;
                            default:
                                break;
                            }
                        }
                        return super.getText(element);
                    }
                });
                comboLocation.getCombo().setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER)
                        .grab(true, false).span(2, 1).indent(horizontalIndent, 0).create());

                final ControlDecoration locationErrorDecoration = createDecoration(comboLocation.getCombo(),
                        Messages.ERROR_SNIPPET_LOCATION_CANNOT_BE_EMPTY + "\n" //$NON-NLS-1$
                                + Messages.EDITOR_DESCRIPTION_LOCATION,
                        errorDecorationImage, SWT.LEFT);

                final ControlDecoration locationDescriptionDecoration = createDecoration(comboLocation.getCombo(),
                        Messages.EDITOR_DESCRIPTION_LOCATION, infoDecorationImage, SWT.LEFT);

                comboLocation.addSelectionChangedListener(new ISelectionChangedListener() {
                    @Override
                    public void selectionChanged(SelectionChangedEvent event) {
                        if (event.getSelection().isEmpty()) {
                            locationErrorDecoration.show();
                            locationDescriptionDecoration.hide();
                        } else {
                            locationErrorDecoration.hide();
                            locationDescriptionDecoration.show();
                        }
                    }

                });
                comboLocation.setSelection(new StructuredSelection(snippet.getLocation()));

                createLabel(managedForm, Messages.EDITOR_LABEL_SNIPPET_FILENAME_RESTRICTIONS);
                listViewerFilenameRestrictions = createListViewer(managedForm, horizontalIndent);
                createDecoration(listViewerFilenameRestrictions.getList(),
                        Messages.EDITOR_DESCRIPTION_FILENAME_RESTRICTIONS, infoDecorationImage, SWT.TOP | SWT.LEFT);

                filenameRestrictionsButtonContainer = createButtonContainer(managedForm);
                btnAddFilenameRestriction = createButton(managedForm, filenameRestrictionsButtonContainer,
                        Messages.EDITOR_BUTTON_ADD, new SelectionAdapter() {
                            @Override
                            public void widgetSelected(SelectionEvent e) {
                                createFilenameRestrictionInputDialog(filenameRestrictionsButtonContainer.getShell())
                                        .open();
                            }
                        });
                btnAddFilenameRestriction.setEnabled(snippet.getLocation() == Location.FILE);
                btnRemoveFilenameRestriction = createButton(managedForm, filenameRestrictionsButtonContainer,
                        Messages.EDITOR_BUTTON_REMOVE, new SelectionAdapter() {
                            @Override
                            public void widgetSelected(SelectionEvent e) {
                                Optional<String> o = Selections.getFirstSelected(listViewerFilenameRestrictions);
                                if (o.isPresent()) {
                                    modelSnippetFilenameRestrictions.remove(o.get());
                                }
                            }
                        });

                createLabel(managedForm, Messages.EDITOR_LABEL_SNIPPET_EXTRA_SEARCH_TERMS);
                listViewerExtraSearchTerms = createListViewer(managedForm, horizontalIndent);
                createDecoration(listViewerExtraSearchTerms.getList(), Messages.EDITOR_DESCRIPTION_EXTRA_SEARCH_TERMS,
                        infoDecorationImage, SWT.TOP | SWT.LEFT);

                extraSearchTermsButtonContainer = createButtonContainer(managedForm);
                createButton(managedForm, extraSearchTermsButtonContainer, Messages.EDITOR_BUTTON_ADD,
                        new SelectionAdapter() {
                            @Override
                            public void widgetSelected(SelectionEvent e) {
                                createExtraSearchTermInputDialog(extraSearchTermsButtonContainer.getShell()).open();
                            }
                        });
                btnRemoveExtraSearchTerm = createButton(managedForm, extraSearchTermsButtonContainer,
                        Messages.EDITOR_BUTTON_REMOVE, new SelectionAdapter() {
                            @Override
                            public void widgetSelected(SelectionEvent e) {
                                Optional<String> o = Selections.getFirstSelected(listViewerExtraSearchTerms);
                                if (o.isPresent()) {
                                    modelSnippetExtraSearchTerms.remove(o.get());
                                }
                            }
                        });

                createLabel(managedForm, Messages.EDITOR_LABEL_SNIPPET_TAG);
                listViewerTags = createListViewer(managedForm, horizontalIndent);
                createDecoration(listViewerTags.getList(), Messages.EDITOR_DESCRIPTION_TAGS, infoDecorationImage,
                        SWT.TOP | SWT.LEFT);

                tagsButtonContainer = createButtonContainer(managedForm);
                createButton(managedForm, tagsButtonContainer, Messages.EDITOR_BUTTON_ADD, new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        createTagInputDialog(tagsButtonContainer.getShell()).open();
                    }
                });
                btnRemoveTag = createButton(managedForm, tagsButtonContainer, Messages.EDITOR_BUTTON_REMOVE,
                        new SelectionAdapter() {
                            @Override
                            public void widgetSelected(SelectionEvent e) {
                                Optional<String> o = Selections.getFirstSelected(listViewerTags);
                                if (o.isPresent()) {
                                    modelSnippetTags.remove(o.get());
                                }
                            }
                        });

                createLabel(managedForm, Messages.EDITOR_LABEL_SNIPPET_DEPENDENCIES);
                listViewerDependencies = createListViewer(managedForm, horizontalIndent);
                createDecoration(listViewerDependencies.getList(), Messages.EDITOR_DESCRIPTION_DEPENDENCIES,
                        infoDecorationImage, SWT.TOP | SWT.LEFT);

                dependenciesButtonContainer = createButtonContainer(managedForm);
                createButton(managedForm, dependenciesButtonContainer, Messages.EDITOR_BUTTON_ADD,
                        new SelectionAdapter() {
                            @Override
                            public void widgetSelected(SelectionEvent e) {
                                Shell shell = dependenciesButtonContainer.getShell();
                                ProjectCoordinateSelectionDialog dialog = new ProjectCoordinateSelectionDialog(shell) {
                                    @Override
                                    public String createLabelForProjectCoordinate(ProjectCoordinate element) {
                                        return getStringForDependency(element);
                                    }

                                    private final Set<String> alreadyAddedPcLabels = Sets.newHashSet();

                                    @Override
                                    public boolean filter(ProjectCoordinate pc) {
                                        for (String dependencylistItem : fetchDependencyListItems()) {
                                            if (dependencylistItem.equals(getStringForDependency(pc))) {
                                                return true;
                                            }
                                        }

                                        String labelForPc = createLabelForProjectCoordinate(pc);
                                        if (alreadyAddedPcLabels.contains(labelForPc)) {
                                            return true;
                                        } else {
                                            alreadyAddedPcLabels.add(labelForPc);
                                            return false;
                                        }
                                    }
                                };
                                dialog.setInitialPattern(""); //$NON-NLS-1$
                                dialog.setTitle(Messages.DIALOG_TITLE_SELECT_DEPENDENCY);
                                dialog.setMessage(Messages.DIALOG_MESSAGE_SELECT_DEPENDENCY);
                                dialog.open();

                                Set<ProjectCoordinate> selectedElements = changeVersionsToZero(
                                        dialog.getSelectedElements());
                                modelSnippetDependencies.addAll(selectedElements);
                            }
                        });
                btnRemoveDependency = createButton(managedForm, dependenciesButtonContainer,
                        Messages.EDITOR_BUTTON_REMOVE, new SelectionAdapter() {
                            @Override
                            public void widgetSelected(SelectionEvent e) {
                                Optional<String> o = Selections.getFirstSelected(listViewerDependencies);
                                if (o.isPresent()) {
                                    modelSnippetDependencies.remove(o.get());
                                }
                            }
                        });

                createLabel(managedForm, Messages.EDITOR_LABEL_SNIPPET_UUID);
                txtUuid = managedForm.getToolkit().createText(managedForm.getForm().getBody(),
                        snippet.getUuid().toString(), SWT.READ_ONLY);
                txtUuid.setLayoutData(
                        GridDataFactory.fillDefaults().grab(true, false).indent(horizontalIndent, 0).create());
            }

            private void createLabel(IManagedForm managedForm, String text) {
                Label label = managedForm.getToolkit().createLabel(managedForm.getForm().getBody(), text, SWT.NONE);
                label.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
            }

            private Text createTextField(IManagedForm managedForm, String value, String message, int horizontalIndent) {
                Text text = managedForm.getToolkit().createText(managedForm.getForm().getBody(), value, SWT.NONE);
                text.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false)
                        .span(2, 1).indent(horizontalIndent, 0).create());
                text.setMessage(message);
                return text;

            }

            private ControlDecoration createDecoration(Control control, String description, Image image, int style) {
                final ControlDecoration decoration = new ControlDecoration(control, style);
                decoration.setDescriptionText(description);
                decoration.setImage(image);
                decoration.setMarginWidth(1);
                return decoration;
            }

            private ListViewer createListViewer(IManagedForm managedForm, int horizontalIndent) {
                ListViewer listViewer = new ListViewer(managedForm.getForm().getBody(), SWT.BORDER | SWT.V_SCROLL);
                List lstFilenames = listViewer.getList();
                lstFilenames.setLayoutData(
                        GridDataFactory.fillDefaults().grab(true, true).indent(horizontalIndent, 0).create());
                return listViewer;
            }

            private Composite createButtonContainer(IManagedForm managedForm) {
                Composite container = managedForm.getToolkit().createComposite(managedForm.getForm().getBody(),
                        SWT.NONE);
                container.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 1, 1));
                managedForm.getToolkit().paintBordersFor(container);
                container.setLayout(new GridLayout(1, false));
                return container;
            }

            private Button createButton(IManagedForm managedForm, Composite parent, String label,
                    SelectionListener listener) {
                Button button = managedForm.getToolkit().createButton(parent, label, SWT.NONE);
                button.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
                button.addSelectionListener(listener);
                return button;
            }

            @Override
            public void commit(boolean onSave) {
                if (onSave) {
                    super.commit(onSave);
                }
            }

            @Override
            public void refresh() {
                context.updateTargets();
                super.refresh();
            }

        };
        managedForm.addPart(contentsPart);
        context = createDataBindingContext();
    }

    private ScrolledForm createHeader(ScrolledForm form) {
        form.setText(Messages.EDITOR_TITLE_METADATA);
        SharedImages sharedImages = InjectionService.getInstance().getInjector().getInstance(SharedImages.class);

        Action openDiscoveryAction = new Action(Messages.EDITOR_EXTENSIONS_HEADER_EXT_LINK,
                sharedImages.getDescriptor(SharedImages.Images.ELCL_INSTALL_EXTENSIONS)) {
            @Override
            public void run() {
                showDiscoveryDialog();
            };
        };
        EditorUtils.addActionToForm(form, openDiscoveryAction, Messages.EDITOR_EXTENSIONS_HEADER_EXT_LINK);

        Action showHelpAction = new Action(Messages.EDITOR_TOOLBAR_ITEM_HELP,
                sharedImages.getDescriptor(SharedImages.Images.ELCL_HELP)) {
            @Override
            public void run() {
                Browsers.tryOpenInExternalBrowser(HELP_URL);
            };
        };
        EditorUtils.addActionToForm(form, showHelpAction, Messages.EDITOR_TOOLBAR_ITEM_HELP);
        return form;
    }

    private Collection<String> fetchDependencyListItems() {
        final Collection<String> items = Lists.newArrayList();
        Display.getDefault().syncExec(new Runnable() {

            @Override
            public void run() {
                for (String string : listViewerDependencies.getList().getItems()) {
                    items.add(string);
                }
            }
        });
        return items;
    }

    private Set<ProjectCoordinate> changeVersionsToZero(Set<ProjectCoordinate> resolved) {
        Set<ProjectCoordinate> result = Sets.newHashSet();
        for (ProjectCoordinate projectCoordinate : resolved) {
            result.add(
                    new ProjectCoordinate(projectCoordinate.getGroupId(), projectCoordinate.getArtifactId(), "0.0.0")); //$NON-NLS-1$
        }
        return result;
    }

    private InputDialog createFilenameRestrictionInputDialog(Shell shell) {
        IInputValidator validator = new IInputValidator() {

            @Override
            public String isValid(String newText) {
                if (newText == null) {
                    return ""; //$NON-NLS-1$
                }
                if (StringUtils.isBlank(newText)) {
                    return ""; //$NON-NLS-1$
                }
                if (newText.contains("*")) { //$NON-NLS-1$
                    return MessageFormat
                            .format(Messages.DIALOG_VALIDATOR_FILENAME_RESTRICTION_CONTAINS_ILLEGAL_CHARACTER, "*"); //$NON-NLS-1$
                }
                if (snippet.getFilenameRestrictions().contains(newText.trim().toLowerCase())) {
                    return Messages.DIALOG_VALIDATOR_FILENAME_RESTRICTION_ALREADY_ADDED;
                }
                return null;
            }
        };
        return new InputDialog(shell, Messages.DIALOG_TITLE_ENTER_NEW_FILENAME_RESTRICTION,
                Messages.DIALOG_MESSAGE_ENTER_NEW_FILENAME_RESTRICTION, "", validator) { //$NON-NLS-1$
            @Override
            protected void okPressed() {
                modelSnippetFilenameRestrictions.add(getValue().toLowerCase());
                super.okPressed();
            }
        };
    }

    private InputDialog createExtraSearchTermInputDialog(Shell shell) {
        IInputValidator validator = new IInputValidator() {

            @Override
            public String isValid(String newText) {
                if (isNullOrEmpty(newText)) {
                    return ""; //$NON-NLS-1$
                }
                if (snippet.getExtraSearchTerms().contains(newText)) {
                    return Messages.DIALOG_VALIDATOR_EXTRA_SEARCH_TERM_ALREADY_ADDED;
                }
                return null;
            }
        };
        return new InputDialog(shell, Messages.DIALOG_TITLE_ENTER_NEW_EXTRA_SEARCH_TERM,
                Messages.DIALOG_MESSAGE_ENTER_NEW_EXTRA_SEARCH_TERM, "", validator) { //$NON-NLS-1$
            @Override
            protected void okPressed() {
                modelSnippetExtraSearchTerms.add(getValue());
                super.okPressed();
            }
        };
    }

    private InputDialog createTagInputDialog(Shell shell) {
        IInputValidator validator = new IInputValidator() {

            @Override
            public String isValid(String newText) {
                if (isNullOrEmpty(newText)) {
                    return ""; //$NON-NLS-1$
                }
                if (snippet.getTags().contains(newText)) {
                    return Messages.DIALOG_VALIDATOR_TAG_ALREADY_ADDED;
                }
                return null;
            }
        };
        return new InputDialog(shell, Messages.DIALOG_TITLE_ENTER_NEW_TAG, Messages.DIALOG_MESSAGE_ENTER_NEW_TAG, "", //$NON-NLS-1$
                validator) {
            @Override
            protected void okPressed() {
                modelSnippetTags.add(getValue());
                super.okPressed();
            }
        };
    }

    private DataBindingContext createDataBindingContext() {
        DataBindingContext context = new DataBindingContext();

        // Name
        IObservableValue targetNameWidget = WidgetProperties.text(SWT.Modify).observe(txtName);
        IObservableValue modelSnippetName = BeanProperties.value(Snippet.class, "name", String.class).observe(snippet); //$NON-NLS-1$
        context.bindValue(targetNameWidget, modelSnippetName);
        modelSnippetName.addChangeListener(new ContentsPartDirtyListener());

        // Description
        IObservableValue targetDescriptionWidget = WidgetProperties.text(SWT.Modify).observe(txtDescription);
        IObservableValue modelSnippetDescription = BeanProperties.value(Snippet.class, "description", String.class) //$NON-NLS-1$
                .observe(snippet);
        context.bindValue(targetDescriptionWidget, modelSnippetDescription);
        modelSnippetDescription.addChangeListener(new ContentsPartDirtyListener());

        // Location
        IObservableValue targetLocationComboViewer = ViewerProperties.singleSelection().observe(comboLocation);
        IObservableValue modelSnippetLocation = BeanProperties.value(Snippet.class, "location", Location.class) //$NON-NLS-1$
                .observe(snippet);
        context.bindValue(targetLocationComboViewer, modelSnippetLocation);
        modelSnippetLocation.addChangeListener(new ContentsPartDirtyListener());

        // Filename restrictions
        modelSnippetFilenameRestrictions = BeanProperties.list(Snippet.class, "filenameRestrictions", String.class) //$NON-NLS-1$
                .observe(snippet);
        modelSnippetFilenameRestrictions.addChangeListener(new ContentsPartDirtyListener());
        ViewerSupport.bind(listViewerFilenameRestrictions, modelSnippetFilenameRestrictions,
                new FilenameRestrictionLabelProperty());

        UpdateValueStrategy locationEnablementStrategy = new UpdateValueStrategy();
        locationEnablementStrategy.setConverter(new NullSafeEnumToBooleanConverter(new Location[] { Location.FILE }));
        IObservableValue modelFilenameRestrictionsEnabled = WidgetProperties.enabled()
                .observe(listViewerFilenameRestrictions.getControl());
        final IObservableValue modelAddFilenameRestrictionButtonEnabled = WidgetProperties.enabled()
                .observe(btnAddFilenameRestriction);

        context.bindValue(targetLocationComboViewer, modelFilenameRestrictionsEnabled, locationEnablementStrategy,
                null);
        context.bindValue(targetLocationComboViewer, modelAddFilenameRestrictionButtonEnabled,
                locationEnablementStrategy, null);

        final IObservableValue targetEnableRemoveFilenameButton = WidgetProperties.enabled()
                .observe(btnRemoveFilenameRestriction);
        final IObservableValue targetFilenameRestrictionSelected = ViewerProperties.singleSelection()
                .observe(listViewerFilenameRestrictions);
        ComputedValue computedRemoveButtonEnablement = new ObserveValueWithNullChecker(targetEnableRemoveFilenameButton,
                modelAddFilenameRestrictionButtonEnabled, targetFilenameRestrictionSelected);
        context.bindValue(targetEnableRemoveFilenameButton, computedRemoveButtonEnablement);

        // Extra search terms
        modelSnippetExtraSearchTerms = BeanProperties.list(Snippet.class, "extraSearchTerms", String.class) //$NON-NLS-1$
                .observe(snippet);
        ViewerSupport.bind(listViewerExtraSearchTerms, modelSnippetExtraSearchTerms,
                new SelfValueProperty(String.class));
        modelSnippetExtraSearchTerms.addChangeListener(new ContentsPartDirtyListener());

        UpdateValueStrategy objectToBooleanStrategy = new UpdateValueStrategy();
        objectToBooleanStrategy.setConverter(new ObjectToBooleanConverter());
        IObservableValue targetExtraSearchTermSelected = ViewerProperties.singleSelection()
                .observe(listViewerExtraSearchTerms);
        IObservableValue modelRemoveExtraSearchTermButtonEnabled = WidgetProperties.enabled()
                .observe(btnRemoveExtraSearchTerm);
        context.bindValue(targetExtraSearchTermSelected, modelRemoveExtraSearchTermButtonEnabled,
                objectToBooleanStrategy, null);

        // Tags
        modelSnippetTags = BeanProperties.list(Snippet.class, "tags", String.class).observe(snippet); //$NON-NLS-1$
        ViewerSupport.bind(listViewerTags, modelSnippetTags, new SelfValueProperty(String.class));
        modelSnippetTags.addChangeListener(new ContentsPartDirtyListener());

        IObservableValue targetTagSelected = ViewerProperties.singleSelection().observe(listViewerTags);
        IObservableValue modelRemoveTagButtonEnabled = WidgetProperties.enabled().observe(btnRemoveTag);
        context.bindValue(targetTagSelected, modelRemoveTagButtonEnabled, objectToBooleanStrategy, null);

        // Dependencies
        modelSnippetDependencies = BeanProperties.set(Snippet.class, "neededDependencies", ProjectCoordinate.class) //$NON-NLS-1$
                .observe(snippet);
        ViewerSupport.bind(listViewerDependencies, modelSnippetDependencies, new SimpleValueProperty() {

            @Override
            public Object getValueType() {
                return ProjectCoordinate.class;
            }

            @Override
            protected Object doGetValue(Object source) {
                if (source != null) {
                    ProjectCoordinate pc = cast(source);
                    return getStringForDependency(pc);
                }
                return ""; //$NON-NLS-1$
            }

            @Override
            protected void doSetValue(Object source, Object value) {
            }

            @Override
            public INativePropertyListener adaptListener(ISimplePropertyListener listener) {
                return null;
            }

        });
        modelSnippetDependencies.addChangeListener(new ContentsPartDirtyListener());

        IObservableValue targetDependencySelected = ViewerProperties.singleSelection().observe(listViewerDependencies);
        IObservableValue modelRemoveDependencyButtonEnabled = WidgetProperties.enabled().observe(btnRemoveDependency);
        context.bindValue(targetDependencySelected, modelRemoveDependencyButtonEnabled, objectToBooleanStrategy, null);

        // uuid text
        UpdateValueStrategy stringToUuidStrategy = new UpdateValueStrategy();
        stringToUuidStrategy.setConverter(new StringToUuidConverter());
        IObservableValue targetUuidWidget = WidgetProperties.text(SWT.Modify).observe(txtUuid);
        IObservableValue modelSnippetUuid = BeanProperties.value(Snippet.class, "uuid", UUID.class).observe(snippet); //$NON-NLS-1$
        context.bindValue(targetUuidWidget, modelSnippetUuid, stringToUuidStrategy, null);
        modelSnippetUuid.addChangeListener(new ContentsPartDirtyListener());

        context.updateModels();
        return context;
    }

    String getStringForDependency(ProjectCoordinate pc) {
        return pc.getGroupId() + ":" + pc.getArtifactId(); //$NON-NLS-1$
    }

    @Override
    public void setFocus() {
        super.setFocus();
        txtName.setFocus();
    }

    @Override
    public void init(IEditorSite site, IEditorInput input) {
        snippet = ((SnippetEditorInput) input).getSnippet();
        registerEditorInputListener();

        super.init(site, input);
    }

    private void registerEditorInputListener() {

        getEditor().addPropertyListener(new IPropertyListener() {
            @Override
            public void propertyChanged(Object source, int propId) {
                if (propId == PROP_INPUT) {
                    setInputWithNotify(getEditor().getEditorInput());
                }
            }
        });
    }

    @Override
    protected void setInputWithNotify(IEditorInput input) {
        snippet = ((SnippetEditorInput) input).getSnippet();
        context.dispose();
        context = createDataBindingContext();

        super.setInputWithNotify(input);
    }

    @Override
    public void dispose() {
        context.dispose();
        super.dispose();
    }

    /**
     * Show the snippet editor extensions discovery dialog.
     */
    private void showDiscoveryDialog() {
        ICommandService cmdService = (ICommandService) getSite().getService(ICommandService.class);
        Command cmd = cmdService.getCommand(EXTENSION_DISCOVERY_COMMAND_ID);
        IHandlerService handlerService = (IHandlerService) getSite().getService(IHandlerService.class);

        try {
            ParameterizedCommand parmCommand = new ParameterizedCommand(cmd,
                    new Parameterization[] {
                            new Parameterization(cmd.getParameter(AbstractLinkContributionPage.COMMAND_HREF_ID),
                                    SNIPMATCH_P2_DISCOVERY_URL) });
            handlerService.executeCommand(parmCommand, null);
        } catch (Exception e) {
            Logs.log(LogMessages.ERROR_FAILED_TO_EXECUTE_COMMAND, e, EXTENSION_DISCOVERY_COMMAND_ID);
        }
    }

    private static class NullSafeEnumToBooleanConverter extends EnumToBooleanConverter<Location> {

        private NullSafeEnumToBooleanConverter(Location[] trueValues) {
            super(trueValues);
        }

        @Override
        public Object convert(Object fromObject) {
            return fromObject != null ? super.convert(fromObject) : false;
        }
    }

    private static class FilenameRestrictionLabelProperty extends SimpleValueProperty {

        @Override
        public Object getValueType() {
            return String.class;
        }

        @Override
        protected Object doGetValue(Object source) {
            String text = (String) source;
            if (text.startsWith(".")) {
                return "*" + text; //$NON-NLS-1$
            } else {
                return text;
            }
        }

        @Override
        protected void doSetValue(Object source, Object value) {
        }

        @Override
        public INativePropertyListener adaptListener(ISimplePropertyListener listener) {
            return null;
        }
    }

    private final class ObserveValueWithNullChecker extends ComputedValue {
        private final IObservableValue target;
        private final IObservableValue observableNullChecker;
        private final IObservableValue observableValue;

        private ObserveValueWithNullChecker(IObservableValue target, IObservableValue observableValue,
                IObservableValue observableNullChecker) {
            this.target = target;
            this.observableNullChecker = observableNullChecker;
            this.observableValue = observableValue;
        }

        @Override
        protected void doSetValue(Object value) {
            target.setValue(value);
        }

        @Override
        protected Object calculate() {
            boolean value = (boolean) observableValue.getValue();
            return observableNullChecker.getValue() != null && value;
        }
    }
}
