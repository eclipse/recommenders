/**
 * Copyright (c) 2011 Doug Wightman, Zi Ye
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.eclipse.recommenders.snipmatch.web;

import org.eclipse.recommenders.snipmatch.core.ArgumentMatchNode;
import org.eclipse.recommenders.snipmatch.core.Effect;
import org.eclipse.recommenders.snipmatch.core.EffectMatchNode;
import org.eclipse.recommenders.snipmatch.core.EffectParameter;
import org.eclipse.recommenders.snipmatch.core.MatchNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Contains static methods for parsing and writing effects and matches to and from XML.
 */
public class MatchConverter {

    public static Effect parseEffect(final Element effectXml) {

        final Effect effect = new Effect();

        effect.setEnvironmentName(effectXml.getAttribute("env"));
        effect.setId(effectXml.getAttribute("id"));

        String fullType = effectXml.getAttribute("type").replace('{', '<').replace('}', '>');
        int colon = fullType.indexOf(":");

        if (colon != -1) {
            effect.setMajorType(fullType.substring(0, colon));
            effect.setMinorType(fullType.substring(colon + 1));
        } else {
            effect.setMajorType(fullType);
        }

        final Node sumNode = effectXml.getElementsByTagName("summary").item(0);
        effect.setSummary(sumNode.getTextContent());

        final Node codeNode = effectXml.getElementsByTagName("code").item(0);
        effect.setCode(codeNode.getTextContent());

        final Node paramsNode = effectXml.getElementsByTagName("params").item(0);
        final NodeList paramNodes = ((Element) paramsNode).getElementsByTagName("param");

        for (int i = 0; i < paramNodes.getLength(); i++) {

            final Element paramNode = (Element) paramNodes.item(i);
            final EffectParameter param = new EffectParameter();
            param.setName(paramNode.getAttribute("name"));

            fullType = paramNode.getAttribute("type").replace('{', '<').replace('}', '>');
            colon = fullType.indexOf(":");

            if (colon != -1) {
                param.setMajorType(fullType.substring(0, colon));
                param.setMinorType(fullType.substring(colon + 1));
            } else {
                effect.setMajorType(fullType);
            }

            effect.addParameter(param);
        }

        final Node patsNode = effectXml.getElementsByTagName("patterns").item(0);
        final NodeList patNodes = ((Element) patsNode).getElementsByTagName("pattern");

        for (int i = 0; i < patNodes.getLength(); i++) {

            final Element patNode = (Element) patNodes.item(i);
            effect.addPattern(patNode.getTextContent());
        }

        return effect;
    }

    public static MatchNode parseMatchNode(final Element matchXml, final Effect[] effects, final EffectParameter param) {

        if (matchXml.hasAttribute("effect")) {

            final NodeList childNodes = matchXml.getChildNodes();
            final MatchNode[] children = new MatchNode[childNodes.getLength()];

            final String pattern = matchXml.getAttribute("pattern");
            final int whichEffect = Integer.parseInt(matchXml.getAttribute("effect"));

            for (int i = 0; i < childNodes.getLength(); i++) {

                children[i] = parseMatchNode((Element) childNodes.item(i), effects,
                        effects[whichEffect].getParameter(i));
            }

            final EffectMatchNode effectNode = new EffectMatchNode(effects[whichEffect], pattern, children);

            return effectNode;
        } else {
            return new ArgumentMatchNode(matchXml.getTextContent(), param);
        }
    }

    public static Element writeEffect(final Document document, final Effect effect) {

        final Element rootNode = document.createElement("effect");

        rootNode.setAttribute("type", effect.getFullType().replace('<', '{').replace('>', '}'));
        rootNode.setAttribute("env", effect.getEnvironmentName());

        final Element summaryNode = document.createElement("summary");
        rootNode.appendChild(summaryNode);
        summaryNode.setTextContent(effect.getSummary());

        final Element patternsNode = document.createElement("patterns");
        rootNode.appendChild(patternsNode);

        for (final String pattern : effect.getPatterns()) {

            final Element patternNode = document.createElement("pattern");
            patternsNode.appendChild(patternNode);
            patternNode.setTextContent(pattern);
        }

        final Element paramsNode = document.createElement("params");
        rootNode.appendChild(paramsNode);

        for (final EffectParameter param : effect.getParameters()) {

            final Element paramNode = document.createElement("param");
            paramsNode.appendChild(paramNode);
            paramNode.setAttribute("name", param.getName());
            paramNode.setAttribute("type", param.getFullType().replace('<', '{').replace('>', '}'));
        }

        final Element codeNode = document.createElement("code");
        rootNode.appendChild(codeNode);
        codeNode.setTextContent(effect.getCode());

        return rootNode;
    }

    public static Element writeMatchNode(final Document document, final MatchNode match) {

        final Element rootNode = document.createElement("node");

        if (match instanceof ArgumentMatchNode) {
            rootNode.setTextContent(((ArgumentMatchNode) match).getArgument());
        } else {

            final EffectMatchNode effectMatch = (EffectMatchNode) match;
            rootNode.setAttribute("effect", effectMatch.getEffect().getId());
            rootNode.setAttribute("pattern", effectMatch.getPattern());
            rootNode.setAttribute("type", "" + effectMatch.getMatchType());

            for (final MatchNode childNode : effectMatch.getChildren()) {
                rootNode.appendChild(writeMatchNode(document, childNode));
            }
        }

        return rootNode;
    }
}
