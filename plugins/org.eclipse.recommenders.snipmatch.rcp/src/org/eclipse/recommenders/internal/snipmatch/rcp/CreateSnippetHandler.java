/**
 * Copyright (c) 2010, 2014 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 */
package org.eclipse.recommenders.internal.snipmatch.rcp;

import static org.apache.commons.lang3.SystemUtils.LINE_SEPARATOR;
import static org.eclipse.recommenders.internal.snipmatch.rcp.Constants.EDITOR_ID;
import static org.eclipse.recommenders.utils.Checks.cast;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.internal.corext.dom.Selection;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jdt.ui.SharedASTProvider;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.recommenders.internal.snipmatch.rcp.editors.SnippetEditorInput;
import org.eclipse.recommenders.snipmatch.ISnippetRepository;
import org.eclipse.recommenders.snipmatch.Snippet;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@SuppressWarnings("restriction")
public class CreateSnippetHandler extends AbstractHandler {

    // TODO no injection requested yet... always null
    @Inject
    ISnippetRepository repo;

    private CompilationUnitEditor editor;
    private ISourceViewer viewer;

    private ITypeRoot root;
    private CompilationUnit ast;
    private ASTNode enclosingNode;

    private IDocument doc;
    private ITextSelection textSelection;
    private int start;
    private int length;
    private char[] text;

    private Set<String> imports;
    private Set<String> vars;
    private StringBuilder sb;

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        editor = cast(HandlerUtil.getActiveEditor(event));
        viewer = editor.getViewer();
        root = cast(editor.getViewPartInput());
        ast = SharedASTProvider.getAST(root, SharedASTProvider.WAIT_YES, null);

        doc = viewer.getDocument();
        textSelection = cast(viewer.getSelectionProvider().getSelection());
        start = textSelection.getOffset();
        length = textSelection.getLength();
        text = textSelection.getText().toCharArray();

        imports = Sets.newHashSet();
        vars = Sets.newHashSet();
        sb = new StringBuilder();

        enclosingNode = NodeFinder.perform(ast, start, length);
        Selection selection = Selection.createFromStartLength(start, length);

        outer: for (int i = 0; i < text.length; i++) {
            char ch = text[i];
            // every non-identifier character can be copied right away. This is necessary since the NodeFinder somtimes
            // associates a whitespace with a previous AST node (not exactly understood yet).
            if (!Character.isJavaIdentifierPart(ch)) {
                sb.append(ch);
                continue outer;
            }

            NodeFinder nodeFinder = new NodeFinder(enclosingNode, start + i, 0);
            ASTNode node = nodeFinder.getCoveringNode();
            if (selection.covers(node)) {
                switch (node.getNodeType()) {
                case ASTNode.SIMPLE_NAME:
                    SimpleName name = (SimpleName) node;
                    IBinding b = name.resolveBinding();
                    if (b == null) {
                        break;
                    }
                    switch (b.getKind()) {
                    case IBinding.VARIABLE:
                        IVariableBinding vb = (IVariableBinding) b;
                        ITypeBinding type = vb.getType();
                        addImport(type);

                        String varname = name.toString();
                        sb.append("${").append(varname);
                        if (vars.add(varname)) {
                            sb.append(":var(").append(type.getQualifiedName()).append(")");
                        }
                        sb.append("}");
                        i += name.getLength() - 1;
                        continue outer;
                    case IBinding.TYPE:
                        ITypeBinding tb = (ITypeBinding) b;
                        addImport(tb);
                        sb.append(name);
                        i += name.getLength() - 1;
                        continue outer;
                    }
                }
            }
            sb.append(ch);
        }

        for (String type : imports) {
            sb.append("\n").append("${:import(").append(type).append(")}");
        }
        sb.append("${cursor}");

        // TODO not implemented but all necessary data available...
        replacePrefixWhitespaces();

        openSnippetInEditor(event);
        return null;
    }

    private void addImport(ITypeBinding type) {
        // need importable types only. Get the component type if it's an array type
        if (type.isArray()) {
            addImport(type.getComponentType());
            return;
        }
        if (type.isPrimitive()) {
            return;
        }
        String name = type.getQualifiedName();
        imports.add(name);
    }

    private void openSnippetInEditor(ExecutionEvent event) {
        UUID uuid = UUID.randomUUID();
        List<String> keywords = Lists.<String>newArrayList();
        List<String> tags = Lists.<String>newArrayList();
        Snippet snippet = new Snippet(uuid, "<new snippet>", "<enter description>", keywords, tags, sb.toString());
        try {
            // TODO does not work...
            // repo.importSnippet(snippet);
            final SnippetEditorInput input = new SnippetEditorInput(snippet, repo);
            HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().openEditor(input, EDITOR_ID);
        } catch (PartInitException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void replacePrefixWhitespaces() {
        try {

            // fetch the selection's starting line from the editor document to determine the number of leading
            // whitespace characters to remove from the snippet:
            int startLineIndex = textSelection.getStartLine();
            int startLineBeginOffset = doc.getLineOffset(startLineIndex);
            int startLineEndOffset = doc.getLineOffset(startLineIndex + 1) - 1;
            int lineLength = startLineEndOffset - startLineBeginOffset;
            String line = doc.get(startLineBeginOffset, lineLength);

            int index = 0;
            for (; index < line.length(); index++) {
                if (!Character.isWhitespace(line.charAt(index))) {
                    break;
                }
            }
            String wsPrefix = line.substring(0, index);

            // rewrite the buffer and try to remove the leading whitespace. This is a simple heuristic only...
            String[] code = sb.toString().split("\\r?\\n");
            sb.setLength(0);
            for (String l : code) {
                String clean = StringUtils.removeStart(l, wsPrefix);
                sb.append(clean).append(LINE_SEPARATOR);
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
}
