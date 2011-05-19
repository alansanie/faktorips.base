/*******************************************************************************
 * Copyright (c) 2005-2011 Faktor Zehn AG und andere.
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

package org.faktorips.devtools.stdbuilder.refactor;

import static org.faktorips.devtools.stdbuilder.StdBuilderHelper.booleanParam;
import static org.faktorips.devtools.stdbuilder.StdBuilderHelper.intParam;
import static org.faktorips.devtools.stdbuilder.StdBuilderHelper.stringParam;
import static org.faktorips.devtools.stdbuilder.StdBuilderHelper.unresolvedParam;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IType;
import org.faktorips.datatype.Datatype;
import org.faktorips.devtools.core.model.bf.IBusinessFunction;
import org.faktorips.devtools.core.model.enums.IEnumLiteralNameAttributeValue;
import org.faktorips.devtools.core.model.enums.IEnumType;
import org.faktorips.devtools.core.model.ipsobject.Modifier;
import org.faktorips.devtools.core.model.pctype.AttributeType;
import org.faktorips.devtools.core.model.pctype.IPolicyCmptType;
import org.faktorips.devtools.core.model.pctype.IPolicyCmptTypeAttribute;
import org.faktorips.devtools.core.model.productcmpttype.IProductCmptType;
import org.faktorips.devtools.core.model.productcmpttype.IProductCmptTypeAttribute;
import org.faktorips.devtools.core.model.tablestructure.ITableStructure;
import org.faktorips.devtools.core.model.testcasetype.ITestCaseType;
import org.faktorips.devtools.core.model.valueset.ValueSetType;
import org.faktorips.devtools.stdbuilder.ProjectConfigurationUtil;
import org.faktorips.runtime.IValidationContext;
import org.faktorips.util.StringUtil;
import org.junit.Test;

/**
 * Tests the various Faktor-IPS "Rename" refactorings with regard to the generated Java source code.
 * 
 * @author Alexander Weickmann
 */
public class RenameRefactoringParticipantTest extends RefactoringParticipantTest {

    @Test
    public void testRenamePolicyCmptTypeAttributeValueSetUnrestricted() throws CoreException {
        performTestRenamePolicyCmptTypeAttribute(ValueSetType.UNRESTRICTED);
    }

    @Test
    public void testRenamePolicyCmptTypeAttributeValueSetEnum() throws CoreException {
        performTestRenamePolicyCmptTypeAttribute(ValueSetType.ENUM);
    }

    @Test
    public void testRenamePolicyCmptTypeAttributeValueSetRange() throws CoreException {
        performTestRenamePolicyCmptTypeAttribute(ValueSetType.RANGE);
    }

    private void performTestRenamePolicyCmptTypeAttribute(ValueSetType valueSetType) throws CoreException {
        IPolicyCmptTypeAttribute policyCmptTypeAttribute = createPolicyCmptTypeAttribute("policyAttribute", "Policy",
                "Product");
        policyCmptTypeAttribute.setValueSetType(valueSetType);

        IPolicyCmptType policyCmptType = policyCmptTypeAttribute.getPolicyCmptType();
        IProductCmptType productCmptType = policyCmptType.findProductCmptType(ipsProject);
        saveIpsSrcFile(policyCmptType);
        saveIpsSrcFile(productCmptType);
        performFullBuild(ipsProject);

        performRenameRefactoring(policyCmptTypeAttribute, "test");

        PolicyCmptTypeAttributeExpectations expectations = new PolicyCmptTypeAttributeExpectations(
                policyCmptTypeAttribute, policyCmptType, productCmptType);
        expectations.check("policyAttribute", "test", intParam());
    }

