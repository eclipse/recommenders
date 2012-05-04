/**
 * Copyright (c) 2011 Doug Wightman, Zi Ye, Cheng Chen
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.eclipse.recommenders.snipmatch.local;

import org.eclipse.recommenders.snipmatch.core.Effect;
import org.eclipse.recommenders.snipmatch.core.MatchEnvironment;
import org.eclipse.recommenders.snipmatch.core.MatchNode;
import org.eclipse.recommenders.snipmatch.search.SearchClient;
import org.eclipse.recommenders.snipmatch.web.IDeleteEffectListener;
import org.eclipse.recommenders.snipmatch.web.ILoadProfileListener;
import org.eclipse.recommenders.snipmatch.web.ILoginListener;
import org.eclipse.recommenders.snipmatch.web.ISearchListener;
import org.eclipse.recommenders.snipmatch.web.ISendFeedbackListener;
import org.eclipse.recommenders.snipmatch.web.ISubmitEffectListener;

/**
 * This class is used to search local snippets store.
 */
public final class LocalMatchClient implements SearchClient {
    private String name;
    private String version;
    private String username;
    private String password;
    private LocalSearchThread workThread;

    private static LocalMatchClient instance = new LocalMatchClient();

    private LocalMatchClient() {

    }

    public static LocalMatchClient getInstance() {
        return instance;
    }

    @Override
    public void startSearch(final String query, final MatchEnvironment env, final ISearchListener listener) {
        workThread = new LocalSearchThread(this, env, query, listener);
        workThread.start();
    }

    @Override
    public void cancelWork() {

        if (isWorking()) {
            workThread.cancel();
        }
    }

    @Override
    public boolean isWorking() {

        return (workThread != null) && !workThread.isDone();
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    @Override
    public void startSendFeedback(final String query, final MatchNode result, final String comment, final int rating,
            final boolean flag, final boolean isLog, final boolean isStartup, final long clientId, final boolean used,
            final ISendFeedbackListener listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isLoggedIn() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void logout() {
        // TODO Auto-generated method stub

    }

    @Override
    public void startProcessing() {
        // TODO Auto-generated method stub

    }

    @Override
    public void stopProcessing() {
        // TODO Auto-generated method stub

    }

    @Override
    public void setServerProcessingTime(final float serverProcessingTime) {
        // TODO Auto-generated method stub

    }

    @Override
    public float getServerProcessingTime() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public float getProcessingTime() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void startLogin(final String username, final String password, final ILoginListener listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public void startDeleteEffect(final Effect effect, final IDeleteEffectListener listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public void startSubmitEffect(final Effect effect, final boolean isPublic, final ISubmitEffectListener listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public void startLoadProfile(final ILoadProfileListener listener) {
        // TODO Auto-generated method stub

    }
}
