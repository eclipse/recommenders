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
package org.eclipse.recommenders.completion.rcp.processable;

import static org.eclipse.recommenders.completion.rcp.processable.ProposalTag.CONTEXT;

import java.util.Map;

import org.eclipse.jdt.core.CompletionProposal;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.recommenders.completion.rcp.IRecommendersCompletionContext;
import org.eclipse.swt.graphics.Image;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;

public final class Proposals {

    private static Map<CacheKey, Image> cache = Maps.newHashMap();

    private Proposals() {
        throw new IllegalStateException("Not meant to be instantiated"); //$NON-NLS-1$
    }

    public static void overlay(IProcessableProposal proposal, ImageDescriptor icon) {
        overlay(proposal, icon, IDecoration.TOP_LEFT);
    }

    /**
     * @param decorationCorner
     *            e.g. {@link IDecoration#TOP_LEFT}
     */
    public static void overlay(IProcessableProposal proposal, ImageDescriptor icon, int decorationCorner) {
        Image originalImage = proposal.getImage();
        CacheKey key = new CacheKey(originalImage, icon, decorationCorner);
        Image newImage = cache.get(key);
        if (newImage == null) {
            DecorationOverlayIcon decorator = new DecorationOverlayIcon(originalImage, icon, decorationCorner);
            newImage = decorator.createImage();
            cache.put(key, newImage);
        }
        proposal.setImage(newImage);
    }

    public static IRecommendersCompletionContext getContext(IProcessableProposal proposal) {
        return proposal.<IRecommendersCompletionContext>getTag(CONTEXT).orNull();
    }

    /**
     * Null-safe variant of {@link IProcessableProposal#getPrefix()} that returns the
     * {@link IRecommendersCompletionContext#getPrefix()} in case the proposal returns null.
     */
    public static String getPrefix(IProcessableProposal proposal) {
        String prefix = proposal.getPrefix();
        if (prefix == null) {
            prefix = getContext(proposal).getPrefix();
        }
        return prefix;
    }

    public static Optional<CompletionProposal> getCoreProposal(ICompletionProposal proposal) {
        if (proposal instanceof IProcessableProposal) {
            IProcessableProposal pp = (IProcessableProposal) proposal;
            return pp.getCoreProposal();
        }
        return Optional.absent();
    }

    public static <T> Optional<T> getTag(ICompletionProposal proposal, String tag) {
        if (proposal instanceof IProcessableProposal) {
            IProcessableProposal pp = (IProcessableProposal) proposal;
            for (IProposalTag t : pp.tags()) {
                if (t.name().equals(tag)) {
                    return pp.getTag(t);
                }
            }
        }
        return Optional.absent();
    }

    public static boolean isKindOneOf(ICompletionProposal proposal, int... kinds) {
        CompletionProposal cp = getCoreProposal(proposal).orNull();
        if (cp != null) {
            for (int kind : kinds) {
                if (cp.getKind() == kind) {
                    return true;
                }
            }
        }
        return false;
    }

    private static final class CacheKey {
        Image image;
        ImageDescriptor overlay;
        int corner;

        public CacheKey(Image image, ImageDescriptor overlay, int corner) {
            super();
            this.image = image;
            this.overlay = overlay;
            this.corner = corner;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + corner;
            result = prime * result + (image == null ? 0 : image.hashCode());
            result = prime * result + (overlay == null ? 0 : overlay.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            CacheKey other = (CacheKey) obj;
            if (corner != other.corner) {
                return false;
            }
            if (image == null) {
                if (other.image != null) {
                    return false;
                }
            } else if (!image.equals(other.image)) {
                return false;
            }
            if (overlay == null) {
                if (other.overlay != null) {
                    return false;
                }
            } else if (!overlay.equals(other.overlay)) {
                return false;
            }
            return true;
        }

    }
}
