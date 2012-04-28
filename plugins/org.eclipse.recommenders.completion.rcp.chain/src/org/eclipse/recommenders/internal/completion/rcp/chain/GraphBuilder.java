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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.recommenders.utils.Checks;
import org.eclipse.recommenders.utils.rcp.JdtUtils;
import org.eclipse.recommenders.utils.rcp.internal.RecommendersUtilsPlugin;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * A graph builder creates the call chain graph from a list of given entry points.
 * 
 * @see MemberEdge
 */
final class GraphBuilder {

    private static final int maxdepth = 5;
    private static final int maxchains = 20;

    private final List<List<MemberEdge>> chains = Lists.newLinkedList();

    void startChainSearch(final List<MemberEdge> entrypoints, final IType expectedType) {
        final LinkedList<LinkedList<MemberEdge>> incompleteChains = prepareQueue(entrypoints);
        final Set<IType> visitedReturnTypes = Sets.newHashSet();

        while (!incompleteChains.isEmpty()) {
            final LinkedList<MemberEdge> chain = incompleteChains.poll();
            final MemberEdge edge = chain.getLast();
            final Optional<IType> returnTypeOpt = edge.getReturnType();
            if (!returnTypeOpt.isPresent() || visitedReturnTypes.contains(returnTypeOpt.get())) {
                continue;
            }
            final IType returnType = returnTypeOpt.get();
            if (edge.isAssignableTo(expectedType)) {
                chains.add(chain);
                if (chains.size() == maxchains) {
                    break;
                }
            } else {
                visitedReturnTypes.add(returnType);
            }
            if (chain.size() >= maxdepth) {
                continue;
            }
            final Collection<IMember> allMethodsAndFields = JdtUtils
                    .findAllPublicInstanceFieldsAndNonVoidNonPrimitiveInstanceMethods(returnType);
            for (final IJavaElement element : allMethodsAndFields) {
                if (element.getElementType() == IJavaElement.METHOD && isToStringMethod((IMethod) element)) {
                    continue;
                }
                final MemberEdge newEdge = new MemberEdge(returnType, element);
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

    private static LinkedList<MemberEdge> cloneChainAndAppend(final LinkedList<MemberEdge> chain,
            final MemberEdge newEdge) {
        final LinkedList<MemberEdge> chainCopy = Checks.cast(chain.clone());
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
