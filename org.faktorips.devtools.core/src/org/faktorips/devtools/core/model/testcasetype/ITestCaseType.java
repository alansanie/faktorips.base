/***************************************************************************************************
 * Copyright (c) 2005,2006 Faktor Zehn GmbH und andere.
 * 
 * Alle Rechte vorbehalten.
 * 
 * Dieses Programm und alle mitgelieferten Sachen (Dokumentationen, Beispiele, Konfigurationen,
 * etc.) dürfen nur unter den Bedingungen der Faktor-Zehn-Community Lizenzvereinbarung – Version 0.1
 * (vor Gründung Community) genutzt werden, die Bestandteil der Auslieferung ist und auch unter
 * http://www.faktorips.org/legal/cl-v01.html eingesehen werden kann.
 * 
 * Mitwirkende: Faktor Zehn GmbH - initial API and implementation
 * 
 **************************************************************************************************/

package org.faktorips.devtools.core.model.testcasetype;

import org.eclipse.core.runtime.CoreException;
import org.faktorips.devtools.core.internal.model.testcasetype.TestRuleParameter;
import org.faktorips.devtools.core.model.ipsobject.IIpsObject;
import org.faktorips.devtools.core.model.ipsproject.IIpsProject;
import org.faktorips.devtools.core.model.pctype.IValidationRule;

/**
 * Specification of a test case type.
 * 
 * @author Jan Ortmann
 */
public interface ITestCaseType extends IIpsObject {

    /**
     * Prefix for all message codes of this class.
     */
    public final static String MSGCODE_PREFIX = "TESTCASETYPE-"; //$NON-NLS-1$

    /**
     * Creates a new test input value parameter.
     */
    public ITestValueParameter newInputTestValueParameter();

    /**
     * Creates a new test input policy component type parameter.
     */
    public ITestPolicyCmptTypeParameter newInputTestPolicyCmptTypeParameter();

    /**
     * Creates a new test expected result value parameter.
     */
    public ITestValueParameter newExpectedResultValueParameter();

    /**
     * Creates a new test expected result policy component type parameter.
     */
    public ITestPolicyCmptTypeParameter newExpectedResultPolicyCmptTypeParameter();

    /**
     * Creates a new test expected result rule parameter.
     */
    public TestRuleParameter newExpectedResultRuleParameter();
    
    /**
     * Creates a new test combined value parameter.
     */
    public ITestValueParameter newCombinedValueParameter();

    /**
     * Creates a new test combined policy component type parameter.
     */
    public ITestPolicyCmptTypeParameter newCombinedPolicyCmptTypeParameter();

    /**
     * Search and return the test parameter by the given name.
     * <p>
     * Returns <code>null</code> if the test parameter was not found.
     * Returns the first test parameter if more than one test parameter found.
     * 
     * @throws CoreException if an error occurs.
     */
    public ITestParameter getTestParameterByName(String testParameterName) throws CoreException;

    /**
     * Returns all test parameters.
     * <p>
     * Returns an empty list if the test case type contains no test parameters.
     */
    public ITestParameter[] getTestParameters();
    
    /**
     * Returns all input parameters or an empty array if the test case type hasn't got any input
     * parameters.
     */
    public ITestParameter[] getInputTestParameters();

    /**
     * Returns all test value parameters or an empty array if the test case type hasn't got
     * any test value parameters.
     */
    public ITestValueParameter[] getTestValueParameters();
    
    /**
     * Returns all test rule parameters or an empty array if the test case
     * type hasn't got any test rule parameters.
     */
    public ITestRuleParameter[] getTestRuleParameters();
    
    /**
     * Returns all root test policy component type parameters or an empty array if the test case
     * type hasn't got any test policy component type parameters.
     */
    public ITestPolicyCmptTypeParameter[] getTestPolicyCmptTypeParameters();
    
    /**
     * Returns all input test value parameters or an empty array if the test case type hasn't got
     * any input test value parameters.
     */
    public ITestValueParameter[] getInputTestValueParameters();

    /**
     * Returns all input test policy component type parameters or an empty array if the test case
     * type hasn't got any input test policy component type parameters.
     */
    public ITestPolicyCmptTypeParameter[] getInputTestPolicyCmptTypeParameters();

    /**
     * Returns the input test value parameter or <code>null</code> if not found.
     * Returns the first test parameter if more than one test parameter found.
     *      
     * @throws CoreException if an error occurs.
     */
    public ITestValueParameter getInputTestValueParameter(String inputTestValueParameter) throws CoreException;

    /**
     * Returns the input test policy component type parameter or <code>null</code> if not found.
     * Returns the first test parameter if more than one test parameter found.
     * 
     * @throws CoreException if an error occurs.
     */
    public ITestPolicyCmptTypeParameter getInputTestPolicyCmptTypeParameter(String inputTestPolicyCmptTypeParameter)
            throws CoreException;

    /**
     * Returns all expected result parameters or an empty array if the test case type hasn't got any
     * result parameters.
     */
    public ITestParameter[] getExpectedResultTestParameters();

    /**
     * Returns all expected result test value parameters or an empty array if the test case type
     * hasn't got any expected result test value parameters.
     */
    public ITestValueParameter[] getExpectedResultTestValueParameters();

    /**
     * Returns all expected result test policy component type parameters or an empty array if the
     * test case type hasn't got any expected result test policy component type parameters.
     */
    public ITestPolicyCmptTypeParameter[] getExpectedResultTestPolicyCmptTypeParameters();

    /**
     * Returns the expected result test value parameter or <code>null</code> if not found.
     * Returns the first test parameter if more than one test parameter found.
     * 
     * @throws CoreException if an error occurs.
     */
    public ITestValueParameter getExpectedResultTestValueParameter(String expResultTestValueParameter)
            throws CoreException;

    /**
     * Returns the expected result test policy component type parameter or <code>null</code> if
     * not found.
     * Returns the first test parameter if more than one test parameter found.
     * 
     * @throws CoreException if an error occurs.
     */
    public ITestPolicyCmptTypeParameter getExpectedResultTestPolicyCmptTypeParameter(String expResultTestPolicyCmptTypeParameter)
            throws CoreException;
    
    /**
     * Evaluates and returns an unique name (inside this test case) for the test attribute.
     */
    public String generateUniqueNameForTestAttribute(ITestAttribute testAttribute, String name); 
    
    /**
     * Moves the test parameter identified by the indexes up or down by one position. If one of the
     * indexes is 0 (the first test parameter), nothing is moved up. If one of the indexes is the
     * number of parameters - 1 (the last test parameter) nothing moved down
     * 
     * @param indexes The indexes identifying the test parameter.
     * @param up <code>true</code>, to move up, <false> to move them down.
     * 
     * @return The new indexes of the moved test parameter.
     * 
     * @throws NullPointerException if indexes is null.
     * @throws IndexOutOfBoundsException if one of the indexes does not identify a test parameter.
     */
    public int[] moveTestParameters(int[] indexes, boolean up);
    
    /**
     * Returns all validation rules from the test policy cmpts which are related by the test policy
     * cmpt type parameters inside this test case type.
     * @param ipsProject TODO
     * 
     * @throws CoreException if an error occurs
     */
    public IValidationRule[] getTestRuleCandidates(IIpsProject ipsProject) throws CoreException;
    
    /**
     * Searchs and returns the validation rule with the given name which is inside the test case type.
     * The rule will be searched in all policy cmpt types which are related by test test policy cmpt type 
     * parameters.
     * @param ipsProject TODO
     */
    public IValidationRule findValidationRule(String validationRuleName, IIpsProject ipsProject) throws CoreException;
}
