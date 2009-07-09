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

package org.faktorips.devtools.core.ui.controller.fields;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.faktorips.datatype.Datatype;
import org.faktorips.devtools.core.AbstractIpsPluginTest;
import org.faktorips.devtools.core.EnumTypeDisplay;
import org.faktorips.devtools.core.IpsPlugin;
import org.faktorips.devtools.core.IpsPreferences;
import org.faktorips.devtools.core.model.enums.EnumTypeDatatypeAdapter;
import org.faktorips.devtools.core.model.enums.IEnumAttribute;
import org.faktorips.devtools.core.model.enums.IEnumAttributeValue;
import org.faktorips.devtools.core.model.enums.IEnumContent;
import org.faktorips.devtools.core.model.enums.IEnumType;
import org.faktorips.devtools.core.model.enums.IEnumValue;
import org.faktorips.devtools.core.model.ipsproject.IIpsProject;

public class EnumTypeDatatypeFieldTest extends AbstractIpsPluginTest {

    IIpsProject ipsProject;
    IEnumType enum1;

    public void setUp() throws Exception{
        ipsProject = newIpsProject("TestProject");

        enum1 = newEnumType(ipsProject, "enum1");
        enum1.setAbstract(false);
        enum1.setContainingValues(true);

        IEnumAttribute attr1 = enum1.newEnumAttribute();
        attr1.setDatatype(Datatype.STRING.getQualifiedName());
        attr1.setLiteralName(true);
        attr1.setName("id");
        attr1.setUnique(true);
        attr1.setIdentifier(true);

        IEnumAttribute attr2 = enum1.newEnumAttribute();
        attr2.setDatatype(Datatype.STRING.getQualifiedName());
        attr2.setName("name");
        attr2.setUnique(true);
        attr2.setUsedAsNameInFaktorIpsUi(true);

        IEnumAttribute attr3 = enum1.newEnumAttribute();
        attr3.setDatatype(Datatype.STRING.getQualifiedName());
        attr3.setName("description");
        attr3.setUnique(false);
        
        IEnumValue enumValue = enum1.newEnumValue();
        List<IEnumAttributeValue> values = enumValue.getEnumAttributeValues();
        values.get(0).setValue("a");
        values.get(1).setValue("aname");
        values.get(2).setValue("adesc");

        IEnumValue enumValue2 = enum1.newEnumValue();
        values = enumValue2.getEnumAttributeValues();
        values.get(0).setValue("b");
        values.get(1).setValue("bname");
        values.get(2).setValue("bdesc");

        IEnumValue enumValue3 = enum1.newEnumValue();
        values = enumValue3.getEnumAttributeValues();
        values.get(0).setValue("c");
        values.get(1).setValue("cname");
        values.get(2).setValue("cdesc");
        
        IpsPreferences ipsPreferences = IpsPlugin.getDefault().getIpsPreferences();
        ipsPreferences.setEnumTypeDisplay(EnumTypeDisplay.NAME);
        
    }
    
    public void testGetDatatypeValueIds() throws Exception{
        Combo combo = new Combo(Display.getDefault().getActiveShell(), SWT.None);
        EnumTypeDatatypeField field = new EnumTypeDatatypeField(combo, new EnumTypeDatatypeAdapter(enum1, null));
        field.setValue("a");
        assertEquals("a", field.getValue());
        assertEquals("aname", field.getText());

        field.setValue("b");
        assertEquals("b", field.getValue());
        assertEquals("bname", field.getText());

        field.setValue(null);
        assertNull(field.getValue());
        assertEquals(IpsPlugin.getDefault().getIpsPreferences().getNullPresentation(), field.getText());
    }

    public void testGetDatatypeValueIdsWithEnumContent() throws Exception{
        
        enum1.setContainingValues(false);
        enum1.setEnumContentPackageFragment(enum1.getIpsPackageFragment().getName());
        
        IEnumContent enumContent = newEnumContent(enum1, "enum1Content");
        IEnumValue value1 = enumContent.newEnumValue();
        List<IEnumAttributeValue> values = value1.getEnumAttributeValues();
        values.get(0).setValue("AContent");
        values.get(1).setValue("ANameContent");
        values.get(2).setValue("ADescContent");

        IEnumValue value2 = enumContent.newEnumValue();
        values = value2.getEnumAttributeValues();
        values.get(0).setValue("BContent");
        values.get(1).setValue("BNameContent");
        values.get(2).setValue("BDescContent");

        IEnumValue value3 = enumContent.newEnumValue();
        values = value3.getEnumAttributeValues();
        values.get(0).setValue("CContent");
        values.get(1).setValue("CNameContent");
        values.get(2).setValue("CDescContent");
        
        Combo combo = new Combo(Display.getDefault().getActiveShell(), SWT.None);
        EnumTypeDatatypeField field = new EnumTypeDatatypeField(combo, new EnumTypeDatatypeAdapter(enum1, enumContent));
        field.setValue("AContent");
        assertEquals("AContent", field.getValue());
        assertEquals("ANameContent", field.getText());
        
        field.setValue("BContent");
        assertEquals("BContent", field.getValue());
        assertEquals("BNameContent", field.getText());
        
        field.setValue(null);
        assertNull(field.getValue());
        assertEquals(IpsPlugin.getDefault().getIpsPreferences().getNullPresentation(), field.getText());
        
        field.setEnableEnumContentDisplay(false);
        field.setValue(null);
        assertNull(field.getValue());
        assertEquals(IpsPlugin.getDefault().getIpsPreferences().getNullPresentation(), field.getText());
        assertNull(field.getInvalidValue());

        field.setValue("AContent");
        assertEquals("AContent", field.getValue());
        assertEquals("AContent", field.getInvalidValue());
        
    }

}
