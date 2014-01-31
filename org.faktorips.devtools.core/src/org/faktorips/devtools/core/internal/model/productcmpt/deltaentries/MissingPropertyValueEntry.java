/*******************************************************************************
 * Copyright (c) Faktor Zehn AG. <http://www.faktorzehn.org>
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version
 * 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and
 * restrictions as well as the possibility of alternative license terms.
 *******************************************************************************/

package org.faktorips.devtools.core.internal.model.productcmpt.deltaentries;

import org.eclipse.osgi.util.NLS;
import org.faktorips.devtools.core.IpsPlugin;
import org.faktorips.devtools.core.model.productcmpt.DeltaType;
import org.faktorips.devtools.core.model.productcmpt.IProductCmptGeneration;
import org.faktorips.devtools.core.model.productcmpt.IPropertyValue;
import org.faktorips.devtools.core.model.productcmpt.IPropertyValueContainer;
import org.faktorips.devtools.core.model.type.IProductCmptProperty;
import org.faktorips.devtools.core.model.type.ProductCmptPropertyType;

/**
 * 
 * @author Jan Ortmann
 */
public class MissingPropertyValueEntry extends AbstractDeltaEntryForProperty {

    private final IProductCmptProperty property;

    private final IPropertyValueContainer propertyValueContainer;

    private ValueWithoutPropertyEntry predecessor;

    public MissingPropertyValueEntry(IPropertyValueContainer propertyValueContainer, IProductCmptProperty property) {
        super(null);
        this.propertyValueContainer = propertyValueContainer;
        this.property = property;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation receives the type from the property because there is no property value.
     */
    @Override
    public ProductCmptPropertyType getPropertyType() {
        return property.getProductCmptPropertyType();
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation receives the name from the property because there is no property value.
     */
    @Override
    public String getPropertyName() {
        return property.getPropertyName();
    }

    @Override
    public String getDescription() {
        String description = IpsPlugin.getMultiLanguageSupport().getLocalizedLabel(property);
        if (hasPredecessorValue()) {
            IPropertyValueContainer predecessorContainer = getPredecessor().getPropertyValue()
                    .getPropertyValueContainer();
            String name = predecessorContainer.getName();
            if (predecessorContainer instanceof IProductCmptGeneration) {
                name = IpsPlugin.getDefault().getIpsPreferences().getChangesOverTimeNamingConvention()
                        .getGenerationConceptNameSingular()
                        + " " + name; //$NON-NLS-1$
            }
            description += NLS.bind(Messages.MissingPropertyValueEntry_valueTransferedInformation, name);
        }
        return description;
    }

    @Override
    public DeltaType getDeltaType() {
        return DeltaType.MISSING_PROPERTY_VALUE;
    }

    /**
     * @param predecessor The predecessor to set.
     */
    public void setPredecessor(ValueWithoutPropertyEntry predecessor) {
        this.predecessor = predecessor;
    }

    /**
     * @return Returns the predecessor.
     */
    public AbstractDeltaEntryForProperty getPredecessor() {
        return predecessor;
    }

    @Override
    public void fix() {
        IPropertyValue newPropertyValue = propertyValueContainer.newPropertyValue(property);
        if (hasPredecessorValue()) {
            // if there was a predecessor value we copy the whole value
            IPropertyValue predecessorValue = getPredecessor().getPropertyValue();
            newPropertyValue.copyFrom(predecessorValue);
        }
    }

    private boolean hasPredecessorValue() {
        return getPredecessor() != null && getPredecessor().getPropertyValue() != null;
    }

}
