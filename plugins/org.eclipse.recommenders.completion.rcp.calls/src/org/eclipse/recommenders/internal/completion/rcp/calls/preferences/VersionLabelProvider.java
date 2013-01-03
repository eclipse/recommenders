/**
 * Copyright (c) 2010, 2012 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 *    Patrick Gottschaemmer, Olav Lenz - externalize Strings.
 */
package org.eclipse.recommenders.internal.completion.rcp.calls.preferences;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.eclipse.recommenders.utils.Checks.cast;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.recommenders.completion.rcp.calls.l10n.Messages;
import org.eclipse.recommenders.internal.rcp.models.ModelArchiveMetadata;
import org.eclipse.recommenders.rcp.ClasspathEntryInfo;
import org.eclipse.recommenders.utils.Tuple;
import org.eclipse.swt.graphics.Image;

import com.google.common.annotations.VisibleForTesting;

public class VersionLabelProvider extends ColumnLabelProvider {

    @VisibleForTesting
    public static final String UNKNOWN_DEPENDENCY_DETAILS = Messages.PrefPage_unknown_dependency_details;
    @VisibleForTesting
    public static final String KNOWN_DEPENDENCY_DETAILS = Messages.PrefPage_know_dependency_details;

    private final Image versionUnknownImage;
    private final Image versionImage;

    public VersionLabelProvider(Image versionUnknownImage, Image versionImage) {
        this.versionUnknownImage = versionUnknownImage;
        this.versionImage = versionImage;
    }

    @Override
    public Image getImage(final Object element) {
        return hasDependencyInformation(element) ? versionImage : versionUnknownImage;
    }

    @Override
    public String getToolTipText(final Object element) {
        if (hasDependencyInformation(element)){
            return KNOWN_DEPENDENCY_DETAILS;
        }else{
            return UNKNOWN_DEPENDENCY_DETAILS;
        }
    }

    private boolean hasDependencyInformation(final Object element) {
        Tuple<ClasspathEntryInfo, ModelArchiveMetadata<?, ?>> e = cast(element);
        ClasspathEntryInfo cpei = e.getFirst();
        if (isEmpty(cpei.getSymbolicName()) || cpei.getVersion().isUnknown()) {
            return false;
        }
        return true;
    }
}