/**
 * Copyright (c) 2010 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 */
package org.eclipse.recommenders.internal.completion.rcp.overrides;

import static org.eclipse.recommenders.utils.Checks.ensureIsTrue;
import static org.eclipse.recommenders.utils.Throws.throwUnhandledException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.recommenders.internal.completion.rcp.overrides.net.ClassOverridesNetwork;
import org.eclipse.recommenders.internal.completion.rcp.overrides.net.ClassOverridesNetworkBuilder;
import org.eclipse.recommenders.internal.completion.rcp.overrides.net.ClassOverridesObservation;
import org.eclipse.recommenders.utils.FixedSizeLinkedHashMap;
import org.eclipse.recommenders.utils.annotations.Clumsy;
import org.eclipse.recommenders.utils.annotations.Nullable;
import org.eclipse.recommenders.utils.names.ITypeName;

import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;

@Clumsy
public class OverridesModelStore {

    @Inject
    private IOverridesModelLoader loader;

    private Set<ITypeName> supportedTypes;

    private final Map<ITypeName, ClassOverridesNetwork> loadedNetworks = FixedSizeLinkedHashMap.create(30);

    private void init() {
        if (supportedTypes == null) {
            supportedTypes = loader.readAvailableTypes();
        }
    }

    public boolean hasModel(@Nullable final ITypeName name) {
        init();
        return name == null ? false : supportedTypes.contains(name);
    }

    public ClassOverridesNetwork getModel(final ITypeName name) {
        ensureIsTrue(hasModel(name));
        //
        ClassOverridesNetwork network = loadedNetworks.get(name);
        if (network == null) {
            network = loadNetwork(name);
            loadedNetworks.put(name, network);
        }
        return network;
    }

    private ClassOverridesNetwork loadNetwork(final ITypeName name) {
        try {
            final List<ClassOverridesObservation> observations = loadObservations(name);
            return createNetwork(name, observations);
        } catch (final IOException x) {
            throw throwUnhandledException(x);
        }
    }

    private List<ClassOverridesObservation> loadObservations(final ITypeName name) throws IOException {
        final Type listType = new TypeToken<List<ClassOverridesObservation>>() {
        }.getType();
        final List<ClassOverridesObservation> observations = loader.loadObjectForTypeName(name, listType);
        if (observations.size() == 0) {
            // XXX sanitize bad models! need to ensure minimum quality for
            // models.
            observations.add(new ClassOverridesObservation());
        }
        return observations;
    }

    private ClassOverridesNetwork createNetwork(final ITypeName name, final List<ClassOverridesObservation> observations) {
        final ClassOverridesNetworkBuilder b = new ClassOverridesNetworkBuilder(name, observations);
        b.createPatternsNode();
        b.createMethodNodes();
        final ClassOverridesNetwork network = b.build();
        return network;
    }

}
