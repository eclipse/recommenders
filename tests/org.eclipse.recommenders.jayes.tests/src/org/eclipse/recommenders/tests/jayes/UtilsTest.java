/*******************************************************************************
 * Copyright (c) 2012 Michael Kutschke.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Michael Kutschke - initial API and implementation
 ******************************************************************************/
package org.eclipse.recommenders.tests.jayes;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.eclipse.recommenders.internal.jayes.util.AddressCalc;
import org.eclipse.recommenders.jayes.util.MathUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class UtilsTest {

    private static final int[] testVector = new int[] { 2, 3, 5, 7 };
    private static final double[] testDoubleVector = new double[] { 2.0, 3.0, 5.0, 7.0 };
    private static final double TOLERANCE = 0.00001;

    @Test
    public void testSumOfRange() {
        assertEquals(MathUtils.sum(testDoubleVector), 17.0, TOLERANCE);
        assertEquals(MathUtils.sumRange(testDoubleVector, 1, 1), 0.0, TOLERANCE);
        assertEquals(MathUtils.sumRange(testDoubleVector, 0, 2), 5.0, TOLERANCE);
        assertEquals(MathUtils.sumRange(testDoubleVector, 0, 3), 10.0, TOLERANCE);
        assertEquals(MathUtils.sumRange(testDoubleVector, 0, 4), 17.0, TOLERANCE);

        assertEquals(MathUtils.sumRange(testDoubleVector, 1, 4), 15.0, TOLERANCE);
        assertEquals(MathUtils.sumRange(testDoubleVector, 2, 4), 12.0, TOLERANCE);
        assertEquals(MathUtils.sumRange(testDoubleVector, 3, 4), 7.0, TOLERANCE);
    }

    @Test
    public void testProductOfRange() {
        assertEquals(MathUtils.productOfRange(testVector, 1, 3), 3 * 5);
        assertEquals(MathUtils.productOfRange(testVector, 0, 1), 2);
        assertEquals(MathUtils.productOfRange(testVector, 2, 2), 1);

        // having a result of 1 if start > end is consistent,
        // because the set of numbers that are multiplied is still empty,
        // as in the case start == end
        assertEquals(MathUtils.productOfRange(testVector, 3, 2), 1);
    }

    @Test
    public void testProduct() {
        assertEquals(MathUtils.product(testVector), 2 * 3 * 5 * 7);
    }

    @Test
    public void testScalarProduct() {
        assertEquals(MathUtils.scalarProduct(testVector, testVector), 2 * 2 + 3 * 3 + 5 * 5 + 7 * 7);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testScalaProductFailure() {
        int[] pair = new int[2];
        int[] triple = new int[3];
        MathUtils.scalarProduct(pair, triple);
    }

    @Test
    public void testNormalize() {
        assertArrayEquals(new double[] { 2.0 / 17.0, 3.0 / 17.0, 5.0 / 17.0, 7.0 / 17.0 },
                MathUtils.normalize(testDoubleVector.clone()), TOLERANCE);
    }

    @Test
    public void testNormalizeRange() {
        assertArrayEquals(new double[] { 2.0 / 5.0, 3.0 / 5.0, 5.0, 7.0 },
                MathUtils.normalizeRange(testDoubleVector.clone(), 0, 2), TOLERANCE);

        assertArrayEquals(new double[] { 2.0, 3.0, 5.0 / 12.0, 7.0 / 12.0 },
                MathUtils.normalizeRange(testDoubleVector.clone(), 2, 4), TOLERANCE);
    }

    @Test
    public void testNormalizeCpt() {
        assertArrayEquals(new double[] { 2.0 / 5.0, 3.0 / 5.0, 5.0 / 12.0, 7.0 / 12.0 },
                MathUtils.normalizeCpt(testDoubleVector.clone(), 2), TOLERANCE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNormalizeCptIllegalOutcomes() {
        MathUtils.normalizeCpt(testDoubleVector.clone(), 3);
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testMultiDimensionalCounter() {
        int[] dimensions = new int[] { 3, 2, 1 };
        int[] counter = new int[] { 0, 0, -1 };

        AddressCalc.incrementMultiDimensionalCounter(counter, dimensions);

        assertArrayEquals(new int[] { 0, 0, 0 }, counter);

        AddressCalc.incrementMultiDimensionalCounter(counter, dimensions);

        assertArrayEquals(new int[] { 0, 1, 0 }, counter);

        AddressCalc.incrementMultiDimensionalCounter(counter, dimensions);

        assertArrayEquals(new int[] { 1, 0, 0 }, counter);

        AddressCalc.incrementMultiDimensionalCounter(counter, dimensions);

        assertArrayEquals(new int[] { 1, 1, 0 }, counter);

        AddressCalc.incrementMultiDimensionalCounter(counter, dimensions);

        assertArrayEquals(new int[] { 2, 0, 0 }, counter);

        AddressCalc.incrementMultiDimensionalCounter(counter, dimensions);

        assertArrayEquals(new int[] { 2, 1, 0 }, counter);

        thrown.expect(ArrayIndexOutOfBoundsException.class);

        AddressCalc.incrementMultiDimensionalCounter(counter, dimensions);
    }

}
