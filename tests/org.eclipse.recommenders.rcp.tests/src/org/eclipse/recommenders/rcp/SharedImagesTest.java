package org.eclipse.recommenders.rcp;

import static org.apache.commons.lang3.ArrayUtils.isEquals;
import static org.junit.Assert.*;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.recommenders.rcp.SharedImages.ImageResource;
import org.eclipse.recommenders.rcp.SharedImages.Images;
import org.eclipse.swt.graphics.Image;
import org.junit.Test;

public class SharedImagesTest {

    @Test
    public void testLoadImages() {
        SharedImages sut = new SharedImages();
        for (Images i : Images.values()) {
            Image image = sut.getImage(i);
            assertNotNull(image);
            ImageDescriptor desc = sut.getDescriptor(i);
            assertNotNull(desc);
            // comparing image equality is a bit tricky:
            Image descImage = desc.createImage();
            assertTrue(isEquals(image.getImageData().data, descImage.getImageData().data));
        }
    }

    @Test
    public void testDuplicatedImagesWork() {
        SharedImages sut = new SharedImages();
        Image image1 = sut.getImage(Images.VIEW_SLICE);
        Image image2 = sut.getImage(new ImageResource() {
            @Override
            public String getName() {
                return Images.VIEW_SLICE.getName();
            }
        });
        assertNotSame(image1, image2);
    }
}