    /**
     * Tests that the Java elements generated by the original attribute are renamed when renaming an
     * {@link IPolicyCmptTypeAttribute} that overwrites another attribute of the super type
     * hierarchy.
     */
    @Test
    public void testRenameOverwritingPolicyCmptTypeAttribute() throws CoreException {
        IPolicyCmptType superPolicyCmptType = newPolicyAndProductCmptType(ipsProject, "SuperPolicy", "SuperProduct");
        IPolicyCmptType policyCmptType = newPolicyAndProductCmptType(ipsProject, "Policy", "Product");
        policyCmptType.setSupertype(superPolicyCmptType.getQualifiedName());

        IPolicyCmptTypeAttribute superPolicyCmptTypeAttribute = createPolicyCmptTypeAttribute("policyAttribute",
                superPolicyCmptType);
        IPolicyCmptTypeAttribute policyCmptTypeAttribute = createPolicyCmptTypeAttribute(
                superPolicyCmptTypeAttribute.getName(), policyCmptType);
        policyCmptTypeAttribute.setOverwrite(true);

        saveIpsSrcFile(superPolicyCmptType);
        saveIpsSrcFile(superPolicyCmptType.findProductCmptType(ipsProject));
        saveIpsSrcFile(policyCmptType);
        saveIpsSrcFile(policyCmptType.findProductCmptType(ipsProject));
        performFullBuild(ipsProject);

        performRenameRefactoring(policyCmptTypeAttribute, "test");

        IProductCmptType superProductCmptType = superPolicyCmptType.findProductCmptType(ipsProject);
        PolicyCmptTypeAttributeExpectations expectations = new PolicyCmptTypeAttributeExpectations(
                policyCmptTypeAttribute, superPolicyCmptType, superProductCmptType);
        expectations.check("policyAttribute", "test", intParam());
    }

    @Test
    public void testRenameProductCmptTypeAttribute() throws CoreException {
        IProductCmptTypeAttribute productCmptTypeAttribute = createProductCmptTypeAttribute("productAttribute",
                "Product", "Policy");

        IProductCmptType productCmptType = productCmptTypeAttribute.getProductCmptType();
        IPolicyCmptType policyCmptType = productCmptType.findPolicyCmptType(ipsProject);
        saveIpsSrcFile(policyCmptType);
        saveIpsSrcFile(productCmptType);
        performFullBuild(ipsProject);

        performRenameRefactoring(productCmptTypeAttribute, "test");

        ProductCmptTypeAttributeExpectations expectations = new ProductCmptTypeAttributeExpectations(productCmptType,
                policyCmptType);
        expectations.check("productAttribute", "test");
    }

    @Test
    public void testRenameEnumAttributeAbstractJava5Enums() throws CoreException {
        ProjectConfigurationUtil.setUpUseJava5Enums(ipsProject, true);
        IEnumType enumType = createEnumType("EnumType", null, "id", "name");
        enumType.setAbstract(true);

        saveIpsSrcFile(enumType);
        performFullBuild(ipsProject);

        performRenameRefactoring(enumType.getEnumAttribute("id"), "test");

        AbstractEnumAttributeExpectations expectations = new AbstractEnumAttributeExpectations(enumType);
        expectations.check("id", "test", true);
    }

    @Test
    public void testRenameEnumAttributeAbstract() throws CoreException {
        ProjectConfigurationUtil.setUpUseJava5Enums(ipsProject, false);
        IEnumType enumType = createEnumType("EnumType", null, "id", "name");
        enumType.setAbstract(true);

        saveIpsSrcFile(enumType);
        performFullBuild(ipsProject);

        performRenameRefactoring(enumType.getEnumAttribute("id"), "test");

        AbstractEnumAttributeExpectations expectations = new AbstractEnumAttributeExpectations(enumType);
        expectations.check("id", "test", false);
    }

