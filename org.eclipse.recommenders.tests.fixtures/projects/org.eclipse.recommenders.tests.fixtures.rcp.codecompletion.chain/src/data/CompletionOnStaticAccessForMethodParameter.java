/**
 * Copyright (c) 2010 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andreas Kaluza, Marko Martin, Marcel Bruch - chain completion test scenario definitions 
 */
package data;

import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;

public class CompletionOnStaticAccessForMethodParameter {

    int bla = callMe(PlatformUI.<@Ignore^Space>);
    /* calling context --> PlatformUI
     * expected type --> IWorkbenchHelpSystem
     * variable name --> XXX here we need a convention!
     */ 
   
    
    public CompletionOnStaticAccessForMethodParameter()  {
        final int bla = callMe(PlatformUI.<@Ignore^Space>);
        /* calling context --> PlatformUI
         * expected type --> IWorkbenchHelpSystem
         * variable name --> XXX here we need a convention!
         */ 
    }
    
    
    public int callMe(final IWorkbenchHelpSystem fillMe){
        return 0;
    }
    
    
    public void method () {
        final int i = callMe(PlatformUI.<@Ignore^Space>);
        /* calling context --> PlatformUI
         * expected type --> IWorkbenchHelpSystem
         * variable name --> XXX here we need a convention!
         */
        
    }
    
    
    
}
