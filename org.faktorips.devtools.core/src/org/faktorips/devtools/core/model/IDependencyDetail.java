/*******************************************************************************
 * Copyright (c) 2005-2009 Faktor Zehn AG und andere.
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

package org.faktorips.devtools.core.model;

import org.faktorips.devtools.core.model.ipsobject.IIpsObject;
import org.faktorips.devtools.core.model.ipsobject.IIpsObjectPartContainer;

/**
 * This interface describes details of a dependency. Instances are created by the
 * {@link IIpsObject#getDependencyDetails(IDependency)}
 * 
 * @author Thorsten Günther
 */
public interface IDependencyDetail {

    /**
     * Returns the part container that is responsible for the existence of this dependency.
     * Examples:
     * <ul>
     * <li>If a policy component type has a super type, the ips object representing the policy
     * component type is responsible for the dependency (the policy component type depends on its
     * supertype). So this method returns the ips object representing the policy component type.
     * <li/>
     * <li>If a policy component type has an association and the target of the association is
     * another policy component type, the association is responsible for the dependency between the
     * policy component type and the other type. OIn this case this method returns the association.
     * </ul>
     * 
     * @return The part of the source causing the dependency
     */
    public IIpsObjectPartContainer getPart();

    /**
     * The property name of the part causing this dependency.
     * <p>
     * <strong>Caution:</strong> This method will return null if this object was deserialized and
     * not gatherd from the {@link IIpsObject#dependsOn()} method.
     * 
     * @return The name of the property causing this dependency
     */
    public String getPropertyName();

}
