/**
 * Copyright (c) 2010, 2014 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Johannes Dorn - initial API and implementation.
 */
package org.eclipse.recommenders.completion.rcp.tips;

import java.util.Date;

import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.recommenders.utils.Nullable;

public interface ICompletionTipProposal extends ICompletionProposal {

    /**
     * Should this completion tip proposal be shown to the user.
     *
     * @param lastSeen
     *            the point in time the proposal was last seen by the user (i.e., selected) or {@code null}, if it was
     *            never seen before.
     */
    boolean isApplicable(@Nullable Date lastSeen);

    void setCursorPosition(int cursorPosition);
}
