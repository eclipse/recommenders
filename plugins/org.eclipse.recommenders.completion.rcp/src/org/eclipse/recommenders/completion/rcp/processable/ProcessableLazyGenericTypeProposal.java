/**
 * Copyright (c) 2010, 2012 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - Initial API
 */
package org.eclipse.recommenders.completion.rcp.processable;

import static com.google.common.base.Optional.fromNullable;
import static org.eclipse.recommenders.utils.Checks.ensureIsNotNull;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.CompletionProposal;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTRequestor;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.internal.corext.dom.Bindings;
import org.eclipse.jdt.internal.corext.template.java.SignatureUtil;
import org.eclipse.jdt.internal.corext.util.Strings;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.javaeditor.ASTProvider;
import org.eclipse.jdt.internal.ui.javaeditor.EditorHighlightingSynchronizer;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.internal.ui.text.java.LazyJavaTypeCompletionProposal;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationExtension;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.link.EditorLinkedModeUI;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;

@SuppressWarnings("restriction")
public class ProcessableLazyGenericTypeProposal extends LazyJavaTypeCompletionProposal implements IProcessableProposal {

    private ProposalProcessorManager mgr;
    private CompletionProposal coreProposal;

    public ProcessableLazyGenericTypeProposal(final CompletionProposal coreProposal,
            final JavaContentAssistInvocationContext context) {
        super(coreProposal, context);
        this.coreProposal = coreProposal;
    }

    // jdt code below ==============================================
    /** Triggers for types. Do not modify. */
    private static final char[] GENERIC_TYPE_TRIGGERS = new char[] { '.', '\t', '[', '(', '<', ' ' };

    /**
     * Short-lived context information object for generic types. Currently, these are only created after inserting a
     * type proposal, as core doesn't give us the correct type proposal from within SomeType<|>.
     */
    private static class ContextInformation implements IContextInformation, IContextInformationExtension {
        private final String fInformationDisplayString;
        private final String fContextDisplayString;
        private final Image fImage;
        private final int fPosition;

        ContextInformation(final ProcessableLazyGenericTypeProposal swLazyGenericTypeProposal) {
            // don't cache the proposal as content assistant
            // might hang on to the context info
            fContextDisplayString = swLazyGenericTypeProposal.getDisplayString();
            fInformationDisplayString = computeContextString(swLazyGenericTypeProposal);
            fImage = swLazyGenericTypeProposal.getImage();
            fPosition = swLazyGenericTypeProposal.getReplacementOffset()
                    + swLazyGenericTypeProposal.getReplacementString().indexOf('<') + 1;
        }

        /*
         * @see org.eclipse.jface.text.contentassist.IContextInformation#getContextDisplayString()
         */
        @Override
        public String getContextDisplayString() {
            return fContextDisplayString;
        }

        /*
         * @see org.eclipse.jface.text.contentassist.IContextInformation#getImage()
         */
        @Override
        public Image getImage() {
            return fImage;
        }

        /*
         * @see org.eclipse.jface.text.contentassist.IContextInformation#getInformationDisplayString()
         */
        @Override
        public String getInformationDisplayString() {
            return fInformationDisplayString;
        }

        private String computeContextString(final ProcessableLazyGenericTypeProposal proposal) {
            try {
                final TypeArgumentProposal[] proposals = proposal.computeTypeArgumentProposals();
                if (proposals.length == 0) {
                    return null;
                }

                final StringBuffer buf = new StringBuffer();
                for (int i = 0; i < proposals.length; i++) {
                    buf.append(proposals[i].getDisplayName());
                    if (i < proposals.length - 1) {
                        buf.append(", "); //$NON-NLS-1$
                    }
                }
                return Strings.markJavaElementLabelLTR(buf.toString());

            } catch (final JavaModelException e) {
                return null;
            }
        }

        /*
         * @see org.eclipse.jface.text.contentassist.IContextInformationExtension#getContextInformationPosition()
         */
        @Override
        public int getContextInformationPosition() {
            return fPosition;
        }

