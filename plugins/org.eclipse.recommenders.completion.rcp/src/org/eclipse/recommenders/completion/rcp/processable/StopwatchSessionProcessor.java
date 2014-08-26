/**
 * Copyright (c) 2014 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 */
package org.eclipse.recommenders.completion.rcp.processable;

import java.util.List;

import org.apache.commons.lang3.time.StopWatch;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.recommenders.completion.rcp.IRecommendersCompletionContext;

public class StopwatchSessionProcessor extends SessionProcessor {

    private StopWatch watch = new StopWatch();
    private SessionProcessor delegate;

    public StopwatchSessionProcessor(SessionProcessor delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean startSession(IRecommendersCompletionContext context) {
        watch.start();
        try {
            return delegate.startSession(context);
        } finally {
            watch.suspend();
        }
    }

    @Override
    public void process(IProcessableProposal proposal) throws Exception {
        try {
            watch.resume();
            delegate.process(proposal);
        } finally {
            watch.suspend();
        }
    }

    @Override
    public void endSession(List<ICompletionProposal> proposals) {
        try {
            watch.resume();
            delegate.endSession(proposals);
        } finally {
            watch.suspend();
        }
    }

    @Override
    public void aboutToShow(List<ICompletionProposal> proposals) {
        try {
            watch.resume();
            delegate.aboutToShow(proposals);
        } finally {
            watch.suspend();
        }
    }

    @Override
    public void selected(ICompletionProposal proposal) {
        try {
            watch.resume();
            delegate.selected(proposal);
        } finally {
            watch.suspend();
        }
    }

    @Override
    public void applied(ICompletionProposal proposal) {
        try {
            watch.resume();
            delegate.applied(proposal);
        } finally {
            watch.suspend();
        }
    }

    @Override
    public void aboutToClose() {
        try {
            watch.resume();
            delegate.aboutToClose();
        } finally {
            watch.suspend();
        }
    }

    /**
     * @return the time in milliseconds
     */
    public long getTime() {
        return watch.getTime();
    }
}
