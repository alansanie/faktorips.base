/*******************************************************************************
 * Copyright (c) 2005-2011 Faktor Zehn AG und andere.
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

package org.faktorips.devtools.core;

import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.osgi.framework.Bundle;

public class IpsClasspathContainerInitializer extends ClasspathContainerInitializer {

    public static final String RUNTIME_BUNDLE = "org.faktorips.runtime.java5"; //$NON-NLS-1$

    public static final String VALUETYPES_BUNDLE = "org.faktorips.valuetypes.java5"; //$NON-NLS-1$

    public IpsClasspathContainerInitializer() {
        // empty constructor
    }

    @Override
    public void initialize(IPath containerPath, IJavaProject project) throws CoreException {
        System.out.println("Initialize container: " + containerPath + " for project " + project);
        IClasspathContainer[] respectiveContainers = new IClasspathContainer[] { new IpsClasspathContainer(
                containerPath) };
        JavaCore.setClasspathContainer(containerPath, new IJavaProject[] { project }, respectiveContainers, null);

    }

    private IPath getBundlePath(String pluginId, boolean sources) {
        Bundle bundle = Platform.getBundle(pluginId);
        System.out.println("Bundle is: " + bundle);
        if (bundle == null) {
            IpsPlugin.log(new IpsStatus("Error initializing classpath container. Bundle " + pluginId + " not found.")); //$NON-NLS-1$ //$NON-NLS-2$
            return null;
        }

        URL installLocation;
        if (sources) {
            installLocation = bundle.getEntry(""); //$NON-NLS-1$
        } else {
            installLocation = bundle.getResource(""); //$NON-NLS-1$
        }
        System.out.println("installLocation is: " + installLocation);

        if (installLocation == null) {
            IpsPlugin.log(new IpsStatus(
                    "Error initializing classpath container. InstallLocation for " + pluginId + " not found.")); //$NON-NLS-1$ //$NON-NLS-2$
            return null;
        }
        // Install location is something like bundleentry://140/
        URL local = null;
        System.out.println("local is: " + local);
        try {
            local = FileLocator.toFileURL(installLocation);
            return new Path(local.getPath());
        } catch (Exception e) {
            IpsPlugin.log(new IpsStatus(
                    "Error initializing classpath variable. Bundle install locaction: " + installLocation, e)); //$NON-NLS-1$
            return null;
        }
    }

    class IpsClasspathContainer implements IClasspathContainer {

        private final IPath containerPath;
        private IClasspathEntry[] entries;

        public IpsClasspathContainer(IPath containerPath) {
            this.containerPath = containerPath;
            IClasspathEntry runtime = JavaCore.newLibraryEntry(getBundlePath(RUNTIME_BUNDLE, false),
                    getBundlePath(RUNTIME_BUNDLE, true), null);
            System.out.println("runtime-entry: " + runtime);
            IClasspathEntry valuetypes = JavaCore.newLibraryEntry(getBundlePath(VALUETYPES_BUNDLE, false),
                    getBundlePath(VALUETYPES_BUNDLE, true), null);
            System.out.println("valuetypes-entry: " + valuetypes);
            entries = new IClasspathEntry[] { runtime, valuetypes };
        }

        @Override
        public IClasspathEntry[] getClasspathEntries() {
            return entries;
        }

        @Override
        public String getDescription() {
            return Messages.IpsClasspathContainerInitializer_containerDescription;
        }

        @Override
        public int getKind() {
            return K_APPLICATION;
        }

        @Override
        public IPath getPath() {
            return containerPath;
        }

    }

}