        /*
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof ContextInformation) {
                final ContextInformation ci = (ContextInformation) obj;
                return getContextInformationPosition() == ci.getContextInformationPosition()
                        && getInformationDisplayString().equals(ci.getInformationDisplayString());
            }
            return false;
        }

        /*
         * @see java.lang.Object#hashCode()
         * 
         * @since 3.1
         */
        @Override
        public int hashCode() {
            final int low = fContextDisplayString != null ? fContextDisplayString.hashCode() : 0;
            return fPosition << 24 | fInformationDisplayString.hashCode() << 16 | low;
        }

    }

    private static final class TypeArgumentProposal {
        private final boolean fIsAmbiguous;
        private final String fProposal;
        private final String fTypeDisplayName;

        TypeArgumentProposal(final String proposal, final boolean ambiguous, final String typeDisplayName) {
            fIsAmbiguous = ambiguous;
            fProposal = proposal;
            fTypeDisplayName = typeDisplayName;
        }

        public String getDisplayName() {
            return fTypeDisplayName;
        }

        boolean isAmbiguous() {
            return fIsAmbiguous;
        }

        @Override
        public String toString() {
            return fProposal;
        }
    }

    private IRegion fSelectedRegion; // initialized by apply()
    private TypeArgumentProposal[] fTypeArgumentProposals;
    private boolean fCanUseDiamond;
    private String lastPrefix;

    /*
     * @see ICompletionProposalExtension#apply(IDocument, char)
     */
    @Override
    public void apply(final IDocument document, final char trigger, final int offset) {
        boolean onlyAppendArguments;
        try {
            onlyAppendArguments = fProposal.getCompletion().length == 0 && offset > 0
                    && document.getChar(offset - 1) == '<';
        } catch (final BadLocationException e) {
            onlyAppendArguments = false;
        }

        if (onlyAppendArguments || shouldAppendArguments(document, offset, trigger)) {
            try {
                final TypeArgumentProposal[] typeArgumentProposals = computeTypeArgumentProposals();
                if (typeArgumentProposals.length > 0) {

                    final int[] offsets = new int[typeArgumentProposals.length];
                    final int[] lengths = new int[typeArgumentProposals.length];
                    StringBuffer buffer;

                    if (canUseDiamond()) {
                        buffer = new StringBuffer(getReplacementString());
                        buffer.append("<>"); //$NON-NLS-1$
                    } else {
                        buffer = createParameterList(typeArgumentProposals, offsets, lengths, onlyAppendArguments);
                    }

                    // set the generic type as replacement string
                    final boolean insertClosingParenthesis = trigger == '(' && autocloseBrackets();
                    if (insertClosingParenthesis) {
                        updateReplacementWithParentheses(buffer);
                    }
                    super.setReplacementString(buffer.toString());

                    // add import & remove package, update replacement offset
                    super.apply(document, '\0', offset);

                    if (getTextViewer() != null) {
                        if (hasAmbiguousProposals(typeArgumentProposals)) {
                            adaptOffsets(offsets, buffer);
                            installLinkedMode(document, offsets, lengths, typeArgumentProposals,
                                    insertClosingParenthesis);
                        } else {
                            if (insertClosingParenthesis) {
                                setUpLinkedMode(document, ')');
                            } else {
                                fSelectedRegion = new Region(getReplacementOffset() + getReplacementString().length(),
                                        0);
                            }
                        }
                    }

                    return;
                }
            } catch (final JavaModelException e) {
                // log and continue
                JavaPlugin.log(e);
            }
        }

        // default is to use the super implementation
        // reasons:
        // - not a parameterized type,
        // - already followed by <type arguments>
        // - proposal type does not inherit from expected type
        super.apply(document, trigger, offset);
    }

    /*
     * @see org.eclipse.jdt.internal.ui.text.java.LazyJavaTypeCompletionProposal#computeTriggerCharacters()
     */
    @Override
    protected char[] computeTriggerCharacters() {
        return GENERIC_TYPE_TRIGGERS;
    }

