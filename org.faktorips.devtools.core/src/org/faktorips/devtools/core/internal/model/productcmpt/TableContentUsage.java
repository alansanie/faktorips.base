/*******************************************************************************
 * Copyright (c) Faktor Zehn AG. <http://www.faktorzehn.org>
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version
 * 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and
 * restrictions as well as the possibility of alternative license terms.
 *******************************************************************************/

package org.faktorips.devtools.core.internal.model.productcmpt;

import java.beans.PropertyChangeEvent;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.osgi.util.NLS;
import org.faktorips.devtools.core.internal.model.productcmpt.template.TemplateValueFinder;
import org.faktorips.devtools.core.internal.model.productcmpt.template.TemplateValueSettings;
import org.faktorips.devtools.core.model.ipsobject.IpsObjectType;
import org.faktorips.devtools.core.model.ipsproject.IIpsProject;
import org.faktorips.devtools.core.model.productcmpt.IProductCmpt;
import org.faktorips.devtools.core.model.productcmpt.IProductCmptGeneration;
import org.faktorips.devtools.core.model.productcmpt.IPropertyValueContainer;
import org.faktorips.devtools.core.model.productcmpt.ITableContentUsage;
import org.faktorips.devtools.core.model.productcmpt.PropertyValueType;
import org.faktorips.devtools.core.model.productcmpt.template.TemplateValueStatus;
import org.faktorips.devtools.core.model.productcmpttype.IProductCmptType;
import org.faktorips.devtools.core.model.productcmpttype.ITableStructureUsage;
import org.faktorips.devtools.core.model.tablecontents.ITableContents;
import org.faktorips.devtools.core.model.type.IProductCmptProperty;
import org.faktorips.runtime.internal.ValueToXmlHelper;
import org.faktorips.util.ArgumentCheck;
import org.faktorips.util.message.Message;
import org.faktorips.util.message.MessageList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 
 * @author Thorsten Guenther
 */
public class TableContentUsage extends AbstractSimplePropertyValue implements ITableContentUsage {

    public static final String TAG_NAME = ValueToXmlHelper.XML_TAG_TABLE_CONTENT_USAGE;
    /**
     * The full qualified name of the table content this usage defines.
     */
    private String tableContentName = ""; //$NON-NLS-1$

    /**
     * The role-name of the structure usage this content usage is based on.
     */
    private String structureUsage = ""; //$NON-NLS-1$

    private final TemplateValueSettings templateValueSettings;

    public TableContentUsage(IPropertyValueContainer parent, String id) {
        this(parent, id, ""); //$NON-NLS-1$
    }

    public TableContentUsage(IPropertyValueContainer parent, String id, String structureUsage) {
        super(parent, id);
        this.structureUsage = structureUsage;
        this.templateValueSettings = new TemplateValueSettings(this);
    }

    @Override
    public String getPropertyName() {
        return structureUsage;
    }

    @Override
    public IProductCmptProperty findProperty(IIpsProject ipsProject) throws CoreException {
        return findTableStructureUsage(ipsProject);
    }

    @Override
    public PropertyValueType getPropertyValueType() {
        return PropertyValueType.TABLE_CONTENT_USAGE;
    }

    @Override
    public String getPropertyValue() {
        return getTableContentName();
    }

    /**
     * {@inheritDoc}
     * 
     * @deprecated As of 3.14 {@link TableContentUsage table content usages} can be part of both
     *             {@link IProductCmpt product components} and {@link ProductCmptGeneration product
     *             component generations}. Use {@link #getPropertyValueContainer()} and the common
     *             interface {@link IPropertyValueContainer} instead.
     */
    @Override
    @Deprecated
    public IProductCmptGeneration getProductCmptGeneration() {
        if (getParent() instanceof IProductCmptGeneration) {
            return (ProductCmptGeneration)getParent();
        }
        return null;
    }

    @Override
    public IProductCmpt getProductCmpt() {
        return getPropertyValueContainer().getProductCmpt();
    }

    private IProductCmptType getProductCmptType(IIpsProject ipsProject) {
        return getPropertyValueContainer().findProductCmptType(ipsProject);
    }

    @Override
    protected Element createElement(Document doc) {
        return doc.createElement(TAG_NAME);
    }

    @Override
    public void setStructureUsage(String structureUsage) {
        this.structureUsage = structureUsage;
        objectHasChanged();
    }

    @Override
    public String getStructureUsage() {
        return structureUsage;
    }

    @Override
    public void setTableContentName(String tableContentName) {
        this.tableContentName = tableContentName;
        objectHasChanged();
    }

    @Override
    public String getName() {
        return getTableContentName();
    }

    @Override
    public String getTableContentName() {
        if (getTemplateValueStatus() == TemplateValueStatus.INHERITED) {
            return findTemplateTableContentName();
        }

        if (getTemplateValueStatus() == TemplateValueStatus.UNDEFINED) {
            return ""; //$NON-NLS-1$
        }

        return tableContentName;
    }

