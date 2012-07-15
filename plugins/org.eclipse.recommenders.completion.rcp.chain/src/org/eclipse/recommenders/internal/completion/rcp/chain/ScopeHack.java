/**
 * Copyright (c) 2011 Stefan Henss.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Stefan Henß - initial API and implementation.
 */
package org.eclipse.recommenders.internal.completion.rcp.chain;

import java.lang.reflect.Field;

import org.eclipse.jdt.internal.codeassist.InternalCompletionContext;
import org.eclipse.jdt.internal.codeassist.InternalExtendedCompletionContext;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.recommenders.completion.rcp.IRecommendersCompletionContext;
import org.eclipse.recommenders.utils.annotations.Provisional;

/**
 * A scope is required to determine for methods and fields if they are visible from the invocation site.
 */
@Provisional
public final class ScopeHack {

    private static Field extendedContextField;
    private static Field assistScopeField;

    private ScopeHack() {
    }

    static {
        try {
            extendedContextField = InternalCompletionContext.class.getDeclaredField("extendedContext");
            extendedContextField.setAccessible(true);
            assistScopeField = InternalExtendedCompletionContext.class.getDeclaredField("assistScope");
            assistScopeField.setAccessible(true);
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }

    static Scope resolveScope(final IRecommendersCompletionContext ctx) {
        final InternalCompletionContext context = (InternalCompletionContext) ctx.getJavaContext().getCoreContext();
        try {
            return (Scope) assistScopeField.get(extendedContextField.get(context));
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
