package org.eclipse.recommenders.internal.completion.rcp;

import org.eclipse.jface.text.contentassist.ICompletionProposal;

public interface ICompletionTipProposal extends ICompletionProposal {

    boolean isApplicable();
}
