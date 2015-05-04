/**
 * Copyright (c) 2015 Codetrails GmbH. All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Johannes Dorn - initial API and implementation.
 */
package org.eclipse.recommenders.internal.news.rcp;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import org.eclipse.recommenders.news.rcp.IFeedMessage;

public class FeedMessage implements IFeedMessage {
    private String id;
    private Date date;
    private String description;
    private String title;
    private URL url;

    public FeedMessage() {
    }

    public FeedMessage(String id, Date date, String description, String title, URL url) {
        super();
        this.id = id;
        this.date = date;
        this.description = description;
        this.title = title;
        this.url = url;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public void setUrl(String url) {
        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!obj.getClass().isInstance(FeedMessage.class)) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        FeedMessage rhs = (FeedMessage) obj;
        return this.getId().equals(rhs.getId());
    }

    @Override
    public int hashCode() {
        int result = 7;
        result = 43 * result + this.getId().hashCode();
        return result;
    }
}
