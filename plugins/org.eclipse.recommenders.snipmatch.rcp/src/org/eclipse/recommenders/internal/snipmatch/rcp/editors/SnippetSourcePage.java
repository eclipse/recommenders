/**
 * Copyright (c) 2010, 2013 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Marcel Bruch - Initial design and API
 */
package org.eclipse.recommenders.internal.snipmatch.rcp.editors;

import static org.eclipse.jface.databinding.swt.WidgetProperties.text;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.recommenders.internal.snipmatch.rcp.Messages;
import org.eclipse.recommenders.snipmatch.ISnippet;
import org.eclipse.recommenders.snipmatch.Snippet;
import org.eclipse.recommenders.snipmatch.rcp.SnippetEditorInput;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

public class SnippetSourcePage extends FormPage {

    private ISnippet snippet;
    private ScrolledForm form;

    public SnippetSourcePage(FormEditor editor, String id, String title) {
        super(editor, id, title);
    }

    @Override
    protected void createFormContent(IManagedForm managedForm) {
        FormToolkit toolkit = managedForm.getToolkit();

        form = managedForm.getForm();
        form.setText(Messages.EDITOR_TITLE_RAW_SOURCE);
        toolkit.decorateFormHeading(form.getForm());

        Composite body = form.getBody();
        toolkit.paintBordersFor(body);
        body.setLayout(new FillLayout(SWT.HORIZONTAL));
        snippet = ((SnippetEditorInput) getEditorInput()).getSnippet();
        managedForm.addPart(new CodePart(body, toolkit));
    }

    @Override
    public void init(IEditorSite site, IEditorInput input) {
        snippet = ((SnippetEditorInput) input).getSnippet();
        super.init(site, input);
    }

    private final class CodePart extends AbstractFormPart {

        private final Text textWidget;
        private final DataBindingContext context;

        public CodePart(Composite parent, FormToolkit toolkit) {
            textWidget = toolkit.createText(parent, snippet.getCode(), SWT.WRAP | SWT.MULTI);
            context = createDataBindingContext();
        }

        private DataBindingContext createDataBindingContext() {
            DataBindingContext ctx = new DataBindingContext();

            IObservableValue snippetBeanCode = BeanProperties.value(Snippet.class, "code", String.class).observe(
                    snippet);
            IObservableValue textWidgetCode = text(SWT.Modify).observe(textWidget);

            ctx.bindValue(textWidgetCode, snippetBeanCode);

            snippetBeanCode.addChangeListener(new IChangeListener() {

                @Override
                public void handleChange(ChangeEvent event) {
                    if (!textWidget.getText().equals(snippet.getCode())) {
                        markStale();
                    } else {
                        markDirty();
                    }
                    updateMessage();
                }
            });
            return ctx;
        }

        @Override
        public void refresh() {
            context.updateTargets();
            super.refresh();
            updateMessage();
        }

        private void updateMessage() {
            String sourceValid = SnippetSourceValidator.isSourceValid(textWidget.getText());
            if (sourceValid.isEmpty()) {
                form.setMessage(null, IMessageProvider.NONE);
            } else {
                form.setMessage(sourceValid, IMessageProvider.ERROR);
            }
        }

        @Override
        public void commit(boolean onSave) {
            if (onSave) {
                super.commit(onSave);
            }
        }

        @Override
        public void dispose() {
            context.dispose();
            super.dispose();
        }
    }
}
