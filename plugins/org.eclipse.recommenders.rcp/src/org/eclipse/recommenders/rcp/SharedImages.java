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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

public final class SharedImages {

    public interface ImageResource {

        /**
         * @return the resource name of an image, i.e., a valid parameter to {@link Class#getResource(String)}
         * 
         * @see <a href="http://wiki.eclipse.org/User_Interface_Guidelines#Folder_Structure">Eclipse standard for folder
         *      names</a>
         */
        String getName();
    }

    /**
     * @return resource name for a <strong>d</strong>isabled <strong>l</strong>o<strong>c</strong>a<strong>l</strong>
     *         toolbar
     */
    public static String dlcl16(String image) {
        return "/icons/dlcl16/" + image;
    }

    /**
     * @return resource name for an <strong>e</strong>nabled <strong>l</strong>o<strong>c</strong>a<strong>l</strong>
     *         toolbar
     */
    public static String elcl16(String image) {
        return "/icons/elcl16/" + image;
    }

    /**
     * @return resource name for a model <strong>obj</strong>ect
     */
    public static String obj16(String image) {
        return "/icons/obj16/" + image;
    }

    /**
     * @return resource name for an object <strong>ov</strong>e<strong>r</strong>lay
     */
    public static String ovr16(String image) {
        return "/icons/ovr16/" + image;
    }

    /**
     * @return resource name for a <strong>view</strong>
     */
    public static String view16(String image) {
        return "/icons/view16/" + image;
    }

    /**
     * @return resource name for a <strong>wiz</strong>ard <strong>ban</strong>ner graphic
     */
    public static String wizban(String image) {
        return "/icons/wizban/" + image;
    }

    public static enum Images implements ImageResource {
        // @formatter:off
        ELCL_COLLAPSE_ALL(elcl16("collapseall.gif")),
        ELCL_DELETE(elcl16("delete.gif")),
        ELCL_EXPAND_ALL(elcl16("expandall.gif")),
        ELCL_REFRESH(elcl16("refresh_tab.gif")),
        ELCL_SYNCED(elcl16("synced.gif")),
        OBJ_CHECK_GREEN(obj16("tick_small.png")),
        OBJ_CROSS_RED(obj16("cross_small.png")),
        OBJ_BULLET_BLUE(obj16("bullet_blue.png")),
        OBJ_BULLET_GREEN(obj16("bullet_green.png")),
        OBJ_BULLET_ORANGE(obj16("bullet_orange.png")),
        OBJ_BULLET_RED(obj16("bullet_red.png")),
        OBJ_BULLET_STAR(obj16("bullet_star.png")),
        OBJ_BULLET_YELLOW(obj16("bullet_yellow.png")),
        OBJ_JAR(obj16("jar.gif")),
        OBJ_JAVA_PROJECT(obj16("project.gif")),
        OBJ_JRE(obj16("jre.gif")),
        OBJ_REPOSITORY(obj16("repository.gif")),
        OVR_STAR(ovr16("star.png")),
        VIEW_SLICE(view16("slice.gif"));
        // @formatter:on

        private final String name;

        private Images(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    private ImageRegistry registry = new ImageRegistry();

    public synchronized ImageDescriptor getDescriptor(ImageResource resource) {
        ImageDescriptor desc = registry.getDescriptor(resource.getName());
        if (desc == null) {
            desc = register(resource);
        }
        return desc;
    }

    public synchronized Image getImage(ImageResource resource) {
        Image img = registry.get(resource.getName());
        if (img == null) {
            register(resource);
            img = registry.get(resource.getName());
        }
        return img;
    }

    private ImageDescriptor register(ImageResource key) {
        ImageDescriptor desc = ImageDescriptor.createFromFile(key.getClass(), key.getName());
        registry.put(key.getName(), desc);
        return desc;
    }
}