    /**
     * Assures that Java elements referring to Java elements in the type hierarchy of an enum are
     * properly renamed.
     */
    @Test
    public void testRenameEnumAttributeHierarchy() throws CoreException {
        ProjectConfigurationUtil.setUpUseJava5Enums(ipsProject, false);
        // Create the hierarchy
        IEnumType superEnumType = createEnumType("SuperEnumType", null, "id", "name");
        superEnumType.setAbstract(true);
        IEnumType midEnumType = createEnumType("MidEnumType", superEnumType, "id", "name");
        midEnumType.setAbstract(true);
        IEnumType subEnumType = createEnumType("SubEnumType", midEnumType, "id", "name");
        subEnumType.newEnumLiteralNameAttribute();

        saveIpsSrcFile(superEnumType);
        saveIpsSrcFile(midEnumType);
        saveIpsSrcFile(subEnumType);
        performFullBuild(ipsProject);

        performRenameRefactoring(superEnumType.getEnumAttribute("id"), "test");

        IType subJavaType = getJavaType("", "SubEnumType", true, false);
        assertFalse(subJavaType.getMethod("getValueById", new String[] { stringParam() }).exists());
        assertFalse(subJavaType.getMethod("isValueById", new String[] { stringParam() }).exists());
        assertTrue(subJavaType.getMethod("getValueByTest", new String[] { stringParam() }).exists());
        assertTrue(subJavaType.getMethod("isValueByTest", new String[] { stringParam() }).exists());
    }

    @Test
    public void testRenamePolicyCmptType() throws CoreException {
        IPolicyCmptType policyCmptType = newPolicyAndProductCmptType(ipsProject, "Policy", "Product");
        IProductCmptType productCmptType = policyCmptType.findProductCmptType(ipsProject);

        saveIpsSrcFile(policyCmptType);
        saveIpsSrcFile(productCmptType);
        performFullBuild(ipsProject);

        performRenameRefactoring(policyCmptType, "RenamedPolicy");

        checkJavaSourceFilesPolicyCmptType("", "Policy", "", "RenamedPolicy");
        PolicyCmptTypeExpectations expectations = new PolicyCmptTypeExpectations(productCmptType);
        expectations.check("Policy", "RenamedPolicy");
    }

    @Test
    public void testRenameProductCmptType() throws CoreException {
        IPolicyCmptType policyCmptType = newPolicyAndProductCmptType(ipsProject, "Policy", "Product");
        IProductCmptType productCmptType = policyCmptType.findProductCmptType(ipsProject);

        saveIpsSrcFile(policyCmptType);
        saveIpsSrcFile(productCmptType);
        performFullBuild(ipsProject);

        performRenameRefactoring(productCmptType, "RenamedProduct");

        checkJavaSourceFilesProductCmptType("", "Product", "", "RenamedProduct");
        ProductCmptTypeExpectations expectations = new ProductCmptTypeExpectations(policyCmptType);
        expectations.check("Product", "RenamedProduct");
    }

    @Test
    public void testRenameEnumLiteralNameAttributeValue() throws CoreException {
        ProjectConfigurationUtil.setUpUseJava5Enums(ipsProject, false);
        performTestRenameEnumLiteralNameAttributeValue();
    }

    @Test
    public void testRenameEnumLiteralNameAttributeValueJava5Enums() throws CoreException {
        ProjectConfigurationUtil.setUpUseJava5Enums(ipsProject, true);
        performTestRenameEnumLiteralNameAttributeValue();
    }

    private void performTestRenameEnumLiteralNameAttributeValue() throws CoreException {
        IEnumType enumType = createEnumType("EnumType", null, "id", "name", "name", "0", "foo", "FOO");
        IEnumLiteralNameAttributeValue enumLiteralNameAttributeValue = enumType.getEnumValues().get(0)
                .getEnumLiteralNameAttributeValue();

        saveIpsSrcFile(enumType);
        performFullBuild(ipsProject);

        performRenameRefactoring(enumLiteralNameAttributeValue, "bar");

        IType javaEnum = getJavaType("", "EnumType", true, false);
        assertFalse(javaEnum.getField("FOO").exists());
        assertTrue(javaEnum.getField("bar").exists());
    }

    @Test
    public void testRenameEnumType() throws CoreException {
        IEnumType enumType = createEnumType("EnumType", null, "id", "name");
        enumType.setContainingValues(false);
        enumType.setEnumContentName("EnumContent");

        saveIpsSrcFile(enumType);
        performFullBuild(ipsProject);

        performRenameRefactoring(enumType, "RenamedEnumType");

        checkJavaSourceFilesEnumType("", "EnumType", "", "RenamedEnumType");
    }

