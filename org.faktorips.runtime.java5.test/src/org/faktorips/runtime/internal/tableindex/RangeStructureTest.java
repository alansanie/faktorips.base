/*******************************************************************************
 * Copyright (c) 2005-2012 Faktor Zehn AG und andere.
 * 
 * Alle Rechte vorbehalten.
 * 
 * Dieses Programm und alle mitgelieferten Sachen (Dokumentationen, Beispiele, Konfigurationen,
 * etc.) duerfen nur unter den Bedingungen der Faktor-Zehn-Community Lizenzvereinbarung - Version
 * 0.1 (vor Gruendung Community) genutzt werden, die Bestandteil der Auslieferung ist und auch unter
 * http://www.faktorzehn.org/fips:lizenz eingesehen werden kann.
 * 
 * Mitwirkende: Faktor Zehn AG - initial API and implementation - http://www.faktorzehn.de
 *******************************************************************************/

package org.faktorips.runtime.internal.tableindex;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.HashSet;
import java.util.Set;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;

public class RangeStructureTest {
    private RangeStructure<Integer, ResultStructure<String>, String> structure;

    @Test(expected = NullPointerException.class)
    public void testGet_null() {
        structure.get(null);
    }

    @Test(expected = NullPointerException.class)
    public void testConstructor() {
        createStructure(null);
    }

    @Test
    public void testGet_KeyTypeLowerBoundEqual() {
        createStructure(RangeType.LOWER_BOUND_EQUAL);
        resultSetForKeyWayOutOfLowerBound(isEmpty());
        resultSetForKeyLessThanSmallestLowerBound(isEmpty());
        resultSetForKeyExactSmallestLowerBound(hasItem("A"));
        resultSetForKeyGreaterThanSmallestLowerBound(hasItem("A"));
        resultSetForKeyInRange1(hasItem("A"));
        resultSetForKeyInRange2(hasItem("B"));
        resultSetForKeyLessThanGreatestUpperBound(hasItem("B"));
        resultSetForKeyExactGreatestUpperBound(hasItem("C"));
        resultSetForKeyGreaterThanGreatestUpperBound(hasItem("C"));
        resultSetForKeyWayOutOfUpperBound(hasItem("C"));
        resultSetForKeyNull(isEmpty());
    }

    @SuppressWarnings("deprecation")
    /**
     * Tests the {@link RangeStructure} with {@link RangeType#LOWER_BOUND}. May be deleted when
     * {@link RangeType#LOWER_BOUND} is removed.
     */
    @Test
    public void testGet_KeyTypeLowerBound() {
        createStructure(RangeType.LOWER_BOUND);
        resultSetForKeyWayOutOfLowerBound(isEmpty());
        resultSetForKeyLessThanSmallestLowerBound(isEmpty());
        resultSetForKeyExactSmallestLowerBound(isEmpty());
        resultSetForKeyGreaterThanSmallestLowerBound(hasItem("A"));
        resultSetForKeyInRange1(hasItem("A"));
        resultSetForKeyInRange2(hasItem("B"));
        resultSetForKeyLessThanGreatestUpperBound(hasItem("B"));
        resultSetForKeyExactGreatestUpperBound(hasItem("B"));
        resultSetForKeyGreaterThanGreatestUpperBound(hasItem("C"));
        resultSetForKeyWayOutOfUpperBound(hasItem("C"));
        resultSetForKeyNull(isEmpty());
    }

    @Test
    public void testGet_KeyTypeUpperBoundEqual() {
        createStructure(RangeType.UPPER_BOUND_EQUAL);
        resultSetForKeyWayOutOfLowerBound(hasItem("A"));
        resultSetForKeyLessThanSmallestLowerBound(hasItem("A"));
        resultSetForKeyExactSmallestLowerBound(hasItem("A"));
        resultSetForKeyGreaterThanSmallestLowerBound(hasItem("B"));
        resultSetForKeyInRange1(hasItem("B"));
        resultSetForKeyInRange2(hasItem("C"));
        resultSetForKeyLessThanGreatestUpperBound(hasItem("C"));
        resultSetForKeyExactGreatestUpperBound(hasItem("C"));
        resultSetForKeyGreaterThanGreatestUpperBound(isEmpty());
        resultSetForKeyWayOutOfUpperBound(isEmpty());
        resultSetForKeyNull(isEmpty());
    }

