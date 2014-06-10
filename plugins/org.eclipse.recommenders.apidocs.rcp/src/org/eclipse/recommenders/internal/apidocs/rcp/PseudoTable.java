/**
 * Copyright (c) 2010, 2014 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Johannes Dorn - initial API and implementation
 */
package org.eclipse.recommenders.internal.apidocs.rcp;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

/**
 * This ignores setBackground() calls, so we are sure to be the only ones changing the background color.
 *
 * TODO delete me once Bug 434942 is fixed
 *
 * @see <a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=434942">Bug 434942</a>
 */
public class PseudoTable extends Table {

    public PseudoTable(Composite parent, int style) {
        super(parent, style);
    }

    @Override
    public void setBackground(Color color) {
        // ignore setBackground() calls
    }

    public void setBackgroundInternal(Color color) {
        super.setBackground(color);
    }

    @Override
    protected void checkSubclass() {
        // We know Table shouldn't be subclassed, but we do need to for this simple workaround.
    }

}
