/**
 * Copyright (c) 2015 Pawel Nowak.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.internal.news.rcp;

import static org.eclipse.recommenders.utils.Checks.cast;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.window.Window;
import org.eclipse.recommenders.internal.news.rcp.l10n.Messages;
import org.eclipse.recommenders.news.rcp.INewsService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class NewsFeedPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    private final INewsService service;
    private final NewsRcpPreferences newsRcpPreferences;
    private BooleanFieldEditor enabledEditor;
    private FeedEditor feedEditor;
    private IntegerFieldEditor pollingIntervalEditor;

    @Inject
    public NewsFeedPreferencePage(INewsService service, NewsRcpPreferences newsRcpPreferences) {
        super(GRID);
        this.service = service;
        this.newsRcpPreferences = newsRcpPreferences;
    }

    @Override
    protected void createFieldEditors() {
        Composite parent = getFieldEditorParent();
        PixelConverter pixelConverter = new PixelConverter(parent);
        GridLayout layout = new GridLayout(1, false);
        layout.marginWidth = 0;
        parent.setLayout(layout);

        Composite container = new Composite(parent, SWT.NONE);
        layout = new GridLayout(1, false);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.verticalSpacing = pixelConverter.convertHeightInCharsToPixels(1);
        container.setLayout(layout);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        Composite topGroup = new Composite(container, SWT.NONE);
        layout = new GridLayout(2, false);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        topGroup.setLayout(layout);
        topGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        enabledEditor = new BooleanFieldEditor(Constants.PREF_NEWS_ENABLED, Messages.FIELD_LABEL_NEWS_ENABLED, 0,
                topGroup);
        addField(new BooleanFieldEditor(Constants.PREF_NOTIFICATION_ENABLED, Messages.FIELD_LABEL_NOTIFICATION_ENABLED,
                topGroup));
        addField(enabledEditor);
        pollingIntervalEditor = new IntegerFieldEditor(Constants.PREF_POLLING_INTERVAL,
                Messages.FIELD_LABEL_POLLING_INTERVAL, topGroup, 4);
        addField(pollingIntervalEditor);

        final Composite bottomGroup = new Composite(container, SWT.NONE);
        layout = new GridLayout(2, false);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        bottomGroup.setLayout(layout);
        bottomGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        feedEditor = new FeedEditor(Constants.PREF_FEED_LIST_SORTED, Messages.FIELD_LABEL_FEEDS, bottomGroup);
        addField(feedEditor);
    }

    @Override
    public void init(IWorkbench workbench) {
        setPreferenceStore(new ScopedPreferenceStore(InstanceScope.INSTANCE, Constants.PLUGIN_ID));
        setMessage(Messages.PREFPAGE_TITLE);
        setDescription(Messages.PREFPAGE_DESCRIPTION);
    }

    @Override
    public boolean performOk() {
        IPreferenceStore store = getPreferenceStore();
        boolean oldEnabled = store.getBoolean(Constants.PREF_NEWS_ENABLED);
        boolean newEnabled = enabledEditor.getBooleanValue();
        List<FeedDescriptor> oldFeedValue = newsRcpPreferences.getFeedDescriptors();
        List<FeedDescriptor> newFeedValue = feedEditor.getValue();
        boolean result = super.performOk();
        boolean forceStop = false;
        boolean forceStart = false;
        if (!oldEnabled && newEnabled) {
            // News has been activated
            forceStart = true;
        } else if (oldEnabled && !newEnabled) {
            forceStop = true;
        }

        for (FeedDescriptor oldFeed : oldFeedValue) {
            if (!newFeedValue.contains(oldFeed)) {
                service.removeFeed(oldFeed);
                break;
            }
            FeedDescriptor newFeed = newFeedValue.get(newFeedValue.indexOf(oldFeed));
            if (!oldFeed.isEnabled() && newFeed.isEnabled()) {
                forceStart = true;
            }
            if (oldFeed.isEnabled() && !newFeed.isEnabled()) {
                service.removeFeed(newFeed);
            }
            for (FeedDescriptor feed : newFeedValue) {
                if (!oldFeedValue.contains(feed)) {
                    forceStart = true;
                }
            }
        }

        if (forceStart) {
            service.start();
        }
        if (forceStop) {
            service.forceStop();
        }
        return result;
    }

    private final class FeedEditor extends FieldEditor {

        private CheckboxTableViewer tableViewer;

        private Composite buttonBox;
        private Button newButton;
        private Button editButton;
        private Button removeButton;

        private FeedEditor(String name, String labelText, Composite parent) {
            super(name, labelText, parent);
        }

        @Override
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
                    }
                    updateButtonStatus();
                }
            });
            tableViewer.getTable().addMouseListener(new MouseAdapter() {

                @Override
                public void mouseDoubleClick(MouseEvent e) {
                    TableItem item = tableViewer.getTable().getItem(new Point(e.x, e.y));
                    if (item == null) {
                        return;
                    }

                    Rectangle bounds = item.getBounds();
                    boolean isClickOnCheckbox = e.x < bounds.x;
                    if (isClickOnCheckbox) {
                        return;
                    }
                    FeedDescriptor feed = cast(item.getData());
                    if (!feed.isDefaultRepository()) {
                        editFeed(feed);
                    }
                    updateButtonStatus();
                }
            });

            buttonBox = getButtonControl(parent);
            updateButtonStatus();
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).applyTo(buttonBox);
        }

        private void updateButtonStatus() {
            boolean selected = tableViewer.getTable().getSelectionIndex() != -1;
            FeedDescriptor feed = getSelectedFeed();
            if (feed == null) {
                editButton.setEnabled(false);
                removeButton.setEnabled(false);
                return;
            }
            editButton.setEnabled(selected && !feed.isDefaultRepository());
            removeButton.setEnabled(selected && !feed.isDefaultRepository());
        }

        private Composite getButtonControl(Composite parent) {
            Composite box = new Composite(parent, SWT.NONE);
            GridLayoutFactory.fillDefaults().applyTo(box);

            newButton = createButton(box, Messages.PREFPAGE_BUTTON_NEW);
            newButton.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    addNewFeed();
                    updateButtonStatus();
                }

            });

            editButton = createButton(box, Messages.PREFPAGE_BUTTON_EDIT);
            editButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    editFeed(getSelectedFeed());
                    updateButtonStatus();
                }

            });

            editButton.setEnabled(false);

            removeButton = createButton(box, Messages.PREFPAGE_BUTTON_REMOVE);
            removeButton.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    removeFeed(getSelectedFeed());
                    updateButtonStatus();
                }

            });

            return box;
        }

        private FeedDescriptor getSelectedFeed() {
            List<FeedDescriptor> tableInput = getTableInput();
            int index = tableViewer.getTable().getSelectionIndex();
            if (index != -1) {
                return tableInput.get(index);
            }
            return null;
        }

        protected void removeFeed(FeedDescriptor feed) {
            List<FeedDescriptor> feeds = getTableInput();
            feeds.remove(feed);
            updateTableContent(feeds);
        }

        protected void editFeed(FeedDescriptor oldFeed) {
            FeedDialog dialog = new FeedDialog(getShell(), oldFeed, newsRcpPreferences);
            List<FeedDescriptor> feeds = getTableInput();
            if (dialog.open() == Window.OK) {
                int index = feeds.indexOf(oldFeed);
                feeds.remove(oldFeed);
                feeds.add(index, dialog.getFeed());
                updateTableContent(feeds);
            }
        }

        private List<FeedDescriptor> getTableInput() {
            List<FeedDescriptor> configurations = cast(tableViewer.getInput());
            if (configurations == null) {
                return Lists.newArrayList();
            }
            return Lists.newArrayList(configurations);
        }

        protected void addNewFeed() {
            FeedDialog dialog = new FeedDialog(getShell(), newsRcpPreferences);
            List<FeedDescriptor> feeds = getTableInput();
            if (dialog.open() == Window.OK) {
                feeds.add(dialog.getFeed());
                updateTableContent(feeds);
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
            CheckboxTableViewer tableViewer = CheckboxTableViewer.newCheckList(parent, SWT.BORDER | SWT.FULL_SELECTION);
            tableViewer.setLabelProvider(new ColumnLabelProvider() {

                @Override
                public String getText(Object element) {
                    FeedDescriptor descriptor = cast(element);
                    return descriptor.getName();
                }

                @Override
                public String getToolTipText(Object element) {
                    FeedDescriptor descriptor = cast(element);
                    return descriptor.getDescription();
                }
            });
            ColumnViewerToolTipSupport.enableFor(tableViewer);
            tableViewer.setContentProvider(new ArrayContentProvider());
            return tableViewer;
        }

        @Override
        protected void doLoad() {
            String value = getPreferenceStore().getString(getPreferenceName());
            load(value);
        }

        private void load(String value) {
            List<FeedDescriptor> input = FeedDescriptors.load(value, FeedDescriptors.getRegisteredFeeds());
            input.addAll(
                    FeedDescriptors.getFeeds(getPreferenceStore().getString(Constants.PREF_CUSTOM_FEED_LIST_SORTED)));
            List<FeedDescriptor> checkedElements = Lists.newArrayList();
            for (FeedDescriptor descriptor : input) {
                if (descriptor.isEnabled()) {
                    checkedElements.add(descriptor);
                }
            }

            tableViewer.setInput(input);
            tableViewer.setCheckedElements(checkedElements.toArray());
        }

        public void updateTableContent(List<FeedDescriptor> feeds) {
            final List<FeedDescriptor> oldFeeds = getTableInput();
            Collection<FeedDescriptor> checkedFeeds = Collections2.filter(feeds, new Predicate<FeedDescriptor>() {

                @Override
                public boolean apply(FeedDescriptor input) {
                    if (oldFeeds != null && oldFeeds.contains(input)) {
                        return tableViewer.getChecked(input);
                    }
                    return input.isEnabled();
                }

            });

            tableViewer.setInput(feeds);
            tableViewer.setCheckedElements(checkedFeeds.toArray());
        }

        @Override
        public void loadDefault() {
            super.loadDefault();
            setPresentsDefaultValue(false);
        }

        @Override
        protected void doLoadDefault() {
            String value = getPreferenceStore().getDefaultString(getPreferenceName());
            load(value);
        }

        @Override
        protected void doStore() {
            List<FeedDescriptor> descriptors = cast(tableViewer.getInput());
            for (FeedDescriptor descriptor : descriptors) {
                descriptor.setEnabled(tableViewer.getChecked(descriptor));
            }
            List<FeedDescriptor> feeds = Lists
                    .newArrayList(Iterables.filter(descriptors, new Predicate<FeedDescriptor>() {

                        @Override
                        public boolean apply(FeedDescriptor input) {
                            return input.isDefaultRepository();
                        }

                    }));
            List<FeedDescriptor> customFeeds = Lists
                    .newArrayList(Iterables.filter(descriptors, new Predicate<FeedDescriptor>() {

                        @Override
                        public boolean apply(FeedDescriptor input) {
                            return !input.isDefaultRepository();
                        }

                    }));

            String newValue = FeedDescriptors.feedsToString(feeds);
            getPreferenceStore().setValue(getPreferenceName(), newValue);
            getPreferenceStore().setValue(Constants.PREF_CUSTOM_FEED_LIST_SORTED,
                    FeedDescriptors.customFeedsToString(customFeeds));
        }

        @Override
        public int getNumberOfControls() {
            return 2;
        }

        public List<FeedDescriptor> getValue() {
            List<FeedDescriptor> descriptors = cast(tableViewer.getInput());
            for (FeedDescriptor descriptor : descriptors) {
                descriptor.setEnabled(tableViewer.getChecked(descriptor));
            }
            return descriptors;
        }
    }
}
