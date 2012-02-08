/*******************************************************************************
 * Copyright (c) 2005-2012 Faktor Zehn AG und andere.
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

package org.faktorips.devtools.core.model.productcmpttype;

import org.eclipse.core.runtime.CoreException;
import org.faktorips.devtools.core.model.ipsobject.IIpsObjectPart;
import org.faktorips.devtools.core.model.ipsobject.IpsObjectType;
import org.faktorips.devtools.core.model.ipsproject.IIpsProject;
import org.faktorips.devtools.core.model.type.IProductCmptProperty;
import org.faktorips.devtools.core.model.type.IType;

/**
 * An {@link IIpsObjectPart} that references an {@link IProductCmptProperty}.
 * <p>
 * Such references are created by product component types as soon as the user changes the ordering
 * of product component properties in their respective categories.
 * <p>
 * References to IPS object parts via their names are not always the best solution as these are
 * fragile with respect to the 'Rename' refactoring. Therefore, an
 * {@link IProductCmptPropertyReference} utilizes the part id. As the part id is not necessarily
 * unique across types, the {@link IpsObjectType} of the poperty's {@link IType} is stored as well.
 * 
 * @since 3.6
 * 
 * @author Alexander Weickmann, Faktor Zehn AG
 * 
 * @see IProductCmptProperty
 * @see IProductCmptCategory
 * @see IProductCmptType
 */
public interface IProductCmptPropertyReference extends IIpsObjectPart {

    public static final String PROPERTY_REFERENCED_PART_ID = "referencedPartId"; //$NON-NLS-1$

    public static final String PROPERTY_REFERENCED_IPS_OBJECT_TYPE = "referencedIpsObjectType"; //$NON-NLS-1$

    /**
     * Sets the referenced {@link IProductCmptProperty}.
     */
    public void setReferencedProperty(IProductCmptProperty property);

    /**
     * Returns whether the given {@link IProductCmptProperty} is identified by this
     * {@link IProductCmptPropertyReference}.
     */
    public boolean isReferencedProperty(IProductCmptProperty property);

    /**
     * Returns the referenced {@link IProductCmptProperty} or null if it cannot be found.
     * 
     * @throws CoreException if an error occurs during the search
     */
    public IProductCmptProperty findProductCmptProperty(IIpsProject ipsProject) throws CoreException;

    /**
     * Returns the part id of the referenced {@link IProductCmptProperty}.
     * <p>
     * Note that we reference the part id instead of the part's name due to the independence of the
     * part id with respect to the 'Rename' refactoring.
     */
    public String getReferencedPartId();

    /**
     * Sets the part id of the referenced {@link IProductCmptProperty}.
     * 
     * @see #getReferencedPartId()
     */
    public void setReferencedPartId(String partId);

    /**
     * Returns the {@link IpsObjectType} of the {@link IType} that is the origin of the referenced
     * {@link IProductCmptProperty}.
     * <p>
     * As the part id of an {@link IProductCmptProperty} is not necessarily unique across types,
     * this information is essential to be able to correctly determine the referenced
     * {@link IProductCmptProperty}.
     */
    public IpsObjectType getReferencedIpsObjectType();

    /**
     * Sets the {@link IpsObjectType} of the {@link IType} that is the origin of the referenced
     * {@link IProductCmptProperty}.
     * 
     * @see #getReferencedIpsObjectType()
     */
    public void setReferencedIpsObjectType(IpsObjectType ipsObjectType);

}
