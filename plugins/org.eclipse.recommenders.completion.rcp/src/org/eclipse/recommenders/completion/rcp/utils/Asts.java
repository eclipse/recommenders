/**
 * Copyright (c) 2015 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 */
package org.eclipse.recommenders.completion.rcp.utils;

import java.lang.reflect.Field;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.internal.ui.javaeditor.ASTProvider;
import org.eclipse.recommenders.internal.completion.rcp.LogMessages;
import org.eclipse.recommenders.utils.Logs;
import org.eclipse.recommenders.utils.Reflections;

@SuppressWarnings("restriction")
public class Asts {
    private static final Field SHARED_AST_LEVEL = Reflections.getDeclaredField(ASTProvider.class, "SHARED_AST_LEVEL")
            .orNull();

    public static int getSharedAstLevel() {
        try {
            return (Integer) SHARED_AST_LEVEL.get(null);
        } catch (Exception e) {
            Logs.log(LogMessages.LOG_WARNING_LINKAGE_ERROR, e);
            // Not really a good default. But with automated error reporting, we'll notice that error early enough
            return AST.JLS8;
        }
    }
}
