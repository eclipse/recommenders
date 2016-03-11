/**
 * Copyright (c) 2016 Yasett Acurana.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Yasett Acurana - initial API and implementation.
 */
package org.eclipse.recommenders.internal.snipmatch.rcp.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jgit.transport.Transport;
import org.eclipse.jgit.transport.TransportProtocol;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.recommenders.internal.snipmatch.rcp.Constants;
import org.eclipse.recommenders.internal.snipmatch.rcp.l10n.Messages;
import org.eclipse.recommenders.utils.Urls;

import com.google.common.collect.ImmutableList;

public class RepositoryUrlValidator {
    private static final List<String> ACCEPTED_PROTOCOLS = ImmutableList.of("file", "git", "http", "https", "ssh"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$

    public static IStatus isValidUri(String repositoryUri) {
        try {
            URIish urish = new URIish(repositoryUri);
            URI uri = new URI(repositoryUri);

            if (uri.isAbsolute()) {
                if (uri.getHost() != null) {
                    if (uri.getPath() != null) {
                        if (Urls.isUriProtocolSupported(uri, ACCEPTED_PROTOCOLS)) {

                            for (TransportProtocol protocol : Transport.getTransportProtocols()) {
                                if (protocol.canHandle(urish)) {
                                    return Status.OK_STATUS;
                                }
                            }
                            return new Status(IStatus.ERROR, Constants.BUNDLE_ID,
                                    Messages.WIZARD_GIT_REPOSITORY_ERROR_NO_PROTOCOL_CAN_HANDLE_URI);

                        } else {
                            return new Status(IStatus.ERROR, Constants.BUNDLE_ID,
                                    MessageFormat.format(Messages.WIZARD_GIT_REPOSITORY_ERROR_URL_PROTOCOL_UNSUPPORTED,
                                            repositoryUri,
                                            StringUtils.join(ACCEPTED_PROTOCOLS, Messages.LIST_SEPARATOR)));
                        }
                    } else {
                        return new Status(IStatus.ERROR, Constants.BUNDLE_ID,
                                Messages.WIZARD_GIT_REPOSITORY_ERROR_MISSING_PATH_URI);
                    }
                } else {
                    return new Status(IStatus.ERROR, Constants.BUNDLE_ID,
                            Messages.WIZARD_GIT_REPOSITORY_ERROR_MISSING_HOST_URI);
                }
            } else {
                return new Status(IStatus.ERROR, Constants.BUNDLE_ID, MessageFormat
                        .format(Messages.WIZARD_GIT_REPOSITORY_ERROR_ABSOLUTE_URL_REQUIRED, repositoryUri));
            }
        } catch (URISyntaxException e) {
            return new Status(IStatus.ERROR, Constants.BUNDLE_ID, e.getLocalizedMessage(), e);
        }
    }
}
