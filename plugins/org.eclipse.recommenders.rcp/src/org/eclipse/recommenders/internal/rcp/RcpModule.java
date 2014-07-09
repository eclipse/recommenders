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
package org.eclipse.recommenders.internal.rcp;

import static com.google.inject.Scopes.SINGLETON;
import static java.lang.Thread.MIN_PRIORITY;
import static org.apache.commons.lang3.ArrayUtils.contains;
import static org.eclipse.recommenders.internal.rcp.Messages.*;
import static org.eclipse.recommenders.utils.Executors.coreThreadsTimoutExecutor;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.recommenders.rcp.IAstProvider;
import org.eclipse.recommenders.rcp.IRcpService;
import org.eclipse.recommenders.rcp.JavaElementResolver;
import org.eclipse.recommenders.rcp.SharedImages;
import org.eclipse.recommenders.rcp.utils.ASTNodeUtils;
import org.eclipse.recommenders.rcp.utils.ASTStringUtils;
import org.eclipse.recommenders.rcp.utils.AstBindings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.eclipse.ui.progress.UIJob;
import org.osgi.framework.Bundle;

import com.google.common.collect.Lists;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

@SuppressWarnings("restriction")
public class RcpModule extends AbstractModule implements Module {

    @Override
    protected void configure() {
        bind(JavaElementResolver.class).in(SINGLETON);
        requestStaticInjection(ASTStringUtils.class);
        requestStaticInjection(ASTNodeUtils.class);
        requestStaticInjection(AstBindings.class);
        bind(Helper.class).asEagerSingleton();
        bind(SharedImages.class).in(SINGLETON);
        configureAstProvider();
        bindRcpServiceListener();

        checkBundleResolution();
    }

    private void configureAstProvider() {
        final CachingAstProvider p = new CachingAstProvider();
        JavaCore.addElementChangedListener(p);
        bind(IAstProvider.class).toInstance(p);
    }

    private void bindRcpServiceListener() {
        bindListener(new RcpServiceMatcher(), new Listener());
    }

    @Singleton
    @Provides
    public JavaModelEventsService provideJavaModelEventsProvider(final EventBus bus, final IWorkspaceRoot workspace) {
        final JavaModelEventsService p = new JavaModelEventsService(bus, workspace);
        JavaCore.addElementChangedListener(p);
        return p;
    }

    @Singleton
    @Provides
    public EventBus provideWorkspaceEventBus() {
        final int numberOfCores = Runtime.getRuntime().availableProcessors();
        final ExecutorService pool = coreThreadsTimoutExecutor(numberOfCores + 1, MIN_PRIORITY,
                "Recommenders-Bus-Thread-", //$NON-NLS-1$
                1L, TimeUnit.MINUTES);
        return new AsyncEventBus("Recommenders asychronous Workspace Event Bus", pool); //$NON-NLS-1$
    }

    @Provides
    public IWebBrowser provideWebBrowser(IWorkbench wb) throws PartInitException {
        return wb.getBrowserSupport().getExternalBrowser();
    }

    @Provides
    @Singleton
    public JavaElementSelectionService provideJavaSelectionProvider(final EventBus bus) {
        final JavaElementSelectionService provider = new JavaElementSelectionService(bus);
        new UIJob("Registering workbench selection listener.") { //$NON-NLS-1$
            {
                schedule();
            }

            @Override
            public IStatus runInUIThread(final IProgressMonitor monitor) {
                final IWorkbenchWindow ww = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                final ISelectionService service = (ISelectionService) ww.getService(ISelectionService.class);
                service.addPostSelectionListener(provider);
                return Status.OK_STATUS;
            }
        };
        return provider;
    }

    @Provides
    public IWorkspaceRoot provideWorkspaceRoot() {
        return ResourcesPlugin.getWorkspace().getRoot();
    }

    @Provides
    public IWorkspace provideWorkspace() {
        return ResourcesPlugin.getWorkspace();
    }

    @Provides
    public Display provideDisplay() {
        Display d = Display.getCurrent();
        if (d == null) {
            d = Display.getDefault();
        }
        return d;
    }

    @Provides
    public IWorkbench provideWorkbench() {
        return PlatformUI.getWorkbench();
    }

    @Provides
    public IWorkbenchPage provideActiveWorkbenchPage(final IWorkbench wb) {

        if (isRunningInUiThread()) {
            return wb.getActiveWorkbenchWindow().getActivePage();
        }

        return runUiFinder().activePage;
    }

    @Provides
    public Shell provideActiveShell(IWorkbench wb) {
        return wb.getActiveWorkbenchWindow().getShell();
    }

