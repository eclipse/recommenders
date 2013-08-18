package org.eclipse.recommenders.rcp;

import static org.eclipse.jdt.core.dom.Modifier.isStatic;
import static org.eclipse.recommenders.internal.rcp.Constants.BUNDLE_NAME;
import static org.eclipse.recommenders.utils.Checks.ensureIsNotNull;
import static org.eclipse.ui.plugin.AbstractUIPlugin.imageDescriptorFromPlugin;

import java.lang.reflect.Field;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

public final class SharedImages {

    public static final String BULLET_BLUE = "/icons/full/elcl16/bullet_blue.png";
    public static final String BULLET_GREEN = "/icons/full/elcl16/bullet_green.png";
    public static final String BULLET_ORANGE = "/icons/full/elcl16/bullet_orange.png";
    public static final String BULLET_RED = "/icons/full/elcl16/bullet_red.png";
    public static final String BULLET_STAR = "/icons/full/elcl16/bullet_star.png";
    public static final String BULLET_YELLOW = "/icons/full/elcl16/bullet_yellow.png";
    public static final String OVERLAY_STAR = "/icons/full/elcl16/overlay_star.png";
    public static final String COLLAPSE_ALL = "/icons/full/elcl16/collapseall.gif";
    public static final String EXPAND_ALL = "/icons/full/elcl16/expandall.gif";
    public static final String SYNCED = "/icons/full/elcl16/synced.gif";
    public static final String SLICE = "/icons/full/view16/slice.gif";

    private ImageRegistry registry = new ImageRegistry();

    public SharedImages() {
        initializeImages();
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
            e.printStackTrace();
        }
    }

    public Image getImage(String key) {
        return registry.get(key);
    }

    public ImageDescriptor getDescriptor(String key) {
        return registry.getDescriptor(key);
    }
}