    @Test
    public void testRenameTableStructure() throws CoreException {
        ITableStructure tableStructure = createTableStructure("TableStructure");

        saveIpsSrcFile(tableStructure);
        performFullBuild(ipsProject);

        performRenameRefactoring(tableStructure, "RenamedTableStructure");

        checkJavaSourceFilesTableStructure("", "TableStructure", "", "RenamedTableStructure");
    }

    @Test
    public void testRenameTestCaseType() throws CoreException {
        ITestCaseType testCaseType = createTestCaseType("TestCaseType");

        saveIpsSrcFile(testCaseType);
        performFullBuild(ipsProject);

        performRenameRefactoring(testCaseType, "RenamedTestCaseType");

        checkJavaSourceFilesTestCaseType("", "TestCaseType", "", "RenamedTestCaseType");
    }

    @Test
    public void testRenameBusinessFunction() throws CoreException {
        IBusinessFunction businessFunction = createBusinessFunction("BusinessFunction");

        saveIpsSrcFile(businessFunction);
        performFullBuild(ipsProject);

        performRenameRefactoring(businessFunction, "RenamedBusinessFunction");

        checkJavaSourceFilesBusinessFunction("", "BusinessFunction", "", "RenamedBusinessFunction");
    }

    @Test
    public void testRenameOnlyLetterCaseChanged() throws CoreException {
        IPolicyCmptType policyCmptType = newPolicyCmptTypeWithoutProductCmptType(ipsProject, "PolicyCmptType");

        saveIpsSrcFile(policyCmptType);
        performFullBuild(ipsProject);

        performRenameRefactoring(policyCmptType, "policyCmptType");

        assertTrue(getJavaType("", getPublishedInterfaceName("PolicyCmptType"), true, false).exists());
        assertTrue(getJavaType("", "PolicyCmptType", false, false).exists());
    }

    private IPolicyCmptTypeAttribute createPolicyCmptTypeAttribute(String name,
            String policyCmptTypeName,
            String productCmptTypeName) throws CoreException {

        IPolicyCmptType policyCmptType = newPolicyAndProductCmptType(ipsProject, policyCmptTypeName,
                productCmptTypeName);
        return createPolicyCmptTypeAttribute(name, policyCmptType);
    }

    private IPolicyCmptTypeAttribute createPolicyCmptTypeAttribute(String name, IPolicyCmptType policyCmptType) {
        IPolicyCmptTypeAttribute policyCmptTypeAttribute = policyCmptType.newPolicyCmptTypeAttribute();
        policyCmptTypeAttribute.setName(name);
        policyCmptTypeAttribute.setDatatype(Datatype.PRIMITIVE_INT.getQualifiedName());
        policyCmptTypeAttribute.setModifier(Modifier.PUBLISHED);
        policyCmptTypeAttribute.setAttributeType(AttributeType.CHANGEABLE);
        policyCmptTypeAttribute.setProductRelevant(true);
        policyCmptTypeAttribute.setDefaultValue("0");
        return policyCmptTypeAttribute;
    }

    private IProductCmptTypeAttribute createProductCmptTypeAttribute(String name,
            String productCmptTypeName,
            String policyCmptTypeName) throws CoreException {

        IPolicyCmptType policyCmptType = newPolicyAndProductCmptType(ipsProject, policyCmptTypeName,
                productCmptTypeName);
        IProductCmptTypeAttribute productCmptTypeAttribute = policyCmptType.findProductCmptType(ipsProject)
                .newProductCmptTypeAttribute();
        productCmptTypeAttribute.setName(name);
        productCmptTypeAttribute.setDatatype(Datatype.STRING.getQualifiedName());
        productCmptTypeAttribute.setModifier(Modifier.PUBLISHED);
        productCmptTypeAttribute.setChangingOverTime(true);
        return productCmptTypeAttribute;
    }

    private class PolicyCmptTypeAttributeExpectations {

