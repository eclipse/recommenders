/**
 * Copyright (c) 2010, 2014 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 */
package org.eclipse.recommenders.stacktraces;

public class StackTraceElementDto {

    public static StackTraceElementDto from(StackTraceElement e) {
        StackTraceElementDto res = new StackTraceElementDto();
        res.classname = e.getClassName();
        res.methodname = e.getMethodName();
        res.line = e.getLineNumber();
        res.isNative = e.isNativeMethod();
        return res;
    }

    public String classname;
    public String methodname;
    public int line;
    public boolean isNative;

    @Override
    public String toString() {
        return classname + "." + methodname + " (line: " + line + ")";
    }
}
