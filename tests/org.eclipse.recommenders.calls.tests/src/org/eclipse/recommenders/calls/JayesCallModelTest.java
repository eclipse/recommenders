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
package org.eclipse.recommenders.calls;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.eclipse.recommenders.calls.ICallModel.DefinitionKind;
import org.eclipse.recommenders.jayes.BayesNet;
import org.eclipse.recommenders.jayes.BayesNode;
import org.eclipse.recommenders.utils.Constants;
import org.eclipse.recommenders.utils.Recommendation;
import org.eclipse.recommenders.utils.Recommendations;
import org.eclipse.recommenders.utils.names.IMethodName;
import org.eclipse.recommenders.utils.names.VmMethodName;
import org.eclipse.recommenders.utils.names.VmTypeName;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import com.google.common.collect.Sets;

public class JayesCallModelTest {

    @Test
    public void testPrediction() {
        VmMethodName valueOf = VmMethodName.get("Ljava/lang/String.valueOf(Ljava/lang/String;)Ljava/lang/String;");
        VmMethodName toString = VmMethodName.get("Ljava/lang/String.toString()Ljava/lang/String;");

        BayesNet net = new BayesNet();
        BayesNode callgroupNode = net.createNode(Constants.N_NODEID_CALL_GROUPS);
        callgroupNode.addOutcomes("group1", "group2");
        callgroupNode.setProbabilities(0.5, 0.5);

        BayesNode defkindNode = net.createNode(Constants.N_NODEID_DEF_KIND);
        defkindNode.addOutcomes(DefinitionKind.NEW.name(), DefinitionKind.RETURN.name());
        defkindNode.setParents(Arrays.asList(callgroupNode));
        defkindNode.setProbabilities(1.0, 0.0, 0.0, 1.0);

        BayesNode contextNode = net.createNode(Constants.N_NODEID_CONTEXT);
        contextNode.addOutcomes(VmMethodName.NULL.toString(), valueOf.toString());
        contextNode.setParents(Arrays.asList(callgroupNode));
        contextNode.setProbabilities(0.5, 0.5, 0.5, 0.5);

        BayesNode defNode = net.createNode(Constants.N_NODEID_DEF);
        defNode.setParents(Arrays.asList(callgroupNode));
        defNode.addOutcomes(VmMethodName.NULL.toString(), valueOf.toString());
        defNode.setProbabilities(0.5, 0.5, 0.5, 0.5);

        BayesNode valueOfNode = net.createNode(valueOf.toString());
        valueOfNode.addOutcomes(Constants.N_STATE_TRUE, Constants.N_STATE_FALSE);
        valueOfNode.setParents(Arrays.asList(callgroupNode));
        valueOfNode.setProbabilities(0.9, 0.1, 0.1, 0.9);

        BayesNode toStringNode = net.createNode(toString.toString());
        toStringNode.addOutcomes(Constants.N_STATE_TRUE, Constants.N_STATE_FALSE);
        toStringNode.setParents(Arrays.asList(callgroupNode));
        toStringNode.setProbabilities(0.1, 0.9, 0.9, 0.1);

        JayesCallModel model = new JayesCallModel(VmTypeName.STRING, net);
        model.setObservedCalls(Sets.<IMethodName>newHashSet(valueOf));

        assertThat(getTopPatterns(model).get(0).getProposal(), is("group1"));
        assertThat(getTopPatterns(model).get(1).getProposal(), is("group2"));

        assertThat(Recommendations.top(model.recommendCalls(), 1).get(0).getProposal(),
                CoreMatchers.<IMethodName>is(toString));

        model.reset();
        model.setObservedCalls(Sets.<IMethodName>newHashSet(toString));
        model.setObservedDefinitionKind(DefinitionKind.STRING_LITERAL);

        assertThat(getTopPatterns(model).get(0).getProposal(), is("group2"));
        assertThat(getTopPatterns(model).get(1).getProposal(), is("group1"));

        assertThat(Recommendations.top(model.recommendCalls(), 1).get(0).getProposal(),
                CoreMatchers.<IMethodName>is(valueOf));

    }

    private List<Recommendation<String>> getTopPatterns(JayesCallModel model) {
        return Recommendations.top(model.recommendPatterns(), 2);
    }

}
