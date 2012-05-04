/**
 * Copyright (c) 2011 Doug Wightman, Zi Ye
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.eclipse.recommenders.snipmatch.web;

import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.recommenders.snipmatch.core.Effect;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Sends a request asynchronously to submit a new effect to the SnipMatch repository.
 */
class SubmitEffectThread extends PostThread {

    private final Effect effect;
    private final boolean isPublic;
    private final long waitTime;
    private final ISubmitEffectListener listener;

    public SubmitEffectThread(final RemoteMatchClient client, final Effect effect, final boolean isPublic,
            final long waitTime, final ISubmitEffectListener listener) {

        super(client, RemoteMatchClient.SUBMIT_URL);
        this.effect = effect;
        this.isPublic = isPublic;
        this.waitTime = waitTime;
        this.listener = listener;
    }

    @Override
    public void run() {

        if (!client.isLoggedIn()) {
            listener.submitEffectFailed("User not authenticated.");
            done = true;
            return;
        }

        try {
            sleep(waitTime);
        } catch (final Exception e) {
            e.printStackTrace();
            listener.submitEffectFailed("Client thread error.");
            done = true;
            return;
        }

        final String effectString = buildEffectString();
        // System.out.println(effectString);

        if (effectString == null) {
            listener.submitEffectFailed("Client XML error.");
            done = true;
            return;
        }

        addParameter("username", client.getUsername());
        addParameter("password", client.getPassword());
        addParameter("clientName", client.getName());
        addParameter("clientVersion", client.getVersion());
        addParameter("effectXML", effectString);
        addParameter("public", isPublic ? "true" : "false");

        if (done) {
            return;
        }

        done = true;
    }

    private String buildEffectString() {

        DocumentBuilderFactory dbf;
        DocumentBuilder db;
        Document doc;

        try {
            dbf = DocumentBuilderFactory.newInstance();
            db = dbf.newDocumentBuilder();
            doc = db.newDocument();
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }

        final Element effectXml = MatchConverter.writeEffect(doc, effect);
        doc.appendChild(effectXml);

        final StringWriter sw = new StringWriter();

        try {
            final Transformer tr = TransformerFactory.newInstance().newTransformer();
            tr.transform(new DOMSource(doc), new StreamResult(sw));
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }

        return sw.toString();
    }
}
