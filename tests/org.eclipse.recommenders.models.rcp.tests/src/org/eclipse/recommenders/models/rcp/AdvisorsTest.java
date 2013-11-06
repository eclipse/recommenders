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
package org.eclipse.recommenders.models.rcp;

import static com.google.common.base.Optional.absent;
import static org.eclipse.recommenders.internal.models.rcp.Advisors.*;
import static org.eclipse.recommenders.internal.models.rcp.Advisors.Filter.*;
import static org.junit.Assert.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.recommenders.models.DependencyInfo;
import org.eclipse.recommenders.models.DependencyType;
import org.eclipse.recommenders.models.IProjectCoordinateAdvisor;
import org.eclipse.recommenders.models.ProjectCoordinate;
import org.eclipse.recommenders.models.advisors.AbstractProjectCoordinateAdvisor;
import org.junit.Test;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class AdvisorsTest {

    private static class Advisor1 extends DefaultAdvisor {
    }

    private static class Advisor2 extends DefaultAdvisor {
    }

    private static class Advisor3 extends DefaultAdvisor {
    }

    private static class DefaultAdvisor extends AbstractProjectCoordinateAdvisor {

        @Override
        protected boolean isApplicable(DependencyType type) {
            return false;
        }

        @Override
        protected Optional<ProjectCoordinate> doSuggest(DependencyInfo dependencyInfo) {
            return absent();
        }
    }

    private static final IProjectCoordinateAdvisor A1 = new Advisor1();
    private static final IProjectCoordinateAdvisor A2 = new Advisor2();
    private static final IProjectCoordinateAdvisor A3 = new Advisor3();

    @Test
    public void testPrefStringCreationWithEmptyAdvisorList() {
        String prefString = createPreferenceStringFromAdvisors(Collections.<IProjectCoordinateAdvisor>emptyList(),
                Collections.<IProjectCoordinateAdvisor>emptySet());
        assertEquals("", prefString);
    }

    @Test
    public void testPrefStringCreationAllAdvisorDisabled() {
        String actual = createPreferenceStringFromAdvisors(ImmutableList.of(A1, A2, A3),
                Collections.<IProjectCoordinateAdvisor>emptySet());
        String expected = "!" + A1.getClass().getName() + ";!" + A2.getClass().getName() + ";!"
                + A3.getClass().getName() + ";";
        assertEquals(expected, actual);
    }

    @Test
    public void testPrefStringCreationAllAdvisorEnabled() {
        String actual = createPreferenceStringFromAdvisors(ImmutableList.of(A1, A2, A3), ImmutableSet.of(A1, A2, A3));
        String expected = A1.getClass().getName() + ";" + A2.getClass().getName() + ";" + A3.getClass().getName() + ";";
        assertEquals(expected, actual);
    }

    @Test
    public void testPrefStringCreationOneAdvisorEnabled() {
        String actual = createPreferenceStringFromAdvisors(ImmutableList.of(A1, A2, A3), ImmutableSet.of(A2));
        String expected = "!" + A1.getClass().getName() + ";" + A2.getClass().getName() + ";!"
                + A3.getClass().getName() + ";";
        assertEquals(expected, actual);
    }

    @Test
    public void testPrefStringCreationOrderOfAdvisorsMatters() {
        String actual = createPreferenceStringFromAdvisors(ImmutableList.of(A3, A1, A2), ImmutableSet.of(A1, A2, A3));
        String expected = A3.getClass().getName() + ";" + A1.getClass().getName() + ";" + A2.getClass().getName() + ";";
        assertEquals(expected, actual);
    }

    @Test
    public void testPrefStringCreationTwoAdvisorEnabled() {
        String actual = createPreferenceStringFromAdvisors(ImmutableList.of(A1, A2, A3), ImmutableSet.of(A2, A3));
        String expected = "!" + A1.getClass().getName() + ";" + A2.getClass().getName() + ";" + A3.getClass().getName()
                + ";";
        assertEquals(expected, actual);
    }

    @Test
    public void testAdvisorListCreationWithEmptyParameters() {
        List<IProjectCoordinateAdvisor> actual = createAdvisorList(Collections.<IProjectCoordinateAdvisor>emptyList(),
                "");
        assertTrue(actual.isEmpty());
    }

    @Test
    public void testAdvisorListCreationWithAllAdvisorsEnabled() {
        List<IProjectCoordinateAdvisor> availableAdvisors = ImmutableList.of(A1, A2, A3);

        String prefString = createPreferenceStringFromAdvisors(ImmutableList.of(A1, A2, A3),
                ImmutableSet.of(A1, A2, A3));

        List<IProjectCoordinateAdvisor> actual = createAdvisorList(availableAdvisors, prefString);
        assertEquals(availableAdvisors, actual);
    }

    @Test
    public void testAdvisorListCreationWithAllAdvisorsDisabled() {
        List<IProjectCoordinateAdvisor> availableAdvisors = ImmutableList.of(A1, A2, A3);

        String prefString = createPreferenceStringFromAdvisors(ImmutableList.of(A1, A2, A3),
                Collections.<IProjectCoordinateAdvisor>emptySet());

        List<IProjectCoordinateAdvisor> actual = createAdvisorList(availableAdvisors, prefString);
        assertTrue(actual.isEmpty());
    }

    @Test
    public void testAdvisorListCreationWithOneAdvisorsDisabled() {
        List<IProjectCoordinateAdvisor> availableAdvisors = ImmutableList.of(A1, A2, A3);

        String prefString = createPreferenceStringFromAdvisors(ImmutableList.of(A1, A2, A3), ImmutableSet.of(A1));

        List<IProjectCoordinateAdvisor> actual = createAdvisorList(availableAdvisors, prefString);
        assertEquals(ImmutableList.of(A1), actual);
    }

    @Test
    public void testAdvisorListCreationWithTwoAdvisorsDisabled() {
        List<IProjectCoordinateAdvisor> availableAdvisors = ImmutableList.of(A1, A2, A3);

        String prefString = createPreferenceStringFromAdvisors(ImmutableList.of(A1, A2, A3), ImmutableSet.of(A1, A2));

        List<IProjectCoordinateAdvisor> actual = createAdvisorList(availableAdvisors, prefString);
        assertEquals(ImmutableList.of(A1, A2), actual);
    }

    @Test
    public void testAdvisorListCreationWithAdvisorOrderMatters() {
        List<IProjectCoordinateAdvisor> availableAdvisors = ImmutableList.of(A1, A2, A3);

        String prefString = createPreferenceStringFromAdvisors(ImmutableList.of(A3, A1, A2),
                ImmutableSet.of(A1, A2, A3));

        List<IProjectCoordinateAdvisor> actual = createAdvisorList(availableAdvisors, prefString);
        assertEquals(ImmutableList.of(A3, A1, A2), actual);
    }

    @Test
    public void testAdvisorListCreationWithEmptyPrefString() {
        List<IProjectCoordinateAdvisor> availableAdvisors = ImmutableList.of(A1, A2, A3);

        List<IProjectCoordinateAdvisor> actual = createAdvisorList(availableAdvisors, "");
        assertEquals(ImmutableList.of(), actual);
    }

    @Test
    public void testAdvisorStringExtractionOfAll() {
        String prefString = createPreferenceStringFromAdvisors(ImmutableList.of(A1, A2, A3),
                ImmutableSet.of(A1, A2, A3));
        List<String> actual = extractAdvisors(prefString, ALL);

        List<String> expected = ImmutableList.of(A1.getClass().getName(), A2.getClass().getName(), A3.getClass()
                .getName());
        assertEquals(expected, actual);
    }

    @Test
    public void testAdvisorStringExtractionOfEnabled() {
        String prefString = createPreferenceStringFromAdvisors(ImmutableList.of(A1, A2, A3), ImmutableSet.of(A1, A3));
        List<String> actual = extractAdvisors(prefString, ENABLED);

        List<String> expected = ImmutableList.of(A1.getClass().getName(), A3.getClass().getName());
        assertEquals(expected, actual);
    }

    @Test
    public void testAdvisorStringExtractionOfDisabled() {
        String prefString = createPreferenceStringFromAdvisors(ImmutableList.of(A1, A2, A3), ImmutableSet.of(A1));
        List<String> actual = extractAdvisors(prefString, DISABLED);

        List<String> expected = ImmutableList.of(A2.getClass().getName(), A3.getClass().getName());
        assertEquals(expected, actual);
    }

    @Test
    public void testPrefStringCreationWithAllEnabledAdvisors() {
        List<String> orderedAdvisors = ImmutableList.of(A1.getClass().getName(), A2.getClass().getName(), A3.getClass()
                .getName());
        Set<String> enabledAdvisors = ImmutableSet.of(A1.getClass().getName(), A2.getClass().getName(), A3.getClass()
                .getName());
        String actual = createPreferenceStringFromClassNames(orderedAdvisors, enabledAdvisors);

        String expected = createPreferenceStringFromAdvisors(ImmutableList.of(A1, A2, A3), ImmutableSet.of(A1, A2, A3));
        assertEquals(expected, actual);
    }

    @Test
    public void testPrefStringCreationWithAllDisabledAdvisors() {
        List<String> orderedAdvisors = ImmutableList.of(A1.getClass().getName(), A2.getClass().getName(), A3.getClass()
                .getName());
        String actual = createPreferenceStringFromClassNames(orderedAdvisors, Collections.<String>emptySet());

        String expected = createPreferenceStringFromAdvisors(ImmutableList.of(A1, A2, A3),
                Collections.<IProjectCoordinateAdvisor>emptySet());
        assertEquals(expected, actual);
    }

    @Test
    public void testPrefStringCreationWithDisAnEnabledAdvisors() {
        List<String> orderedAdvisors = ImmutableList.of(A1.getClass().getName(), A2.getClass().getName(), A3.getClass()
                .getName());
        Set<String> enabledAdvisors = ImmutableSet.of(A2.getClass().getName());
        String actual = createPreferenceStringFromClassNames(orderedAdvisors, enabledAdvisors);

        String expected = createPreferenceStringFromAdvisors(ImmutableList.of(A1, A2, A3), ImmutableSet.of(A2));
        assertEquals(expected, actual);
    }

    @Test
    public void testPrefStringCreationOrderMatters() {
        List<String> orderedAdvisors = ImmutableList.of(A3.getClass().getName(), A1.getClass().getName(), A2.getClass()
                .getName());
        Set<String> enabledAdvisors = ImmutableSet.of(A2.getClass().getName());
        String actual = createPreferenceStringFromClassNames(orderedAdvisors, enabledAdvisors);

        String expected = createPreferenceStringFromAdvisors(ImmutableList.of(A3, A1, A2), ImmutableSet.of(A2));
        assertEquals(expected, actual);
    }
}
