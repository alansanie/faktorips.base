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

package org.faktorips.devtools.stdbuilder.xpand.model;

import org.apache.commons.lang.StringUtils;

/**
 * This class represents an import statement for the XPAND builder.
 * 
 * @author dirmeier
 */
public class ImportStatement {

    private static final String SEPERATOR = ".";

    private final String packageName;

    private final String className;

    public static ImportStatement newInstance(String qName) {
        return new ImportStatement(qName);
    }

    public ImportStatement(String qualifiedName) {
        String[] packageAndClassName = splitPackageAndClassName(qualifiedName);
        this.packageName = packageAndClassName[0];
        this.className = packageAndClassName[1];
    }

    public ImportStatement(Class<?> clazz) {
        this.packageName = clazz.getPackage().getName();
        this.className = clazz.getSimpleName();
    }

    private String[] splitPackageAndClassName(String qualifiedName) {
        String[] result = new String[2];
        int lastIndexOf = qualifiedName.lastIndexOf(SEPERATOR);
        if (lastIndexOf > 0) {
            result[0] = qualifiedName.substring(0, lastIndexOf);
            result[1] = qualifiedName.substring(lastIndexOf + 1);
        } else {
            result[0] = StringUtils.EMPTY;
            result[1] = qualifiedName;
        }
        return result;
    }

    /**
     * Gets the full qualified name of the import statement.
     * 
     * @return Returns the qualifiedName.
     */
    public String getQualifiedName() {
        if (StringUtils.isEmpty(packageName)) {
            return className;
        } else {
            return packageName + SEPERATOR + className;
        }
    }

    /**
     * Gets the package name of the import statement
     * 
     * @return The package part of the import statement
     */
    public String getPackage() {
        return packageName;
    }

    public String getUnqualifiedName() {
        return className;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((className == null) ? 0 : className.hashCode());
        result = prime * result + ((packageName == null) ? 0 : packageName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ImportStatement other = (ImportStatement)obj;
        if (className == null) {
            if (other.className != null) {
                return false;
            }
        } else if (!className.equals(other.className)) {
            return false;
        }
        if (packageName == null) {
            if (other.packageName != null) {
                return false;
            }
        } else if (!packageName.equals(other.packageName)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ImportStatement [" + getQualifiedName() + "]";
    }

}
