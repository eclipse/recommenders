/**
 * Copyright (c) 2017 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.internal.statics.rcp;

import java.util.List;

import org.eclipse.recommenders.utils.Recommendation;
import org.eclipse.recommenders.utils.names.IMethodName;
import org.eclipse.recommenders.utils.names.ITypeName;

import com.google.common.annotations.Beta;

/**
 * A thin layer around a Bayesian network designed for recommending static method calls.
 */
@Beta
public interface IStaticModel {

    /**
     * Returns the type this net makes recommendations for.
     */
    ITypeName getReceiverType();

    /**
     * Clears all observations and puts the network in its initial state.
     */
    void reset();

    /**
     * Sets the (name of) the enclosing method, including parameter and return types.
     *
     * @return returns true if this context was known - for an imprecise definition of "known"
     */
    boolean setEnclosingMethod(IMethodName context);

    /**
     * Returns a list of recommended static method calls.
     */
    List<Recommendation<IMethodName>> recommendCalls();

    double recommendCall(IMethodName method);

}
