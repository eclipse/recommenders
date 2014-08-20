/**
 * Copyright (c) 2010, 2012 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 */
package org.eclipse.recommenders.internal.calls.rcp;

import org.eclipse.jdt.core.CompletionProposal;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.recommenders.rcp.utils.ProposalMatchingUtils;
import org.eclipse.recommenders.utils.Nullable;
import org.eclipse.recommenders.utils.names.IMethodName;
import org.eclipse.recommenders.utils.names.ITypeName;

@SuppressWarnings({ "restriction" })
public class ProposalMatcher {

    private final String proposedName;
    private final ITypeName[] proposedParameterTypes;

    public ProposalMatcher(CompletionProposal proposal, @Nullable LookupEnvironment env) {
        IMethodName proposedMethod = ProposalMatchingUtils.asMethodName(proposal, env).orNull();
        if (proposedMethod != null) {
            proposedName = proposedMethod.getName();
            proposedParameterTypes = proposedMethod.getParameterTypes();
        } else {
            proposedName = null;
            proposedParameterTypes = null;
        }
    }

    public boolean match(IMethodName candidate) {
        String candidateName = candidate.getName();
        if (!candidateName.equals(proposedName)) {
            return false;
        }

        ITypeName[] candidateParameterTypes = candidate.getParameterTypes();
        if (proposedParameterTypes == null || candidateParameterTypes.length != proposedParameterTypes.length) {
            return false;
        }
        for (int i = candidateParameterTypes.length; i-- > 0;) {
            if (!candidateParameterTypes[i].equals(proposedParameterTypes[i])) {
                return false;
            }
        }

        return true;
    }
}
