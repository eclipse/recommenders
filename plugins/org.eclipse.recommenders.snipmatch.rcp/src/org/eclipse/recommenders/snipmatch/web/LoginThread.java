/**
 * Copyright (c) 2011 Doug Wightman, Zi Ye
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.eclipse.recommenders.snipmatch.web;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

/**
 * Sends a login request asynchronously and uses listeners to notify caller about the response.
 */
class LoginThread extends PostThread {

    private final String username;
    private final String password;
    private final ArrayList<ILoginListener> listeners;

    public LoginThread(final MatchClient client, final String username, final String password) {

        super(client, MatchClient.LOGIN_URL);
        this.username = username;
        this.password = password;
        this.listeners = new ArrayList<ILoginListener>();
    }

    public void addListener(final ILoginListener listener) {

        listeners.add(listener);
    }

    public void removeListener(final ILoginListener listener) {

        listeners.remove(listener);
    }

    public void clearListeners() {

        listeners.clear();
    }

    @Override
    public void run() {

        addParameter("username", username);
        addParameter("password", password);
        addParameter("clientName", client.getName());
        addParameter("clientVersion", client.getVersion());

        final InputStream response = post();

        if (response == null) {

            for (final ILoginListener listener : listeners) {
                listener.loginFailed("Connection error.");
            }

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

            for (final ILoginListener listener : listeners) {
                listener.loginFailed("Bad response format.");
            }

            done = true;
            return;
        }

        final String msg = responseXml.getDocumentElement().getTextContent();

        if (msg.equals("User authenticated.")) {

            for (final ILoginListener listener : listeners) {
                listener.loginSucceeded();
            }
        } else {

            for (final ILoginListener listener : listeners) {
                listener.loginFailed(msg);
            }
        }

        done = true;
    }
}
