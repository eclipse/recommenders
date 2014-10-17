package org.eclipse.recommenders.internal.stacktraces.rcp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

class FadeJob extends Job {

    public interface FadeJobProgressListener {
        public void fadeJobFinished(int currentAlpha);
    }

    private int currentAlpha;
    private Shell shell;
    private int increasePerTick;
    private boolean finished;
    private int delayMs;
    private FadeJob.FadeJobProgressListener listener;

    public FadeJob(Shell shell, int delayMs, int increasePerTick, FadeJob.FadeJobProgressListener listener) {
        super("Fade");
        this.delayMs = delayMs;
        this.increasePerTick = increasePerTick;
        this.listener = listener;
        this.shell = shell;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                FadeJob.this.currentAlpha = shell.getAlpha();
            }
        });
        currentAlpha += increasePerTick;
        if (currentAlpha <= 0) {
            currentAlpha = 0;
        } else if (currentAlpha >= 255) {
            currentAlpha = 255;
        }
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                if (shell.isDisposed()) {
                    return;
                }
                shell.setAlpha(currentAlpha);
                if (currentAlpha == 0 || currentAlpha == 255) {
                    if (listener != null) {
                        listener.fadeJobFinished(currentAlpha);
                    }
                    finished = true;
                }
            }
        });
        if (!finished) {
            schedule(delayMs);
        }
        return Status.OK_STATUS;
    }

}