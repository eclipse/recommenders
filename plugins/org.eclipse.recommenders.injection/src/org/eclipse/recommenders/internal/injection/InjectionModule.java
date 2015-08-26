package org.eclipse.recommenders.internal.injection;

import static java.lang.Thread.MIN_PRIORITY;
import static org.apache.commons.lang3.ArrayUtils.contains;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import org.eclipse.recommenders.injection.IWorkbenchLifecycle;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

public class InjectionModule extends AbstractModule {

    @Override
    protected void configure() {
        bindInjectionServiceListener();
    }

    private void bindInjectionServiceListener() {
        bindListener(new InjectionServiceMatcher(), new Listener());
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

    static class InjectionServiceMatcher extends AbstractMatcher<Object> {

        @Override
        public boolean matches(Object t) {
            if (t instanceof TypeLiteral<?>) {
                Class<?> rawType = ((TypeLiteral<?>) t).getRawType();
                Class<?>[] implemented = rawType.getInterfaces();
                return contains(implemented, IWorkbenchLifecycle.class);
            }
            return false;
        }
    }

    static class Listener implements TypeListener {

        @Override
        public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
            final Provider<EventBus> provider = encounter.getProvider(EventBus.class);

            encounter.register(new InjectionListener<I>() {

                @Override
                public void afterInjection(final Object i) {
                    EventBus bus = provider.get();
                    bus.register(i);
                }
            });
        }
    }

    private static ThreadPoolExecutor coreThreadsTimoutExecutor(final int numberOfThreads, final int threadPriority,
            final String threadNamePrefix, final long timeout, final TimeUnit unit) {
        final ThreadFactory factory = new ThreadFactoryBuilder().setPriority(threadPriority)
                .setNameFormat(threadNamePrefix + "%d").setDaemon(true).build();
        final ThreadPoolExecutor pool = new ThreadPoolExecutor(numberOfThreads, numberOfThreads, timeout, unit,
                new LinkedBlockingQueue<Runnable>(), factory);
        pool.allowCoreThreadTimeOut(true);
        return pool;
    }
}