    private String findTemplateTableContentName() {
        ITableContentUsage templateContentUsage = findTemplateProperty(getIpsProject());
        if (templateContentUsage == null) {
            // Template should exist but does not. Use the "last known" value as a more or less
            // helpful fallback while some validation hopefully addresses the missing template...
            return tableContentName;
        }
        return templateContentUsage.getTableContentName();
    }

    @Override
    public ITableContents findTableContents(IIpsProject ipsProject) throws CoreException {
        return (ITableContents)ipsProject.findIpsObject(IpsObjectType.TABLE_CONTENTS, getTableContentName());
    }

    @Override
    protected void validateThis(MessageList list, IIpsProject ipsProject) throws CoreException {
        super.validateThis(list, ipsProject);
        String tableContentNameToValidate = getTableContentName();
        IProductCmptType type = getProductCmptType(ipsProject);
        if (type == null) {
            list.add(new Message(MSGCODE_NO_TYPE, Messages.TableContentUsage_msgNoType, Message.WARNING, this));
            return;
        }

        ITableStructureUsage tsu = type.findTableStructureUsage(structureUsage, ipsProject);
        if (tsu == null) {
            String text = NLS.bind(Messages.TableContentUsage_msgUnknownStructureUsage, structureUsage);
            list.add(new Message(MSGCODE_UNKNOWN_STRUCTURE_USAGE, text, Message.ERROR, this, PROPERTY_STRUCTURE_USAGE));
            return;
        }
        list.add(templateValueSettings.validate(this, ipsProject));
        ITableContents content = null;
        if (tableContentNameToValidate != null) {
            content = findTableContents(ipsProject);
        }
        if (content == null) {
            if (!isNullContentAllowed(tsu)) {
                String text = NLS.bind(Messages.TableContentUsage_msgUnknownTableContent, tableContentNameToValidate);
                list.add(new Message(MSGCODE_UNKNOWN_TABLE_CONTENT, text, Message.ERROR, this, PROPERTY_TABLE_CONTENT));
            }
            return;
        }
        String usedStructure = content.getTableStructure();
        if (!tsu.isUsed(usedStructure)) {
            String[] params = { tableContentNameToValidate, usedStructure, structureUsage };
            String text = NLS.bind(Messages.TableContentUsage_msgInvalidTableContent, params);
            list.add(new Message(MSGCODE_INVALID_TABLE_CONTENT, text, Message.ERROR, this, PROPERTY_TABLE_CONTENT));
        }
    }

    private boolean isNullContentAllowed(ITableStructureUsage tsu) {
        if (getTemplateValueStatus() == TemplateValueStatus.UNDEFINED) {
            return true;
        } else {
            return StringUtils.isEmpty(getTableContentName()) && !tsu.isMandatoryTableContent();
        }
    }

    @Override
    protected void initPropertiesFromXml(Element element, String id) {
        super.initPropertiesFromXml(element, id);
        structureUsage = element.getAttribute(PROPERTY_STRUCTURE_USAGE);
        tableContentName = ValueToXmlHelper.getValueFromElement(element, "TableContentName"); //$NON-NLS-1$
        templateValueSettings.initPropertiesFromXml(element);
    }

    @Override
    protected void propertiesToXml(Element element) {
        super.propertiesToXml(element);
        element.setAttribute(PROPERTY_STRUCTURE_USAGE, structureUsage);
        ValueToXmlHelper.addValueToElement(getTableContentName(), element, "TableContentName"); //$NON-NLS-1$
        templateValueSettings.propertiesToXml(element);
    }

    @Override
    public ITableStructureUsage findTableStructureUsage(IIpsProject ipsProject) throws CoreException {
        IProductCmptType type = getProductCmptType(ipsProject);
        if (type == null) {
            return null;
        }
        return type.findTableStructureUsage(structureUsage, ipsProject);
    }

    @Override
    public String getCaption(Locale locale) throws CoreException {
        ArgumentCheck.notNull(locale);

        ITableStructureUsage currentStructureUsage = findTableStructureUsage(getIpsProject());
        if (currentStructureUsage != null) {
            return currentStructureUsage.getLabelValue(locale);
        } else {
            return null;
        }
    }

    @Override
    public String getLastResortCaption() {
        return StringUtils.capitalize(structureUsage);
    }

    @Override
    public void setTemplateValueStatus(TemplateValueStatus newStatus) {
        if (newStatus == TemplateValueStatus.DEFINED) {
            // Copy table name values from template (if present)
            this.tableContentName = getTableContentName();
        }
        TemplateValueStatus oldStatus = templateValueSettings.getStatus();
        templateValueSettings.setStatus(newStatus);
        objectHasChanged(new PropertyChangeEvent(this, PROPERTY_TEMPLATE_VALUE_STATUS, oldStatus, newStatus));

    }

    @Override
    public TemplateValueStatus getTemplateValueStatus() {
        return templateValueSettings.getStatus();
    }

    @Override
    public ITableContentUsage findTemplateProperty(IIpsProject ipsProject) {
        return TemplateValueFinder.findTemplateValue(this, ITableContentUsage.class);
    }

    @Override
    public boolean hasTemplateForProperty(IIpsProject ipsProject) {
        return TemplateValueFinder.hasTemplateForValue(this, ITableContentUsage.class);
    }

}
