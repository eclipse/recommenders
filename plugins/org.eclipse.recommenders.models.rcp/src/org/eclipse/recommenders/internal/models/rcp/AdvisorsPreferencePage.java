/**
 * Copyright (c) 2010, 2013 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Olav Lenz - initial API and implementation.
 */
package org.eclipse.recommenders.internal.models.rcp;

import static java.lang.Math.*;
import static org.eclipse.recommenders.internal.models.rcp.Advisors.Filter.*;
import static org.eclipse.recommenders.internal.models.rcp.Messages.*;
import static org.eclipse.recommenders.utils.Checks.cast;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.recommenders.models.IProjectCoordinateAdvisor;
import org.eclipse.recommenders.models.advisors.ProjectCoordinateAdvisorService;
import org.eclipse.recommenders.models.rcp.ModelEvents.AdvisorConfigurationChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;

public class AdvisorsPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    private EventBus bus;

    private ProjectCoordinateAdvisorService advisorService;

    private List<IProjectCoordinateAdvisor> availableAdvisors;

    private static final int UP = -1;
    private static final int DOWN = +1;

    @Inject
    public AdvisorsPreferencePage(EventBus bus, ProjectCoordinateAdvisorService advisorService,
            List<IProjectCoordinateAdvisor> availableAdvisors) {
        super(GRID);
        this.bus = bus;
        this.advisorService = advisorService;
        this.availableAdvisors = availableAdvisors;
    }

    @Override
    public void init(IWorkbench workbench) {
        setPreferenceStore(new ScopedPreferenceStore(InstanceScope.INSTANCE, Constants.BUNDLE_ID));
        setMessage(PREFPAGE_ADVISOR_TITLE);
        setDescription(PREFPAGE_ADVISOR_DESCRIPTION);
    }

    @Override
    protected void createFieldEditors() {
        addField(new AdvisorEditor(Constants.P_ADVISOR_LIST_SORTED, PREFPAGE_ADVISOR_ADVISORS, getFieldEditorParent()));
    }

    private final class AdvisorEditor extends FieldEditor {

        private CheckboxTableViewer tableViewer;
        private Composite buttonBox;
        private Button upButton;
        private Button downButton;

        private AdvisorEditor(String name, String labelText, Composite parent) {
            super(name, labelText, parent);
        }

        @Override
        protected void adjustForNumColumns(int numColumns) {
        }

        @Override
        protected void doFillIntoGrid(Composite parent, int numColumns) {
            Control control = getLabelControl(parent);
            GridData gd = new GridData();
            gd.horizontalSpan = numColumns;
            control.setLayoutData(gd);

            tableViewer = getTableControl(parent);
            gd = new GridData(GridData.FILL_HORIZONTAL);
            gd.horizontalSpan = numColumns - 1;
            gd.verticalAlignment = GridData.FILL;
            tableViewer.getTable().setLayoutData(gd);

            buttonBox = getButtonControl(parent);
            gd = new GridData(GridData.FILL_HORIZONTAL);
            gd.horizontalSpan = 1;
            gd.verticalAlignment = GridData.BEGINNING;
            buttonBox.setLayoutData(gd);

            tableViewer.getTable().addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    updateButtonStatus();
                }

            });
        }

        private void updateButtonStatus() {
            int selectionIndex = tableViewer.getTable().getSelectionIndex();
            if (selectionIndex == -1) {
                upButton.setEnabled(false);
                downButton.setEnabled(false);
            } else if (selectionIndex == 0) {
                upButton.setEnabled(false);
                downButton.setEnabled(true);
            } else if (selectionIndex == tableViewer.getTable().getItemCount() - 1) {
                upButton.setEnabled(true);
                downButton.setEnabled(false);
            } else {
                upButton.setEnabled(true);
                downButton.setEnabled(true);
            }
        }

        private final class MoveSelectionListener extends SelectionAdapter {

            private final int direction;

            public MoveSelectionListener(int direction) {
                this.direction = direction;
            }

            @Override
            public void widgetSelected(SelectionEvent e) {
                List<String> input = cast(tableViewer.getInput());
                int index = tableViewer.getTable().getSelectionIndex();
                String movedElement = input.remove(index);
                int newIndex = min(max(0, index + direction), input.size());
                input.add(newIndex, movedElement);
                tableViewer.setInput(input);
                updateButtonStatus();
            }
        }

        private Composite getButtonControl(Composite parent) {
            Composite box = new Composite(parent, SWT.NONE);
            GridLayout layout = new GridLayout();
            layout.marginHeight = 0;
            layout.marginWidth = 0;
            box.setLayout(layout);

            upButton = new Button(box, SWT.PUSH);
            upButton.setText(PREFPAGE_ADVISOR_BUTTON_UP);
            upButton.setEnabled(false);
            upButton.addSelectionListener(new MoveSelectionListener(UP));
            GridData gd = new GridData(GridData.FILL_HORIZONTAL);
            gd.verticalAlignment = SWT.BEGINNING;
            upButton.setLayoutData(gd);

            downButton = new Button(box, SWT.PUSH);
            downButton.setText(PREFPAGE_ADVISOR_BUTTON_DOWN);
            downButton.setEnabled(false);
            downButton.addSelectionListener(new MoveSelectionListener(DOWN));
            gd = new GridData(GridData.FILL_HORIZONTAL);
            gd.verticalAlignment = SWT.BEGINNING;
            downButton.setLayoutData(gd);

            return box;
        }

        private CheckboxTableViewer getTableControl(Composite parent) {
            CheckboxTableViewer tableViewer = CheckboxTableViewer.newCheckList(parent, SWT.BORDER | SWT.FULL_SELECTION);
            tableViewer.setLabelProvider(new ColumnLabelProvider() {

                @Override
                public String getText(Object element) {
                    return StringUtils.substringAfterLast((String) element, "."); //$NON-NLS-1$
                }
            });

            tableViewer.setContentProvider(new ArrayContentProvider());
            return tableViewer;
        }

        @Override
        protected void doLoad() {
            String value = getPreferenceStore().getString(getPreferenceName());
            load(value);
        }

        private void load(String value) {
            List<String> input = Advisors.extractAdvisors(value, ALL);
            List<String> checkedElements = Advisors.extractAdvisors(value, ENABLED);

            tableViewer.setInput(input);
            tableViewer.setCheckedElements(checkedElements.toArray());
        }

        @Override
        protected void doLoadDefault() {
            String value = getPreferenceStore().getDefaultString(getPreferenceName());
            load(value);
        }

        @Override
        protected void doStore() {
            Set<String> enabledElements = cast(Sets.newHashSet(tableViewer.getCheckedElements()));
            List<String> advisors = cast(tableViewer.getInput());

            String newValue = Advisors.createPreferenceStringFromClassNames(advisors, enabledElements);
            getPreferenceStore().setValue(getPreferenceName(), newValue);
            reconfigureAdvisorService(newValue);
        }

        private void reconfigureAdvisorService(String newValue) {
            advisorService.setAdvisors(Advisors.createAdvisorList(availableAdvisors, newValue));
            bus.post(new AdvisorConfigurationChangedEvent());
        }

        @Override
        public int getNumberOfControls() {
            return 2;
        }

    }

}
