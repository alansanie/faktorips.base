/*******************************************************************************
 * Copyright (c) Faktor Zehn AG. <http://www.faktorzehn.org>
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version
 * 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and
 * restrictions as well as the possibility of alternative license terms.
 *******************************************************************************/

package org.faktorips.datatype.joda;

import junit.framework.TestCase;

import org.junit.Test;

public class LocalTimeDatatypeTest extends TestCase {

    private LocalTimeDatatype datatype;

    @Test
    public void testIsParsable() {
        datatype = new LocalTimeDatatype();
        assertTrue(datatype.isParsable(null));
        assertTrue(datatype.isParsable(""));
        assertTrue(datatype.isParsable("10:44:00"));
        assertTrue(datatype.isParsable("23:59:59"));
        assertFalse(datatype.isParsable("24:61:61"));
    }
}