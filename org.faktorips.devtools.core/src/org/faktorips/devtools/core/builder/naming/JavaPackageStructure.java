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

package org.faktorips.devtools.core.builder.naming;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.faktorips.devtools.core.builder.IJavaPackageStructure;
import org.faktorips.devtools.core.exception.CoreRuntimeException;
import org.faktorips.devtools.core.model.ipsobject.IIpsSrcFile;
import org.faktorips.devtools.core.model.ipsproject.IIpsSrcFolderEntry;
import org.faktorips.devtools.core.util.QNameUtil;

/**
 * Implements the old interface {@link IJavaPackageStructure} containing a lot of static util
 * methods to get the correct package name for generated artifacts
 * 
 * @author widmaier
 */
public class JavaPackageStructure implements IJavaPackageStructure {

    private final static String INTERNAL_PACKAGE = "internal"; //$NON-NLS-1$

    /**
     * Returns the name of the package the generated artifacts for the given IPS source file will be
     * placed in.
     * 
     * @param ipsSrcFile The source file to get the package from
     * 
     */
    @Override
    public String getPackageName(IIpsSrcFile ipsSrcFile, boolean internalArtifacts, boolean mergableArtifacts) {
        return getPackageNameForGeneratedArtefacts(ipsSrcFile, internalArtifacts, mergableArtifacts);
    }

    @Override
    public String getBasePackageName(IIpsSrcFolderEntry entry, boolean internalArtifacts, boolean mergableArtifacts) {
        String basePackName = mergableArtifacts ? entry.getBasePackageNameForMergableJavaClasses() : entry
                .getBasePackageNameForDerivedJavaClasses();
        return getPackageName(internalArtifacts, basePackName, StringUtils.EMPTY);
    }

    /**
     * Returns the name of the (Java) package that contains the artifacts specified by the
     * parameters generated for the given ips source file.
     * 
     * @param internalArtifacts <code>true</code> if the artifacts are internal <code>false</code>
     *            if they are published (usable by clients).
     * @param mergableArtifacts <code>true</code> if the generated artifact is mergable (at the
     *            moment this applies to Java Source files only). <code>false</code) if the artifact
     *            is 100% generated and can't be modified by the user.
     */
    protected String getPackageNameForGeneratedArtefacts(IIpsSrcFile ipsSrcFile,
            boolean internalArtifacts,
            boolean mergableArtifacts) {
        try {
            String basePackName = mergableArtifacts ? ipsSrcFile.getBasePackageNameForMergableArtefacts() : ipsSrcFile
                    .getBasePackageNameForDerivedArtefacts();
            String packageFragName = ipsSrcFile.getIpsPackageFragment().getName().toLowerCase();
            return getPackageName(internalArtifacts, basePackName, packageFragName);
        } catch (CoreException e) {
            throw new CoreRuntimeException(e);
        }
    }

    private String getPackageName(boolean internalArtifacts, String basePackName, String packageFragName) {
        if (internalArtifacts) {
            return getInternalPackage(basePackName, packageFragName);
        } else {
            return QNameUtil.concat(basePackName, packageFragName);
        }
    }

    String getInternalPackage(final String basePackName, final String subPackageFragment) {
        String internalBasePack = QNameUtil.concat(basePackName, INTERNAL_PACKAGE);
        return QNameUtil.concat(internalBasePack, subPackageFragment);
    }

}