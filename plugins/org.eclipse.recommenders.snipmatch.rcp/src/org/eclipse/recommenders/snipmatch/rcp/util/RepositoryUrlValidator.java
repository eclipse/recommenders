package org.eclipse.recommenders.snipmatch.rcp.util;

import java.net.URISyntaxException;

import org.eclipse.jgit.transport.Transport;
import org.eclipse.jgit.transport.TransportProtocol;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.recommenders.internal.snipmatch.rcp.l10n.Messages;

public class RepositoryUrlValidator {
    public static Boolean isValidUri(String repoUri) {
        try {
            URIish uri = new URIish(repoUri);
            for (TransportProtocol protocol : Transport.getTransportProtocols()) {
                if (protocol.canHandle(uri)) {
                    return true;
                }
            }
        } catch (URISyntaxException e) {
            return false;
        }
        return false;
    }

    public static String verifyValidUri(String repoUri) {
        try {
            URIish uri = new URIish(repoUri);
            for (TransportProtocol protocol : Transport.getTransportProtocols()) {
                if (protocol.canHandle(uri)) {
                    return null;
                }
            }
        } catch (URISyntaxException e) {
            return e.getLocalizedMessage();
        }
        return Messages.WIZARD_GIT_REPOSITORY_ERROR_NO_PROTOCOL_CAN_HANDLE;
    }
}