    private ActivePageFinder runUiFinder() {
        final ActivePageFinder finder = new ActivePageFinder();
        try {
            if (isRunningInUiThread()) {
                finder.call();
            } else {
                final FutureTask<IWorkbenchPage> task = new FutureTask<IWorkbenchPage>(finder);
                Display.getDefault().asyncExec(task);
                task.get(2, TimeUnit.SECONDS);
            }
        } catch (final Exception e) {
            RcpPlugin.logError(e, Messages.LOG_ERROR_ACTIVE_PAGE_FINDER_TOO_EARLY);
        }
        return finder;
    }

    private boolean isRunningInUiThread() {
        return Display.getCurrent() != null;
    }

    @Provides
    public IJavaModel provideJavaModel() {
        return JavaModelManager.getJavaModelManager().getJavaModel();
    }

    @Provides
    public JavaModelManager provideJavaModelManger() {
        return JavaModelManager.getJavaModelManager();
    }

    @Provides
    public IExtensionRegistry provideRegistry() {
        return Platform.getExtensionRegistry();
    }

    static class RcpServiceMatcher extends AbstractMatcher<Object> {

        @Override
        public boolean matches(Object t) {
            if (t instanceof TypeLiteral<?>) {
                Class<?> rawType = ((TypeLiteral<?>) t).getRawType();
                Class<?>[] implemented = rawType.getInterfaces();
                return contains(implemented, IRcpService.class);
            }
            return false;
        }
    }

    /**
     * Ensures that the services are created
     */
    static class Helper {

        @Inject
        JavaElementSelectionService provider;

        @Inject
        JavaModelEventsService JavaModelEventsService;
    }

    static class Listener implements TypeListener {

        @Override
        public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
            final Provider<EventBus> provider = encounter.getProvider(EventBus.class);

