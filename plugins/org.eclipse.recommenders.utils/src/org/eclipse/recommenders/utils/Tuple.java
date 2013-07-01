/**
 * Copyright (c) 2010 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 */
package org.eclipse.recommenders.utils;

@Deprecated
public class Tuple<T0, T1> extends Pair<T0, T1> {

    public static <T0, S0 extends T0, T1, S1 extends T1> Tuple<T0, T1> newTuple(final S0 t0, final S1 t1) {
        return new Tuple<T0, T1>(t0, t1);
    }

    public static <T0, S0 extends T0, T1, S1 extends T1> Tuple<T0, T1> create(final S0 t0, final S1 t1) {
        return newTuple(t0, t1);
    }

    protected Tuple() {
        // Used for deserialization
    }

    protected Tuple(final T0 t0, final T1 t1) {
        super(t0, t1);
    }
}
