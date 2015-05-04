/**
 * Copyright (c) 2015 Codetrails GmbH. All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Johannes Dorn - initial API and implementation.
 */
package org.eclipse.recommenders.internal.news.rcp;

import java.net.URL;
import java.util.Date;

import org.eclipse.recommenders.news.rcp.IFeedMessage;

public class FeedMessage implements IFeedMessage {

    @Override
    public String getId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Date getDate() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getTitle() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public URL getUrl() {
        // TODO Auto-generated method stub
        return null;
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
        return this.getId().equalsIgnoreCase(rhs.getId());
    }

    @Override
    public int hashCode() {
        int result = 7;
        result = 43 * result + this.getId().hashCode();
        return result;
    }
}
