/**
 * Copyright (c) 2015 Codetrails GmbH. All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Johannes Dorn - initial API and implementation.
 */
package org.eclipse.recommenders.internal.news.rcp;

import javax.inject.Singleton;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.recommenders.news.rcp.IFeed;
import org.eclipse.recommenders.news.rcp.IFeedEvents;

@Creatable
@Singleton
public class FeedEvents implements IFeedEvents {

    @Override
    public NewFeedItemsEvent createNewFeedItemsEvent() {
        return new NewFeedItemsEvent();
    }

    @Override
    public FeedMessageReadEvent createFeedMessageReadEvent(String id) {
        return new FeedMessageReadEvent(id);
    }

    @Override
    public FeedReadEvent createFeedReadEvent(IFeed feed) {
        return new FeedReadEvent(feed);
    }

    @Override
    public AllReadEvent createAllReadEvent() {
        return new AllReadEvent();
    }

}