    /**
     * Adapt the parameter offsets to any modification of the replacement string done by <code>apply</code>. For
     * example, applying the proposal may add an import instead of inserting the fully qualified name.
     * <p>
     * This assumes that modifications happen only at the beginning of the replacement string and do not touch the type
     * arguments list.
     * </p>
     * 
     * @param offsets
     *            the offsets to modify
     * @param buffer
     *            the original replacement string
     */
    private void adaptOffsets(final int[] offsets, final StringBuffer buffer) {
        final String replacementString = getReplacementString();
        final int delta = buffer.length() - replacementString.length(); // due to using an import instead of package
        for (int i = 0; i < offsets.length; i++) {
            offsets[i] -= delta;
        }
    }

    /**
     * Computes the type argument proposals for this type proposals. If there is an expected type binding that is a
     * super type of the proposed type, the wildcard type arguments of the proposed type that can be mapped through to
     * type the arguments of the expected type binding are bound accordingly.
     * <p>
     * For type arguments that cannot be mapped to arguments in the expected type, or if there is no expected type, the
     * upper bound of the type argument is proposed.
     * </p>
     * <p>
     * The argument proposals have their <code>isAmbiguos</code> flag set to <code>false</code> if the argument can be
     * mapped to a non-wildcard type argument in the expected type, otherwise the proposal is ambiguous.
     * </p>
     * 
     * @return the type argument proposals for the proposed type
     * @throws JavaModelException
     *             if accessing the java model fails
     */
    private TypeArgumentProposal[] computeTypeArgumentProposals() throws JavaModelException {
        if (fTypeArgumentProposals == null) {

            final IType type = (IType) getJavaElement();
            if (type == null) {
                return new TypeArgumentProposal[0];
            }

            final ITypeParameter[] parameters = type.getTypeParameters();
            if (parameters.length == 0) {
                return new TypeArgumentProposal[0];
            }

            final TypeArgumentProposal[] arguments = new TypeArgumentProposal[parameters.length];

            final ITypeBinding expectedTypeBinding = getExpectedType();
            if (expectedTypeBinding != null && expectedTypeBinding.isParameterizedType()) {
                // in this case, the type arguments we propose need to be compatible
                // with the corresponding type parameters to declared type

                final IType expectedType = (IType) expectedTypeBinding.getJavaElement();

                final IType[] path = computeInheritancePath(type, expectedType);
                if (path == null) {
                    // proposed type does not inherit from expected type
                    // the user might be looking for an inner type of proposed type
                    // to instantiate -> do not add any type arguments
                    return new TypeArgumentProposal[0];
                }

                final int[] indices = new int[parameters.length];
                for (int paramIdx = 0; paramIdx < parameters.length; paramIdx++) {
                    indices[paramIdx] = mapTypeParameterIndex(path, path.length - 1, paramIdx);
                }

                // for type arguments that are mapped through to the expected type's
                // parameters, take the arguments of the expected type
                final ITypeBinding[] typeArguments = expectedTypeBinding.getTypeArguments();
                for (int paramIdx = 0; paramIdx < parameters.length; paramIdx++) {
                    if (indices[paramIdx] != -1) {
                        // type argument is mapped through
                        final ITypeBinding binding = typeArguments[indices[paramIdx]];
                        arguments[paramIdx] = computeTypeProposal(binding, parameters[paramIdx]);
                    }
                }
            }

            // for type arguments that are not mapped through to the expected type,
            // take the lower bound of the type parameter
            for (int i = 0; i < arguments.length; i++) {
                if (arguments[i] == null) {
                    arguments[i] = computeTypeProposal(parameters[i]);
                }
            }
            fTypeArgumentProposals = arguments;
        }
        return fTypeArgumentProposals;
    }

