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

import static com.google.inject.Scopes.SINGLETON;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;

import org.eclipse.core.internal.net.ProxyManager;
import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.recommenders.internal.rcp.RcpPlugin;
import org.eclipse.recommenders.models.IModelArchiveCoordinateAdvisor;
import org.eclipse.recommenders.models.IModelIndex;
import org.eclipse.recommenders.models.IModelRepository;
import org.eclipse.recommenders.models.IProjectCoordinateAdvisor;
import org.eclipse.recommenders.models.advisors.JREDirectoryNameAdvisor;
import org.eclipse.recommenders.models.advisors.JREExecutionEnvironmentAdvisor;
import org.eclipse.recommenders.models.advisors.JREReleaseFileAdvisor;
import org.eclipse.recommenders.models.advisors.MavenCentralFingerprintSearchAdvisor;
import org.eclipse.recommenders.models.advisors.MavenPomPropertiesAdvisor;
import org.eclipse.recommenders.models.advisors.MavenPomXmlAdvisor;
import org.eclipse.recommenders.models.advisors.ModelIndexBundleSymbolicNameAdvisor;
import org.eclipse.recommenders.models.advisors.ModelIndexFingerprintAdvisor;
import org.eclipse.recommenders.models.advisors.OsgiManifestAdvisor;
import org.eclipse.recommenders.models.advisors.ProjectCoordinateAdvisorService;
import org.eclipse.recommenders.models.rcp.IProjectCoordinateProvider;
import org.eclipse.ui.IWorkbench;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.io.Files;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.name.Names;

@SuppressWarnings("restriction")
public class ModelsRcpModule extends AbstractModule implements Module {

    public static final String IDENTIFIED_PACKAGE_FRAGMENT_ROOTS = "IDENTIFIED_PACKAGE_FRAGMENT_ROOTS";
    public static final String REPOSITORY_BASEDIR = "REPOSITORY_BASEDIR";
    public static final String INDEX_BASEDIR = "INDEX_BASEDIR";
    public static final String MANUAL_MAPPINGS = "MANUAL_MAPPINGS";

    private static final Logger LOG = LoggerFactory.getLogger(ModelsRcpModule.class);

    private static final String EXT_ID_PROVIDER = "org.eclipse.recommenders.models.rcp.advisors";

    @Override
    protected void configure() {
        requestStaticInjection(Dependencies.class);
        //
        bind(IProjectCoordinateProvider.class).to(ProjectCoordinateProvider.class).in(SINGLETON);

        // bind all clients of IRecommendersModelIndex or its super interface IModelArchiveCoordinateProvider to a
        // single instance in Eclipse:
        bind(EclipseModelIndex.class).in(SINGLETON);
        bind(IModelArchiveCoordinateAdvisor.class).to(EclipseModelIndex.class);
        bind(IModelIndex.class).to(EclipseModelIndex.class);
        createAndBindNamedFile("index", INDEX_BASEDIR);

        //
        bind(EclipseModelRepository.class).in(SINGLETON);
        bind(IModelRepository.class).to(EclipseModelRepository.class);
        createAndBindNamedFile("repository", REPOSITORY_BASEDIR);

        // configure caching
        bind(ManualProjectCoordinateAdvisor.class).in(SINGLETON);
        createAndBindNamedFile("caches/manual-mappings.json", MANUAL_MAPPINGS);
        createAndBindNamedFile("caches/identified-project-coordinates.json", IDENTIFIED_PACKAGE_FRAGMENT_ROOTS);

    }

    private void createAndBindNamedFile(String fileName, String name) {
        Bundle bundle = FrameworkUtil.getBundle(getClass());
        File stateLocation = Platform.getStateLocation(bundle).toFile();
        File file = new File(stateLocation, fileName);
        try {
            Files.createParentDirs(file);
        } catch (IOException e) {
            LOG.error("failed to bind file name", e);
        }
        bind(File.class).annotatedWith(Names.named(name)).toInstance(file);
    }

    @Singleton
    @Provides
    public EclipseDependencyListener provideMappingProvider(EventBus bus) {
        return new EclipseDependencyListener(bus);
    }

    @Provides
    public IProxyService provideProxyService() {
        return ProxyManager.getProxyManager();
    }

