package org.eclipse.recommenders.internal.snipmatch.rcp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.recommenders.coordinates.DependencyInfo;
import org.eclipse.recommenders.models.rcp.IProjectCoordinateProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.google.common.collect.ImmutableSet;

public class ContextLoadingProposal extends Job
        implements ICompletionProposal, ICompletionProposalExtension, IJobChangeListener {

    private final IProjectCoordinateProvider pcProvider;
    private final ImmutableSet<DependencyInfo> dependencies;
    private final Image image;

    private boolean resolutionJobDone = false;

    public ContextLoadingProposal(IProjectCoordinateProvider pcProvider, ImmutableSet<DependencyInfo> dependencies, Image image) {
        super(Messages.JOB_NAME_IDENTIFYING_PROJECT_DEPENDENCIES);
        this.pcProvider = pcProvider;
        this.dependencies = dependencies;
        this.image = image;
        this.addJobChangeListener(this);
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        for (DependencyInfo dependencyInfo : dependencies) {
            pcProvider.resolve(dependencyInfo);
        }
        return Status.OK_STATUS;
    }

    @Override
    public void apply(IDocument document, char trigger, int offset) {
        // Do nothing
    }

    @Override
    public boolean isValidFor(IDocument document, int offset) {
        return false;
    }

    @Override
    public char[] getTriggerCharacters() {
        return null;
    }

    @Override
    public int getContextInformationPosition() {
        return -1;
    }

    @Override
    public void apply(IDocument document) {
        // Do nothing
    }

    @Override
    public Point getSelection(IDocument document) {
        return null;
    }

    @Override
    public String getDisplayString() {
        return Messages.PROPOSAL_LABEL_IDENTIFYING_PROJECT_DEPENDENCIES;
    }

    @Override
    public String getAdditionalProposalInfo() {
        return Messages.PROPOSAL_INFO_IDENTIFYING_PROJECT_DEPENDENCIES;
    }

    @Override
    public Image getImage() {
        return image;
    }

    @Override
    public IContextInformation getContextInformation() {
        return null;
    }

    @Override
    public void aboutToRun(IJobChangeEvent event) {
    }

    @Override
    public void awake(IJobChangeEvent event) {
    }

    @Override
    public void done(IJobChangeEvent event) {
        resolutionJobDone = true;
    }

    @Override
    public void running(IJobChangeEvent event) {
    }

    @Override
    public void scheduled(IJobChangeEvent event) {
    }

    @Override
    public void sleeping(IJobChangeEvent event) {
    }

    public boolean isStillLoading() {
        return !resolutionJobDone;
    }
}
