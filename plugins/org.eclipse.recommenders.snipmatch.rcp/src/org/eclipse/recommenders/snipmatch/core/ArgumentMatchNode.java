/**
 * Copyright (c) 2011 Doug Wightman, Zi Ye
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.eclipse.recommenders.snipmatch.core;

/**
 * A leaf match node that represents an effect argument.
 */
public class ArgumentMatchNode extends MatchNode {

    private String arg;
    private final EffectParameter param;

    public ArgumentMatchNode(final String arg, final EffectParameter param) {

        this.arg = arg;
        this.param = param;
    }

    @Override
    public MatchNode clone() {

        final ArgumentMatchNode clone = new ArgumentMatchNode(arg, param);
        clone.setMatchType(matchType);
        return clone;
    }

    public String getArgument() {

        return arg;
    }

    public void setArgument(final String arg) {

        this.arg = arg;
    }

    public EffectParameter getParameter() {

        return param;
    }

    @Override
    public boolean equals(final Object other) {

        if (!(other instanceof ArgumentMatchNode)) {
            return false;
        }
        final ArgumentMatchNode otherArgNode = (ArgumentMatchNode) other;

        return param.equals(otherArgNode.param) && arg.equals(otherArgNode.arg);
    }
}