    /**
     * Returns a type argument proposal for a given type parameter. The proposal is:
     * <ul>
     * <li>the type bound for type parameters with a single bound</li>
     * <li>the type parameter name for all other (unbounded or more than one bound) type parameters</li>
     * </ul>
     * Type argument proposals for type parameters are always ambiguous.
     * 
     * @param parameter
     *            the type parameter of the inserted type
     * @return a type argument proposal for <code>parameter</code>
     * @throws JavaModelException
     *             if this element does not exist or if an exception occurs while accessing its corresponding resource
     */
    private TypeArgumentProposal computeTypeProposal(final ITypeParameter parameter) throws JavaModelException {
        final String[] bounds = parameter.getBounds();
        final String elementName = parameter.getElementName();
        final String displayName = computeTypeParameterDisplayName(parameter, bounds);
        if (bounds.length == 1 && !"java.lang.Object".equals(bounds[0])) {
            return new TypeArgumentProposal(Signature.getSimpleName(bounds[0]), true, displayName);
        } else {
            return new TypeArgumentProposal(elementName, true, displayName);
        }
    }

    private String computeTypeParameterDisplayName(final ITypeParameter parameter, final String[] bounds) {
        if (bounds.length == 0 || bounds.length == 1 && "java.lang.Object".equals(bounds[0])) {
            return parameter.getElementName();
        }
        final StringBuffer buf = new StringBuffer(parameter.getElementName());
        buf.append(" extends "); //$NON-NLS-1$
        for (int i = 0; i < bounds.length; i++) {
            buf.append(Signature.getSimpleName(bounds[i]));
            if (i < bounds.length - 1) {
                buf.append(" & "); //$NON-NLS-1$
            }
        }
        return buf.toString();
    }

    /**
     * Returns a type argument proposal for a given type binding. The proposal is:
     * <ul>
     * <li>the simple type name for normal types or type variables (unambigous proposal)</li>
     * <li>for wildcard types (ambigous proposals):
     * <ul>
     * <li>the upper bound for wildcards with an upper bound</li>
     * <li>the {@linkplain #computeTypeProposal(ITypeParameter) parameter proposal} for unbounded wildcards or wildcards
     * with a lower bound</li>
     * </ul>
     * </li>
     * </ul>
     * 
     * @param binding
     *            the type argument binding in the expected type
     * @param parameter
     *            the type parameter of the inserted type
     * @return a type argument proposal for <code>binding</code>
     * @throws JavaModelException
     *             if this element does not exist or if an exception occurs while accessing its corresponding resource
     * @see #computeTypeProposal(ITypeParameter)
     */
    private TypeArgumentProposal computeTypeProposal(final ITypeBinding binding, final ITypeParameter parameter)
            throws JavaModelException {
        final String name = Bindings.getTypeQualifiedName(binding);
        if (binding.isWildcardType()) {

            if (binding.isUpperbound()) {
                // replace the wildcard ? with the type parameter name to get "E extends Bound" instead of
                // "? extends Bound"
                final String contextName = name.replaceFirst("\\?", parameter.getElementName()); //$NON-NLS-1$
                // upper bound - the upper bound is the bound itself
                return new TypeArgumentProposal(binding.getBound().getName(), true, contextName);
            }

            // no or upper bound - use the type parameter of the inserted type, as it may be more
            // restrictive (eg. List<?> list= new SerializableList<Serializable>())
            return computeTypeProposal(parameter);
        }

        // not a wildcard but a type or type variable - this is unambigously the right thing to insert
        return new TypeArgumentProposal(name, false, name);
    }

