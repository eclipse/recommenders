package org.eclipse.recommenders.internal.snipmatch.rcp;

import static org.eclipse.recommenders.internal.snipmatch.rcp.Constants.EDITOR_ID;
import static org.eclipse.recommenders.utils.Checks.cast;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

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

    @SuppressWarnings("restriction")
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
            NodeFinder nodeFinder = new NodeFinder(enclosingNode, start + i, 0);
            ASTNode node = nodeFinder.getCoveredNode();
            if (node == null) {
                node = nodeFinder.getCoveringNode();
            }
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
                        String type = vb.getType().getQualifiedName();
                        imports.add(type);

                        String varname = name.toString();
                        sb.append("${").append(varname);
                        if (vars.add(varname)) {
                            sb.append(":var(").append(type).append(")");
                        }
                        sb.append("}");
                        i += name.getLength();
                        continue outer;
                    case IBinding.TYPE:
                        String typeName = ((ITypeBinding) b).getQualifiedName();
                        imports.add(typeName);
                        sb.append(name);
                        i += name.getLength();
                        continue outer;
                    }
                }
            }
            sb.append(text[i]);
        }

        for (String type : imports) {
            sb.append("\n").append("${:import(").append(type).append(")}${cursor}");
        }

        // TODO not implemented but all necessary data available...
        replacePrefixWhitespaces();

        openSnippetInEditor(event);
        return null;
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
            int startLine = textSelection.getStartLine();
            int startLineOffset = doc.getLineOffset(startLine);
            int lineLength = start - startLineOffset;

            // TODO now we need to determine the whitespace characters which we then remove from every line in the
            // string buffer...
            String line = doc.get(startLineOffset, lineLength);
            System.out.println(line);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
}
