/*******************************************************************************
 * Copyright (c) Faktor Zehn AG. <http://www.faktorzehn.org>
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version
 * 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and
 * restrictions as well as the possibility of alternative license terms.
 *******************************************************************************/
package org.faktorips.devtools.core.internal.model.productcmpttype;

import org.apache.commons.lang.StringUtils;
import org.eclipse.osgi.util.NLS;
import org.faktorips.devtools.core.IpsPlugin;
import org.faktorips.devtools.core.model.productcmpttype.IProductCmptType;
import org.faktorips.devtools.core.model.type.IProductCmptProperty;
import org.faktorips.util.message.Message;
import org.faktorips.util.message.MessageList;

/**
 * This class is used to validate {@link IProductCmptProperty}s.
 */
public class ProductCmptPropertyValidator {

    public static final String PROPERTY_CHANGING_OVER_TIME = "changingOverTime"; //$NON-NLS-1$

    public static final String MSGCODE_PREFIX = "PRODUCTCMPTPROPERTY-"; //$NON-NLS-1$

    /**
     * Validation message code to indicate that a product component property is changing over time
     * while the product component type does not accept changes in time
     */
    public static final String MSGCODE_TYPE_DOES_NOT_ACCEPT_CHANGING_OVER_TIME = MSGCODE_PREFIX
            + "TypeDoesNotAcceptChangingOverTime"; //$NON-NLS-1$

    private IProductCmptProperty productCmptProperty;
    private IProductCmptType productCmptType;

    public ProductCmptPropertyValidator(IProductCmptProperty productCmptProperty, IProductCmptType productCmptType) {
        this.productCmptProperty = productCmptProperty;
        this.productCmptType = productCmptType;
    }

    /**
     * Validates if the changing over time flag of the {@link IProductCmptProperty} can be enabled.
     * This is only possible, if the changing over time flag of its related {@link IProductCmptType}
     * is enabled. If the flag of the type is disabled and the flag of the property is enabled, a
     * new error message will be generated.
     * 
     * @param messageList The {@link MessageList} that holds the validation messages including the
     *            possibly new validation message
     */
    public void validateTypeDoesNotAcceptChangingOverTime(MessageList messageList) {
        if (!StringUtils.isEmpty(productCmptProperty.getName())) {
            if (!productCmptType.isChangingOverTime() && productCmptProperty.isChangingOverTime()) {
                String changingOverTimePluralName = IpsPlugin.getDefault().getIpsPreferences()
                        .getChangesOverTimeNamingConvention().getGenerationConceptNamePlural();
                String text = NLS.bind(Messages.ProductCmptPropertyValidator_msgTypeDoesNotAcceptChangingOverTime,
                        productCmptProperty.getName(), changingOverTimePluralName);
                messageList.add(Message.newError(MSGCODE_TYPE_DOES_NOT_ACCEPT_CHANGING_OVER_TIME, text,
                        productCmptProperty, PROPERTY_CHANGING_OVER_TIME));
            }
        }
    }
}
