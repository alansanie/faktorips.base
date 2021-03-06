/*******************************************************************************
 * Copyright (c) Faktor Zehn AG. <http://www.faktorzehn.org>
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version
 * 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and
 * restrictions as well as the possibility of alternative license terms.
 *******************************************************************************/

package org.faktorips.valueset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Set;

import org.junit.Test;

public class LongRangeTest {

    @Test
    public void testConstructor() {
        LongRange range = new LongRange(5L, 10L);
        assertEquals(range.getLowerBound().longValue(), 5L);
        assertEquals(range.getUpperBound().longValue(), 10L);
    }

    @Test
    public void testSize() {
        LongRange range = new LongRange(5L, 10L);
        assertEquals(6, range.size());

        range = new LongRange(null, new Long(10));
        assertEquals(Integer.MAX_VALUE, range.size());

        range = new LongRange(new Long(10), null);
        assertEquals(Integer.MAX_VALUE, range.size());

        range = new LongRange(null, null);
        assertEquals(Integer.MAX_VALUE, range.size());

        try {
            range = new LongRange(1L, Integer.MAX_VALUE + 3L);
            range.size();
        } catch (RuntimeException e) {
            // Expected exception.
        }

        range = LongRange.valueOf(100L, 1100L, 200L);
        assertEquals(6, range.size());
    }

    @Test
    public void testGetValues() {
        LongRange range = LongRange.valueOf(100L, 1100L, 200L);
        Set<Long> values = range.getValues(false);
        assertTrue(values.contains(new Long(100)));
        assertTrue(values.contains(new Long(300)));
        assertTrue(values.contains(new Long(500)));
        assertTrue(values.contains(new Long(1100)));

        assertFalse(values.contains(new Long(200)));
        assertFalse(values.contains(new Long(1200)));
        assertFalse(values.contains(new Long(0)));

        range = LongRange.valueOf(100L, 1100L, 200L, true);
        values = range.getValues(false);
        assertTrue(values.contains(null));
    }

    @Test
    public void testValueOf() {
        assertEquals(new LongRange(2L, 5L), LongRange.valueOf("2", "5"));
        assertEquals(new LongRange(null, null), LongRange.valueOf("", ""));
        assertEquals(new LongRange(null, null), LongRange.valueOf(null, null));

        LongRange range = LongRange.valueOf(10L, 100L, 10L);
        assertEquals(new Long(10), range.getLowerBound());
        assertEquals(new Long(100), range.getUpperBound());
        assertEquals(new Long(10), range.getStep());

        try {
            LongRange.valueOf(10L, 101L, 10L);
            fail();
        } catch (IllegalArgumentException e) {
            // Expected exception.
        }

        range = LongRange.valueOf(new Long(10), new Long(100), new Long(10));
        assertEquals(new Long(10), range.getLowerBound());
        assertEquals(new Long(100), range.getUpperBound());
        assertEquals(new Long(10), range.getStep());
        assertFalse(range.containsNull());

        try {
            LongRange.valueOf(new Long(10), new Long(101), new Long(10));
            fail();
        } catch (IllegalArgumentException e) {
            // Expected exception.
        }

        range = LongRange.valueOf(new Long(10), new Long(100), new Long(10), true);
        assertTrue(range.containsNull());

        try {
            LongRange.valueOf(new Long(10), new Long(101), new Long(10), true);
            fail();
        } catch (IllegalArgumentException e) {
            // Expected exception.
        }

        try {
            LongRange.valueOf(new Long(10), new Long(101), new Long(0), true);
            fail("Expect to since zero step size is not allowed.");
        } catch (IllegalArgumentException e) {
            // Expected exception.
        }
    }

    @Test
    public void testSerializable() throws Exception {
        TestUtil.testSerializable(LongRange.valueOf(new Long(10), new Long(100), new Long(10)));
    }

}
