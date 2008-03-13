/*******************************************************************************
 * Copyright (c) 2005,2006 Faktor Zehn GmbH und andere.
 *
 * Alle Rechte vorbehalten.
 *
 * Dieses Programm und alle mitgelieferten Sachen (Dokumentationen, Beispiele,
 * Konfigurationen, etc.) dürfen nur unter den Bedingungen der 
 * Faktor-Zehn-Community Lizenzvereinbarung – Version 0.1 (vor Gründung Community) 
 * genutzt werden, die Bestandteil der Auslieferung ist und auch unter
 *   http://www.faktorips.org/legal/cl-v01.html
 * eingesehen werden kann.
 *
 * Mitwirkende:
 *   Faktor Zehn GmbH - initial API and implementation 
 *
 *******************************************************************************/

package org.faktorips.devtools.stdbuilder.enums;

import java.lang.reflect.Modifier;

import org.eclipse.core.runtime.CoreException;
import org.faktorips.codegen.JavaCodeFragmentBuilder;
import org.faktorips.datatype.Datatype;
import org.faktorips.devtools.core.builder.DefaultJavaSourceFileBuilder;
import org.faktorips.devtools.core.builder.TypeSection;
import org.faktorips.devtools.core.model.ipsobject.IIpsSrcFile;
import org.faktorips.devtools.core.model.ipsobject.IpsObjectType;
import org.faktorips.devtools.core.model.ipsproject.IIpsArtefactBuilderSet;
import org.faktorips.devtools.core.model.tablestructure.IColumn;
import org.faktorips.devtools.core.model.tablestructure.ITableStructure;
import org.faktorips.util.LocalizedStringsSet;

/**
 * A builder that generates interfaces for enumeration types. The enumeration types are defined by ips table structures
 * with the table structure type <code>TableStructureType.ENUMTYPE_MODEL</code>. The generated interface provides
 * getter-Methods for the columns that are defined for the table structure. The classes that are generated by the
 * {@link EnumClassesBuilder} implement the interface that is generated by this builder.
 * 
 * @author Peter Erzberger
 */
public class EnumTypeInterfaceBuilder extends DefaultJavaSourceFileBuilder {

    public final static String PACKAGE_STRUCTURE_KIND_ID = "EnumTypeInterfaceBuilder.enums.stdbuilder.devtools.faktorips.org"; //$NON-NLS-1$

    public EnumTypeInterfaceBuilder(IIpsArtefactBuilderSet builderSet, String kindId) {
        super(builderSet, kindId, new LocalizedStringsSet(EnumTypeInterfaceBuilder.class));
        setMergeEnabled(true);
    }

    protected String generate() throws CoreException{
        if(!getTableStructure().isModelEnumType()){
            return null;
        }
        return super.generate();
    }

    protected void generateCodeForJavatype() throws CoreException {
        ITableStructure structure = getTableStructure();
        TypeSection mainSection = getMainTypeSection();
        mainSection.setClassModifier(Modifier.PUBLIC);
        mainSection.setUnqualifiedName(structure.getName());
        mainSection.setClass(false);
        appendLocalizedJavaDoc("INTERFACE_DESCRIPTION", structure.getQualifiedName(), getIpsObject().getDescription(), 
                structure, getMainTypeSection().getJavaDocForTypeBuilder());
        
        generateMethodGetField(mainSection.getMethodBuilder());
    }

    public String getMethodNameGetField(IColumn column, Datatype datatype){
        return getJavaNamingConvention().getGetterMethodName(column.getName(), datatype);
    }
    
    private boolean checkTableColumns(IColumn[] columns) throws CoreException{
        for (int i = 0; i < columns.length; i++) {
            if(!columns[i].validate(getIpsProject()).isEmpty()){
                return false;
            }
        }
        return true;
    }

    private void generateMethodGetField(JavaCodeFragmentBuilder methodBuilder) throws CoreException{
        IColumn[] columns = getTableStructure().getColumns();
        if(!checkTableColumns(columns)){
            return;
        }
        for (int i = 0; i < columns.length; i++) {
            Datatype datatype = columns[i].findValueDatatype(getIpsProject());
            if(datatype == null){
                continue;
            }
            appendLocalizedJavaDoc("GET_FIELD_METHOD", columns[i].getName(), columns[i], methodBuilder);
            methodBuilder.signature(Modifier.PUBLIC, datatype.getJavaClassName(), getMethodNameGetField(columns[i], datatype), new String[0], 
                    new String[0], null);
            methodBuilder.append(';');
        }
    }
    
    private ITableStructure getTableStructure(){
        return (ITableStructure)getIpsObject();
    }
    
    public boolean isBuilderFor(IIpsSrcFile ipsSrcFile) throws CoreException {
        if(IpsObjectType.TABLE_STRUCTURE.equals(ipsSrcFile.getIpsObjectType())){
            return true;
        }
        return false;
    }

    
}
