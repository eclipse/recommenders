/*******************************************************************************
 * Copyright (c) 2013 Michael Kutschke.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Michael Kutschke - initial API and implementation
 ******************************************************************************/
package org.eclipse.recommenders.jayes.io;

public enum JBifNodeHeader {

    DEFAULT(0x00), FREQUENCIES(0x01), REDUCED(0x02);

    private int flag;

    JBifNodeHeader(int flag) {
        this.flag = flag;
    }

    public int getFlag() {
        return flag;
    }

    public static JBifNodeHeader forFlag(int flag) {
        switch (flag) {
        case 0x00:
            return DEFAULT;
        case 0x01:
            return FREQUENCIES;
        case 0x02:
            return REDUCED;
        default:
            throw new IllegalArgumentException("illegal flag value");
        }
    }

}