    /**
     * Computes one inheritance path from <code>superType</code> to <code>subType</code> or <code>null</code> if
     * <code>subType</code> does not inherit from <code>superType</code>. Note that there may be more than one
     * inheritance path - this method simply returns one.
     * <p>
     * The returned array contains <code>superType</code> at its first index, and <code>subType</code> at its last
     * index. If <code>subType</code> equals <code>superType</code> , an array of length 1 is returned containing that
     * type.
     * </p>
     * 
     * @param subType
     *            the sub type
     * @param superType
     *            the super type
     * @return an inheritance path from <code>superType</code> to <code>subType</code>, or <code>null</code> if
     *         <code>subType</code> does not inherit from <code>superType</code>
     * @throws JavaModelException
     *             if this element does not exist or if an exception occurs while accessing its corresponding resource
     */
    private IType[] computeInheritancePath(final IType subType, IType superType) throws JavaModelException {
        if (superType == null) {
            return null;
        }

        // optimization: avoid building the type hierarchy for the identity case
        if (superType.equals(subType)) {
            return new IType[] { subType };
        }

        final ITypeHierarchy hierarchy = subType.newSupertypeHierarchy(getProgressMonitor());
        if (!hierarchy.contains(superType)) {
            return null; // no path
        }

        final List<IType> path = new LinkedList<IType>();
        path.add(superType);
        do {
            // any sub type must be on a hierarchy chain from superType to subType
            superType = hierarchy.getSubtypes(superType)[0];
            path.add(superType);
        } while (!superType.equals(subType)); // since the equality case is handled above, we can spare one check

        return path.toArray(new IType[path.size()]);
    }

    private NullProgressMonitor getProgressMonitor() {
        return new NullProgressMonitor();
    }

    /**
     * For the type parameter at <code>paramIndex</code> in the type at <code>path[pathIndex]</code> , this method
     * computes the corresponding type parameter index in the type at <code>path[0]</code>. If the type parameter does
     * not map to a type parameter of the super type, <code>-1</code> is returned.
     * 
     * @param path
     *            the type inheritance path, a non-empty array of consecutive sub types
     * @param pathIndex
     *            an index into <code>path</code> specifying the type to start with
     * @param paramIndex
     *            the index of the type parameter to map - <code>path[pathIndex]</code> must have a type parameter at
     *            that index, lest an <code>ArrayIndexOutOfBoundsException</code> is thrown
     * @return the index of the type parameter in <code>path[0]</code> corresponding to the type parameter at
     *         <code>paramIndex</code> in <code>path[pathIndex]</code>, or -1 if there is no corresponding type
     *         parameter
     * @throws JavaModelException
     *             if this element does not exist or if an exception occurs while accessing its corresponding resource
     * @throws ArrayIndexOutOfBoundsException
     *             if <code>path[pathIndex]</code> has &lt;= <code>paramIndex</code> parameters
     */
    private int mapTypeParameterIndex(final IType[] path, final int pathIndex, final int paramIndex)
            throws JavaModelException, ArrayIndexOutOfBoundsException {
        if (pathIndex == 0) {
            // break condition: we've reached the top of the hierarchy
            return paramIndex;
        }

        final IType subType = path[pathIndex];
        final IType superType = path[pathIndex - 1];

        final String superSignature = findMatchingSuperTypeSignature(subType, superType);
        final ITypeParameter param = subType.getTypeParameters()[paramIndex];
        final int index = findMatchingTypeArgumentIndex(superSignature, param.getElementName());
        if (index == -1) {
            // not mapped through
            return -1;
        }

        return mapTypeParameterIndex(path, pathIndex - 1, index);
    }

    /**
     * Finds and returns the super type signature in the <code>extends</code> or <code>implements</code> clause of
     * <code>subType</code> that corresponds to <code>superType</code>.
     * 
     * @param subType
     *            a direct and true sub type of <code>superType</code>
     * @param superType
     *            a direct super type (super class or interface) of <code>subType</code>
     * @return the super type signature of <code>subType</code> referring to <code>superType</code>
     * @throws JavaModelException
     *             if extracting the super type signatures fails, or if <code>subType</code> contains no super type
     *             signature to <code>superType</code>
     */
    private String findMatchingSuperTypeSignature(final IType subType, final IType superType) throws JavaModelException {
        final String[] signatures = getSuperTypeSignatures(subType, superType);
        for (int i = 0; i < signatures.length; i++) {
            final String signature = signatures[i];
            final String qualified = SignatureUtil.qualifySignature(signature, subType);
            final String subFQN = SignatureUtil.stripSignatureToFQN(qualified);

            final String superFQN = superType.getFullyQualifiedName();
            if (subFQN.equals(superFQN)) {
                return signature;
            }

            // TODO handle local types
        }

        throw new JavaModelException(new CoreException(new Status(IStatus.ERROR, JavaPlugin.getPluginId(), IStatus.OK,
                "Illegal hierarchy", null))); //$NON-NLS-1$
    }

