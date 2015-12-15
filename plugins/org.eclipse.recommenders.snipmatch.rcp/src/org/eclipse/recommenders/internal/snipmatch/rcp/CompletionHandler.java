/**
 * Copyright (c) 2013 Madhuranga Lakjeewa.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Madhuranga Lakjeewa - initial API and implementation.
 */
package org.eclipse.recommenders.internal.snipmatch.rcp;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.recommenders.injection.InjectionService;
import org.eclipse.recommenders.utils.Reflections;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.AbstractTextEditor;

@SuppressWarnings("restriction")
public class CompletionHandler extends AbstractHandler {

    private SnipmatchCompletionEngine engine;

    private <T> T request(Class<T> clazz) {
        return InjectionService.getInstance().requestInstance(clazz);
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IEditorPart editor = HandlerUtil.getActiveEditor(event);

        IEditorInput input = editor.getEditorInput();
        if (input.getPersistable() == null) {
            return null;
        }

        if (!(editor instanceof AbstractTextEditor)) {
            return null;
        }
        Method getSourceViewerMethod = Reflections.getDeclaredMethod(AbstractTextEditor.class, "getSourceViewer")
                .orNull();
        if (getSourceViewerMethod == null) {
            throw new ExecutionException("TODO");
        }
        ISourceViewer viewer;
        try {
            viewer = (ISourceViewer) getSourceViewerMethod.invoke(editor);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new ExecutionException("TODO", e);
        }

        int offset = viewer.getSelectedRange().x;
        ContentAssistInvocationContext context;
        if (editor instanceof JavaEditor) {
            context = new JavaContentAssistInvocationContext(viewer, offset, editor);
        } else {
            context = new ContentAssistInvocationContext(viewer, offset);
        }
        if (engine == null) {
            engine = request(SnipmatchCompletionEngine.class);
        }
        engine.show(context);
        return null;
    }
}
