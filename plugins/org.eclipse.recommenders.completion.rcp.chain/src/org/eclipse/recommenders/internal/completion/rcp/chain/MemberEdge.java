/**
 * Copyright (c) 2010, 2011 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 */
package org.eclipse.recommenders.internal.completion.rcp.chain;

import static org.eclipse.recommenders.utils.Checks.ensureIsNotNull;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

/**
 * Represents a transition from Type A to Type B by some chain element ( {@link IField} access, {@link IMethod} call, or
 * {@link ILocalVariable} (as entrypoints only)).
 * 
 * @see GraphBuilder
 */
@SuppressWarnings("restriction")
public class MemberEdge {

    public enum EdgeType {
        METHOD, FIELD, LOCAL_VARIABLE
    }

    private final Binding element;
    private TypeBinding returnType;
    private int dimension;
    private EdgeType edgeType;

    public MemberEdge(final Binding member) {
        element = ensureIsNotNull(member);
        initializeReturnType();
    }

    private void initializeReturnType() {
        switch (getEdgeElement().kind()) {
        case Binding.FIELD:
            returnType = ((FieldBinding) getEdgeElement()).type;
            edgeType = EdgeType.FIELD;
            break;
        case Binding.LOCAL:
            returnType = ((LocalVariableBinding) getEdgeElement()).type;
            edgeType = EdgeType.LOCAL_VARIABLE;
            break;
        case Binding.METHOD:
            returnType = ((MethodBinding) getEdgeElement()).returnType;
            edgeType = EdgeType.METHOD;
            break;
        default:
            throw new IllegalStateException();
        }
        dimension = returnType.dimensions();
    }

    /**
     * @return Instance of {@link IMethod}, {@link IField}, or {@link ILocalVariable}.
     */
    public <T extends Binding> T getEdgeElement() {
        return (T) element;
    }

    @Override
    public String toString() {
        switch (getEdgeElement().kind()) {
        case Binding.FIELD:
            return ((FieldBinding) getEdgeElement()).toString();
        case Binding.LOCAL:
            return ((LocalVariableBinding) getEdgeElement()).toString();
        case Binding.METHOD:
            final MethodBinding m = getEdgeElement();
            return new StringBuilder().append(m.selector).append(m.signature()).toString();
        default:
            return super.toString();
        }
    }

    public EdgeType getEdgeType() {
        return edgeType;
    }

    public TypeBinding getReturnType() {
        return returnType;
    }

    public int getDimension() {
        return dimension;
    }

    @Override
    public int hashCode() {
        return element.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof MemberEdge) {
            final MemberEdge other = (MemberEdge) obj;
            return element.equals(other.element);
        }
        return super.equals(obj);
    }
}
