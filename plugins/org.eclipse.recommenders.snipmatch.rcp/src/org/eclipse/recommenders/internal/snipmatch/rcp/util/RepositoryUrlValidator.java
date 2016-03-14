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
import java.util.EnumSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jgit.transport.Transport;
import org.eclipse.jgit.transport.TransportProtocol;
import org.eclipse.jgit.transport.TransportProtocol.URIishField;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.util.StringUtils;
import org.eclipse.recommenders.internal.snipmatch.rcp.Constants;
import org.eclipse.recommenders.internal.snipmatch.rcp.l10n.Messages;

public class RepositoryUrlValidator {

    public static IStatus isValidUri(String repositoryUri) {
        try {
            URIish urish = new URIish(repositoryUri);
            URI uri = new URI(repositoryUri);

            if (!uri.isAbsolute()) {
                return new Status(IStatus.ERROR, Constants.BUNDLE_ID, MessageFormat
                        .format(Messages.WIZARD_GIT_REPOSITORY_ERROR_ABSOLUTE_URL_REQUIRED, repositoryUri));
            }

            if (StringUtils.isEmptyOrNull(urish.getScheme())) {
                return new Status(IStatus.ERROR, Constants.BUNDLE_ID, MessageFormat
                        .format(Messages.WIZARD_GIT_REPOSITORY_ERROR_HIERARCHICAL_URL_REQUIRED, repositoryUri));
            }

            boolean isSupportedScheme = false;

            for (TransportProtocol protocol : Transport.getTransportProtocols()) {
                if (protocol.getSchemes().contains(urish.getScheme())) {
                    isSupportedScheme = true;

                    for (URIishField field : protocol.getRequiredFields()) {
                        switch (field) {
                        case HOST:
                            if (StringUtils.isEmptyOrNull(urish.getHost())) {
                                return new Status(IStatus.ERROR, Constants.BUNDLE_ID,
                                        MessageFormat.format(Messages.WIZARD_GIT_REPOSITORY_ERROR_MISSING_COMPONENT,
                                                field.toString().toLowerCase()));
                            }
                            break;
                        case PATH:
                            if (StringUtils.isEmptyOrNull(urish.getPath())) {
                                return new Status(IStatus.ERROR, Constants.BUNDLE_ID,
                                        MessageFormat.format(Messages.WIZARD_GIT_REPOSITORY_ERROR_MISSING_COMPONENT,
                                                field.toString().toLowerCase()));
                            }
                            break;
                        case USER:
                            if (StringUtils.isEmptyOrNull(urish.getUser())) {
                                return new Status(IStatus.ERROR, Constants.BUNDLE_ID,
                                        MessageFormat.format(Messages.WIZARD_GIT_REPOSITORY_ERROR_MISSING_COMPONENT,
                                                field.toString().toLowerCase()));
                            }
                            break;
                        case PASS:
                            if (StringUtils.isEmptyOrNull(urish.getPass())) {
                                return new Status(IStatus.ERROR, Constants.BUNDLE_ID,
                                        MessageFormat.format(Messages.WIZARD_GIT_REPOSITORY_ERROR_MISSING_COMPONENT,
                                                field.toString().toLowerCase()));
                            }
                            break;
                        case PORT:
                            if (urish.getPort() <= 0) {
                                return new Status(IStatus.ERROR, Constants.BUNDLE_ID,
                                        MessageFormat.format(Messages.WIZARD_GIT_REPOSITORY_ERROR_MISSING_COMPONENT,
                                                field.toString().toLowerCase()));
                            }
                            break;
                        default:
                            break;
                        }
                    }

                    Set<URIishField> validFields = EnumSet.copyOf(protocol.getRequiredFields());
                    validFields.addAll(protocol.getOptionalFields());

                    if (!StringUtils.isEmptyOrNull(urish.getHost()) && !validFields.contains(URIishField.HOST)) {
                        return new Status(IStatus.ERROR, Constants.BUNDLE_ID,
                                MessageFormat.format(Messages.WIZARD_GIT_REPOSITORY_ERROR_INVALID_COMPONENT,
                                        URIishField.HOST.toString().toLowerCase()));
                    }

                    if (!StringUtils.isEmptyOrNull(urish.getPath()) && !validFields.contains(URIishField.PATH)) {
                        return new Status(IStatus.ERROR, Constants.BUNDLE_ID,
                                MessageFormat.format(Messages.WIZARD_GIT_REPOSITORY_ERROR_INVALID_COMPONENT,
                                        URIishField.PATH.toString().toLowerCase()));
                    }

                    if (!StringUtils.isEmptyOrNull(urish.getUser()) && !validFields.contains(URIishField.USER)) {
                        return new Status(IStatus.ERROR, Constants.BUNDLE_ID,
                                MessageFormat.format(Messages.WIZARD_GIT_REPOSITORY_ERROR_INVALID_COMPONENT,
                                        URIishField.USER.toString().toLowerCase()));
                    }

                    if (!StringUtils.isEmptyOrNull(urish.getPass()) && !validFields.contains(URIishField.PASS)) {
                        return new Status(IStatus.ERROR, Constants.BUNDLE_ID,
                                MessageFormat.format(Messages.WIZARD_GIT_REPOSITORY_ERROR_INVALID_COMPONENT,
                                        URIishField.PASS.toString().toLowerCase()));
                    }

                    if (urish.getPort() > 0 && !validFields.contains(URIishField.PORT)) {
                        return new Status(IStatus.ERROR, Constants.BUNDLE_ID,
                                MessageFormat.format(Messages.WIZARD_GIT_REPOSITORY_ERROR_INVALID_COMPONENT,
                                        URIishField.PORT.toString().toLowerCase()));
                    }
                }
            }

            if (!isSupportedScheme) {
                return new Status(IStatus.ERROR, Constants.BUNDLE_ID,
                        MessageFormat.format(Messages.WIZARD_GIT_REPOSITORY_ERROR_URL_PROTOCOL_UNSUPPORTED,
                                urish.getScheme(), StringUtils.join(getSupportedSchemes(), Messages.LIST_SEPARATOR)));
            } else {
                return Status.OK_STATUS;
            }

        } catch (URISyntaxException e) {
            return new Status(IStatus.ERROR, Constants.BUNDLE_ID, e.getLocalizedMessage(), e);
        }
    }

    private static SortedSet<String> getSupportedSchemes() {

        SortedSet<String> supportedSchemes = new TreeSet<String>();
        for (TransportProtocol protocol : Transport.getTransportProtocols()) {
            supportedSchemes.addAll(protocol.getSchemes());
        }

        return supportedSchemes;
    }
}
