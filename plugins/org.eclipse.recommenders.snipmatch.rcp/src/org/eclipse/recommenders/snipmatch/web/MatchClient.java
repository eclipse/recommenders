/**
 * Copyright (c) 2011 Doug Wightman, Zi Ye
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.eclipse.recommenders.snipmatch.web;

import org.eclipse.recommenders.snipmatch.core.Effect;
import org.eclipse.recommenders.snipmatch.core.MatchEnvironment;
import org.eclipse.recommenders.snipmatch.core.MatchNode;

/**
 * This class is used to communicate with the SnipMatch server.
 */
public final class MatchClient {

    /**
     * This listener saves the username and password upon successfully logging in.
     */
    private class InternalLoginListener implements ILoginListener {

        String attemptedUsername;
        String attemptedPassword;

        public InternalLoginListener(final String attemptedUsername, final String attemptedPassword) {

            this.attemptedUsername = attemptedUsername;
            this.attemptedPassword = attemptedPassword;
        }

        @Override
        public void loginFailed(final String error) {

            username = null;
            password = null;
        }

        @Override
        public void loginSucceeded() {

            username = attemptedUsername;
            password = attemptedPassword;
        }
    }

    public static final String HOST_URL = "http://snipmatch.com/";
    public static final String LOGIN_URL = HOST_URL + "Login.php";
    public static final String SEARCH_URL = HOST_URL + "Search.php";
    public static final String SUBMIT_URL = HOST_URL + "InsertEffect.php";
    public static final String FEEDBACK_URL = HOST_URL + "InsertFlag.php";
    public static final String PROFILE_URL = HOST_URL + "RetrieveUserEffects.php";
    public static final String DELETE_URL = HOST_URL + "DeletePattern.php";
    public static final long TIMEOUT = 250;

    private final String name;
    private final String version;
    private String username;
    private String password;
    private PostThread workThread;
    private long lastPostTime;
    private long processingStartTime;
    private long processingStopTime;
    private float serverProcessingTime;

    public MatchClient(final String name, final String version) {

        this.name = name;
        this.version = version;
        lastPostTime = 0;
    }

    public String getName() {

        return name;
    }

    public String getVersion() {

        return version;
    }

    public void logout() {

        this.username = null;
        this.password = null;
    }

    public String getUsername() {

        return username;
    }

    public String getPassword() {

        return password;
    }

    public boolean isLoggedIn() {

        return username != null;
    }

    public boolean isWorking() {

        return (workThread != null) && !workThread.isDone();
    }

    public void cancelWork() {

        if (isWorking()) {
            workThread.cancel();
        }
    }

    /**
     * Used for usage statistics logging.
     */
    public float getServerProcessingTime() {

        return serverProcessingTime;
    }

    /**
     * Used for usage statistics logging.
     */
    public void setServerProcessingTime(final float serverProcessingTime) {

        this.serverProcessingTime = serverProcessingTime;
    }

    /**
     * Used for usage statistics logging.
     */
    public float getProcessingTime() {

        return (processingStopTime - processingStartTime) / 1000f;
    }

    /**
     * Used for usage statistics logging.
     */
    public void startProcessing() {

        this.processingStartTime = System.currentTimeMillis();
    }

    /**
     * Used for usage statistics logging.
     */
    public void stopProcessing() {

        this.processingStopTime = System.currentTimeMillis();
    }

    public void startLogin(final String username, final String password, final ILoginListener listener) {

        cancelWork();

        workThread = new LoginThread(this, username, password);
        ((LoginThread) workThread).addListener(listener);
        ((LoginThread) workThread).addListener(new InternalLoginListener(username, password));
        workThread.start();
        lastPostTime = System.currentTimeMillis();
    }

    public void startSearch(final String query, final MatchEnvironment env, final ISearchListener listener) {

        cancelWork();

        final long waitTime = TIMEOUT - (System.currentTimeMillis() - lastPostTime);

        workThread = new SearchThread(this, env, query, Math.max(0, waitTime), listener);
        workThread.start();
        lastPostTime = System.currentTimeMillis();
    }

    public void startSubmitEffect(final Effect effect, final boolean isPublic, final ISubmitEffectListener listener) {

        cancelWork();

        final long waitTime = TIMEOUT - (System.currentTimeMillis() - lastPostTime);

        workThread = new SubmitEffectThread(this, effect, isPublic, Math.max(0, waitTime), listener);
        workThread.start();
        lastPostTime = System.currentTimeMillis();
    }

    public void startSendFeedback(final String query, final MatchNode result, final String comment, final int rating,
            final boolean flag, final boolean isLog, final boolean isStartup, final long clientId, final boolean used,
            final ISendFeedbackListener listener) {

        cancelWork();

        final long waitTime = TIMEOUT - (System.currentTimeMillis() - lastPostTime);

        workThread = new SendFeedbackThread(this, query, result, comment, rating, flag, isLog, isStartup, clientId,
                used, Math.max(0, waitTime), listener);
        workThread.start();
        lastPostTime = System.currentTimeMillis();
    }

    public void startLoadProfile(final ILoadProfileListener listener) {

        cancelWork();

        final long waitTime = TIMEOUT - (System.currentTimeMillis() - lastPostTime);

        workThread = new LoadProfileThread(this, Math.max(0, waitTime), listener);
        workThread.start();
        lastPostTime = System.currentTimeMillis();
    }

    public void startDeleteEffect(final Effect effect, final IDeleteEffectListener listener) {

        cancelWork();

        final long waitTime = TIMEOUT - (System.currentTimeMillis() - lastPostTime);

        workThread = new DeleteEffectThread(this, effect, Math.max(0, waitTime), listener);
        workThread.start();
        lastPostTime = System.currentTimeMillis();
    }
}
