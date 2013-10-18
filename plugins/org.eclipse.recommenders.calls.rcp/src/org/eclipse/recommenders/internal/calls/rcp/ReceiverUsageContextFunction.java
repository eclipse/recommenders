package org.eclipse.recommenders.internal.calls.rcp;

import org.eclipse.recommenders.completion.rcp.ICompletionContextFunction;
import org.eclipse.recommenders.completion.rcp.IRecommendersCompletionContext;

public class ReceiverUsageContextFunction implements ICompletionContextFunction<Object> {

    public static final String CCTX_RECEIVER_TYPE2 = "receiver-type2";
    public static final String CCTX_RECEIVER_DEF_BY = "receiver-def-by";
    public static final String CCTX_RECEIVER_CALLS = "receiver-calls";
    public static final String CCTX_RECEIVER_DEF_TYPE = "receiver-def-type";

    @Override
    public Object compute(IRecommendersCompletionContext context, String key) {
        AstCallCompletionAnalyzer a = new AstCallCompletionAnalyzer(context);
        context.set(CCTX_RECEIVER_TYPE2, a.getReceiverType().orNull());
        context.set(CCTX_RECEIVER_CALLS, a.getCalls());
        context.set(CCTX_RECEIVER_DEF_BY, a.getDefinedBy().orNull());
        context.set(CCTX_RECEIVER_DEF_TYPE, a.getReceiverDefinitionType());
        return context.get(key).orNull();
    }
}
