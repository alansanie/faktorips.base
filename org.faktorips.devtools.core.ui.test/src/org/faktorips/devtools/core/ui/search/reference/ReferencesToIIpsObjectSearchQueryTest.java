/*******************************************************************************
 * Copyright (c) 2005-2012 Faktor Zehn AG und andere.
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

package org.faktorips.devtools.core.ui.search.reference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anySet;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.faktorips.devtools.core.model.DependencyDetail;
import org.faktorips.devtools.core.model.DependencyType;
import org.faktorips.devtools.core.model.IDependency;
import org.faktorips.devtools.core.model.IDependencyDetail;
import org.faktorips.devtools.core.model.IIpsElement;
import org.faktorips.devtools.core.model.IpsObjectDependency;
import org.faktorips.devtools.core.model.ipsobject.IIpsObject;
import org.faktorips.devtools.core.model.ipsobject.IIpsSrcFile;
import org.faktorips.devtools.core.model.ipsobject.QualifiedNameType;
import org.faktorips.devtools.core.model.ipsproject.IIpsProject;
import org.faktorips.devtools.core.model.productcmpt.IProductCmpt;
import org.faktorips.devtools.core.model.productcmpt.IProductCmptGeneration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ReferencesToIIpsObjectSearchQueryTest {

    @Mock
    private IIpsProject proj;
    @Mock
    private IIpsProject proj2;
    @Mock
    private IIpsProject proj3;
    @Mock
    private IIpsObject objectReferenced;
    @Mock
    private IIpsObject objectReferenced2;
    @Mock
    private IProductCmpt objectReferencedProductCmpt;
    @Mock
    private IIpsSrcFile srcFileReferenced;
    @Mock
    private IIpsSrcFile srcFile1;
    @Mock
    private IIpsSrcFile srcFileReferenced2;
    @Mock
    private IIpsSrcFile srcFileReferencedProdCmpt;
    @Mock
    private IIpsObject object1;
    @Mock
    private IProductCmpt prodCmpt;
    @Mock
    private QualifiedNameType prodCmptQualifiedNameType;
    @Mock
    private QualifiedNameType object1QualifiedNameType;
    @Mock
    private QualifiedNameType objectReferencedQualifiedNameType;
    @Mock
    private QualifiedNameType objectReferenced2QualifiedNameType;
    @Mock
    private QualifiedNameType objRefProdCmptQualifiedNameType;
    @Mock
    private IProductCmptGeneration prodCmptGeneration1;
    @Mock
    private IProductCmptGeneration prodCmptGeneration2;
    @Mock
    private IIpsElement elementObject1ProdCmptGeneration1;
    @Mock
    private IIpsElement elementObject1ProdCmptGeneration2;

    @Before
    public void initSrcFilesSetUp() throws CoreException {
        MockitoAnnotations.initMocks(this);

        when(prodCmpt.getIpsProject()).thenReturn(proj);
        when(prodCmpt.getQualifiedNameType()).thenReturn(prodCmptQualifiedNameType);

        when(object1.getIpsProject()).thenReturn(proj);
        when(objectReferenced.getIpsProject()).thenReturn(proj);
        when(objectReferencedProductCmpt.getIpsProject()).thenReturn(proj);

        when(object1.getQualifiedNameType()).thenReturn(object1QualifiedNameType);
        when(objectReferenced.getQualifiedNameType()).thenReturn(objectReferencedQualifiedNameType);
        when(objectReferenced2.getQualifiedNameType()).thenReturn(objectReferenced2QualifiedNameType);
        when(objectReferencedProductCmpt.getQualifiedNameType()).thenReturn(objRefProdCmptQualifiedNameType);

        when(srcFile1.getIpsObject()).thenReturn(object1);
        when(srcFileReferenced.getIpsObject()).thenReturn(objectReferenced);
        when(srcFileReferenced2.getIpsObject()).thenReturn(objectReferenced2);
        when(srcFileReferencedProdCmpt.getIpsObject()).thenReturn(objectReferencedProductCmpt);

        when(prodCmptGeneration1.getIpsObject()).thenReturn(object1);
        when(prodCmptGeneration2.getIpsObject()).thenReturn(object1);

    }

    private void initProjectSetUp() {
        List<IIpsSrcFile> result = new ArrayList<IIpsSrcFile>();
        result.add(srcFile1);
        result.add(srcFileReferenced);
        result.add(srcFileReferenced2);
        when(proj.findAllIpsSrcFiles()).thenReturn(result);
    }

    private void initDependencySetUp() throws CoreException {
        IDependency dependencyObj1ToObjRef = IpsObjectDependency.create(object1.getQualifiedNameType(),
                objectReferenced.getQualifiedNameType(), DependencyType.REFERENCE);
        IDependency dependencyObj1ToObjRef2 = IpsObjectDependency.create(object1.getQualifiedNameType(),
                objectReferenced2.getQualifiedNameType(), DependencyType.REFERENCE);
        when(object1.dependsOn()).thenReturn(new IDependency[] { dependencyObj1ToObjRef, dependencyObj1ToObjRef2 });
        IDependency dependencyObjRefToObj1 = IpsObjectDependency.create(objectReferenced.getQualifiedNameType(),
                object1.getQualifiedNameType(), DependencyType.REFERENCE);
        IDependency dependencyObjRefToObjRef2 = IpsObjectDependency.create(objectReferenced.getQualifiedNameType(),
                objectReferenced2.getQualifiedNameType(), DependencyType.REFERENCE);
        when(objectReferenced.dependsOn()).thenReturn(
                new IDependency[] { dependencyObjRefToObj1, dependencyObjRefToObjRef2 });
        when(objectReferenced2.dependsOn()).thenReturn(new IDependency[] {});
    }

    @Test
    public void testFindReferences() throws CoreException {
        IIpsProject[] projects = new IIpsProject[] { proj, proj2, proj3 };
        when(proj.findReferencingProjectLeavesOrSelf()).thenReturn(projects);

        ReferencesToIpsObjectSearchQuery querySpy = spy(new ReferencesToIpsObjectSearchQuery(object1));
        querySpy.findReferences();

        verify(querySpy).findReferencingIpsObjTypes(proj);
        verify(querySpy).findReferencingIpsObjTypes(proj2);
        verify(querySpy).findReferencingIpsObjTypes(proj3);
        verify(querySpy).findReferences();
    }

    @Test
    public void testFindReferencingIpsObjTypes_NoSrcFile() throws CoreException {
        when(proj.findAllIpsSrcFiles()).thenReturn(new ArrayList<IIpsSrcFile>());

        ReferencesToIpsObjectSearchQuery query = new ReferencesToIpsObjectSearchQuery(object1);
        Set<IIpsElement> resultFindReferencingIpsObjTypes = query.findReferencingIpsObjTypes(proj);

        assertEquals(0, resultFindReferencingIpsObjTypes.size());
    }

    @Test
    public void testFindReferencingIpsObjTypes_SrcFileWithoutDependenciesAndResultingReferences() throws CoreException {
        initProjectSetUp();
        when(srcFile1.getIpsObject().dependsOn()).thenReturn(new IDependency[] {});
        when(srcFileReferenced.getIpsObject().dependsOn()).thenReturn(new IDependency[] {});
        when(srcFileReferenced2.getIpsObject().dependsOn()).thenReturn(new IDependency[] {});

        ReferencesToIpsObjectSearchQuery query = new ReferencesToIpsObjectSearchQuery(object1);
        Set<IIpsElement> resultFindReferencingIpsObjTypes = query.findReferencingIpsObjTypes(proj);

        assertEquals(0, resultFindReferencingIpsObjTypes.size());
    }

    @Test
    public void testFindReferencingIpsObjTypes_SrcFilesWithDependenciesAndResultingReference() throws CoreException {
        initProjectSetUp();
        initDependencySetUp();

        ReferencesToIpsObjectSearchQuery query = new ReferencesToIpsObjectSearchQuery(object1);
        Set<IIpsElement> resultFindReferencingIpsObjTypes = query.findReferencingIpsObjTypes(proj);

        assertEquals(1, resultFindReferencingIpsObjTypes.size());
        assertFalse(resultFindReferencingIpsObjTypes.contains(objectReferenced2));
    }

    @Test
    public void testFindReferencingIpsObjTypes_SrcFilesWithNoDependenciesButResultingReferences() throws CoreException {
        initProjectSetUp();
        initDependencySetUp();

        ReferencesToIpsObjectSearchQuery query = new ReferencesToIpsObjectSearchQuery(objectReferenced2);
        Set<IIpsElement> resultFindReferencingIpsObjTypes = query.findReferencingIpsObjTypes(proj);

        assertEquals(2, resultFindReferencingIpsObjTypes.size());
        assertTrue(resultFindReferencingIpsObjTypes.contains(objectReferenced));
        assertTrue(resultFindReferencingIpsObjTypes.contains(object1));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testFindReferencingIpsObjTypes_OtherMethods() throws CoreException {
        List<IIpsSrcFile> ipsScrFiles = new ArrayList<IIpsSrcFile>();
        ipsScrFiles.add(srcFile1);
        ipsScrFiles.add(srcFileReferencedProdCmpt);
        when(proj.findAllIpsSrcFiles()).thenReturn(ipsScrFiles);
        IDependency dependencyObj1ToObjRefProdCmp = IpsObjectDependency.create(object1.getQualifiedNameType(),
                objectReferencedProductCmpt.getQualifiedNameType(), DependencyType.REFERENCE);
        when(object1.dependsOn()).thenReturn(new IDependency[] { dependencyObj1ToObjRefProdCmp });
        IDependency dependencyRefProdCmptToObj1 = IpsObjectDependency.create(
                objectReferencedProductCmpt.getQualifiedNameType(), object1.getQualifiedNameType(),
                DependencyType.REFERENCE);
        when(objectReferencedProductCmpt.dependsOn()).thenReturn(new IDependency[] { dependencyRefProdCmptToObj1 });
        when(object1.getQualifiedNameType()).thenReturn((QualifiedNameType)dependencyRefProdCmptToObj1.getTarget());

        ReferencesToIpsObjectSearchQuery querySpy = spy(new ReferencesToIpsObjectSearchQuery(object1));
        querySpy.findReferencingIpsObjTypes(proj);

        verify(querySpy).findReferencingIpsObjTypes(proj);
        verify(querySpy).checkIIPsSrcFileDependencies(anySet(), eq(ipsScrFiles));
        verify(querySpy).addProdCmpGenerations(anySet(), eq(objectReferencedProductCmpt),
                eq(dependencyRefProdCmptToObj1));
        verifyNoMoreInteractions(querySpy);
    }

    @Test
    public void testAddProdCmpGenerations_NoGenerations() throws CoreException {
        initProjectSetUp();

        ReferencesToIpsObjectSearchQuery query = new ReferencesToIpsObjectSearchQuery(objectReferenced);
        Set<IIpsElement> resultList = new HashSet<IIpsElement>();
        query.addProdCmpGenerations(resultList, prodCmpt, null);

        assertTrue(resultList.isEmpty());
    }

    @Test
    public void testAddProdCmpGenerations_OneOrMoreGenerations() throws CoreException {
        IDependency dependencyObj1ToObjRef = IpsObjectDependency.create(prodCmpt.getQualifiedNameType(),
                objectReferenced.getQualifiedNameType(), DependencyType.REFERENCE);
        when(prodCmpt.dependsOn()).thenReturn(new IDependency[] { dependencyObj1ToObjRef });
        IDependencyDetail generation1 = new DependencyDetail(prodCmptGeneration1, "ProdCmpGeneration1");
        IDependencyDetail generation2 = new DependencyDetail(prodCmptGeneration2, "ProdCmpGeneration2");
        List<IDependencyDetail> obj1ProdCmptGenerations = new ArrayList<IDependencyDetail>();
        obj1ProdCmptGenerations.add(generation1);
        obj1ProdCmptGenerations.add(generation2);
        when(prodCmpt.getDependencyDetails(dependencyObj1ToObjRef)).thenReturn(obj1ProdCmptGenerations);
        when(generation1.getPart().getParent()).thenReturn(elementObject1ProdCmptGeneration1);
        when(generation2.getPart().getParent()).thenReturn(elementObject1ProdCmptGeneration2);

        ReferencesToIpsObjectSearchQuery query = new ReferencesToIpsObjectSearchQuery(objectReferenced);
        Set<IIpsElement> resultList = new HashSet<IIpsElement>();
        query.addProdCmpGenerations(resultList, prodCmpt, dependencyObj1ToObjRef);

        assertEquals(2, resultList.size());
        assertTrue(resultList.contains(elementObject1ProdCmptGeneration1));
        assertTrue(resultList.contains(elementObject1ProdCmptGeneration2));
    }
}