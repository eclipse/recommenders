/**
 * Copyright (c) 2011 Doug Wightman, Zi Ye
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.eclipse.recommenders.snipmatch.core;

/**
 * Represents a snippet node that is a formula. A formula has a name, arguments, and evaluates to text. Optionally, the
 * formula can also specify the name of a new variable to store the formula's evaluated result for future use.
 */
public class FormulaSnippetNode implements ISnippetNode {

    private final String name;
    private String[] args;
    private String newVarName;
    private final Effect effect;

    public FormulaSnippetNode(final String name, final String[] args, final Effect effect) {

        this.name = name;
        this.args = args;
        this.effect = effect;

        if (args == null) {
            this.args = new String[] {};
        }
    }

    public String getName() {

        return name;
    }

    public String[] getArguments() {

        return args;
    }

    public String getArgument(final int index) {

        return args[index];
    }

    public int numArguments() {

        return args.length;
    }

    public void setNewVariableName(final String newVarName) {

        this.newVarName = newVarName;
    }

    public String getNewVariableName() {

        return newVarName;
    }

    @Override
    public Effect getEffect() {

        return effect;
    }
}
