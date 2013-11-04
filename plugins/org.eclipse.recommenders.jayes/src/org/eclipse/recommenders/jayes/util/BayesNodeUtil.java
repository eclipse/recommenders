package org.eclipse.recommenders.jayes.util;

import java.util.Map;

import org.eclipse.recommenders.jayes.BayesNode;
import org.eclipse.recommenders.jayes.factor.AbstractFactor;

public class BayesNodeUtil {

    private final static double TOLERANCE = 1e-9;

    public static boolean isNormalized(BayesNode node) {
        return isNormalized(node.getFactor());
    }

    private static boolean isNormalized(AbstractFactor factor) {
        int lowestDimension = factor.getDimensions()[factor.getDimensions().length - 1];
        int numCpds = MathUtils.productOfRange(factor.getDimensions(), 0, factor.getDimensions().length - 1);
        for (int i = 0; i < numCpds; i++) {
            double sum = 0;
            for (int j = 0; j < lowestDimension; j++) {
                // using getValue(int) allows the usage with SparseFactors
                sum += factor.getValue(i * lowestDimension + j);
            }
            if (Math.abs(1 - sum) > TOLERANCE) {
                return false;
            }
        }
        return true;
    }

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
