/**
 * Copyright (c) 2010, 2011 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Patrick Gottschaemmer, Olav Lenz - initial tests
 */
package org.eclipse.recommenders.tests.extdoc;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.recommenders.extdoc.rcp.providers.ExtdocProvider;
import org.eclipse.recommenders.internal.extdoc.rcp.ui.ExtdocPreferences;
import org.eclipse.recommenders.internal.extdoc.rcp.ui.ExtdocView;
import org.eclipse.recommenders.internal.extdoc.rcp.ui.SubscriptionManager;
import org.junit.Before;
import org.junit.Test;

import com.google.common.eventbus.EventBus;

public class ExtdocDnDTest {

    private ExtdocView view;
    private List<ExtdocProvider> providers;
    private List<ExtdocProvider> defaultOrder;
    private ExtdocProvider firstProvider;
    private ExtdocProvider secondProvider;
    private ExtdocProvider thirdProvider;
    private ExtdocProvider fourthProvider;
    private ExtdocProvider fifthProvider;

    @Before
    public void setup() {
        EventBus bus = mock(EventBus.class);
        SubscriptionManager subManger = mock(SubscriptionManager.class);

        firstProvider = mock(ExtdocProvider.class);
        secondProvider = mock(ExtdocProvider.class);
        thirdProvider = mock(ExtdocProvider.class);
        fourthProvider = mock(ExtdocProvider.class);
        fifthProvider = mock(ExtdocProvider.class);

        providers = new LinkedList<ExtdocProvider>();
        providers.add(firstProvider);
        providers.add(secondProvider);
        providers.add(thirdProvider);
        providers.add(fourthProvider);
        providers.add(fifthProvider);

        defaultOrder = new LinkedList<ExtdocProvider>(providers);

        ExtdocPreferences preferences = mock(ExtdocPreferences.class);
        view = new ExtdocView(bus, subManger, providers, preferences);
    }

    @Test
    public void moveOnItselfBefore() {
        view.moveBefore(1, 1);

        assertEquals(defaultOrder, view.getProviderRanking());
    }

    @Test
    public void moveOnItselfAfter() {
        view.moveAfter(0, 0);

        assertEquals(defaultOrder, view.getProviderRanking());
    }

    @Test
    public void moveAfterNoChange() {
        view.moveAfter(3, 2);

        assertEquals(defaultOrder, view.getProviderRanking());
    }

    @Test
    public void moveBeforeNoChange() {
        view.moveBefore(2, 3);

        assertEquals(defaultOrder, view.getProviderRanking());
    }

    @Test
    public void moveBeforeTop() {
        view.moveBefore(4, 0);

        LinkedList<ExtdocProvider> expectedOrder = new LinkedList<ExtdocProvider>();
        expectedOrder.add(fifthProvider);
        expectedOrder.add(firstProvider);
        expectedOrder.add(secondProvider);
        expectedOrder.add(thirdProvider);
        expectedOrder.add(fourthProvider);

        assertEquals(expectedOrder, view.getProviderRanking());
    }

    @Test
    public void moveAfterTop() {
        view.moveAfter(2, 0);

        LinkedList<ExtdocProvider> expectedOrder = new LinkedList<ExtdocProvider>();
        expectedOrder.add(firstProvider);
        expectedOrder.add(thirdProvider);
        expectedOrder.add(secondProvider);
        expectedOrder.add(fourthProvider);
        expectedOrder.add(fifthProvider);

        assertEquals(expectedOrder, view.getProviderRanking());
    }

    @Test
    public void moveBeforeBottom() {
        view.moveBefore(2, 4);

        LinkedList<ExtdocProvider> expectedOrder = new LinkedList<ExtdocProvider>();
        expectedOrder.add(firstProvider);
        expectedOrder.add(secondProvider);
        expectedOrder.add(fourthProvider);
        expectedOrder.add(thirdProvider);
        expectedOrder.add(fifthProvider);

        assertEquals(expectedOrder, view.getProviderRanking());
    }

    @Test
    public void moveAfterBottom() {
        view.moveAfter(2, 4);

        LinkedList<ExtdocProvider> expectedOrder = new LinkedList<ExtdocProvider>();
        expectedOrder.add(firstProvider);
        expectedOrder.add(secondProvider);
        expectedOrder.add(fourthProvider);
        expectedOrder.add(fifthProvider);
        expectedOrder.add(thirdProvider);

        assertEquals(expectedOrder, view.getProviderRanking());
    }
}