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

package org.faktorips.devtools.core.internal.model.adapter;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.PlatformObject;
import org.faktorips.devtools.core.model.IIpsElement;

/**
 * This adapter factory could handle {@link IIpsElementWrapper} and returns the wrapped
 * {@link IIpsElement} as an adapter.
 * <p>
 * To make an object an adaptable {@link IIpsElementWrapper} you only have to implement the interface
 * {@link IIpsElementWrapper} and extend from {@link PlatformObject}. Alternatively you could
 * implement the {@link IAdaptable#getAdapter(Class)} method for your own and calling the workbench
 * adapter manager @see{@link PlatformObject#getAdapter(Class)}
 * 
 * @author dirmeier
 */
public class IpsElementAdapterFactory implements IAdapterFactory {

    @SuppressWarnings("unchecked")
    public Object getAdapter(Object adaptableObject, Class adapterType) {
        if (adaptableObject instanceof IIpsElementWrapper) {
            IIpsElementWrapper wrapper = (IIpsElementWrapper)adaptableObject;
            return wrapper.getWrappedIpsElement();
        }
        return null;
    }

    public Class<?>[] getAdapterList() {
        return new Class<?>[] { IIpsElement.class };
    }

}
