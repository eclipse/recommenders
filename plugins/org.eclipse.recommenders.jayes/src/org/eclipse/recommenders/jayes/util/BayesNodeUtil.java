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
package org.eclipse.recommenders.jayes.util;

import java.util.Map;

import org.eclipse.recommenders.jayes.BayesNode;

public class BayesNodeUtil {

    /**
     * marginalizes the conditional distribution with regard to the evidence. This only corresponds to any meaningful
     * probability distribution if all parents are assigned a probability. This method is mostly used for sampling
     * purposes.
     * 
     * @param node
     * @param evidence
     * @return
     */
    public static double[] marginalize(BayesNode node, Map<BayesNode, String> evidence) {
        for (final BayesNode p : node.getParents()) {
            if (evidence.containsKey(p)) {
                node.getFactor().select(p.getId(), p.getOutcomeIndex(evidence.get(p)));
            } else {
                node.getFactor().select(p.getId(), -1);
            }
        }
        final double[] result = MathUtils.normalize(node.getFactor().marginalizeAllBut(-1));
        node.getFactor().resetSelections();

        return result;
    }

    public static double sumDistribution(BayesNode node) {
        return MathUtils.sum(node.getFactor().getValues().toDoubleArray());
    }

}
