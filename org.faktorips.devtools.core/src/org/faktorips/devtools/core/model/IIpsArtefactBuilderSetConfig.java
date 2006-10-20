/*******************************************************************************
 * Copyright (c) 2005,2006 Faktor Zehn GmbH und andere.
 *
 * Alle Rechte vorbehalten.
 *
 * Dieses Programm und alle mitgelieferten Sachen (Dokumentationen, Beispiele,
 * Konfigurationen, etc.) dürfen nur unter den Bedingungen der 
 * Faktor-Zehn-Community Lizenzvereinbarung – Version 0.1 (vor Gründung Community) 
 * genutzt werden, die Bestandteil der Auslieferung ist und auch unter
 *   http://www.faktorips.org/legal/cl-v01.html
 * eingesehen werden kann.
 *
 * Mitwirkende:
 *   Faktor Zehn GmbH - initial API and implementation 
 *
 *******************************************************************************/

package org.faktorips.devtools.core.model;

/**
 * A configuration object for ips artefact builder sets. Provides string values for string keys.
 * An ips artefact builder set instance can be configured by means of the ips project properties. Therefor 
 * the IpsArtefactBuilderSet tag of an .ipsproject file can contain one IpsArtefactBuilderSetConfig tag.
 * Here is an example for a configuration declaration:
 * 
 * <IpsArtefactBuilderSet id="org.faktorips.devtools.stdbuilder.ipsstdbuilderset">
 *      <IpsArtefactBuilderSetConfig>
 *          <Property name="name" value="value"/>
 *          <Property name="name2" value="value2"/>
 *      </IpsArtefactBuilderSetConfig>
 * </IpsArtefactBuilderSet> 
 * 
 * @author Peter Erzberger
 */
public interface IIpsArtefactBuilderSetConfig{

    /**
     * The xml tag name for instances of this type.
     */
    public final static String XML_ELEMENT = "IpsArtefactBuilderSetConfig";
    
    /**
     * Returns the value of the property of the provided property name.
     */
    public String getPropertyValue(String propertyName);

    /**
     * Returns the Boolean value of the property of the provided property name.
     * Returns <code>null</code> if the property with the specified property name is not found.
     * 
     * @see Boolean#valueOf(java.lang.String)
     */
    public Boolean getBooleanPropertyValue(String propertName);
    
}
