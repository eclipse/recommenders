/**
 * Copyright (c) 2015 Codetrails GmbH. All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Johannes Dorn - initial API and implementation.
 */
package org.eclipse.recommenders.news.rcp;

import java.net.URL;
import java.util.Date;

public interface IFeedMessage {

    /**
     * @return Unique identifier of the message.
     */
    String getId();

    /**
     * @return Date of item's publishing.
     */
    Date getDate();

    /**
     * @return Item synopsis.
     */
    String getDescription();

    /**
     * @return Title of the message.
     */
    String getTitle();

    /**
     * @return URL of the message.
     */
    URL getUrl();

    /**
     * @return States whether message has been read by user or not.
     */
    boolean isRead();

    /**
     * Sets the message as read/unread
     */
    void setRead(boolean read);
}
