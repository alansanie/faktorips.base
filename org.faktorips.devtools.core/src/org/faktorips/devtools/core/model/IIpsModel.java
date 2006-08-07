/*******************************************************************************
 * Copyright (c) 2005,2006 Faktor Zehn GmbH und andere.
 *
 * Alle Rechte vorbehalten.
 *
 * Dieses Programm und alle mitgelieferten Sachen (Dokumentationen, Beispiele,
 * Konfigurationen, etc.) duerfen nur unter den Bedingungen der 
 * Faktor-Zehn-Community Lizenzvereinbarung - Version 0.1 (vor Gruendung Community) 
 * genutzt werden, die Bestandteil der Auslieferung ist und auch unter
 *   http://www.faktorips.org/legal/cl-v01.html
 * eingesehen werden kann.
 *
 * Mitwirkende:
 *   Faktor Zehn GmbH - initial API and implementation - http://www.faktorzehn.de
 *
 *******************************************************************************/

package org.faktorips.devtools.core.model;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.faktorips.datatype.ValueDatatype;

/**
 * The IPS model is the top of the IpsElement hierarchy (like the Java model is  the top of the Java element
 * hierarchy). One model instance exists per workspace. The model instance can be retrievedd via the plugin's
 * <code>getIpsModel()</code> method. 
 */
public interface IIpsModel extends IIpsElement {

	public IProject[] getNonIpsResources() throws CoreException;
    /**
     * Returns the workspace.
     */
    public IWorkspace getWorkspace();
    
    /**
     * Creates an IpsProject for the given Java project by adding the IPS nature and
     * creating (an empty) the .ipsproject file.
     * 
     * @throws NullPointerException if javaProjecct is <code>null</code>.
     * @throws CoreException if an error occurs while creating the ips project.
     */
    public IIpsProject createIpsProject(IJavaProject javaProject) throws CoreException;

    /**
     * Returns all IPS projects opened in the workspace or an empty array if none.
     */
    public IIpsProject[] getIpsProjects() throws CoreException;
    
    /**
     * Returns the IpsProject with the indicated name.
     */
    public IIpsProject getIpsProject(String name);
    
    /**
     * Returns the IpsProject that belongs to the indicated platform project.
     * 
     * @throws NullPointerException if project is null.
     */
    public IIpsProject getIpsProject(IProject project);
    
    /**
     * Returns the IpsElement that corresponds to the indicated resource.
     */
    public IIpsElement getIpsElement(IResource resource);
    
    /**
     * Adds a listener that is notified when something in the model was changed.
     * The notifications are made in the ui-thread.
     * 
     * @throws IllegalArgumentException if listener is null.
     */
    public void addChangeListener(ContentsChangeListener listener);
    
    /**
     * Removes the change listener.
     * 
     * @throws IllegalArgumentException if listener is null.
     */
    public void removeChangeListener(ContentsChangeListener listener);

    /**
     * Returns all package fragment roots containing source files or
     * an empty array if none is found.
     * 
     * @throws CoreException
     */
    public IIpsPackageFragmentRoot[] getSourcePackageFragmentRoots() throws CoreException;
    
    /**
     * Returns all IpsArtefactBuilderSets that have been assigned to this model. 
     */
    public IIpsArtefactBuilderSet[] getAvailableArtefactBuilderSets();
    
    /**
     * Sets the available artefact builder sets of this model.
     */
    public void setAvailableArtefactBuilderSets(IIpsArtefactBuilderSet[] sets);
    
    /**
     * Returns the extension properties for the given type. Returns an empty array
     * if no extension property is defined.
     * 
     * @param type The published interface of the ips object or part 
     * e.g. <code>org.faktorips.plugin.model.pctype.IAttribute</code>
     * @param <code>true</code> if not only the extension properties defined for for the type itself
     * should be returned, but also the ones registered for it's supertype(s) and it's interfaces.
     */
    public IExtensionPropertyDefinition[] getExtensionPropertyDefinitions(Class type, boolean includeSupertypesAndInterfaces);
    
    /**
     * Returns the extension property with the given id that belongs to the given type. Returns <code>null</code>
     * if no such extension property is defined.
     * 
     * @param type The published interface of the ips object or part 
     * e.g. <code>or.faktorips.plugin.model.pctype.Attribute</code>
     * @parma propertyId the extension property id
     * @param <code>true</code> if not only the extension properties defined for for the type itself
     * should be returned, but also the ones registered for it's supertype(s) and it's interfaces.
     */
    public IExtensionPropertyDefinition getExtensionPropertyDefinition(
            Class type, 
            String propertyId, 
            boolean includeSupertypesAndInterfaces);
    
    
    /**
     * Returns the predefines value datatypes like String, Integer etc. 
     */
    public ValueDatatype[] getPredefinedValueDatatypes();
    
    /**
     * Returns <code>true</code> if the datatype is predefined (via the datatypeDefinition
     * extension point), otherwise <code>false</code>. Returns <code>false</code> if 
     * datatypeId is <code>null</code>.
     */
    public boolean isPredefinedValueDatatype(String datatypeId);
    
    /**
     * Returns the available changes over time naming conventions.
     */
    public IChangesOverTimeNamingConvention[] getChangesOverTimeNamingConvention();

    /**
     * Returns the changes in time naming convention identified by the given id.
     * If the id does not identify a naming convention, the VAA naming convention is
     * returned per default.
     */
    public IChangesOverTimeNamingConvention getChangesOverTimeNamingConvention(String id);

    /**
     * Removes the given object from the model.
     */
	public void delete(IIpsElement toDelete);
    
}
