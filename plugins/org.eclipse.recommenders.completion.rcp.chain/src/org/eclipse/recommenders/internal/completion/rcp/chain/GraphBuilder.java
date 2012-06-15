/**
 * Copyright (c) 2010, 2011 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 *    Stefan Henß - re-implementation in response to https://bugs.eclipse.org/bugs/show_bug.cgi?id=376796.
 */
package org.eclipse.recommenders.internal.completion.rcp.chain;

import static org.eclipse.recommenders.utils.Checks.cast;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * A graph builder creates the call chain graph from a list of given entry points.
 * 
 * @see MemberEdge
 */
@SuppressWarnings("restriction")
public class GraphBuilder {

    private static final Predicate<FieldBinding> FILTER_FIELDS = new Predicate<FieldBinding>() {

        @Override
        public boolean apply(final FieldBinding m) {
            return m.isStatic();
        }
    };
    private static final Predicate<MethodBinding> FILTER_METHODS = new Predicate<MethodBinding>() {

        @Override
        public boolean apply(final MethodBinding m) {
            if (ASTUtils.isVoid(m) || m.isConstructor() || m.isStatic() || ASTUtils.hasPrimitiveReturnType(m)) {
                return true;
            }
            return m.selector.equals("toString") && m.signature().equals("()java.lang.String;");
        }
    };

    private final TypeBinding expectedType;
    private final int expectedDimension;
    private final List<List<MemberEdge>> chains = Lists.newLinkedList();

    private final Map<Binding, MemberEdge> edgeCache = Maps.newHashMap();
    private final Map<TypeBinding, Collection<Binding>> fieldsAndMethodsCache = Maps.newHashMap();
    private final Map<MemberEdge, Boolean> assignableCache = Maps.newHashMap();

    GraphBuilder(final TypeBinding expectedType) {
        if (expectedType instanceof ArrayBinding) {
            this.expectedType = ASTUtils.removeArrayWrapper(expectedType);
            expectedDimension = ((ArrayBinding) expectedType).dimensions();
        } else {
            this.expectedType = expectedType;
            expectedDimension = 0;
        }
    }

    void startChainSearch(final IJavaElement enclosingElement, final List<MemberEdge> entrypoints, final int maxChains,
            final int maxDepth) {
        final LinkedList<LinkedList<MemberEdge>> incompleteChains = prepareQueue(entrypoints);
        final IType enclosingType = (IType) enclosingElement.getAncestor(IJavaElement.TYPE);

        while (!incompleteChains.isEmpty()) {
            final LinkedList<MemberEdge> chain = incompleteChains.poll();
            final MemberEdge edge = chain.getLast();
            if (isAssignableTo(edge)) {
                if (chain.size() > 1) {
                    chains.add(chain);
                    if (chains.size() == maxChains) {
                        break;
                    }
                }
                continue;
            }
            if (chain.size() >= maxDepth) {
                continue;
            }
            for (final Binding element : findAllFieldsAndMethods(edge.getReturnType(), enclosingType)) {
                final MemberEdge newEdge = createEdge(element);
                if (!chain.contains(newEdge)) {
                    incompleteChains.add(cloneChainAndAppend(chain, newEdge));
                }
            }
        }
    }

    /**
     * Returns the potentially incomplete list of call chains that could be found before a time out happened. The
     * contents of this list are mutable and may change as the search makes progress.
     */
    public List<List<MemberEdge>> getChains() {
        return chains;
    }

    private static LinkedList<LinkedList<MemberEdge>> prepareQueue(final List<MemberEdge> entrypoints) {
        final LinkedList<LinkedList<MemberEdge>> incompleteChains = Lists.newLinkedList();
        for (final MemberEdge entrypoint : entrypoints) {
            // TODO: centralize filtering.
            if (entrypoint.getEdgeElement() instanceof MethodBinding
                    && ASTUtils.hasPrimitiveReturnType((MethodBinding) entrypoint.getEdgeElement())) {
                continue;
            }
            final LinkedList<MemberEdge> chain = Lists.newLinkedList();
            chain.add(entrypoint);
            incompleteChains.add(chain);
        }
        return incompleteChains;
    }

    private boolean isAssignableTo(final MemberEdge edge) {
        Boolean isAssignable = assignableCache.get(edge);
        if (isAssignable == null) {
            isAssignable = ASTUtils.isAssignable(edge, expectedType, expectedDimension);
            assignableCache.put(edge, isAssignable);
        }
        return isAssignable;
    }

    private Collection<Binding> findAllFieldsAndMethods(final TypeBinding chainElementType,
            final IType contextEnclosingType) {
        Collection<Binding> cached = fieldsAndMethodsCache.get(chainElementType);
        if (cached == null) {
            cached = Lists.newLinkedList();
            final boolean isEverythingVisible = isVisibleFromCompletionContext(contextEnclosingType, chainElementType);
            for (final Binding element : ASTUtils.findAllRelevanFieldsAndMethods(chainElementType, FILTER_FIELDS,
                    FILTER_METHODS)) {
                if (!isEverythingVisible && !isPublic(element)) {
                    continue;
                }
                cached.add(element);
            }
            fieldsAndMethodsCache.put(chainElementType, cached);
        }
        return cached;
    }

    private static boolean isVisibleFromCompletionContext(final IType contextEnclosingType, final TypeBinding element) {
        // TODO: Check type hierarchy and visibility levels.
        return contextEnclosingType.getKey().equals(String.valueOf(element.signature()));
    }

    private boolean isPublic(final Binding element) {
        if (element instanceof MethodBinding) {
            return ((MethodBinding) element).isPublic();
        } else if (element instanceof FieldBinding) {
            return ((FieldBinding) element).isPublic();
        }
        throw new IllegalArgumentException(element.toString());
    }

    private MemberEdge createEdge(final Binding member) {
        MemberEdge cached = edgeCache.get(member);
        if (cached == null) {
            cached = new MemberEdge(member);
            edgeCache.put(member, cached);
        }
        return cached;
    }

    private static LinkedList<MemberEdge> cloneChainAndAppend(final LinkedList<MemberEdge> chain,
            final MemberEdge newEdge) {
        final LinkedList<MemberEdge> chainCopy = cast(chain.clone());
        chainCopy.add(newEdge);
        return chainCopy;
    }

}
