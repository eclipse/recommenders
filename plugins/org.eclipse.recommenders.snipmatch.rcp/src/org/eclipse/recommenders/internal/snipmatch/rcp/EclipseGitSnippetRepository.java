/**
 * Copyright (c) 2010, 2014 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 */
package org.eclipse.recommenders.internal.snipmatch.rcp;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.egit.core.internal.job.JobUtil;
import org.eclipse.egit.core.op.ResetOperation;
import org.eclipse.egit.ui.UIPreferences;
import org.eclipse.egit.ui.internal.commit.CommitUI;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jgit.api.ResetCommand.ResetType;
import org.eclipse.recommenders.injection.InjectionService;
import org.eclipse.recommenders.internal.snipmatch.rcp.Repositories.SnippetRepositoryConfigurationChangedEvent;
import org.eclipse.recommenders.snipmatch.GitSnippetRepository;
import org.eclipse.recommenders.snipmatch.GitSnippetRepository.GitNoCurrentFormatBranchException;
import org.eclipse.recommenders.snipmatch.GitSnippetRepository.GitNoFormatBranchException;
import org.eclipse.recommenders.snipmatch.GitSnippetRepository.GitUpdateException;
import org.eclipse.recommenders.snipmatch.ISearchContext;
import org.eclipse.recommenders.snipmatch.ISnippet;
import org.eclipse.recommenders.snipmatch.ISnippetRepository;
import org.eclipse.recommenders.snipmatch.Snippet;
import org.eclipse.recommenders.snipmatch.model.SnippetRepositoryConfiguration;
import org.eclipse.recommenders.snipmatch.rcp.SnippetRepositoryClosedEvent;
import org.eclipse.recommenders.snipmatch.rcp.SnippetRepositoryContentChangedEvent;
import org.eclipse.recommenders.snipmatch.rcp.SnippetRepositoryOpenedEvent;
import org.eclipse.recommenders.snipmatch.rcp.model.EclipseGitSnippetRepositoryConfiguration;
import org.eclipse.recommenders.utils.Recommendation;
import org.eclipse.recommenders.utils.Urls;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.name.Names;

@SuppressWarnings("restriction")
public class EclipseGitSnippetRepository implements ISnippetRepository {

    private static Logger LOG = LoggerFactory.getLogger(EclipseGitSnippetRepository.class);

    private final EventBus bus;

    private volatile int timesOpened;
    private GitSnippetRepository delegate;
    private volatile boolean delegateOpen;

    private final Lock readLock;
    private final Lock writeLock;

    private volatile Job openJob = null;

    public EclipseGitSnippetRepository(int id, File basedir, String remoteUri, String pushUrl, String pushBranchPrefix,
            EventBus bus) {
        this.bus = bus;

        delegate = new GitSnippetRepository(id, new File(basedir, Urls.mangle(remoteUri)), remoteUri, pushUrl,
                pushBranchPrefix);

        ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        readLock = readWriteLock.readLock();
        writeLock = readWriteLock.writeLock();
    }

