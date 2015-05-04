package org.eclipse.recommenders.news.rcp;

import java.net.URL;
import java.util.Date;

public interface IFeedMessage {

    String getId();

    Date getDate();

    String getDescription();

    String getTitle();

    URL getUrl();
}
