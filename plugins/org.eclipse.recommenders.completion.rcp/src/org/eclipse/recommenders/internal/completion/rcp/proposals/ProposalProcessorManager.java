package org.eclipse.recommenders.internal.completion.rcp.proposals;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.TextStyle;

import com.google.common.collect.Sets;

public class ProposalProcessorManager {

    private Set<ProposalProcessor> contributors = Sets.newHashSet();
    private IProcessableProposal oProposal;
    private StyledString oStyledString;
    private int oRelevance;

    public ProposalProcessorManager(IProcessableProposal oProposal) {
        this.oProposal = oProposal;
        this.oRelevance = oProposal.getRelevance();
        this.oStyledString =  deepCopy(oProposal.getStyledDisplayString());
    }

    public void addContributor(ProposalProcessor contributor) {
        contributors.add(contributor);
    }

    public boolean prefixChanged(String prefix) {
        boolean discardProposal = false;
        StyledString tmpStyledString = deepCopy(oStyledString);
        AtomicInteger tmpRelevance = new AtomicInteger(oRelevance);

        for (ProposalProcessor c : contributors) {
            discardProposal |= c.isPrefix(prefix);
            c.modifyDisplayString(tmpStyledString);
            c.modifyRelevance(tmpRelevance);
        }
        oProposal.setRelevance(tmpRelevance.get());
        oProposal.setStyledDisplayString(tmpStyledString);
        return discardProposal;
    }

    public static StyledString deepCopy(final StyledString displayString) {
        final StyledString copy = new StyledString(displayString.getString());
        for (final StyleRange range : displayString.getStyleRanges()) {
            copy.setStyle(range.start, range.length, new Styler() {

                @Override
                public void applyStyles(final TextStyle textStyle) {
                    textStyle.background = range.background;
                    textStyle.borderColor = range.borderColor;
                    textStyle.borderStyle = range.borderStyle;
                    textStyle.font = range.font;
                    textStyle.foreground = range.foreground;
                }
            });
        }
        return copy;
    }
}
