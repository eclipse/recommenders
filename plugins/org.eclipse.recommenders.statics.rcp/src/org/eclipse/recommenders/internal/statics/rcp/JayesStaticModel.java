/**
 * Copyright (c) 2017 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.internal.statics.rcp;

import static org.eclipse.recommenders.utils.Constants.N_STATE_TRUE;
import static org.eclipse.recommenders.utils.Recommendation.newRecommendation;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.recommenders.commons.bayesnet.BayesianNetwork;
import org.eclipse.recommenders.jayes.BayesNet;
import org.eclipse.recommenders.jayes.BayesNode;
import org.eclipse.recommenders.jayes.inference.jtree.JunctionTreeAlgorithm;
import org.eclipse.recommenders.jayes.inference.jtree.JunctionTreeBuilder;
import org.eclipse.recommenders.jayes.io.IBayesNetReader;
import org.eclipse.recommenders.jayes.io.jbif.JayesBifReader;
import org.eclipse.recommenders.jayes.util.triangulation.MinDegree;
import org.eclipse.recommenders.utils.Constants;
import org.eclipse.recommenders.utils.IOUtils;
import org.eclipse.recommenders.utils.Recommendation;
import org.eclipse.recommenders.utils.names.IFieldName;
import org.eclipse.recommenders.utils.names.IMethodName;
import org.eclipse.recommenders.utils.names.ITypeName;
import org.eclipse.recommenders.utils.names.VmMethodName;

import com.google.common.annotations.Beta;

/**
 * A thin wrapper around a {@link BayesianNetwork} for recommending method calls.
 * <p>
 * The Bayesian network is expected to follow the structure specified below:
 * <ul>
 * <li>every node must have at least <b>2 states</b>!
 * <li>the first state is supposed to be a dummy state. Call it like {@link Constants#DUMMY_METHOD}
 * <li>the second state <b>may</b> to be a dummy state too if no valuable other state could be found.
 * </ul>
 * <ul>
 * <li><b>callgroup node (formerly called pattern node):</b>
 * <ul>
 * <li>node name: {@link Constants#N_NODEID_CALL_GROUPS}
 * <li>state names: no constraints. Recommended schema is to use 'p'#someNumber.
 * </ul>
 * <li><b>context node:</b>
 * <ul>
 * <li>node name: {@link Constants#N_NODEID_CONTEXT}
 * <li>state names: fully-qualified method names as returned by {@link IMethodName#getIdentifier()}.
 * </ul>
 * <li><b>definition node:</b>
 * <ul>
 * <li>node name: {@link Constants#N_NODEID_DEF}
 * <li>state names: fully-qualified names as returned by {@link IMethodName#getIdentifier()} or
 * {@link IFieldName#getIdentifier()}.
 * </ul>
 * <li><b>definition kind node:</b>
 * <ul>
 * <li>node name: {@link Constants#N_NODEID_DEF_KIND}
 * <li>state names: one of {@link DefinitionKind}, i.e., METHOD_RETURN, NEW, FIELD, PARAMETER, THIS, UNKNOWN, or ANY
 * </ul>
 * <li><b>method call node:</b>
 * <ul>
 * <li>node name: {@link IMethodName#getIdentifier()}
 * <li>state names: {@link Constants#N_STATE_TRUE} or {@link Constants#N_STATE_FALSE}
 * </ul>
 * </ul>
 */
@SuppressWarnings("deprecation")
@Beta
public class JayesStaticModel implements IStaticModel {

    private static final String VERBS_NODE_NAME = "method-verbs";
    private static final String VERB_OUTCOME_PRIOR = "#prior";

    public static IStaticModel load(InputStream is, ITypeName type) throws IOException {
        BayesNet net = getModel(is, type);
        return new JayesStaticModel(type, net);
    }

    private static BayesNet getModel(InputStream is, ITypeName type) throws IOException {
        IBayesNetReader rdr = new JayesBifReader(is);
        try {
            return rdr.read();
        } finally {
            IOUtils.closeQuietly(rdr);
        }
    }

    private final BayesNet net;
    private final BayesNode verbNode;
    private final JunctionTreeAlgorithm junctionTree;

    private final ITypeName typeName;

    public JayesStaticModel(final ITypeName name, final BayesNet net) {
        this.net = net;
        this.typeName = name;
        this.junctionTree = new JunctionTreeAlgorithm();

        junctionTree.setJunctionTreeBuilder(JunctionTreeBuilder.forHeuristic(new MinDegree()));
        junctionTree.setNetwork(net);

        verbNode = net.getNode(VERBS_NODE_NAME);
    }

    @Override
    public boolean setEnclosingMethod(IMethodName context) {
        if (context == null) {
            junctionTree.addEvidence(verbNode, VERB_OUTCOME_PRIOR);
            return false;
        }
        String verb = MethodNameUtils.extractVerb(context.getName()).orNull();
        if (verb == null) {
            junctionTree.addEvidence(verbNode, VERB_OUTCOME_PRIOR);
            return false;
        } else if (verbNode.getOutcomes().contains(verb)) {
            junctionTree.addEvidence(verbNode, verb);
            return true;
        } else {
            junctionTree.addEvidence(verbNode, VERB_OUTCOME_PRIOR);
            return false;
        }
    }

    @Override
    public List<Recommendation<IMethodName>> recommendCalls() {
        List<Recommendation<IMethodName>> recs = new LinkedList<>();
        Map<BayesNode, String> evidence = junctionTree.getEvidence();
        for (BayesNode node : net.getNodes()) {
            if (node == verbNode) {
                continue;
            }
            boolean isAlreadyUsedAsEvidence = evidence.containsKey(node);
            if (!isAlreadyUsedAsEvidence) {
                int indexForTrue = node.getOutcomeIndex(N_STATE_TRUE);
                double[] probabilities = junctionTree.getBeliefs(node);
                double probability = probabilities[indexForTrue];
                IMethodName method = VmMethodName.get(node.getName());
                recs.add(newRecommendation(method, probability));
            }
        }
        return recs;
    }

    @Override
    public ITypeName getReceiverType() {
        return typeName;
    }

    @Override
    public void reset() {
        junctionTree.getEvidence().clear();
    }

    @Override
    public double recommendCall(IMethodName method) {
        BayesNode node = net.getNode(method.getIdentifier());
        if (node == null) {
            return 0;
        }
        int indexForTrue = node.getOutcomeIndex(N_STATE_TRUE);
        double[] probabilities = junctionTree.getBeliefs(node);
        double res = probabilities[indexForTrue];
        return res;
    }

}
