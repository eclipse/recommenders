/**
 * Copyright (c) 2011 Doug Wightman, Zi Ye
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.eclipse.recommenders.snipmatch.web;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.recommenders.snipmatch.core.Effect;
import org.eclipse.recommenders.snipmatch.core.MatchEnvironment;
import org.eclipse.recommenders.snipmatch.core.MatchNode;
import org.eclipse.recommenders.snipmatch.core.MatchType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Performs a search asynchronously and uses a listener to notify the caller of search results.
 */
class SearchThread extends PostThread {

    private final MatchEnvironment env;
    private final String query;
    private final long waitTime;
    private final ISearchListener listener;

    public SearchThread(final MatchClient client, final MatchEnvironment env, final String query, final long waitTime,
            final ISearchListener listener) {

        super(client, MatchClient.SEARCH_URL);
        this.env = env;
        this.query = query;
        this.waitTime = waitTime;
        this.listener = listener;
    }

    @Override
    public void run() {

        if (!client.isLoggedIn()) {
            listener.searchFailed("User not authenticated.");
            done = true;
            return;
        }

        try {
            sleep(waitTime);
        } catch (final Exception e) {
            e.printStackTrace();
            listener.searchFailed("Client thread error.");
            done = true;
            return;
        }

        if (done) {
            return;
        }

        final String searchString = buildSearchString();
        // System.out.println(searchString);

        if (searchString == null) {
            listener.searchFailed("Client XML error.");
            done = true;
            return;
        }

        addParameter("username", client.getUsername());
        addParameter("password", client.getPassword());
        addParameter("clientName", client.getName());
        addParameter("clientVersion", client.getVersion());
        addParameter("searchXML", searchString);

        if (done) {
            return;
        }

        final InputStream response = post();

        if (done) {
            return;
        }

        if (response == null) {
            listener.searchFailed("Connection error.");
            done = true;
            return;
        }

        if (done) {
            return;
        }

        client.startProcessing();

        final String error = parseResults(response);

        if (done) {

            client.stopProcessing();
            return;
        }

        if (error != null) {
            listener.searchFailed(error);
        } else {
            listener.searchSucceeded();
        }

        client.stopProcessing();
        done = true;
    }

    /**
     * Creates and returns the search XML string.
     * 
     * @return The search XML string.
     */
    private String buildSearchString() {

        DocumentBuilderFactory dbf;
        DocumentBuilder db;
        Document searchXml;

        try {
            dbf = DocumentBuilderFactory.newInstance();
            db = dbf.newDocumentBuilder();
            searchXml = db.newDocument();
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }

        // Include the match environment's name (e.g., "javasnippets").
        final Element rootNode = searchXml.createElement("search");
        searchXml.appendChild(rootNode);
        rootNode.setAttribute("env", env.getName());

        // Include the search query.
        final Element queryNode = searchXml.createElement("query");
        rootNode.appendChild(queryNode);
        queryNode.setTextContent(query);

        final Element interpsNode = searchXml.createElement("interps");
        rootNode.appendChild(interpsNode);

        // Get a hash map of query token interpretations from the match environment.
        final HashMap<String, String> interps = env.getQueryTokenInterpretations(query);

        /*
         * Add the interpretations to the search XML. Angled brackets are replaced with braces, and are changed back on
         * the server.
         */
        for (final String token : interps.keySet()) {

            final Element interpNode = searchXml.createElement("interp");
            interpsNode.appendChild(interpNode);
            interpNode.setTextContent(token);
            interpNode.setAttribute("type", interps.get(token).replace('<', '{').replace('>', '}'));
        }

        final StringWriter sw = new StringWriter();

        try {
            final Transformer tr = TransformerFactory.newInstance().newTransformer();
            tr.transform(new DOMSource(searchXml), new StreamResult(sw));
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }

        return sw.toString();
    }

    /**
     * Parses the search results and sends notifications.
     * 
     * @param input
     *            The response from the server.
     * @return An error string. Null if no error.
     */
    private String parseResults(final InputStream input) {

        DocumentBuilderFactory dbf;
        DocumentBuilder db;
        Document resultsXml;

        try {
            dbf = DocumentBuilderFactory.newInstance();
            db = dbf.newDocumentBuilder();
            resultsXml = db.parse(input);
        } catch (final Exception e) {
            e.printStackTrace();
            return "Bad response format.";
        }

        final Element rootElement = resultsXml.getDocumentElement();
        if (rootElement.getNodeName().equals("notification")) {
            return rootElement.getTextContent();
        }

        final NodeList effectsNodes = resultsXml.getElementsByTagName("effects");
        final Element effectsNode = (Element) effectsNodes.item(0);
        final NodeList effectNodes = effectsNode.getElementsByTagName("effect");

        final Effect[] effects = new Effect[effectNodes.getLength()];

        for (int i = 0; i < effectNodes.getLength(); i++) {

            if (done) {
                return null;
            }

            try {
                effects[i] = MatchConverter.parseEffect((Element) effectNodes.item(i));
            } catch (final Exception e) {
                e.printStackTrace();
                return "Bad response format.";
            }
        }

        final NodeList serverNodes = resultsXml.getElementsByTagName("server");
        final Element serverNode = (Element) serverNodes.item(0);
        client.setServerProcessingTime(Float.parseFloat(serverNode.getAttribute("searchTime")));

        final NodeList matchesNodes = resultsXml.getElementsByTagName("matches");
        final Element matchesNode = (Element) matchesNodes.item(0);
        final NodeList matchNodes = matchesNode.getElementsByTagName("match");

        for (int i = 0; i < matchNodes.getLength(); i++) {

            if (done) {
                return null;
            }

            final Element matchNode = (Element) matchNodes.item(i);
            final Element rootNodeNode = (Element) matchNode.getElementsByTagName("node").item(0);

            MatchNode match;

            try {
                match = MatchConverter.parseMatchNode(rootNodeNode, effects, null);
            } catch (final Exception e) {
                e.printStackTrace();
                return "Bad response format.";
            }

            if (matchNode.hasAttribute("type")) {
                match.setMatchType(MatchType.values()[Integer.parseInt(matchNode.getAttribute("type"))]);
            }

            if (done) {
                return null;
            }

            if (match != null) {
                listener.matchFound(match);
            }
        }

        return null;
    }
}