    @Provides
    public List<IProjectCoordinateAdvisor> provideAdvisors(IModelIndex index,
            ManualProjectCoordinateAdvisor manualMappingStrategy) {
        List<IProjectCoordinateAdvisor> availableAdvisors = Lists.newArrayList();
        availableAdvisors.add(manualMappingStrategy);
        availableAdvisors.addAll(instantiateAdvisorsFromRegistry());
        // availableAdvisors.add(new MavenPomPropertiesAdvisor());
        // availableAdvisors.add(new JREExecutionEnvironmentAdvisor());
        // availableAdvisors.add(new JREReleaseFileAdvisor());
        // availableAdvisors.add(new JREDirectoryNameAdvisor());
        // availableAdvisors.add(new MavenPomXmlAdvisor());
        // availableAdvisors.add(new ModelIndexBundleSymbolicNameAdvisor(index));
        // availableAdvisors.add(new ModelIndexFingerprintAdvisor(index));
        // availableAdvisors.add(new OsgiManifestAdvisor());
        // availableAdvisors.add(new MavenCentralFingerprintSearchAdvisor());
        return ImmutableList.copyOf(availableAdvisors);
    }

    @Provides
    public ModelIndexBundleSymbolicNameAdvisor provideModelIndexBundleSymbolicNameAdvisor(IModelIndex index) {
        return new ModelIndexBundleSymbolicNameAdvisor(index);
    }

    @Provides
    public ModelIndexFingerprintAdvisor provideModelIndexFingerprintAdvisor(IModelIndex index) {
        return new ModelIndexFingerprintAdvisor(index);
    }

    private static List<IProjectCoordinateAdvisor> instantiateAdvisorsFromRegistry() {
        final IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(
                EXT_ID_PROVIDER);
        final List<AdvisorDescriptor> descriptors = Lists.newLinkedList();

        for (final IConfigurationElement element : elements) {
            final Optional<AdvisorDescriptor> opt = createProvider(element);
            if (opt.isPresent()) {
                descriptors.add(opt.get());
            }
        }

        Collections.sort(descriptors, new Comparator<AdvisorDescriptor>() {

            @Override
            public int compare(AdvisorDescriptor lhs, AdvisorDescriptor rhs) {
                return Integer.valueOf(lhs.defaultPriority).compareTo(rhs.defaultPriority);
            }
        });
        return Lists.transform(descriptors, new Function<AdvisorDescriptor, IProjectCoordinateAdvisor>() {

            @Override
            public IProjectCoordinateAdvisor apply(AdvisorDescriptor descriptor) {
                return descriptor.advisor;
            }
        });
    }

    private static Optional<AdvisorDescriptor> createProvider(final IConfigurationElement element) {
        final String pluginId = element.getContributor().getName();
        try {
            final int defaultPriority = Integer.valueOf(element.getAttribute("defaultPriority")); //$NON-NLS-1$
            final boolean enabledByDefault = Boolean.valueOf(Objects.firstNonNull(
                    element.getAttribute("enabledByDefault"), "true"));
            final IProjectCoordinateAdvisor provider = (IProjectCoordinateAdvisor) element
                    .createExecutableExtension("class"); //$NON-NLS-1$
            AdvisorDescriptor descriptor = new AdvisorDescriptor(provider, defaultPriority, enabledByDefault);
            return Optional.of(descriptor);
        } catch (final Exception e) {
            RcpPlugin.logError(e, "failed to instantiate advisor %s:%s", //$NON-NLS-1$
                    pluginId, element.getAttribute("class")); //$NON-NLS-1$
            return Optional.absent();
        }
    }

    private static class AdvisorDescriptor {

        private final IProjectCoordinateAdvisor advisor;
        private final int defaultPriority;
        private final boolean enabledByDefault;

        public AdvisorDescriptor(IProjectCoordinateAdvisor advisor, int defaultPriority, boolean enabledByDefault) {
            this.advisor = advisor;
            this.defaultPriority = defaultPriority;
            this.enabledByDefault = enabledByDefault;
        }
    }

    @Singleton
    @Provides
    public ProjectCoordinateAdvisorService provideMappingProvider(List<IProjectCoordinateAdvisor> availableAdvisors,
            ModelsRcpPreferences prefs) {
        ProjectCoordinateAdvisorService mappingProvider = new ProjectCoordinateAdvisorService();
        mappingProvider.setAdvisors(Advisors.createAdvisorList(availableAdvisors, prefs.advisors));
        return mappingProvider;
    }

    @Provides
    @Singleton
    public ModelsRcpPreferences provide(IWorkbench wb) {
        IEclipseContext context = (IEclipseContext) wb.getService(IEclipseContext.class);
        ModelsRcpPreferences prefs = ContextInjectionFactory.make(ModelsRcpPreferences.class, context);
        return prefs;
    }
}
