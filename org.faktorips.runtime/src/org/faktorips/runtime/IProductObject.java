/*******************************************************************************
 * Copyright (c) Faktor Zehn AG. <http://www.faktorzehn.org>
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version
 * 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and
 * restrictions as well as the possibility of alternative license terms.
 *******************************************************************************/
package org.faktorips.runtime;

import java.util.Date;
import java.util.TimeZone;

import org.faktorips.runtime.internal.DateTime;

/**
 * Common interface for {@link IProductComponent} and {@link IProductComponentGeneration}.
 */
public interface IProductObject extends IProductComponentLinkSource {

    /**
     * Creates a new policy component that is configured by this product component generation. After
     * creating the policy component it is automatically initialized. The new policy component is
     * not added to any parent structure.
     * <p>
     * 
     * @throws RuntimeException if this product component does not configure a policy component.
     */
    public IConfigurableModelObject createPolicyComponent();

    /**
     * Returns the date from which this generation is valid.
     * 
     * @return The valid from date of this generation
     */
    public DateTime getValidFrom();

    /**
     * Returns the point in time this generation is valid from in the given time zone. This method
     * never returns <code>null</code>.
     * 
     * @throws NullPointerException if zone is <code>null</code>.
     */
    public Date getValidFrom(TimeZone zone);

}
