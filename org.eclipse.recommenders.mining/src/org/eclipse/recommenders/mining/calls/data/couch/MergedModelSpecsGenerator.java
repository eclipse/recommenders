/**
 * Copyright (c) 2010, 2011 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Johannes Lerch - initial API and implementation.
 */
package org.eclipse.recommenders.mining.calls.data.couch;

import static com.google.common.collect.Sets.newHashSet;

import java.util.Collection;
import java.util.Set;

import javax.inject.Inject;

import org.eclipse.recommenders.commons.udc.LibraryIdentifier;
import org.eclipse.recommenders.commons.udc.ModelSpecification;
import org.eclipse.recommenders.commons.utils.Version;
import org.eclipse.recommenders.commons.utils.VersionRange;
import org.eclipse.recommenders.commons.utils.VersionRange.VersionRangeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MergedModelSpecsGenerator {

	private final Logger log = LoggerFactory.getLogger(getClass());
	private final CouchDbDataAccess db;

	@Inject
	public MergedModelSpecsGenerator(final CouchDbDataAccess db) {
		this.db = db;
	}

	public void execute() {
		final Collection<ModelSpecification> specs = db.getModelSpecifications();
		final Collection<LibraryIdentifier> ids = db.getLibraryIdentifiers();

		for (final LibraryIdentifier id : ids) {
			ModelSpecification spec = findMatch(specs, id);
			if (spec == null) {
				spec = createModelSpecification(id);
				specs.add(spec);
			} else {
				spec.addFingerprint(id.fingerprint);
			}
		}

		updateModelSpecsInDatabase(specs);
	}

	private ModelSpecification findMatch(final Collection<ModelSpecification> modelSpecs, final LibraryIdentifier libId) {
		for (final ModelSpecification modelSpec : modelSpecs) {
			if (hasNameOrAlias(modelSpec, libId.name)) {
				if (modelSpec.containsFingerprint(libId.fingerprint)
						|| modelSpec.getVersionRange().isIncluded(libId.version)) {
					return modelSpec;
				}
			}
		}
		return null;
	}

	private static boolean hasNameOrAlias(final ModelSpecification modelSpec, final String name) {
		if (modelSpec.getSymbolicName().equals(name)) {
			return true;
		}

		for (final String alias : modelSpec.getAliases()) {
			if (alias.equals(name)) {
				return true;
			}
		}
		return false;
	}

	private ModelSpecification createModelSpecification(final LibraryIdentifier libraryIdentifier) {
		final VersionRange range = createVersionRange(libraryIdentifier);
		final Set<String> fingerprints = newHashSet(libraryIdentifier.fingerprint);
		return new ModelSpecification(libraryIdentifier.name, new String[0], range, null, fingerprints);
	}

	private VersionRange createVersionRange(final LibraryIdentifier libraryIdentifier) {
		if (libraryIdentifier.version.isUnknown()) {
			return VersionRange.EMPTY;
		} else {
			final int minMajor = libraryIdentifier.version.major;
			final Version minVersion = Version.create(minMajor, 0);
			final Version maxVersion = Version.create(minMajor + 1, 0);
			return new VersionRangeBuilder().minInclusive(minVersion).maxExclusive(maxVersion).build();
		}
	}

	private void updateModelSpecsInDatabase(Collection<ModelSpecification> modelSpecs) {
		for (ModelSpecification spec : modelSpecs) {
			db.save(spec);
			log.info("Updated model spec for '{}'.", spec.getIdentifier());
		}
		log.info("Model spec update opertions done.");
	}
}