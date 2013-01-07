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
package org.eclipse.recommenders.tests.models;

import static com.google.common.base.Optional.fromNullable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.eclipse.recommenders.models.ProjectCoordinate;
import org.eclipse.recommenders.models.mapping.DependencyType;
import org.eclipse.recommenders.models.mapping.IDependencyInfo;
import org.eclipse.recommenders.models.mapping.IMappingProvider;
import org.eclipse.recommenders.models.mapping.IMappingStrategy;
import org.eclipse.recommenders.models.mapping.impl.DependencyInfo;
import org.eclipse.recommenders.models.mapping.impl.MappingProvider;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.base.Optional;
import com.google.common.cache.CacheStats;
import com.google.common.collect.Lists;

public class MappingProviderTest {

    private final static ProjectCoordinate EXPECTED_PROJECT_COORDINATE = new ProjectCoordinate("example",
            "example.project", "1.0.0");
    private final static ProjectCoordinate ANOTHER_EXPECTED_PROJECT_COORDINATE = new ProjectCoordinate(
            "another.example", "another.example.project", "1.2.3");

    private IMappingStrategy createMockedStrategy(ProjectCoordinate projectCoordinate,
            DependencyType... dependencyTypes) {
        IMappingStrategy mockedStrategy = Mockito.mock(IMappingStrategy.class);
        Mockito.when(mockedStrategy.searchForProjectCoordinate(Mockito.any(IDependencyInfo.class))).thenReturn(
                fromNullable(projectCoordinate));
        Mockito.when(mockedStrategy.isApplicable(Mockito.any(DependencyType.class))).thenReturn(false);
        for (DependencyType dependencyType : dependencyTypes) {
            Mockito.when(mockedStrategy.isApplicable(dependencyType)).thenReturn(true);
        }
        return mockedStrategy;
    }

    @Test
    public void testMappingProviderWithNoStrategy() {
        IMappingProvider sut = new MappingProvider();
        Optional<ProjectCoordinate> optionalProjectCoordinate = sut.searchForProjectCoordinate(new DependencyInfo(
                new File("example.jar"), DependencyType.JAR));

        assertFalse(optionalProjectCoordinate.isPresent());
    }

    @Test
    public void testValidJRE() {
        IMappingProvider sut = new MappingProvider();
        sut.addStrategy(createMockedStrategy(EXPECTED_PROJECT_COORDINATE));
        Optional<ProjectCoordinate> optionalProjectCoordinate = sut.searchForProjectCoordinate(new DependencyInfo(
                new File("example.jar"), DependencyType.JAR));

        assertEquals(EXPECTED_PROJECT_COORDINATE, optionalProjectCoordinate.get());
    }

    @Test
    public void testCorrectOrderOfStrategiesWithAddStrategies() {
        IMappingProvider sut = new MappingProvider();
        sut.addStrategy(createMockedStrategy(EXPECTED_PROJECT_COORDINATE));
        sut.addStrategy(createMockedStrategy(ANOTHER_EXPECTED_PROJECT_COORDINATE));

        Optional<ProjectCoordinate> optionalProjectCoordinate = sut.searchForProjectCoordinate(new DependencyInfo(
                new File("example.jar"), DependencyType.JAR));

        assertEquals(EXPECTED_PROJECT_COORDINATE, optionalProjectCoordinate.get());
    }

    @Test
    public void testSetStrategiesSetStrategiesCorrect() {
        IMappingProvider sut = new MappingProvider();

        List<IMappingStrategy> strategies = Lists.newArrayList();
        strategies.add(createMockedStrategy(EXPECTED_PROJECT_COORDINATE));
        strategies.add(createMockedStrategy(ANOTHER_EXPECTED_PROJECT_COORDINATE));
        sut.setStrategy(strategies);

        assertEquals(strategies, sut.getStrategies());
    }

    @Test
    public void testIsApplicableWithoutStrategies() {
        IMappingProvider sut = new MappingProvider();
        assertFalse(sut.isApplicable(DependencyType.JAR));
    }

    @Test
    public void testIsApplicableWithStrategies() {
        IMappingProvider sut = new MappingProvider();
        sut.addStrategy(createMockedStrategy(ProjectCoordinate.UNKNOWN, DependencyType.JRE));
        sut.addStrategy(createMockedStrategy(ProjectCoordinate.UNKNOWN, DependencyType.JAR));
        assertTrue(sut.isApplicable(DependencyType.JAR));
    }

    @Test
    public void testCorrectOrderOfStrategiesWithSetStrategies() {
        IMappingProvider sut = new MappingProvider();

        List<IMappingStrategy> strategies = Lists.newArrayList();
        strategies.add(createMockedStrategy(EXPECTED_PROJECT_COORDINATE));
        strategies.add(createMockedStrategy(ANOTHER_EXPECTED_PROJECT_COORDINATE));
        sut.setStrategy(strategies);

        Optional<ProjectCoordinate> optionalProjectCoordinate = sut.searchForProjectCoordinate(new DependencyInfo(
                new File("example.jar"), DependencyType.JAR));

        assertEquals(EXPECTED_PROJECT_COORDINATE, optionalProjectCoordinate.get());
    }

    @Test
    public void testSecondStrategyWins() {
        IMappingProvider sut = new MappingProvider();

        List<IMappingStrategy> strategies = Lists.newArrayList();
        strategies.add(createMockedStrategy(null));
        strategies.add(createMockedStrategy(EXPECTED_PROJECT_COORDINATE));
        sut.setStrategy(strategies);

        Optional<ProjectCoordinate> optionalProjectCoordinate = sut.searchForProjectCoordinate(new DependencyInfo(
                new File("example.jar"), DependencyType.JAR));

        assertEquals(EXPECTED_PROJECT_COORDINATE, optionalProjectCoordinate.get());
    }

    @Test
    public void testMappingCacheMissAtFirstTime() {
        MappingProvider sut = new MappingProvider();
        sut.addStrategy(createMockedStrategy(EXPECTED_PROJECT_COORDINATE));
        DependencyInfo dependencyInfo = new DependencyInfo(new File("example.jar"), DependencyType.JAR);
        sut.searchForProjectCoordinate(dependencyInfo);

        CacheStats cacheStats = sut.getCacheStats().get();

        assertEquals(1, cacheStats.missCount());
    }

    @Test
    public void testMappingCacheHitAtSecondTime() {
        MappingProvider sut = new MappingProvider();
        sut.addStrategy(createMockedStrategy(EXPECTED_PROJECT_COORDINATE));
        DependencyInfo dependencyInfo = new DependencyInfo(new File("example.jar"), DependencyType.JAR);
        sut.searchForProjectCoordinate(dependencyInfo);
        sut.searchForProjectCoordinate(dependencyInfo);

        CacheStats cacheStats = sut.getCacheStats().get();

        assertEquals(1, cacheStats.hitCount());
    }

}
