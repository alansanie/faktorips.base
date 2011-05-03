/*******************************************************************************
 * Copyright (c) 2005-2010 Faktor Zehn AG und andere.
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

package org.faktorips.datatype;

import junit.framework.TestCase;

import org.faktorips.util.message.MessageList;

public class GenericValueDatatypeTest extends TestCase {

    private DefaultGenericValueDatatype datatype;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        datatype = new DefaultGenericValueDatatype(PaymentMode.class);
    }

    public void testValidate_ClassNotFound() {
        GenericValueDatatype type = new InvalidType();
        MessageList list = type.checkReadyToUse();
        assertEquals(1, list.getNoOfMessages());
        assertNotNull(list.getMessageByCode(GenericValueDatatype.MSGCODE_JAVACLASS_NOT_FOUND));
    }

    public void testValidate_InvalidMethods() {
        datatype.setIsParsableMethodName("unknownMethod"); //$NON-NLS-1$
        datatype.setToStringMethodName("unknownMethod"); //$NON-NLS-1$
        datatype.setValueOfMethodName("unknownMethod"); //$NON-NLS-1$
        datatype.setNullObjectDefined(false);
        MessageList list = datatype.checkReadyToUse();
        assertEquals(3, list.getNoOfMessages());
        assertNotNull(list.getMessageByCode(GenericValueDatatype.MSGCODE_GETVALUE_METHOD_NOT_FOUND));
        assertNotNull(list.getMessageByCode(GenericValueDatatype.MSGCODE_ISPARSABLE_METHOD_NOT_FOUND));
        assertNotNull(list.getMessageByCode(GenericValueDatatype.MSGCODE_TOSTRING_METHOD_NOT_FOUND));
    }

    public void testValidate_InvalidSpecialCaseNull() {
        datatype.setIsParsableMethodName("isParsable"); //$NON-NLS-1$
        datatype.setToStringMethodName("getId"); //$NON-NLS-1$
        datatype.setValueOfMethodName("getPaymentMode"); //$NON-NLS-1$
        datatype.setNullObjectDefined(true);
        datatype.setNullObjectId("unknownValue"); //$NON-NLS-1$
        MessageList list = datatype.checkReadyToUse();
        assertEquals(1, list.getNoOfMessages());
        assertNotNull(list.getMessageByCode(GenericValueDatatype.MSGCODE_SPECIALCASE_NULL_NOT_FOUND));

        datatype.setNullObjectId(PaymentMode.ANNUAL.getId());
        list = datatype.checkReadyToUse();
        assertNotNull(list.getMessageByCode(GenericValueDatatype.MSGCODE_SPECIALCASE_NULL_IS_NOT_NULL));
    }

    public void testIsParsable() {
        datatype.setIsParsableMethodName("isParsable"); //$NON-NLS-1$
        assertTrue(datatype.isParsable(PaymentMode.ANNUAL.getId()));
        assertFalse(datatype.isParsable("unknownId")); //$NON-NLS-1$
        assertTrue(datatype.isParsable(null));

        datatype = new DefaultGenericValueDatatype(TestValueClass.class);
        datatype.setValueOfMethodName("getInteger"); //$NON-NLS-1$
        datatype.setIsParsableMethodName("isInteger"); //$NON-NLS-1$
        assertTrue(datatype.isParsable("42")); //$NON-NLS-1$
        assertTrue(datatype.isParsable(null));
        assertFalse(datatype.isParsable("abc")); //$NON-NLS-1$
    }

    public void testGetIsParsableMethod() {
        assertNotNull(datatype.getIsParsableMethod());
        datatype.setIsParsableMethodName("unknownMethod"); //$NON-NLS-1$
        try {
            datatype.getIsParsableMethod();
            fail();
        } catch (RuntimeException e) {
            // Expected exception
        }
    }

    public void testGetValue() {
        datatype.setValueOfMethodName("getPaymentMode"); //$NON-NLS-1$
        assertEquals(PaymentMode.ANNUAL, datatype.getValue(PaymentMode.ANNUAL.getId()));

        datatype = new DefaultGenericValueDatatype(TestValueClass.class);
        datatype.setValueOfMethodName("getInteger"); //$NON-NLS-1$
        assertEquals(new Integer(42), datatype.getValue("42")); //$NON-NLS-1$
        assertNull(datatype.getValue(null));
    }

    public void testGetValueOfMethod() {
        datatype.setValueOfMethodName("getPaymentMode"); //$NON-NLS-1$
        assertNotNull(datatype.getValueOfMethod());
        datatype.setValueOfMethodName("unknownMethod"); //$NON-NLS-1$
        try {
            datatype.getValueOfMethod();
            fail();
        } catch (RuntimeException e) {
            // Expected exception
        }
    }

    public void testValueToString() {
        datatype.setToStringMethodName("getId"); //$NON-NLS-1$
        assertEquals(PaymentMode.ANNUAL.getId(), datatype.valueToString(PaymentMode.ANNUAL));
    }

    public void testGetToStringMethod() {
        datatype.setToStringMethodName("getId"); //$NON-NLS-1$
        assertNotNull(datatype.getToStringMethod());
        datatype.setToStringMethodName("unknownMethod"); //$NON-NLS-1$
        try {
            datatype.getValueOfMethod();
            fail();
        } catch (RuntimeException e) {
            // Expected exception
        }
        // Payment hasn't got a special toString method, but the super type
        datatype.setToStringMethodName("toString"); //$NON-NLS-1$
        assertNotNull(datatype.getToStringMethod());
    }

    public void testEquals() {
        assertEquals(datatype, datatype);
        assertFalse(datatype.equals(Datatype.INTEGER));
        GenericValueDatatype paymentMode2 = new GenericValueDatatype() {

            @Override
            public Class getAdaptedClass() {
                return null;
            }

            @Override
            public String getAdaptedClassName() {
                return null;
            }

        };
        paymentMode2.setQualifiedName("PaymentMode"); //$NON-NLS-1$
        assertEquals(datatype, paymentMode2);
    }

    public void testHashCode() {
        assertEquals(datatype.hashCode(), datatype.hashCode());
        assertFalse(datatype.hashCode() == Datatype.INTEGER.hashCode());
        GenericValueDatatype paymentMode2 = new GenericValueDatatype() {

            @Override
            public Class getAdaptedClass() {
                return null;
            }

            @Override
            public String getAdaptedClassName() {
                return null;
            }

        };
        paymentMode2.setQualifiedName("PaymentMode"); //$NON-NLS-1$
        assertEquals(datatype.hashCode(), paymentMode2.hashCode());
    }

    public void testIsNull() {
        datatype.setValueOfMethodName("getPaymentMode"); //$NON-NLS-1$
        assertFalse(datatype.isNull(PaymentMode.ANNUAL.getId()));
        assertTrue(datatype.isNull(null));
    }

    private class InvalidType extends GenericValueDatatype {

        @Override
        public Class getAdaptedClass() {
            return null;
        }

        @Override
        public String getAdaptedClassName() {
            return "UnknownClass"; //$NON-NLS-1$
        }

    }

}