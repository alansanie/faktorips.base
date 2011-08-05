/*******************************************************************************
 * Copyright (c) 2005-2011 Faktor Zehn AG und andere.
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

package org.faktorips.devtools.core.internal.model.ipsproject;

import org.faktorips.devtools.core.model.ipsobject.IIpsSrcFile;
import org.faktorips.devtools.core.model.ipsobject.IIpsSrcFileMemento;
import org.w3c.dom.Element;

/**
 * A memento for a source file that stores an old object state (as XML element) and the dirty state.
 * <p>
 * This is an application of the memento pattern.
 */
public class IpsSrcFileMemento implements IIpsSrcFileMemento {

    private IIpsSrcFile file;
    private Element state;
    private boolean dirty;

    public IpsSrcFileMemento(IIpsSrcFile file, Element state, boolean dirty) {
        this.file = file;
        this.state = state;
        this.dirty = dirty;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IIpsSrcFile getIpsSrcFile() {
        return file;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Element getState() {
        return state;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDirty() {
        return dirty;
    }

}