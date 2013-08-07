/**
 * Copyright (c) 2010, 2013 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Olav Lenz - initial API and implementation
 */
package org.eclipse.recommenders.internal.models.rcp;

import java.util.HashMap;
import java.util.Map.Entry;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.google.common.collect.Maps;

public class ImageProvider {

    private HashMap<String, Image> images = Maps.newHashMap();
    
    public Image provideImage(String path){
        Image image = images.get(path);
        if (image == null){
            Image loadedImage = loadImage(path);
            images.put(path, loadedImage);
            return loadedImage;
        }
        return image;
    }
    
    private Image loadImage(final String pathToFile) {
        ImageDescriptor imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(Constants.BUNDLE_ID, pathToFile);
        if (imageDescriptor != null) {
            Image image = imageDescriptor.createImage();
            return image;
        }
        return null;
    }

    public void dispose(){
        for (Entry<String, Image> entry : images.entrySet()) {
            entry.getValue().dispose();
        }
        images.clear();
    }
}
