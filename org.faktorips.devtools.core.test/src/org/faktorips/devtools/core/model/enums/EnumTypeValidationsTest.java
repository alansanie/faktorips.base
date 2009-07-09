/*******************************************************************************
 * Copyright (c) 2005-2009 Faktor Zehn AG und andere.
 * 
 * Alle Rechte vorbehalten.
 * 
 * Dieses Programm und alle mitgelieferten Sachen (Dokumentationen, Beispiele, Konfigurationen,
 * etc.) duerfen nur unter den Bedingungen der Faktor-Zehn-Community Lizenzvereinbarung - Version
 * 0.1 (vor Gruendung Community) genutzt werden, die Bestandteil der Auslieferung ist und auch unter
 * http://www.faktorzehn.org/f10-org:lizenzen:community eingesehen werden kann.
 * 
 * Mitwirkende: Faktor Zehn AG - initial API and implementation - http://www.faktorzehn.de
 *******************************************************************************/

package org.faktorips.devtools.core.model.enums;

import org.faktorips.datatype.Datatype;
import org.faktorips.devtools.core.AbstractIpsPluginTest;
import org.faktorips.devtools.core.model.ipsproject.IIpsProject;
import org.faktorips.util.message.MessageList;

public class EnumTypeValidationsTest extends AbstractIpsPluginTest {

    private IIpsProject ipsProject;
    private IEnumType abstractEnum;
    private IEnumType abstractSubEnum;
    private IEnumType paymentMode;

    public void setUp() throws Exception {
        super.setUp();
        ipsProject = newIpsProject("TestProject");

        abstractEnum = newEnumType(ipsProject, "AbstractEnum");
        abstractEnum.setAbstract(true);
        abstractEnum.setContainingValues(true);
        abstractEnum.setEnumContentPackageFragment("enumcontents");
        IEnumAttribute id = abstractEnum.newEnumAttribute();
        id.setDatatype(Datatype.STRING.getQualifiedName());
        id.setInherited(false);
        id.setLiteralName(true);
        id.setUnique(true);
        id.setName("id");

        abstractSubEnum = newEnumType(ipsProject, "AbstractSubEnum");
        abstractSubEnum.setSuperEnumType(abstractEnum.getQualifiedName());
        abstractSubEnum.setAbstract(true);
        abstractSubEnum.setContainingValues(true);
        abstractSubEnum.setEnumContentPackageFragment("enumcontents");
        IEnumAttribute shortText = abstractSubEnum.newEnumAttribute();
        shortText.setDatatype(Datatype.STRING.getQualifiedName());
        shortText.setInherited(false);
        shortText.setLiteralName(false);
        shortText.setUnique(false);
        shortText.setName("shortText");

        paymentMode = newEnumType(ipsProject, "PaymentMode");
        paymentMode.setSuperEnumType(abstractSubEnum.getQualifiedName());
        paymentMode.setAbstract(false);
        paymentMode.setContainingValues(true);
        paymentMode.setEnumContentPackageFragment("enumcontents");

        id = paymentMode.newEnumAttribute();
        id.setDatatype(Datatype.STRING.getQualifiedName());
        id.setInherited(true);
        id.setLiteralName(true);
        id.setUnique(true);
        id.setName("id");

        shortText = paymentMode.newEnumAttribute();
        shortText.setDatatype(Datatype.STRING.getQualifiedName());
        shortText.setInherited(true);
        shortText.setLiteralName(false);
        shortText.setUnique(false);
        shortText.setName("shortText");

        IEnumValue value1 = paymentMode.newEnumValue();
        IEnumAttributeValue value1id = value1.getEnumAttributeValues().get(0);
        value1id.setValue("monthly");
        IEnumAttributeValue value1Text = value1.getEnumAttributeValues().get(1);
        value1Text.setValue("Monthly Payment");

        IEnumValue value2 = paymentMode.newEnumValue();
        IEnumAttributeValue value2id = value2.getEnumAttributeValues().get(0);
        value2id.setValue("annually");
        IEnumAttributeValue value2Text = value2.getEnumAttributeValues().get(1);
        value2Text.setValue("Annual Payment");

    }

    public void testValidateSuperTypeHierarchyValidHierarchy() throws Exception {
        MessageList msgList = new MessageList();
        EnumTypeValidations.validateSuperTypeHierarchy(msgList, paymentMode, ipsProject);
        assertNull(msgList.getMessageByCode(IEnumType.MSGCODE_CYCLE_IN_TYPE_HIERARCHY));
        assertNull(msgList.getMessageByCode(IEnumType.MSGCODE_INCONSISTENT_TYPE_HIERARCHY));
    }

    public void testValidateSuperTypeHierarchyCycleHierarchy() throws Exception {
        abstractEnum.setSuperEnumType(paymentMode.getQualifiedName());
        MessageList msgList = new MessageList();
        EnumTypeValidations.validateSuperTypeHierarchy(msgList, paymentMode, ipsProject);
        assertNotNull(msgList.getMessageByCode(IEnumType.MSGCODE_CYCLE_IN_TYPE_HIERARCHY));
    }

    public void testValidateSuperTypeHierarchyInconsistentHierarchy() throws Exception {
        abstractEnum.setAbstract(false);
        MessageList msgList = new MessageList();
        EnumTypeValidations.validateSuperTypeHierarchy(msgList, paymentMode, ipsProject);
        assertNotNull(msgList.getMessageByCode(IEnumType.MSGCODE_INCONSISTENT_TYPE_HIERARCHY));

        abstractEnum.setAbstract(true);
        abstractEnum.setSuperEnumType("DoesNotExist");
        msgList = new MessageList();
        EnumTypeValidations.validateSuperTypeHierarchy(msgList, paymentMode, ipsProject);
        assertNotNull(msgList.getMessageByCode(IEnumType.MSGCODE_INCONSISTENT_TYPE_HIERARCHY));
    }

    public void testValidateEnumContentPackageFragment() {
        MessageList msgList = new MessageList();
        EnumTypeValidations.validateEnumContentPackageFragment(msgList, paymentMode, false, "");
        assertNotNull(msgList.getMessageByCode(IEnumType.MSGCODE_ENUM_TYPE_ENUM_CONTENT_PACKAGE_FRAGMENT_EMPTY));
    }

}
