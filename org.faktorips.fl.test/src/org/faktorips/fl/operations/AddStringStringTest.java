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

package org.faktorips.fl.operations;

import org.faktorips.datatype.Datatype;
import org.faktorips.fl.ExprCompiler;
import org.faktorips.fl.JavaExprCompilerAbstractTest;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 */
public class AddStringStringTest extends JavaExprCompilerAbstractTest {

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        compiler.setBinaryOperations(toArray(new AddStringString()));
    }

    @Test
    public void test() throws Exception {
        execAndTestSuccessfull("\"a\" + \"b\"", "ab", Datatype.STRING);
    }

    @Test
    public void testLhsError() throws Exception {
        execAndTestFail("a a + \"b\"", ExprCompiler.SYNTAX_ERROR);
    }

    @Test
    public void testRhsError() throws Exception {
        execAndTestFail("\"b\" + a a", ExprCompiler.SYNTAX_ERROR);
    }

}
