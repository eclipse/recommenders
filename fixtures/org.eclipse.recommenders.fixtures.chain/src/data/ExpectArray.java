/**
 * Copyright (c) 2011 Stefan Henss.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 */
package data;

import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class ExpectArray {

    // TODO: Make sure to only return arrays when an array is requested.
    public void method(){
        final IWorkbenchWindow[] w = PlatformUI.<^Space|getWorkbenchWindows.*>
    }
}