        private final IPolicyCmptTypeAttribute policyCmptTypeAttribute;

        private final IType policyInterface;

        private final IType policyClass;

        private final IType productGenInterface;

        private final IType productGenClass;

        private PolicyCmptTypeAttributeExpectations(IPolicyCmptTypeAttribute policyCmptTypeAttribute,
                IPolicyCmptType policyCmptType, IProductCmptType productCmptType) throws CoreException {

            this.policyCmptTypeAttribute = policyCmptTypeAttribute;

            policyInterface = getJavaType("", getPublishedInterfaceName(policyCmptType.getName()), true, false);
            policyClass = getJavaType("", policyCmptType.getName(), false, false);
            productGenInterface = getJavaType("", getPublishedInterfaceName(productCmptType.getName()
                    + getGenerationConceptNameAbbreviation()), true, false);
            productGenClass = getJavaType("", productCmptType.getName() + getGenerationConceptNameAbbreviation(),
                    false, false);
        }

        private void check(String oldName, String newName, String datatypeSignature) {
            ValueSetType valueSetType = policyCmptTypeAttribute.getValueSet().getValueSetType();

            if (ValueSetType.UNRESTRICTED.equals(valueSetType)) {
                checkValueSetUnrestricted(oldName, newName, datatypeSignature);

            } else if (ValueSetType.ENUM.equals(valueSetType)) {
                checkValueSetEnum(oldName, newName);

            } else if (ValueSetType.RANGE.equals(valueSetType)) {
                checkValueSetRange(oldName, newName);
            }
        }

        private void checkValueSetUnrestricted(String oldName, String newName, String datatypeSignature) {
            String oldNameCamelCase = StringUtil.toCamelCase(oldName, true);
            String newNameCamelCase = StringUtil.toCamelCase(newName, true);

            assertFalse(policyInterface.getField("PROPERTY_" + oldName.toUpperCase()).exists());
            assertFalse(policyInterface.getMethod("get" + oldNameCamelCase, new String[0]).exists());
            assertFalse(policyInterface.getMethod("set" + oldNameCamelCase, new String[] { datatypeSignature })
                    .exists());
            assertFalse(productGenInterface.getMethod("getDefaultValue" + oldNameCamelCase, new String[0]).exists());
            assertFalse(productGenInterface.getMethod("getSetOfAllowedValuesFor" + oldNameCamelCase,
                    new String[] { unresolvedParam(IValidationContext.class) }).exists());

            assertFalse(policyClass.getField(oldName).exists());
            assertFalse(policyClass.getMethod("get" + oldNameCamelCase, new String[0]).exists());
            assertFalse(policyClass.getMethod("set" + oldNameCamelCase, new String[] { datatypeSignature }).exists());
            assertFalse(productGenClass.getField("defaultValue" + oldNameCamelCase).exists());
            assertFalse(productGenClass.getField("setOfAllowedValues" + oldNameCamelCase).exists());
            assertFalse(productGenClass.getMethod("getDefaultValue" + oldNameCamelCase, new String[0]).exists());
            assertFalse(productGenClass.getMethod("getSetOfAllowedValuesFor" + oldNameCamelCase,
                    new String[] { unresolvedParam(IValidationContext.class) }).exists());

            assertTrue(policyInterface.getField("PROPERTY_" + newName.toUpperCase()).exists());
            assertTrue(policyInterface.getMethod("get" + newNameCamelCase, new String[0]).exists());
            assertTrue(policyInterface.getMethod("set" + newNameCamelCase, new String[] { datatypeSignature }).exists());
            assertTrue(productGenInterface.getMethod("getDefaultValue" + newNameCamelCase, new String[0]).exists());
            assertTrue(productGenInterface.getMethod("getSetOfAllowedValuesFor" + newNameCamelCase,
                    new String[] { unresolvedParam(IValidationContext.class) }).exists());

            assertTrue(policyClass.getField(newName).exists());
            assertTrue(policyClass.getMethod("get" + newNameCamelCase, new String[0]).exists());
            assertTrue(policyClass.getMethod("set" + newNameCamelCase, new String[] { datatypeSignature }).exists());
            assertTrue(productGenClass.getField("defaultValue" + newNameCamelCase).exists());
            assertTrue(productGenClass.getField("setOfAllowedValues" + newNameCamelCase).exists());
            assertTrue(productGenClass.getMethod("getDefaultValue" + newNameCamelCase, new String[0]).exists());
            assertTrue(productGenClass.getMethod("getSetOfAllowedValuesFor" + newNameCamelCase,
                    new String[] { unresolvedParam(IValidationContext.class) }).exists());
        }

