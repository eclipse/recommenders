package org.eclipse.recommenders.snipmatch.rcp.util;

import java.net.URISyntaxException;

import org.eclipse.jgit.transport.Transport;
import org.eclipse.jgit.transport.TransportProtocol;
import org.eclipse.jgit.transport.URIish;

public class RepositoryUrlValidator {
    public static boolean isValidUri(String repoUri) {
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
}
