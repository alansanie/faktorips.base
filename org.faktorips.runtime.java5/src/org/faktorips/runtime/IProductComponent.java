/*******************************************************************************
 * Copyright (c) 2005-2012 Faktor Zehn AG und andere.
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

package org.faktorips.runtime;

import java.util.Calendar;
import java.util.List;

import org.faktorips.runtime.internal.DateTime;

/**
 * Interface for all product components.
 * 
 * @author Jan Ortmann
 */
public interface IProductComponent extends IRuntimeObject, IProductComponentLinkSource {

    /**
     * Returns the repository this product component belongs to. This method never returns
     * <code>null</code>.
     */
    public IRuntimeRepository getRepository();

    /**
     * Returns the product component's id that uniquely identifies it in the repository it belongs
     * to.
     */
    public String getId();

    /**
     * Returns the id of the product component kind that this product component belongs to.
     */
    public String getKindId();

    /**
     * Returns the version id that identifies this product component in its kind.
     */
    public String getVersionId();

    /**
     * Returns the date when this product component expires. Returning <code>null</code> means no
     * end of the validity period.
     */
    public DateTime getValidTo();

    /**
     * Returns the generation that is effective on the given date or <code>null</code> if no
     * generation is effective on that date.
     * 
     * @throws NullPointerException if effective date is <code>null</code>.
     */
    public IProductComponentGeneration getGenerationBase(Calendar effectiveDate);

    /**
     * Returns the latest product component generation of the provided product component or
     * <code>null</code> if non available.
     */
    public IProductComponentGeneration getLatestProductComponentGeneration();

    /**
     * Creates a new policy component that is configured by this product component.
     */
    public IConfigurableModelObject createPolicyComponent();

    /**
     * Returns the <code>IProductComponentLink</code> for the association with the given role name
     * to the given product component or <code>null</code> if no such association exists.
     * 
     * @since 3.8
     */
    public IProductComponentLink<? extends IProductComponent> getLink(String linkName, IProductComponent target);

    /**
     * Returns a <code>List</code> of all the <code>IProductComponentLink</code>s from this product
     * component generation to other product components.
     * 
     * @since 3.8
     */
    public List<IProductComponentLink<? extends IProductComponent>> getLinks();

}
