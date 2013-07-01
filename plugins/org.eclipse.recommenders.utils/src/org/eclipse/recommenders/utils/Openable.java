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
package org.eclipse.recommenders.utils;

import java.io.IOException;

/**
 * An {@code Openable} is a resource of data that needs to be opened before it can be accessed. The open method is
 * invoked to perform all necessary io operations that brings this resource in a valid initial state.
 * <p>
 * Note that the constructor of {@code Openable} objects should not perform any IO operation or object initialization.
 */
public interface Openable {
    /**
     * Opens the resource. If an resource is already opened then invoking this method should have no effect.
     * 
     * @throws IOException
     *             if an I/O error occurs
     */
    void open() throws IOException;
}
