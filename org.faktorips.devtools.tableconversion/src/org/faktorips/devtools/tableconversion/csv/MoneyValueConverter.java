/*******************************************************************************
 * Copyright (c) Faktor Zehn AG. <http://www.faktorzehn.org>
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version
 * 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and
 * restrictions as well as the possibility of alternative license terms.
 *******************************************************************************/

package org.faktorips.devtools.tableconversion.csv;

import org.faktorips.datatype.Datatype;
import org.faktorips.devtools.tableconversion.AbstractValueConverter;
import org.faktorips.devtools.tableconversion.ExtSystemsMessageUtil;
import org.faktorips.util.message.MessageList;
import org.faktorips.values.Money;

public class MoneyValueConverter extends AbstractValueConverter {

    @Override
    public Object getExternalDataValue(String ipsValue, MessageList messageList) {
        if (!Datatype.MONEY.isParsable(ipsValue)) {
            messageList.add(ExtSystemsMessageUtil.createConvertIntToExtErrorMessage(ipsValue, getSupportedDatatype()
                    .getQualifiedName(), Money.class.getName()));
        }
        return ipsValue;
    }

    /**
     * Supported type for the externalDataValue is String.
     */
    @Override
    public String getIpsValue(Object externalDataValue, MessageList messageList) {
        if (externalDataValue instanceof String) {
            try {
                return Money.valueOf((String)externalDataValue).toString();
            } catch (IllegalArgumentException e) {
                messageList
                        .add(ExtSystemsMessageUtil
                                .createConvertExtToIntErrorMessage(
                                        "" + externalDataValue, externalDataValue.getClass().getName(), getSupportedDatatype().getQualifiedName())); //$NON-NLS-1$
                return externalDataValue.toString();
            }
        }
        messageList
                .add(ExtSystemsMessageUtil
                        .createConvertExtToIntErrorMessage(
                                "" + externalDataValue, externalDataValue.getClass().getName(), getSupportedDatatype().getQualifiedName())); //$NON-NLS-1$
        return externalDataValue.toString();
    }

    @Override
    public Datatype getSupportedDatatype() {
        return Datatype.MONEY;
    }

}