    /**
     * Finds and returns the index of the type argument named <code>argument</code> in the given super type signature.
     * <p>
     * If <code>signature</code> does not contain a corresponding type argument, or if <code>signature</code> has no
     * type parameters (i.e. is a reference to a non-parameterized type or a raw type), -1 is returned.
     * </p>
     * 
     * @param signature
     *            the super type signature from a type's <code>extends</code> or <code>implements</code> clause
     * @param argument
     *            the name of the type argument to find
     * @return the index of the given type argument, or -1 if there is none
     */
    private int findMatchingTypeArgumentIndex(final String signature, final String argument) {
        final String[] typeArguments = Signature.getTypeArguments(signature);
        for (int i = 0; i < typeArguments.length; i++) {
            if (Signature.getSignatureSimpleName(typeArguments[i]).equals(argument)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the super interface signatures of <code>subType</code> if <code>superType</code> is an interface,
     * otherwise returns the super type signature.
     * 
     * @param subType
     *            the sub type signature
     * @param superType
     *            the super type signature
     * @return the super type signatures of <code>subType</code>
     * @throws JavaModelException
     *             if any java model operation fails
     */
    private String[] getSuperTypeSignatures(final IType subType, final IType superType) throws JavaModelException {
        if (superType.isInterface()) {
            return subType.getSuperInterfaceTypeSignatures();
        } else {
            return new String[] { subType.getSuperclassTypeSignature() };
        }
    }

    /**
     * Returns the type binding of the expected type as it is contained in the code completion context.
     * 
     * @return the binding of the expected type
     */
    private ITypeBinding getExpectedType() {
        final char[][] chKeys = fInvocationContext.getCoreContext().getExpectedTypesKeys();
        if (chKeys == null || chKeys.length == 0) {
            return null;
        }

        final String[] keys = new String[chKeys.length];
        for (int i = 0; i < keys.length; i++) {
            keys[i] = String.valueOf(chKeys[0]);
        }

        final ASTParser parser = ASTParser.newParser(ASTProvider.SHARED_AST_LEVEL);
        parser.setProject(fCompilationUnit.getJavaProject());
        parser.setResolveBindings(true);
        parser.setStatementsRecovery(true);

        final Map<String, IBinding> bindings = new HashMap<String, IBinding>();
        final ASTRequestor requestor = new ASTRequestor() {
            @Override
            public void acceptBinding(final String bindingKey, final IBinding binding) {
                bindings.put(bindingKey, binding);
            }
        };
        parser.createASTs(new ICompilationUnit[0], keys, requestor, null);

        if (bindings.size() > 0) {
            return (ITypeBinding) bindings.get(keys[0]);
        }

        return null;
    }

    /**
     * Returns <code>true</code> if type arguments should be appended when applying this proposal, <code>false</code> if
     * not (for example if the document already contains a type argument list after the insertion point.
     * 
     * @param document
     *            the document
     * @param offset
     *            the insertion offset
     * @param trigger
     *            the trigger character
     * @return <code>true</code> if arguments should be appended
     */
    private boolean shouldAppendArguments(final IDocument document, final int offset, final char trigger) {
        /*
         * No argument list if there were any special triggers (for example a period to qualify an inner type).
         */
        if (trigger != '\0' && trigger != '<' && trigger != '(') {
            return false;
        }

        /* No argument list if the completion is empty (already within the argument list). */
        final char[] completion = fProposal.getCompletion();
        if (completion.length == 0) {
            return false;
        }

        /* No argument list if there already is a generic signature behind the name. */
        try {
            final IRegion region = document.getLineInformationOfOffset(offset);
            final String line = document.get(region.getOffset(), region.getLength());

            int index = offset - region.getOffset();
            while (index != line.length() && Character.isUnicodeIdentifierPart(line.charAt(index))) {
                ++index;
            }

            if (index == line.length()) {
                return true;
            }

            final char ch = line.charAt(index);
            return ch != '<';

        } catch (final BadLocationException e) {
            return true;
        }
    }

    private StringBuffer createParameterList(final TypeArgumentProposal[] typeArguments, final int[] offsets,
            final int[] lengths, final boolean onlyAppendArguments) {
        final StringBuffer buffer = new StringBuffer();
        buffer.append(getReplacementString());

        final FormatterPrefs prefs = getFormatterPrefs();
        final char LESS = '<';
        final char GREATER = '>';
        if (!onlyAppendArguments) {
            if (prefs.beforeOpeningBracket) {
                buffer.append(SPACE);
            }
            buffer.append(LESS);
        }
        if (prefs.afterOpeningBracket) {
            buffer.append(SPACE);
        }
        final StringBuffer separator = new StringBuffer(3);
        if (prefs.beforeTypeArgumentComma) {
            separator.append(SPACE);
        }
        separator.append(COMMA);
        if (prefs.afterTypeArgumentComma) {
            separator.append(SPACE);
        }

        for (int i = 0; i != typeArguments.length; i++) {
            if (i != 0) {
                buffer.append(separator);
            }

            offsets[i] = buffer.length();
            buffer.append(typeArguments[i]);
            lengths[i] = buffer.length() - offsets[i];
        }
        if (prefs.beforeClosingBracket) {
            buffer.append(SPACE);
        }

        if (!onlyAppendArguments) {
            buffer.append(GREATER);
        }

        return buffer;
    }

    private void installLinkedMode(final IDocument document, final int[] offsets, final int[] lengths,
            final TypeArgumentProposal[] typeArgumentProposals, final boolean withParentheses) {
        final int replacementOffset = getReplacementOffset();
        final String replacementString = getReplacementString();

        try {
            final LinkedModeModel model = new LinkedModeModel();
            for (int i = 0; i != offsets.length; i++) {
                if (typeArgumentProposals[i].isAmbiguous()) {
                    final LinkedPositionGroup group = new LinkedPositionGroup();
                    group.addPosition(new LinkedPosition(document, replacementOffset + offsets[i], lengths[i]));
                    model.addGroup(group);
                }
            }
            if (withParentheses) {
                final LinkedPositionGroup group = new LinkedPositionGroup();
                group.addPosition(new LinkedPosition(document, replacementOffset + getCursorPosition(), 0));
                model.addGroup(group);
            }

            model.forceInstall();
            final JavaEditor editor = getJavaEditor();
            if (editor != null) {
                model.addLinkingListener(new EditorHighlightingSynchronizer(editor));
            }

            final LinkedModeUI ui = new EditorLinkedModeUI(model, getTextViewer());
            ui.setExitPolicy(new ExitPolicy(withParentheses ? ')' : '>', document));
            ui.setExitPosition(getTextViewer(), replacementOffset + replacementString.length(), 0, Integer.MAX_VALUE);
            ui.setDoContextInfo(true);
            ui.enter();

            fSelectedRegion = ui.getSelectedRegion();

        } catch (final BadLocationException e) {
            JavaPlugin.log(e);
            openErrorDialog(e);
        }
    }

    private boolean hasAmbiguousProposals(final TypeArgumentProposal[] typeArgumentProposals) {
        boolean hasAmbiguousProposals = false;
        for (int i = 0; i < typeArgumentProposals.length; i++) {
            if (typeArgumentProposals[i].isAmbiguous()) {
                hasAmbiguousProposals = true;
                break;
            }
        }
        return hasAmbiguousProposals;
    }

    /**
     * Returns the currently active java editor, or <code>null</code> if it cannot be determined.
     * 
     * @return the currently active java editor, or <code>null</code>
     */
    private JavaEditor getJavaEditor() {
        final IEditorPart part = JavaPlugin.getActivePage().getActiveEditor();
        if (part instanceof JavaEditor) {
            return (JavaEditor) part;
        } else {
            return null;
        }
    }

    /*
     * @see ICompletionProposal#getSelection(IDocument)
     */
    @Override
    public Point getSelection(final IDocument document) {
        if (fSelectedRegion == null) {
            return super.getSelection(document);
        }

        return new Point(fSelectedRegion.getOffset(), fSelectedRegion.getLength());
    }

    private void openErrorDialog(final BadLocationException e) {
        final Shell shell = getTextViewer().getTextWidget().getShell();
        MessageDialog.openError(shell, "Failed", e.getMessage());
    }

    /*
     * @see org.eclipse.jdt.internal.ui.text.java.LazyJavaCompletionProposal#computeContextInformation()
     */
    @Override
    protected IContextInformation computeContextInformation() {
        try {
            if (hasParameters()) {
                final TypeArgumentProposal[] proposals = computeTypeArgumentProposals();
                if (hasAmbiguousProposals(proposals)) {
                    return new ContextInformation(this);
                }
            }
        } catch (final JavaModelException e) {
        }
        return super.computeContextInformation();
    }

    @Override
    protected int computeCursorPosition() {
        if (fSelectedRegion != null) {
            return fSelectedRegion.getOffset() - getReplacementOffset();
        }
        return super.computeCursorPosition();
    }

    private boolean hasParameters() {
        try {
            final IType type = (IType) getJavaElement();
            if (type == null) {
                return false;
            }

            return type.getTypeParameters().length > 0;
        } catch (final JavaModelException e) {
            return false;
        }
    }

    /**
     * Sets whether this proposal can use the diamond.
     * 
     * @param canUseDiamond
     *            <code>true</code> if a diamond can be inserted
     * @see CompletionProposal#canUseDiamond(org.eclipse.jdt.core.CompletionContext)
     * @since 3.7
     */
    void canUseDiamond(final boolean canUseDiamond) {
        fCanUseDiamond = canUseDiamond;
    }

    /**
     * Tells whether this proposal can use the diamond.
     * 
     * @return <code>true</code> if a diamond can be used
     * @see CompletionProposal#canUseDiamond(org.eclipse.jdt.core.CompletionContext)
     * @since 3.7
     */
    protected boolean canUseDiamond() {
        return fCanUseDiamond;
    }

    // ===========
    @Override
    public boolean isPrefix(final String prefix, final String completion) {
        lastPrefix = prefix;
        if (mgr.prefixChanged(prefix)) {
            return true;
        }
        return super.isPrefix(prefix, completion);
    }

    @Override
    public String getPrefix() {
        return lastPrefix;
    }

    @Override
    public Optional<CompletionProposal> getCoreProposal() {
        return fromNullable(coreProposal);
    }

    @Override
    public ProposalProcessorManager getProposalProcessorManager() {
        return mgr;
    }

    @Override
    public void setProposalProcessorManager(ProposalProcessorManager mgr) {
        this.mgr = mgr;
    }

    private Map<String, Object> tags = Maps.newHashMap();

    @Override
    public void setTag(String key, Object value) {
        ensureIsNotNull(key);
        if (value == null) {
            tags.remove(key);
        } else {
            tags.put(key, value);
        }
    }

    @Override
    public <T> Optional<T> getTag(String key) {
        return Optional.fromNullable((T) tags.get(key));
    }

    @Override
    public <T> T getTag(String key, T defaultValue) {
        T res = (T) tags.get(key);
        return res != null ? res : defaultValue;
    }

}
