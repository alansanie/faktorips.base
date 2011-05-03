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

package org.faktorips.fl;

import java.util.Locale;

import org.faktorips.datatype.Datatype;
import org.faktorips.values.Decimal;
import org.faktorips.values.Money;

/**
 * Contains all tests for the unary plus (+) operator that as defined by the default unary
 * operations.
 */
public class PlusTest extends CompilerAbstractTest {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testDecimal() throws Exception {
        execAndTestSuccessfull("+ 10.23", Decimal.valueOf(1023, 2), Datatype.DECIMAL);
    }

    public void testInteger() throws Exception {
        compiler.add(new ExcelFunctionsResolver(Locale.ENGLISH));
        execAndTestSuccessfull("+ WHOLENUMBER(42.2)", new Integer(42), Datatype.INTEGER);
    }

    public void testInt() throws Exception {
        compiler.setEnsureResultIsObject(false);
        CompilationResult result = compiler.compile("+ 42");
        assertTrue(result.successfull());
        assertEquals(Datatype.PRIMITIVE_INT, result.getDatatype());
        assertEquals("42", result.getCodeFragment().getSourcecode());
    }

    public void testMoney() throws Exception {
        execAndTestSuccessfull("+ 10.12EUR", Money.valueOf("10.12EUR"), Datatype.MONEY);
    }

}