        private void checkValueSetEnum(String oldName, String newName) {
            String oldNameCamelCase = StringUtil.toCamelCase(oldName, true);
            String newNameCamelCase = StringUtil.toCamelCase(newName, true);

            assertFalse(productGenInterface.getMethod("getAllowedValuesFor" + oldNameCamelCase,
                    new String[] { unresolvedParam(IValidationContext.class) }).exists());
            assertFalse(productGenClass.getField("allowedValuesFor" + oldNameCamelCase).exists());
            assertFalse(productGenClass.getMethod("getAllowedValuesFor" + oldNameCamelCase,
                    new String[] { unresolvedParam(IValidationContext.class) }).exists());

            assertTrue(productGenInterface.getMethod("getAllowedValuesFor" + newNameCamelCase,
                    new String[] { unresolvedParam(IValidationContext.class) }).exists());
            assertTrue(productGenClass.getField("allowedValuesFor" + newNameCamelCase).exists());
            assertTrue(productGenClass.getMethod("getAllowedValuesFor" + newNameCamelCase,
                    new String[] { unresolvedParam(IValidationContext.class) }).exists());
        }

        private void checkValueSetRange(String oldName, String newName) {
            String oldNameCamelCase = StringUtil.toCamelCase(oldName, true);
            String newNameCamelCase = StringUtil.toCamelCase(newName, true);

            assertFalse(productGenInterface.getMethod("getRangeFor" + oldNameCamelCase,
                    new String[] { unresolvedParam(IValidationContext.class) }).exists());
            assertFalse(productGenClass.getField("rangeFor" + oldNameCamelCase).exists());
            assertFalse(productGenClass.getMethod("getRangeFor" + oldNameCamelCase,
                    new String[] { unresolvedParam(IValidationContext.class) }).exists());

            assertTrue(productGenInterface.getMethod("getRangeFor" + newNameCamelCase,
                    new String[] { unresolvedParam(IValidationContext.class) }).exists());
            assertTrue(productGenClass.getField("rangeFor" + newNameCamelCase).exists());
            assertTrue(productGenClass.getMethod("getRangeFor" + newNameCamelCase,
                    new String[] { unresolvedParam(IValidationContext.class) }).exists());
        }

    }

    private class ProductCmptTypeAttributeExpectations {

        private final IType policyClass;

        private final IType productGenInterface;

        private final IType productGenClass;

        private ProductCmptTypeAttributeExpectations(IProductCmptType productCmptType, IPolicyCmptType policyCmptType)
                throws CoreException {

            productGenInterface = getJavaType("", getPublishedInterfaceName(productCmptType.getName()
                    + getGenerationConceptNameAbbreviation()), true, false);
            productGenClass = getJavaType("", productCmptType.getName() + getGenerationConceptNameAbbreviation(),
                    false, false);
            policyClass = getJavaType("", policyCmptType.getName(), false, false);
        }

