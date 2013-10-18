/**
 * Copyright (c) 2010, 2013 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 */
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
