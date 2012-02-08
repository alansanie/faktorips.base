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

package org.faktorips.devtools.core.model.productcmpt.treestructure;

import org.faktorips.devtools.core.model.productcmpt.IProductCmpt;
import org.faktorips.devtools.core.model.productcmpt.IProductCmptLink;

/**
 * A reference to a <code>IProductCmpt</code> used in a <code>IProductCmptStructure</code>.
 * 
 * @author Thorsten Guenther
 */
public interface IProductCmptReference extends IProductCmptStructureReference {

    /**
     * Returns the <code>IProductCmpt</code> this reference refers to.
     */
    public IProductCmpt getProductCmpt();

    /**
     * Return the link this reference refers to. May be null.
     */
    public IProductCmptLink getLink();

    /**
     * 
     * Returns this {@link IProductCmptReference} if it references the searched product component's
     * qualified name. If not it searches all children in the same way and returns the result.
     * 
     * @param prodCmptQualifiedName the qualified name of the searched {@link IProductCmpt}
     * @return the {@link IProductCmptReference} referencing the indicated {@link IProductCmpt}, or
     *         <code>null</code> if none was found.
     */
    public IProductCmptReference findProductCmptReference(String prodCmptQualifiedName);

    /**
     * Checks whether this product component reference has outgoing associations as children
     * 
     * @return true if there are any associations defined in the corresponding type
     */
    public boolean hasAssociationChildren();

}
