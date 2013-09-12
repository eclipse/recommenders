package org.eclipse.recommenders.completion.rcp.processable;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.swt.graphics.Image;

public class Proposals {

    public static void overlay(IProcessableProposal proposal, ImageDescriptor icon) {
        Image originalImage = proposal.getImage();
        DecorationOverlayIcon decorator = new DecorationOverlayIcon(originalImage, icon, IDecoration.TOP_LEFT);
        proposal.setImage(decorator.createImage());
    }
}
