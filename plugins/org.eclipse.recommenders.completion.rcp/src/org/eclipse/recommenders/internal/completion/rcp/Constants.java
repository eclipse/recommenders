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
package org.eclipse.recommenders.internal.completion.rcp;

import org.eclipse.recommenders.completion.rcp.CompletionRcpPreferences;
import org.eclipse.recommenders.completion.rcp.processable.SessionProcessorDescriptor;

public class Constants {

    /**
     * Class property name.
     * 
     * @see CompletionRcpPreferences#getEnabled()
     */
    public static final String P_ENABLED = "enabled";
    /**
     * Class property name.
     * 
     * @see CompletionRcpPreferences#getProcessors()
     */
    public static final String P_PROCESSORS = "processors";
    /**
     * Class property name
     * 
     * @see SessionProcessorDescriptor#getName()
     */
    public static final String P_NAME = "name";

    public static final String BUNDLE_ID = "org.eclipse.recommenders.completion.rcp";
    public static final String JDT_ALL_CATEGORY = "org.eclipse.jdt.ui.javaAllProposalCategory";
    public static final String MYLYN_ALL_CATEGORY = "org.eclipse.mylyn.java.ui.javaAllProposalCategory";
    public static final String RECOMMENDERS_ALL_CATEGORY_ID = "org.eclipse.recommenders.completion.category.all";

}
