/**
 * Copyright (c) 2011 Michael Kutschke.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Michael Kutschke - initial API and implementation.
 */
package org.eclipse.recommenders.jayes.inference;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.recommenders.jayes.BayesNet;
import org.eclipse.recommenders.jayes.BayesNode;
import org.eclipse.recommenders.jayes.factor.AbstractFactor;
import org.eclipse.recommenders.jayes.factor.DenseFactor;
import org.eclipse.recommenders.jayes.factor.FactorFactory;
import org.eclipse.recommenders.jayes.factor.arraywrapper.DoubleArrayWrapper;

public abstract class AbstractInferrer implements IBayesInferer {

    protected Map<BayesNode, String> evidence = new HashMap<BayesNode, String>();
    protected Map<BayesNode, AbstractFactor> virtualEvidence = new HashMap<BayesNode, AbstractFactor>();

    protected double[][] beliefs;
    protected boolean beliefsValid;

    protected FactorFactory factory = FactorFactory.defaultFactory();

    public void setFactorFactory(FactorFactory factory) {
        this.factory = factory;
    }

    public FactorFactory getFactory() {
        return factory;
    }

    @Override
    public void addEvidence(final BayesNode node, final String outcome) {
        evidence.put(node, outcome);
        beliefsValid = false;
    }

    public void removeEvidence(BayesNode node) {
        evidence.remove(node);
        beliefsValid = false;
    }

    @Override
    public double[] getBeliefs(final BayesNode node) {
        if (!beliefsValid) {
            beliefsValid = true;
            updateBeliefs();
        }
        return beliefs[node.getId()];
    }

    @Override
    public void setNetwork(final BayesNet bayesNet) {
        beliefs = new double[bayesNet.getNodes().size()][];
        for (final BayesNode n : bayesNet.getNodes()) {
            beliefs[n.getId()] = new double[n.getOutcomeCount()];
        }
        this.factory.setReferenceNetwork(bayesNet);
    }

    @Override
    public void setEvidence(final Map<BayesNode, String> evidence) {
        this.evidence = evidence;
        beliefsValid = false;
    }

    @Override
    public Map<BayesNode, String> getEvidence() {
        return evidence;
    }

    /**
     * although it is possible to set virtual evidence for multiple variables, it is advised to only do so with one var
     *
     * @param node
     * @param evidence
     */
    @Override
    public void addVirtualEvidence(BayesNode node, double[] evidence) {
        DenseFactor f = new DenseFactor();
        f.setDimensionIDs(node.getId());
        f.setDimensions(node.getOutcomeCount());
        f.setValues(new DoubleArrayWrapper(evidence));
        addVirtualEvidence(node, f);
    }

    @Override
    public void addVirtualEvidence(BayesNode node, AbstractFactor evidence) {
        if (evidence.getDimensionIDs().length != 1) {
            throw new IllegalArgumentException("Only virtual evidence over one variable is allowed");
        }
        if (evidence.getDimensionIDs()[0] != node.getId()) {
            throw new IllegalArgumentException("virtual evidence and node don't fit together");
        }
        virtualEvidence.put(node, evidence);
        beliefsValid = false;
    }

    @Override
    public void setVirtualEvidence(Map<BayesNode, double[]> virtualEvidence) {
        this.virtualEvidence.clear();
        for (Entry<BayesNode, double[]> entry : virtualEvidence.entrySet()) {
            addVirtualEvidence(entry.getKey(), entry.getValue());
        }

    }

    @Override
    public Map<BayesNode, AbstractFactor> getVirtualEvidence() {
        return virtualEvidence;
    }

    public void resetVirtualEvidence() {
        virtualEvidence.clear();
    }

    protected abstract void updateBeliefs();
}
