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
import static org.eclipse.jface.dialogs.MessageDialog.openError;
import static org.eclipse.recommenders.internal.snipmatch.rcp.Constants.EDITOR_ID;
import static org.eclipse.recommenders.utils.Checks.cast;
import static org.eclipse.ui.handlers.HandlerUtil.getActiveWorkbenchWindow;

import java.util.HashMap;
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
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.internal.corext.dom.Selection;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jdt.ui.SharedASTProvider;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.recommenders.internal.snipmatch.rcp.editors.SnippetEditor;
import org.eclipse.recommenders.internal.snipmatch.rcp.editors.SnippetEditorInput;
import org.eclipse.recommenders.snipmatch.ISnippetRepository;
import org.eclipse.recommenders.snipmatch.Snippet;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@SuppressWarnings("restriction")
public class CreateSnippetHandler extends AbstractHandler {

    private static final Logger LOG = LoggerFactory.getLogger(CreateSnippetHandler.class);

    private Set<ISnippetRepository> repos;

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
    private HashMap<IVariableBinding, String> vars;
    private HashMap<String, Integer> lastVarIndex;
    private StringBuilder sb;

    private ExecutionEvent event;

    @Inject
    public CreateSnippetHandler(Set<ISnippetRepository> repos) {
        this.repos = repos;
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        this.event = event;
        CompilationUnitEditor editor = cast(HandlerUtil.getActiveEditor(event));
        Snippet snippet = createSnippet(editor);
        openSnippetInEditor(snippet);
        return null;
    }

    @VisibleForTesting
    public Snippet createSnippet(CompilationUnitEditor editor) throws ExecutionException {
        viewer = editor.getViewer();
        root = cast(editor.getViewPartInput());
        ast = SharedASTProvider.getAST(root, SharedASTProvider.WAIT_YES, null);

        doc = viewer.getDocument();
        textSelection = cast(viewer.getSelectionProvider().getSelection());
        start = textSelection.getOffset();
        length = textSelection.getLength();
        text = textSelection.getText().toCharArray();

        imports = Sets.newTreeSet();
        vars = Maps.newHashMap();
        lastVarIndex = new HashMap<String, Integer>();
        sb = new StringBuilder();

        enclosingNode = NodeFinder.perform(ast, start, length);
        Selection selection = Selection.createFromStartLength(start, length);

        outer: for (int i = 0; i < text.length; i++) {
            char ch = text[i];
            // every non-identifier character can be copied right away. This is necessary since the NodeFinder sometimes
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
                    case IBinding.TYPE:
                        ITypeBinding tb = (ITypeBinding) b;
                        appendTypeBinding(name, tb);
                        i += name.getLength() - 1;
                        continue outer;
                    case IBinding.VARIABLE:
                        IVariableBinding vb = (IVariableBinding) b;
                        String uniqueVariableName = generateUniqueVariableName(vb, name.toString());
                        StructuralPropertyDescriptor locationInParent = name.getLocationInParent();
                        if (VariableDeclarationFragment.class == locationInParent.getNodeClass()
                                || SingleVariableDeclaration.class == locationInParent.getNodeClass()) {
                            appendNewName(uniqueVariableName, vb);
                        } else if (selection.covers(ast.findDeclaringNode(vb))) {
                            appendTemplateVariableReference(uniqueVariableName);
                        } else {
                            if (vb.isField()) {
                                appendVarReference(uniqueVariableName, vb, "field");
                            } else {
                                appendVarReference(uniqueVariableName, vb, "var");
                            }
                        }
                        i += name.getLength() - 1;
                        continue outer;
                    }
                }
            }
            sb.append(ch);
        }

        sb.append("\n");
        appendImports();
        appendCursor();
        replaceLeadingWhitespaces();

        List<String> keywords = Lists.<String>newArrayList();
        List<String> tags = Lists.<String>newArrayList();
        return new Snippet(UUID.randomUUID(), "<new snippet>", "<enter description>", keywords, tags, sb.toString());
    }

    private String generateUniqueVariableName(IVariableBinding vb, String name) {

        if (vars.containsKey(vb)) {
            return vars.get(vb);
        } else {
            String newName = name;

            Integer i;
            if (lastVarIndex.containsKey(newName)) {
                i = lastVarIndex.get(newName);
            } else {
                i = 1;
            }

            while (vars.containsValue(newName)) {
                i++;
                newName = name.concat(i.toString());
            }

            lastVarIndex.put(name, i);
            vars.put(vb, newName);
            return newName;
        }
    }

    private void appendTypeBinding(SimpleName name, ITypeBinding tb) {
        sb.append(name);
        addImport(tb);

        for (int i = 0; i < 10; i++) {

        }
    }

    private void appendNewName(String name, IVariableBinding vb) {
        ITypeBinding type = vb.getType();
        String varname = name;
        sb.append("${").append(varname).append(":").append("newName").append("(");
        if (type.isArray()) {
            sb.append("'").append(type.getErasure().getQualifiedName()).append("'");
        } else {
            sb.append(type.getErasure().getQualifiedName());
        }
        sb.append(")").append("}");

        addImport(type);
    }

    private StringBuilder appendTemplateVariableReference(String name) {
        return sb.append("${").append(name).append("}");
    }

    private void appendVarReference(String name, IVariableBinding vb, String kind) {
        ITypeBinding type = vb.getType();
        String varname = name;
        sb.append("${").append(varname).append(":").append(kind).append("(");
        if (type.isArray()) {
            sb.append("'").append(type.getErasure().getQualifiedName()).append("'");
        } else {
            sb.append(type.getErasure().getQualifiedName());
        }
        sb.append(")").append("}");

        addImport(type);
    }

    private void appendImports() {
        if (!imports.isEmpty()) {
            String joinedTypes = Joiner.on(", ").join(imports);
            sb.append("${:import(").append(joinedTypes).append(")}");
        }
    }

    private void appendCursor() {
        sb.append("${cursor}");
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
        if (type.getPackage().getName().equals("java.lang")) {
            return;
        }
        String name = type.getErasure().getQualifiedName();
        imports.add(name);
    }

    private void openSnippetInEditor(Snippet snippet) {
        for (ISnippetRepository r : repos) {
            if (r.isImportSupported()) {
                try {
                    SnippetEditorInput input = new SnippetEditorInput(snippet, r);
                    IWorkbenchPage page = getActiveWorkbenchWindow(event).getActivePage();
                    SnippetEditor ed = cast(page.openEditor(input, EDITOR_ID));
                    ed.setDirty(true);
                    // if we could add the snippet somewhere, return. Otherwise report an error
                    return;
                } catch (PartInitException e) {
                    LOG.error(Messages.ERROR_WHILE_OPENING_EDITOR, e);
                }
            }
        }
        openError(HandlerUtil.getActiveShell(event), Messages.ERROR_NO_EDITABLE_REPO_FOUND,
                Messages.ERROR_NO_EDITABLE_REPO_FOUND_HINT);
    }

    private void replaceLeadingWhitespaces() {
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
            LOG.error("An error occured while determining the leading whitespace characters.", e);
        }
    }
}