        private void check(String oldName, String newName) {
            String oldNameCamelCase = StringUtil.toCamelCase(oldName, true);
            String newNameCamelCase = StringUtil.toCamelCase(newName, true);

            assertFalse(productGenInterface.getMethod("get" + oldNameCamelCase, new String[0]).exists());
            assertFalse(productGenClass.getField(oldName).exists());
            assertFalse(productGenClass.getMethod("get" + oldNameCamelCase, new String[0]).exists());
            assertFalse(productGenClass.getMethod("set" + oldNameCamelCase, new String[] { stringParam() }).exists());
            assertFalse(policyClass.getMethod("get" + oldNameCamelCase, new String[0]).exists());

            assertTrue(productGenInterface.getMethod("get" + newNameCamelCase, new String[0]).exists());
            assertTrue(productGenClass.getField(newName).exists());
            assertTrue(productGenClass.getMethod("get" + newNameCamelCase, new String[0]).exists());
            assertTrue(productGenClass.getMethod("set" + newNameCamelCase, new String[] { stringParam() }).exists());
            assertTrue(policyClass.getMethod("get" + newNameCamelCase, new String[0]).exists());
        }

    }

    private class AbstractEnumAttributeExpectations {

        private final IType javaEnum;

        private AbstractEnumAttributeExpectations(IEnumType enumType) throws CoreException {
            javaEnum = getJavaType("", enumType.getName(), true, false);
        }

        private void check(String oldName, String newName, boolean useJava5Enums) {
            if (useJava5Enums) {
                checkJava5Enums(oldName, newName);
            } else {
                checkNoJava5Enums(oldName, newName);
            }
        }

        private void checkJava5Enums(String oldName, String newName) {
            String oldNameCamelCase = StringUtil.toCamelCase(oldName, true);
            String newNameCamelCase = StringUtil.toCamelCase(newName, true);

            assertFalse(javaEnum.getMethod("get" + oldNameCamelCase, new String[0]).exists());

            assertTrue(javaEnum.getMethod("get" + newNameCamelCase, new String[0]).exists());
        }

        private void checkNoJava5Enums(String oldName, String newName) {
            String oldNameCamelCase = StringUtil.toCamelCase(oldName, true);
            String newNameCamelCase = StringUtil.toCamelCase(newName, true);

            assertFalse(javaEnum.getField(oldName).exists());
            assertFalse(javaEnum.getMethod("get" + oldNameCamelCase, new String[0]).exists());

            assertTrue(javaEnum.getField(newName).exists());
            assertTrue(javaEnum.getMethod("get" + newNameCamelCase, new String[0]).exists());
        }

    }

    private class PolicyCmptTypeExpectations {

        private final IType productInterface;

        private final IType productClass;

        private PolicyCmptTypeExpectations(IProductCmptType productCmptType) throws CoreException {
            productInterface = getJavaType("", getPublishedInterfaceName(productCmptType.getName()), true, false);
            productClass = getJavaType("", productCmptType.getName(), false, false);
        }

        private void check(String oldName, String newName) {
            assertFalse(productClass.getMethod("create" + oldName, new String[0]).exists());
            assertFalse(productInterface.getMethod("create" + oldName, new String[0]).exists());

            assertTrue(productClass.getMethod("create" + newName, new String[0]).exists());
            assertTrue(productInterface.getMethod("create" + newName, new String[0]).exists());
        }

    }

    private class ProductCmptTypeExpectations {

        private final IType policyClass;

        private ProductCmptTypeExpectations(IPolicyCmptType policyCmptType) throws CoreException {
            policyClass = getJavaType("", policyCmptType.getName(), false, false);
        }

        private void check(String oldName, String newName) {
            assertFalse(policyClass.getMethod("get" + oldName, new String[0]).exists());
            assertFalse(policyClass.getMethod("get" + oldName + getGenerationConceptNameAbbreviation(), new String[0])
                    .exists());
            assertFalse(policyClass.getMethod("set" + oldName,
                    new String[] { unresolvedParam(getPublishedInterfaceName(oldName)), booleanParam() }).exists());

            assertTrue(policyClass.getMethod("get" + newName, new String[0]).exists());
            assertTrue(policyClass.getMethod("get" + newName + getGenerationConceptNameAbbreviation(), new String[0])
                    .exists());
            assertTrue(policyClass.getMethod("set" + newName,
                    new String[] { unresolvedParam(getPublishedInterfaceName(newName)), booleanParam() }).exists());
        }

    }

}
