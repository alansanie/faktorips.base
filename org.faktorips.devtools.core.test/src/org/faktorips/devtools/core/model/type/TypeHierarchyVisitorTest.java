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

package org.faktorips.devtools.core.model.type;

import org.eclipse.core.runtime.CoreException;
import org.faktorips.devtools.core.AbstractIpsPluginTest;
import org.faktorips.devtools.core.model.IIpsProject;

/**
 * 
 * @author Jan Ortmann
 */
public class TypeHierarchyVisitorTest extends AbstractIpsPluginTest  {


    private IType type;
    private IType supertype;
    private IType superSupertype;
    private IIpsProject ipsProject;
    
    protected void setUp() throws Exception {
        super.setUp();
        ipsProject = newIpsProject();
        type = newProductCmptType(ipsProject, "Type");
        supertype = newProductCmptType(ipsProject, "Supertype");
        superSupertype = newProductCmptType(ipsProject, "SuperSupertype");
        
        type.setSupertype("Supertype");
        supertype.setSupertype("SuperSupertype");
    }
    
    public void test_NoCycle() throws CoreException {
        MyVisitor visitor = new MyVisitor(ipsProject);
        visitor.start(type);
        IType[] types = visitor.getVisitedTypes();
        assertEquals(3, types.length);
        assertEquals(type, types[0]);
        assertEquals(supertype, types[1]);
        assertEquals(superSupertype, types[2]);
        assertFalse(visitor.cycleDetected());

        visitor.stopVisitingAfterThisType = supertype;
        visitor.start(type);
        types = visitor.getVisitedTypes();
        assertEquals(2, types.length);
        assertEquals(type, types[0]);
        assertEquals(supertype, types[1]);
        assertFalse(visitor.cycleDetected());
        
        visitor.stopVisitingAfterThisType = null;
        visitor.start(superSupertype);
        types = visitor.getVisitedTypes();
        assertEquals(1, types.length);
        assertEquals(superSupertype, types[0]);
        assertFalse(visitor.cycleDetected());
        
        visitor.start(null);
        types = visitor.getVisitedTypes();
        assertEquals(0, types.length);
        assertFalse(visitor.cycleDetected());
    }
    
    public void test_WithCycle() throws CoreException {
        superSupertype.setSupertype("Type");
        MyVisitor visitor = new MyVisitor(ipsProject);
        visitor.start(type);
        IType[] types = visitor.getVisitedTypes();
        assertEquals(3, types.length);
        assertEquals(type, types[0]);
        assertEquals(supertype, types[1]);
        assertEquals(superSupertype, types[2]);
        assertTrue(visitor.cycleDetected());
        
        visitor.start(null);
        types = visitor.getVisitedTypes();
        assertEquals(0, types.length);
        assertFalse(visitor.cycleDetected());
    }
    
    
    public class MyVisitor extends TypeHierarchyVisitor {

        private IType stopVisitingAfterThisType = null;
        
        public MyVisitor(IIpsProject ipsProject) {
            super(ipsProject);
        }

        /**
         * {@inheritDoc}
         */
        protected boolean visit(IType currentType) throws CoreException {
            if (stopVisitingAfterThisType==null) {
                return true;
            }
            return currentType!=stopVisitingAfterThisType;
        }
        
    }
}
