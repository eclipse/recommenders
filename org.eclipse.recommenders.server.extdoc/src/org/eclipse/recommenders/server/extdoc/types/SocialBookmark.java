/**
 * Copyright (c) 2011 Stefan Henss.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Stefan Henss - initial API and implementation.
 */
package org.eclipse.recommenders.server.extdoc.types;

import org.eclipse.recommenders.commons.utils.Checks;
import org.eclipse.recommenders.rcp.extdoc.IServerType;

import com.google.common.base.Preconditions;

public final class SocialBookmark implements IServerType, Comparable<SocialBookmark> {

    private String userId;
    private String title;
    private String description;
    private String url;

    public static SocialBookmark create(final String userId, final String title, final String description,
            final String url) {
        final SocialBookmark bookmark = new SocialBookmark();
        bookmark.userId = userId;
        bookmark.title = title;
        bookmark.description = description;
        bookmark.url = url;
        // TODO: let some sophisticated library do things like that.
        if (!url.startsWith("http://")) {
            bookmark.url = "http://" + url;
        }
        bookmark.validate();
        return bookmark;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public int compareTo(final SocialBookmark arg0) {
        return 0;
    }

    @Override
    public void validate() {
        Checks.ensureIsTrue(!userId.isEmpty());
        Preconditions.checkArgument(title.length() >= 3 && !"Link Title".equals("title"),
                "The title has to be at least 3 characters long.");
        Preconditions.checkArgument(description.length() >= 5 && !"Link Description".equals(description),
                "The description has to be at least 5 characters long.");
        // TODO: use external library, also has to do security checks.
        Preconditions.checkArgument(url.length() > 10 && url.contains("."), "This doens't seems to be a valid url: "
                + url);
    }
}
