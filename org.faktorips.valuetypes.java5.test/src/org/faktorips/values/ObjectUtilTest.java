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

package org.faktorips.values;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * 
 * @author Jan Ortmann
 */
public class ObjectUtilTest {

    @Test
    public void testIsNull() {
        assertTrue(ObjectUtil.isNull(null));
        assertTrue(ObjectUtil.isNull(Money.NULL));
        assertFalse(ObjectUtil.isNull(Money.euro(42, 0)));
    }

    @Test
    public void testEqualsObject() {
        assertTrue(ObjectUtil.equals(null, null));

        Object o1 = new Object();
        assertTrue(ObjectUtil.equals(o1, o1));
        assertFalse(ObjectUtil.equals(null, o1));
        assertFalse(ObjectUtil.equals(o1, null));

        Object o2 = new Object();
        assertFalse(ObjectUtil.equals(o1, o2));
    }

    @Test(expected = ClassCastException.class)
    public void testCheckInstanceOf_fail() throws Exception {
        ObjectUtil.checkInstanceOf("", Integer.class);
    }

    @Test(expected = ClassCastException.class)
    public void testCheckInstanceOf_failOnNull() throws Exception {
        ObjectUtil.checkInstanceOf(null, Integer.class);
    }

    @Test
    public void testCheckInstanceOf() throws Exception {
        ObjectUtil.checkInstanceOf(12, Number.class);
        ObjectUtil.checkInstanceOf(12, Integer.class);
        ObjectUtil.checkInstanceOf(new Long(12), Number.class);
    }

    @Test(expected = ClassCastException.class)
    public void testCheckInstanceOfOrNull_fail() throws Exception {
        ObjectUtil.checkInstanceOfOrNull("", Integer.class);
    }

    @Test
    public void testCheckInstanceOfOrNull_doesNothingForNull() throws Exception {
        ObjectUtil.checkInstanceOfOrNull(null, Number.class);
    }

    @Test
    public void testCheckInstanceOfOrNull() throws Exception {
        ObjectUtil.checkInstanceOfOrNull(12, Number.class);
        ObjectUtil.checkInstanceOfOrNull(12, Integer.class);
        ObjectUtil.checkInstanceOfOrNull(new Long(12), Number.class);
    }

}
