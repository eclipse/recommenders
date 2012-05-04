/**
 * Copyright (c) 2011 Doug Wightman, Zi Ye
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.eclipse.recommenders.snipmatch.web;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.recommenders.snipmatch.core.Effect;
import org.w3c.dom.Document;

/**
 * Sends a request to delete an effect from the SnipMatch repository.
 */
public class DeleteEffectThread extends PostThread {

    private final Effect effect;
    private final long waitTime;
    private final IDeleteEffectListener listener;

    public DeleteEffectThread(final MatchClient client, final Effect effect, final long waitTime,
            final IDeleteEffectListener listener) {

        super(client, MatchClient.DELETE_URL);
        this.effect = effect;
        this.waitTime = waitTime;
        this.listener = listener;
    }

    @Override
    public void run() {

        if (!client.isLoggedIn()) {
            listener.deleteEffectFailed("User not authenticated.");
            done = true;
            return;
        }

        try {
            sleep(waitTime);
        } catch (final Exception e) {
            e.printStackTrace();
            listener.deleteEffectFailed("Client thread error.");
            done = true;
            return;
        }

        addParameter("username", client.getUsername());
        addParameter("password", client.getPassword());
        addParameter("clientName", client.getName());
        addParameter("clientVersion", client.getVersion());
        addParameter("effectId", effect.getId());

        if (done) {
            return;
        }

        final InputStream response = post();

        if (response == null) {
            listener.deleteEffectFailed("Connection error.");
            done = true;
            return;
        }

        if (done) {
            return;
        }

        DocumentBuilderFactory dbf;
        DocumentBuilder db;
        Document responseXml;

        try {

            dbf = DocumentBuilderFactory.newInstance();
            db = dbf.newDocumentBuilder();
            responseXml = db.parse(response);
        } catch (final Exception e) {

            e.printStackTrace();

            listener.deleteEffectFailed("Bad response format.");

            done = true;
            return;
        }

        final String msg = responseXml.getDocumentElement().getTextContent();

        if (msg.equals("Deleted.")) {
            listener.deleteEffectSucceeded();
        } else {
            listener.deleteEffectFailed(msg);
        }

        done = true;
    }
}