            encounter.register(new InjectionListener<I>() {

                @Override
                public void afterInjection(final Object i) {
                    registerWithEventBus(i);
                    for (final Method m : i.getClass().getDeclaredMethods()) {
                        boolean hasPostConstruct = m.getAnnotation(PostConstruct.class) != null;
                        boolean hasPreDestroy = m.getAnnotation(PreDestroy.class) != null;
                        if (hasPreDestroy) {
                            registerPreDestroyHook(i, m);
                        }
                        if (hasPostConstruct) {
                            executeMethod(i, m);
                        }
                    }
                }

                private void executeMethod(final Object i, final Method m) {
                    try {
                        m.setAccessible(true);
                        m.invoke(i);
                    } catch (Exception e) {
                        RcpPlugin.logError(e, Messages.LOG_ERROR_EXCEPTION_IN_SERVICE_HOOK, m);
                    }
                }

                private void registerPreDestroyHook(final Object i, final Method m) {
                    PlatformUI.getWorkbench().addWorkbenchListener(new IWorkbenchListener() {

                        @Override
                        public boolean preShutdown(IWorkbench workbench, boolean forced) {
                            return true;
                        }

                        @Override
                        public void postShutdown(IWorkbench workbench) {
                            executeMethod(i, m);
                        }
                    });
                }

                private void registerWithEventBus(final Object i) {
                    EventBus bus = provider.get();
                    bus.register(i);
                }
            });
        }
    }

    private final class ActivePageFinder implements Callable<IWorkbenchPage> {
        private IWorkbench workbench;
        private IWorkbenchWindow activeWorkbenchWindow;
        private IWorkbenchPage activePage;

        @Override
        public IWorkbenchPage call() throws Exception {
            workbench = PlatformUI.getWorkbench();
            activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();
            activePage = activeWorkbenchWindow.getActivePage();
            return activePage;
        }
    }

    private void checkBundleResolution() {
        Bundle[] bundles = RcpPlugin.getDefault().getBundle().getBundleContext().getBundles();

        final Collection<Bundle> unresolvedBundles = Lists.newArrayList();
        for (Bundle bundle : bundles) {
            if (bundle.getSymbolicName().startsWith("org.eclipse.recommenders")) { //$NON-NLS-1$
                if (bundle.getState() == Bundle.INSTALLED) {
                    unresolvedBundles.add(bundle);
                }
            }
        }
        if (!unresolvedBundles.isEmpty()) {
            final Display display = Display.getDefault();
            display.asyncExec(new Runnable() {

                @Override
                public void run() {
                    BundleResolutionFailureDialog dialog = new BundleResolutionFailureDialog(display.getActiveShell(),
                            unresolvedBundles);
                    if (!dialog.isIgnored()) {
                        dialog.open();
                    }
                }
            });
        }
    }

    private static final class BundleResolutionFailureDialog extends MessageDialogWithToggle {

        private final Collection<Bundle> unresolvedBundles;

        private BundleResolutionFailureDialog(Shell parentShell, Collection<Bundle> unresolvedBundles) {
            super(parentShell, DIALOG_TITLE_BUNDLE_RESOLUTION_FAILURE, null, DIALOG_MESSAGE_BUNDLE_RESOLUTION_FAILURE,
                    MessageDialog.ERROR, new String[] { IDialogConstants.CANCEL_LABEL, DIALOG_BUTTON_RESTART }, 1,
                    DIALOG_TOGGLE_IGNORE_BUNDLE_RESOLUTION_FAILURES, false);
            this.unresolvedBundles = unresolvedBundles;
            setPrefStore(new ScopedPreferenceStore(InstanceScope.INSTANCE, Constants.BUNDLE_ID));
            setPrefKey(Constants.PREF_IGNORE_BUNDLE_RESOLUTION_FAILURE);
        }

        @Override
        protected Control createCustomArea(Composite parent) {
            Label bundleListLabel = new Label(parent, SWT.NONE);
            bundleListLabel.setText(Messages.DIALOG_LABEL_BUNDLE_LIST);

            List bundleList = new List(parent, SWT.BORDER | SWT.V_SCROLL | SWT.SINGLE);
            for (Bundle bundle : unresolvedBundles) {
                bundleList.add(bundle.getSymbolicName());
            }
            GridDataFactory.fillDefaults().grab(true, false).applyTo(bundleList);

            addLink(parent, DIALOG_MESSAGE_BUNDLE_RESOLUTION_FAQ, "https://www.eclipse.org/recommenders/faq/"); //$NON-NLS-1$

            addLink(parent, DIALOG_MESSAGE_BUNDLE_RESOLUTION_FILE_A_BUG,
                    "https://bugs.eclipse.org/bugs/enter_bug.cgi?product=Recommenders"); //$NON-NLS-1$

            return parent;
        }

        private void addLink(Composite parent, String text, String url) {
            Link link = new Link(parent, SWT.BEGINNING);
            link.setText(MessageFormat.format(text, url));
            link.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    try {
                        IWebBrowser browser = PlatformUI.getWorkbench().getBrowserSupport()
                                .createBrowser("recommenders-bugzilla"); //$NON-NLS-1$
                        browser.openURL(new URL(event.text));
                    } catch (Exception e) {
                    }
                }
            });
        }

        @Override
        protected void buttonPressed(int buttonId) {
            setReturnCode(buttonId);
            close();
            if (getToggleState() && getPrefStore() != null && getPrefKey() != null) {
                getPrefStore().setValue(getPrefKey(), ALWAYS);
                try {
                    ((ScopedPreferenceStore) getPrefStore()).save();
                } catch (IOException e) {
                    RcpPlugin.logError(e, "Save error");
                }
            }
            if (buttonId == IDialogConstants.INTERNAL_ID) {
                System.err.println(buttonId + " Restart");
                System.setProperty(PROP_EXIT_CODE, Integer.toString(24));
                System.setProperty(PROP_EXIT_DATA, buildCommandLine());
                PlatformUI.getWorkbench().restart();
            }
        }

        public boolean isIgnored() {
            return getPrefStore().getString(getPrefKey()).equals(MessageDialogWithToggle.ALWAYS);
        }

        private static final String PROP_VM = "eclipse.vm"; //$NON-NLS-1$

        private static final String PROP_VMARGS = "eclipse.vmargs"; //$NON-NLS-1$

        private static final String PROP_COMMANDS = "eclipse.commands"; //$NON-NLS-1$

        private static final String PROP_EXIT_CODE = "eclipse.exitcode"; //$NON-NLS-1$

        private static final String PROP_EXIT_DATA = "eclipse.exitdata"; //$NON-NLS-1$

        private static final String CMD_DATA = "-data"; //$NON-NLS-1$

        private static final String CMD_VMARGS = "-vmargs"; //$NON-NLS-1$

        private static final String NEW_LINE = "\n"; //$NON-NLS-1$

        /**
         * Create and return a string with command line options for eclipse.exe that will launch a new workbench that is
         * the same as the currently running one, but using the argument directory as its workspace.
         *
         * @param workspace
         *            the directory to use as the new workspace
         * @return a string of command line options or null on error
         */
        private String buildCommandLine() {
            String property = System.getProperty(PROP_VM);
            if (property == null) {
                System.err.println("Error with system property");
                // return null;
            }

            StringBuffer result = new StringBuffer(512);
            result.append(property);
            result.append(NEW_LINE);

            // append the vmargs and commands. Assume that these already end in \n
            String vmargs = System.getProperty(PROP_VMARGS);
            if (vmargs != null) {
                result.append(vmargs);
            }

            result.append("-clean");
            result.append(NEW_LINE);

            // append the rest of the args, replacing or adding -data as required
            property = System.getProperty(PROP_COMMANDS);
            result.append(property);
            result.append(NEW_LINE);

            // put the vmargs back at the very end (the eclipse.commands property
            // already contains the -vm arg)
            if (vmargs != null) {
                result.append(CMD_VMARGS);
                result.append(NEW_LINE);
                result.append(vmargs);
            }
            RcpPlugin.logWarning(result.toString());
            return result.toString();
        }
    }
}
