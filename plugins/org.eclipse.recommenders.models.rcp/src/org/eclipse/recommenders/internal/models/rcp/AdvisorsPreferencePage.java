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

import static org.eclipse.recommenders.internal.models.rcp.Advisors.Filter.ALL;
import static org.eclipse.recommenders.internal.models.rcp.Advisors.Filter.ENABLED;
import static org.eclipse.recommenders.utils.Checks.cast;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

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
import org.eclipse.swt.layout.GridData;
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
        setMessage("Advisor Configuration");
        setDescription("Code Recommenders uses so-called advisors to assign project coordinates to your dependencies.");
    }

    @Override
    protected void createFieldEditors() {
        addField(new AdvisorEditor(Constants.P_ADVISOR_LIST_SORTED, "Advisors:", getFieldEditorParent()));
    }

    private final class AdvisorEditor extends FieldEditor {

        private CheckboxTableViewer tableViewer;

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
            gd.horizontalSpan = numColumns;
            gd.verticalAlignment = GridData.FILL;
            tableViewer.getTable().setLayoutData(gd);
        }

        private CheckboxTableViewer getTableControl(Composite parent) {
            CheckboxTableViewer tableViewer = CheckboxTableViewer.newCheckList(parent, SWT.BORDER);
            tableViewer.setLabelProvider(new ColumnLabelProvider() {
                @Override
                public String getText(Object element) {
                    String value = cast(element);
                    return value.substring(value.lastIndexOf(".") + 1);
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
            return 1;
        }

    }

}