    @SuppressWarnings("deprecation")
    /**
     * Tests the {@link RangeStructure} with {@link RangeType#UPPER_BOUND}. May be deleted when
     * {@link RangeType#UPPER_BOUND} is removed.
     */
    @Test
    public void testGet_KeyTypeUpperBound() {
        createStructure(RangeType.UPPER_BOUND);
        resultSetForKeyWayOutOfLowerBound(hasItem("A"));
        resultSetForKeyLessThanSmallestLowerBound(hasItem("A"));
        resultSetForKeyExactSmallestLowerBound(hasItem("B"));
        resultSetForKeyGreaterThanSmallestLowerBound(hasItem("B"));
        resultSetForKeyInRange1(hasItem("B"));
        resultSetForKeyInRange2(hasItem("C"));
        resultSetForKeyLessThanGreatestUpperBound(hasItem("C"));
        resultSetForKeyExactGreatestUpperBound(isEmpty());
        resultSetForKeyGreaterThanGreatestUpperBound(isEmpty());
        resultSetForKeyWayOutOfUpperBound(isEmpty());
        resultSetForKeyNull(isEmpty());
    }

    private void createStructure(RangeType keyType) {
        structure = RangeStructure.create(keyType);
        structure.put(-5, new ResultStructure<String>("A"));
        structure.put(2, new ResultStructure<String>("B"));
        structure.put(10, new ResultStructure<String>("C"));
    }

    public void resultSetForKeyWayOutOfLowerBound(Matcher<Iterable<String>> matcher) {
        assertThat(structure.get(-100).get(), matcher);
    }

    public void resultSetForKeyLessThanSmallestLowerBound(Matcher<Iterable<String>> matcher) {
        assertThat(structure.get(-6).get(), matcher);
    }

    public void resultSetForKeyExactSmallestLowerBound(Matcher<Iterable<String>> matcher) {
        assertThat(structure.get(-5).get(), matcher);
    }

    public void resultSetForKeyGreaterThanSmallestLowerBound(Matcher<Iterable<String>> matcher) {
        assertThat(structure.get(-4).get(), matcher);
    }

    public void resultSetForKeyInRange1(Matcher<Iterable<String>> matcher) {
        assertThat(structure.get(0).get(), matcher);
    }

    public void resultSetForKeyInRange2(Matcher<Iterable<String>> matcher) {
        assertThat(structure.get(5).get(), matcher);
    }

    public void resultSetForKeyLessThanGreatestUpperBound(Matcher<Iterable<String>> matcher) {
        assertThat(structure.get(9).get(), matcher);
    }

    public void resultSetForKeyExactGreatestUpperBound(Matcher<Iterable<String>> matcher) {
        assertThat(structure.get(10).get(), matcher);
    }

    public void resultSetForKeyGreaterThanGreatestUpperBound(Matcher<Iterable<String>> matcher) {
        assertThat(structure.get(11).get(), matcher);
    }

    public void resultSetForKeyWayOutOfUpperBound(Matcher<Iterable<String>> matcher) {
        assertThat(structure.get(100).get(), matcher);
    }

    public void resultSetForKeyNull(Matcher<Iterable<String>> matcher) {
        assertThat(structure.get(null).get(), matcher);
    }

    private static Matcher<Iterable<String>> isEmpty() {
        return new IsEmpty<String>();
    }

    private static class IsEmpty<T> extends BaseMatcher<Iterable<T>> {

        @Override
        public boolean matches(Object item) {
            if (item instanceof Set) {
                Iterable<?> iterable = (Iterable<?>)item;
                return !iterable.iterator().hasNext();
            }
            return false;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("an empty Iterable");
        }
    }

    @Test
    public void testIsEmptyMatcher() {
        Set<String> set = new HashSet<String>();
        IsEmpty<String> matcher = new IsEmpty<String>();
        assertTrue(matcher.matches(set));

        set.add("String");
        assertFalse(matcher.matches(set));
    }

    @Test
    public void testDescribeEmptyMatcher() {
        IsEmpty<String> matcher = new IsEmpty<String>();
        Description description = mock(Description.class);
        matcher.describeTo(description);
        verify(description).appendText("an empty Iterable");
    }

}
