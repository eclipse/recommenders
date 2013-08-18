/**
 * Copyright (c) 2010, 2013 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 */
package org.eclipse.recommenders.rcp;

import static org.eclipse.jdt.core.dom.Modifier.isStatic;
import static org.eclipse.recommenders.internal.rcp.Constants.BUNDLE_NAME;
import static org.eclipse.recommenders.utils.Checks.ensureIsNotNull;
import static org.eclipse.ui.plugin.AbstractUIPlugin.imageDescriptorFromPlugin;

import java.lang.reflect.Field;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.recommenders.internal.rcp.RcpPlugin;
import org.eclipse.swt.graphics.Image;

public final class SharedImages {

    private static final String ELCL = "/icons/full/elcl16/";
    public static final String BULLET_BLUE = ELCL + "bullet_blue.png";
    public static final String BULLET_GREEN = ELCL + "bullet_green.png";
    public static final String BULLET_ORANGE = ELCL + "bullet_orange.png";
    public static final String BULLET_RED = ELCL + "bullet_red.png";
    public static final String BULLET_STAR = ELCL + "bullet_star.png";
    public static final String BULLET_YELLOW = ELCL + "bullet_yellow.png";
    public static final String JRE = ELCL + "classpath.gif";
    public static final String COLLAPSE_ALL = ELCL + "collapseall.gif";
    public static final String EXPAND_ALL = ELCL + "expandall.gif";
    public static final String JAR = ELCL + "jar_obj.gif";
    public static final String JAVA_PROJECT = ELCL + "projects.gif";
    public static final String OVERLAY_STAR = ELCL + "overlay_star.png";
    public static final String REFRESH = ELCL + "refresh_tab.gif";
    public static final String SLICE = "/icons/full/view16/slice.gif";
    public static final String SYNCED = ELCL + "synced.gif";

    private ImageRegistry registry = new ImageRegistry();

    public SharedImages() {
        initializeImages();
    }

    public ImageDescriptor getDescriptor(String key) {
        return registry.getDescriptor(key);
    }

    public Image getImage(String key) {
        return registry.get(key);
    }

    private void initializeImages() {
        try {
            for (Field f : getClass().getDeclaredFields()) {
                if (isStatic(f.getModifiers()) && f.getType() == String.class) {
                    String path = (String) f.get(null);
                    ImageDescriptor image = imageDescriptorFromPlugin(BUNDLE_NAME, path);
                    ensureIsNotNull(image);
                    registry.put(path, image);
                }
            }
        } catch (Exception e) {
            RcpPlugin.logError(e, "Failed to load image desciptor in shared images");
        }
    }
}
