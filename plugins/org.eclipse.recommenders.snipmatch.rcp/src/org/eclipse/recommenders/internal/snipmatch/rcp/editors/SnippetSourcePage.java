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

import org.eclipse.recommenders.snipmatch.ISnippet;
import org.eclipse.recommenders.snipmatch.rcp.SnippetEditorInput;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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

    public SnippetSourcePage(FormEditor editor, String id, String title) {
        super(editor, id, title);
    }

    @Override
    protected void createFormContent(IManagedForm managedForm) {
        FormToolkit toolkit = managedForm.getToolkit();
        ScrolledForm form = managedForm.getForm();
        form.setText("Blablub");
        Composite body = form.getBody();
        toolkit.decorateFormHeading(form.getForm());
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
        private Text txtCode;

        public CodePart(Composite parent, FormToolkit toolkit) {
            txtCode = toolkit.createText(parent, snippet.getCode(), SWT.WRAP | SWT.MULTI);
            txtCode.addModifyListener(new ModifyListener() {

                @Override
                public void modifyText(ModifyEvent e) {
                    markDirty();
                }
            });
        }

        @Override
        public void refresh() {
            txtCode.setText(snippet.getCode());
            super.refresh();
        }
    }
}
