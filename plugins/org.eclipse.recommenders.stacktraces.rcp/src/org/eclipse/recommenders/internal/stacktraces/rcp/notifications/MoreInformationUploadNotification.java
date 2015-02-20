package org.eclipse.recommenders.internal.stacktraces.rcp.notifications;

import org.eclipse.recommenders.internal.stacktraces.rcp.Constants;

public class MoreInformationUploadNotification extends UploadNotification {

    public MoreInformationUploadNotification(UploadState state) {
        super(Constants.NOTIFY_MORE_INFO, state);
    }

}
