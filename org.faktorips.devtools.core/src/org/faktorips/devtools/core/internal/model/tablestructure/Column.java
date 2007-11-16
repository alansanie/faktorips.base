/*******************************************************************************
 * Copyright (c) 2005,2006 Faktor Zehn GmbH und andere.
 *
 * Alle Rechte vorbehalten.
 *
 * Dieses Programm und alle mitgelieferten Sachen (Dokumentationen, Beispiele,
 * Konfigurationen, etc.) duerfen nur unter den Bedingungen der 
 * Faktor-Zehn-Community Lizenzvereinbarung - Version 0.1 (vor Gruendung Community) 
 * genutzt werden, die Bestandteil der Auslieferung ist und auch unter
 *   http://www.faktorips.org/legal/cl-v01.html
 * eingesehen werden kann.
 *
 * Mitwirkende:
 *   Faktor Zehn GmbH - initial API and implementation - http://www.faktorzehn.de
 *
 *******************************************************************************/

package org.faktorips.devtools.core.internal.model.tablestructure;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.swt.graphics.Image;
import org.faktorips.datatype.Datatype;
import org.faktorips.datatype.ValueDatatype;
import org.faktorips.devtools.core.IpsPlugin;
import org.faktorips.devtools.core.internal.model.ValidationUtils;
import org.faktorips.devtools.core.internal.model.ipsobject.AtomicIpsObjectPart;
import org.faktorips.devtools.core.model.ipsproject.IIpsProject;
import org.faktorips.devtools.core.model.tablestructure.IColumn;
import org.faktorips.util.message.Message;
import org.faktorips.util.message.MessageList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 *
 */
public class Column extends AtomicIpsObjectPart implements IColumn {
    
    final static String TAG_NAME = "Column"; //$NON-NLS-1$
    
    private String datatype = ""; //$NON-NLS-1$
    
    Column(TableStructure table, int id) {
        super(table, id);
    }

    /**
     * Constructor for testing purposes.
     */
    Column() {
    }
    
    /**
     * {@inheritDoc}
     */
    public void setName(String newName) {
        this.name = newName;
        objectHasChanged();
    }

    /**
     * {@inheritDoc}
     */
    public String getAccessParameterName() {
        return name;
    }

    /** 
     * {@inheritDoc}
     */
    public String getDatatype() {
        return datatype;
    }

    /** 
     * {@inheritDoc}
     */
    public void setDatatype(String newDatatype) {
        datatype = newDatatype;
        objectHasChanged();
    }

    /** 
     * {@inheritDoc}
     */
    public Image getImage() {
        return IpsPlugin.getDefault().getImage("TableColumn.gif"); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    protected void validateThis(MessageList list, IIpsProject ipsProject) throws CoreException {
        super.validateThis(list, ipsProject);
        ValidationUtils.checkStringPropertyNotEmpty(name, "name", this, PROPERTY_NAME, "", list); //$NON-NLS-1$ //$NON-NLS-2$
        Datatype type = ValidationUtils.checkDatatypeReference(datatype, false, this, PROPERTY_DATATYPE, "", list, ipsProject); //$NON-NLS-1$
        if (type==null) {
        	return;
        }
        if (type.isPrimitive()) {
            String text = Messages.Column_msgPrimitvesArentSupported; 
            list.add(new Message(MSGCODE_DATATYPE_IS_A_PRIMITTVE, text, Message.ERROR, this, PROPERTY_DATATYPE)); //$NON-NLS-1$
        }
        
        IStatus status = JavaConventions.validateIdentifier(StringUtils.uncapitalise(name));
        if (!status.isOK()) {
            String text = Messages.Column_msgInvalidName;
            list.add(new Message(MSGCODE_INVALID_NAME, text, Message.ERROR, this, PROPERTY_NAME));
        }
    }

    /**
     * {@inheritDoc}
     */
    protected Element createElement(Document doc) {
        return doc.createElement(TAG_NAME);
    }
    
    /**
     * {@inheritDoc}
     */
    protected void initPropertiesFromXml(Element element, Integer id) {
        super.initPropertiesFromXml(element, id);
        name = element.getAttribute("name"); //$NON-NLS-1$
        datatype = element.getAttribute("datatype"); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    protected void propertiesToXml(Element element) {
        super.propertiesToXml(element);
        element.setAttribute("name", name); //$NON-NLS-1$
        element.setAttribute("datatype", datatype); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    public IColumn[] getColumns() {
        return new IColumn[]{this};
    }
    
    /**
     * {@inheritDoc} 
     * @throws CoreException 
     */
    public ValueDatatype findValueDatatype(IIpsProject ipsProject) throws CoreException {
        return ipsProject.findValueDatatype(datatype); 
    }
    
}
