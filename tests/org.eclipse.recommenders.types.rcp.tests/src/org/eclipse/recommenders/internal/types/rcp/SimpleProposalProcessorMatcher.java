/**
 * Copyright (c) 2015 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon Laffoy - initial API and implementation.
 */
package org.eclipse.recommenders.internal.types.rcp;

import static org.mockito.Matchers.argThat;

import org.eclipse.recommenders.completion.rcp.processable.SimpleProposalProcessor;
import org.mockito.ArgumentMatcher;

public class SimpleProposalProcessorMatcher extends ArgumentMatcher<SimpleProposalProcessor> {

    private final int boost;

    public static SimpleProposalProcessor processorWithBoost(int boost) {
        return argThat(new SimpleProposalProcessorMatcher(boost));
    }

    private SimpleProposalProcessorMatcher(int boost) {
        this.boost = boost;
    }

    @Override
    public boolean matches(Object argument) {
        if (!(argument instanceof SimpleProposalProcessor)) {
            return false;
        }
        SimpleProposalProcessor processor = (SimpleProposalProcessor) argument;
        if (boost != processor.getIncrement()) {
            return false;
        }

        return true;
    }
}