    @Override
    public void open() {
        writeLock.lock();
        try {
            timesOpened++;
            if (timesOpened > 1) {
                return;
            }
            if (openJob == null && !delegateOpen) {
                openJob = new Job(Messages.JOB_OPENING_SNIPPET_REPOSITORY) {

                    @Override
                    protected IStatus run(IProgressMonitor monitor) {
                        try {
                            delegate.open();
                            changeStateToOpen();
                            return Status.OK_STATUS;
                        } catch (GitUpdateException e) {
                            changeStateToOpen();
                            Status status = new Status(IStatus.WARNING, Constants.BUNDLE_ID, MessageFormat.format(
                                    Messages.WARNING_FAILURE_TO_UPDATE_REPOSITORY, delegate.getRepositoryLocation(),
                                    e.getMessage()), e);
                            Platform.getLog(Platform.getBundle(Constants.BUNDLE_ID)).log(status);
                            return Status.OK_STATUS;
                        } catch (final GitNoCurrentFormatBranchException e) {
                            changeStateToOpen();
                            Status status = new Status(IStatus.WARNING, Constants.BUNDLE_ID, MessageFormat.format(
                                    Messages.WARNING_FAILURE_TO_CHECKOUT_CURRENT_BRANCH, Snippet.FORMAT_VERSION,
                                    delegate.getRepositoryLocation(), e.getCheckoutVersion(), e.getMessage()), e);
                            Platform.getLog(Platform.getBundle(Constants.BUNDLE_ID)).log(status);

                            final Display display = Display.getDefault();
                            display.asyncExec(new Runnable() {

                                @Override
                                public void run() {

                                    BranchCheckoutFailureDialog dialog = new BranchCheckoutFailureDialog(display
                                            .getActiveShell(), delegate.getRepositoryLocation(),
                                            Snippet.FORMAT_VERSION, e.getCheckoutVersion());
                                    dialog.open();
                                }
                            });

                            return Status.OK_STATUS;
                        } catch (final GitNoFormatBranchException e) {
                            LOG.error("Exception while opening repository.", e); //$NON-NLS-1$
                            Status status = new Status(IStatus.ERROR, Constants.BUNDLE_ID, MessageFormat.format(
                                    Messages.ERROR_NO_FORMAT_BRANCH, Snippet.FORMAT_VERSION,
                                    delegate.getRepositoryLocation(), e.getMessage()), e);
                            Platform.getLog(Platform.getBundle(Constants.BUNDLE_ID)).log(status);

                            final Display display = Display.getDefault();
                            display.asyncExec(new Runnable() {

                                @Override
                                public void run() {

                                    BranchCheckoutFailureDialog dialog = new BranchCheckoutFailureDialog(display
                                            .getActiveShell(), delegate.getRepositoryLocation(), Snippet.FORMAT_VERSION);
                                    dialog.open();
                                }
                            });

                            return Status.CANCEL_STATUS;
                        } catch (IOException e) {
                            LOG.error("Exception while opening repository.", e); //$NON-NLS-1$
                            Status status = new Status(IStatus.ERROR, Constants.BUNDLE_ID, MessageFormat.format(
                                    Messages.ERROR_FAILURE_TO_CLONE_REPOSITORY, delegate.getRepositoryLocation(),
                                    timesOpened, e.getMessage()), e);
                            Platform.getLog(Platform.getBundle(Constants.BUNDLE_ID)).log(status);
                            return Status.CANCEL_STATUS;
                        } finally {
                            openJob = null;
                        }
                    }

                    private void changeStateToOpen() {
                        delegateOpen = true;
                        bus.post(new SnippetRepositoryOpenedEvent(EclipseGitSnippetRepository.this));
                    }
                };
                openJob.schedule();
            }
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void close() throws IOException {
        writeLock.lock();
        try {
            if (timesOpened == 0) {
                return;
            } else if (timesOpened > 1) {
                timesOpened--;
                return;
            } else if (timesOpened == 1) {
                timesOpened = 0;
                if (openJob != null) {
                    try {
                        openJob.join();
                        openJob = null;
                    } catch (InterruptedException e) {
                        LOG.error("Failed to join open job", e); //$NON-NLS-1$
                    }
                }
                delegate.close();
                delegateOpen = false;
                bus.post(new SnippetRepositoryClosedEvent(this));
            }
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public List<Recommendation<ISnippet>> search(ISearchContext context) {
        readLock.lock();
        try {
            if (!isOpen() || !delegateOpen) {
                return Collections.emptyList();
            }
            return delegate.search(context);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public List<Recommendation<ISnippet>> search(ISearchContext context, int maxResults) {
        readLock.lock();
        try {
            if (!isOpen() || !delegateOpen) {
                return Collections.emptyList();
            }
            return delegate.search(context, maxResults);
        } finally {
            readLock.unlock();
        }
    }

    @Subscribe
    public void onEvent(SnippetRepositoryConfigurationChangedEvent e) throws IOException {
        close();
        open();
    }

    @Override
    public String getRepositoryLocation() {
        readLock.lock();
        try {
            Preconditions.checkState(isOpen());
            if (!delegateOpen) {
                return ""; //$NON-NLS-1$
            }
            return delegate.getRepositoryLocation();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public int getId() {
        readLock.lock();
        try {
            return delegate.getId();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean hasSnippet(UUID uuid) {
        readLock.lock();
        try {
            if (!isOpen() || !delegateOpen) {
                return false;
            }
            return delegate.hasSnippet(uuid);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean delete(UUID uuid) throws IOException {
        readLock.lock();
        try {
            if (!isOpen() || !delegateOpen) {
                return false;
            }
            boolean deleted = delegate.delete(uuid);
            if (deleted) {
                bus.post(new SnippetRepositoryContentChangedEvent(this));
            }
            return deleted;
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean isDeleteSupported() {
        return delegate.isDeleteSupported();
    }

    private boolean isOpen() {
        return timesOpened > 0;
    }

    @Override
    public void importSnippet(ISnippet snippet) throws IOException {
        writeLock.lock();
        try {
            Preconditions.checkState(isOpen(), Messages.ERROR_REPOSITORY_NOT_OPEN_YET);
            delegate.importSnippet(snippet);
            bus.post(new SnippetRepositoryContentChangedEvent(this));
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean isImportSupported() {
        return delegate.isImportSupported();
    }

    public static ISnippetRepository createRepositoryInstance(EclipseGitSnippetRepositoryConfiguration config) {
        EventBus bus = InjectionService.getInstance().requestInstance(EventBus.class);
        File basedir = InjectionService.getInstance().requestAnnotatedInstance(File.class,
                Names.named(SnipmatchRcpModule.SNIPPET_REPOSITORY_BASEDIR));

        return new EclipseGitSnippetRepository(config.getId(), basedir, config.getUrl(), config.getPushUrl(),
                config.getPushBranchPrefix(), bus);
    }

    public static BasicEList<SnippetRepositoryConfiguration> getDefaultConfiguration() {
        BasicEList<SnippetRepositoryConfiguration> result = new BasicEList<SnippetRepositoryConfiguration>();
        result.addAll(DefaultGitSnippetRepositoryConfigurations.fetchDefaultConfigurations());
        return result;
    }

    @Override
    public boolean delete() {
        writeLock.lock();
        try {
            try {
                close();
                delegate.delete();
                return true;
            } catch (IOException e) {
                LOG.error("Exception while deleting files on disk.", e); //$NON-NLS-1$
                return false;
            }

        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean share(Collection<UUID> uuids) {
        Workspace ws = (Workspace) ResourcesPlugin.getWorkspace();
        List<IResource> resources = Lists.newArrayList();
        for (UUID uuid : uuids) {
            File snippetFile = delegate.getSnippetFile(uuid);
            if (snippetFile == null) {
                continue;
            }
            IPath location = new Path(snippetFile.getAbsolutePath());
            IResource file = ws.newResource(location, IResource.FILE);
            resources.add(file);
        }
        IResource[] res = resources.toArray(new IResource[resources.size()]);
        Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

        // TODO This is a workaround for CommitUI shortcomings. See Bug 447236
        IPreferenceStore preferenceStore = org.eclipse.egit.ui.Activator.getDefault().getPreferenceStore();
        boolean includeUntrackedPreference = preferenceStore.getBoolean(UIPreferences.COMMIT_DIALOG_INCLUDE_UNTRACKED);
        try {
            preferenceStore.setValue(UIPreferences.COMMIT_DIALOG_INCLUDE_UNTRACKED, true);

            CommitUI commitUI = new CommitUI(shell, delegate.getGitRepo(), res, false);
            if (!commitUI.commit()) {
                return false;
            }
            ResetOperation reset = new ResetOperation(delegate.getGitRepo(), "origin/" + Snippet.FORMAT_VERSION,
                    ResetType.MIXED);
            JobUtil.scheduleUserWorkspaceJob(reset, "reset", ws);
            return true;
        } finally {
            preferenceStore.setValue(UIPreferences.COMMIT_DIALOG_INCLUDE_UNTRACKED, includeUntrackedPreference);
        }
    }

    @Override
    public boolean isSharingSupported() {
        return true;
    }
}
