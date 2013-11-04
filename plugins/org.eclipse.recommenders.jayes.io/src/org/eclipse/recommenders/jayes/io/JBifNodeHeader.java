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

    DEFAULT((byte) 0x00), FREQUENCIES((byte) 0x01), REDUCED((byte) 0x02);

    private byte flag;

    private JBifNodeHeader(byte flag) {
        this.flag = flag;
    }

    public byte getFlag() {
        return flag;
    }

    public static JBifNodeHeader valueOf(byte flag) {
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
