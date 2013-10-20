/**
 * Copyright (c) 2010, 2011 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 */
package org.eclipse.recommenders.rcp;

import static com.google.common.collect.Lists.newArrayList;
import static org.eclipse.jface.viewers.StructuredSelection.EMPTY;
import static org.eclipse.recommenders.tests.jdt.JdtMockUtils.someField;
import static org.eclipse.recommenders.tests.jdt.JdtMockUtils.someJavaModel;
import static org.eclipse.recommenders.tests.jdt.JdtMockUtils.someLocalVariable;
import static org.eclipse.recommenders.tests.jdt.JdtMockUtils.someMethod;
import static org.eclipse.recommenders.tests.jdt.JdtMockUtils.someType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.recommenders.internal.rcp.JavaElementSelectionService;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;

public class JavaSelectionListenerTest {

    static final int DELAY = 110;

    JavaElementSelectionService sut;
    JavaSelectionListenerSpy spy;

    @Before
    public void beforeTest() {
        EventBus bus = new EventBus();
        sut = new JavaElementSelectionService(bus);
        spy = new JavaSelectionListenerSpy();
        bus.register(spy);
    }

    @Test
    public void testStructuredSelectionWithType() throws InterruptedException {

        final List<?> expected = newArrayList(someType(), someMethod(), someField(), someLocalVariable(),
                someJavaModel());
        for (final Object e : expected) {
            sut.selectionChanged(null, new StructuredSelection(e));
            Thread.sleep(DELAY);
        }
        assertEquals(expected, Lists.transform(spy.get(), new Function<JavaElementSelectionEvent, IJavaElement>() {

            @Override
            public IJavaElement apply(JavaElementSelectionEvent arg0) {
                return arg0.getElement();
            }
        }));
    }

    @Test
    public void testFireEventTwiceSelection() throws InterruptedException {

        final IType someType = someType();
        sut.selectionChanged(null, new StructuredSelection(someType));
        sut.selectionChanged(null, new StructuredSelection(someType));
        Thread.sleep(DELAY);

        assertEquals(1, spy.get().size());
    }

    @Test
    public void testEmptyStructuredSelection() {
        sut.selectionChanged(null, EMPTY);
        assertTrue(spy.get().isEmpty());
    }

    @Test
    public void testAnyUnknownSelectionType() {
        sut.selectionChanged(null, mock(ISelection.class));
        assertTrue(spy.get().isEmpty());
    }

}
