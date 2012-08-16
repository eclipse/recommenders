package org.eclipse.recommenders.internal.completion.rcp;

import static org.eclipse.jdt.core.CompletionProposal.*;

import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.jdt.core.CompletionProposal;
import org.eclipse.recommenders.completion.rcp.IProcessableProposal;
import org.eclipse.recommenders.completion.rcp.ProposalProcessor;
import org.eclipse.recommenders.completion.rcp.SessionProcessor;

public class BaseRelevanceProcessor extends SessionProcessor {

    @Override
    public void process(IProcessableProposal proposal) throws Exception {
        final CompletionProposal core = proposal.getCoreProposal().orNull();
        if (core != null) {
            proposal.getProposalProcessorManager().addProcessor(new ProposalProcessor() {

                @Override
                public void modifyRelevance(AtomicInteger relevance) {
                    int factor = 1;
                    switch (core.getKind()) {
                    case LOCAL_VARIABLE_REF:
                        factor = 10;
                        break;
                    case FIELD_IMPORT:
                    case FIELD_REF:
                    case FIELD_REF_WITH_CASTED_RECEIVER:
                        factor = 9;
                        break;
                    case CONSTRUCTOR_INVOCATION:
                    case ANONYMOUS_CLASS_CONSTRUCTOR_INVOCATION:
                        factor = 8;
                        break;
                    case METHOD_IMPORT:
                    case METHOD_DECLARATION:
                    case METHOD_NAME_REFERENCE:
                    case METHOD_REF:
                    case METHOD_REF_WITH_CASTED_RECEIVER:
                    case POTENTIAL_METHOD_DECLARATION:
                        factor = 7;
                        break;

                    case VARIABLE_DECLARATION:
                        factor = 6;
                        break;

                    case ANONYMOUS_CLASS_DECLARATION:
                    case TYPE_IMPORT:
                    case TYPE_REF:
                        factor = 5;
                        break;
                    case PACKAGE_REF:
                        factor = 4;
                        break;
                    case ANNOTATION_ATTRIBUTE_REF:
                        factor = 3;
                        break;

                    case LABEL_REF:
                        factor = 2;
                        break;
                    case KEYWORD:
                        factor = 1;
                        break;
                    case JAVADOC_FIELD_REF:
                    case JAVADOC_METHOD_REF:
                    case JAVADOC_TYPE_REF:
                    case JAVADOC_VALUE_REF:
                    case JAVADOC_PARAM_REF:
                    case JAVADOC_BLOCK_TAG:
                    case JAVADOC_INLINE_TAG:
                        factor = 1;
                        break;
                    default:
                        factor = 1;
                    }
                    int score = factor << 24;
                    relevance.set(score);
                }
            });
        }
    }
}
