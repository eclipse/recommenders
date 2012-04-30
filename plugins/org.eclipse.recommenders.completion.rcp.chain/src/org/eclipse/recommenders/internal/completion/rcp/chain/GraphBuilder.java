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

import static org.eclipse.recommenders.utils.Checks.cast;
import static org.eclipse.recommenders.utils.rcp.JdtUtils.findAllPublicInstanceFieldsAndNonVoidNonPrimitiveInstanceMethods;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.recommenders.utils.rcp.JdtUtils;
import org.eclipse.recommenders.utils.rcp.internal.RecommendersUtilsPlugin;

import com.google.common.base.Optional;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;

/**
 * A graph builder creates the call chain graph from a list of given entry points.
 * 
 * @see MemberEdge
 */
public class GraphBuilder {

    private static final int maxdepth = 4;
    private static final int maxchains = 20;

    private final List<List<MemberEdge>> chains = Lists.newLinkedList();

    private final Map<IJavaElement, MemberEdge> edgeCache = Maps.newHashMap();
    private final Map<IType, Collection<IMember>> fieldsAndMethodsCache = Maps.newHashMap();
    private final Table<MemberEdge, IType, Boolean> assignableCache = HashBasedTable.create();

    void startChainSearch(final List<MemberEdge> entrypoints, final IType expectedType, final int expectedDimension) {
        final LinkedList<LinkedList<MemberEdge>> incompleteChains = prepareQueue(entrypoints);
        while (!incompleteChains.isEmpty()) {
            final LinkedList<MemberEdge> chain = incompleteChains.poll();
            final MemberEdge edge = chain.getLast();
            final Optional<IType> returnTypeOpt = edge.getReturnType();
            if (!returnTypeOpt.isPresent()) {
                continue;
            }
            if (isAssignableTo(edge, expectedType, expectedDimension)) {
                // TODO: Rewrite test cases to only expect chains with more than 1 element.
                if (chain.size() > 1 || true) {
                    chains.add(chain);
                }
                if (chains.size() == maxchains) {
                    break;
                }
                // TODO: Rewrite test cases to not expect results like "subList", so we can stop once we found the type.
                // continue;
            }
            if (chain.size() >= maxdepth) {
                continue;
            }
            final Collection<IMember> allMethodsAndFields = findAllFieldsAndMethods(returnTypeOpt.get());
            for (final IJavaElement element : allMethodsAndFields) {
                if (element.getElementType() == IJavaElement.METHOD && isToStringMethod((IMethod) element)) {
                    continue;
                }
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
            final LinkedList<MemberEdge> chain = Lists.newLinkedList();
            chain.add(entrypoint);
            incompleteChains.add(chain);
        }
        return incompleteChains;
    }

    private boolean isAssignableTo(final MemberEdge edge, final IType expectedType, final int expectedDimension) {
        Boolean isAssignable = assignableCache.get(edge, expectedType);
        if (isAssignable == null) {
            isAssignable = expectedDimension <= edge.getDimension()
                    && JdtUtils.isAssignable(expectedType, edge.getReturnType().get());
            assignableCache.put(edge, expectedType, isAssignable);
        }
        return isAssignable;
    }

    private Collection<IMember> findAllFieldsAndMethods(final IType returnType) {
        Collection<IMember> cached = fieldsAndMethodsCache.get(returnType);
        if (cached == null) {
            cached = findAllPublicInstanceFieldsAndNonVoidNonPrimitiveInstanceMethods(returnType);
            fieldsAndMethodsCache.put(returnType, cached);
        }
        return cached;
    }

    private MemberEdge createEdge(final IJavaElement member) {
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

    private static boolean isToStringMethod(final IMethod m) {
        try {
            return m.getElementName().equals("toString") && m.getSignature().equals("()java.lang.String;");
        } catch (final JavaModelException e) {
            RecommendersUtilsPlugin.log(e);
            return false;
        }
    }

}
