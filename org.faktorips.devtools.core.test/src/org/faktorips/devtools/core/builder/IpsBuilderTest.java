/***************************************************************************************************
 *  * Copyright (c) 2005,2006 Faktor Zehn GmbH und andere.  *  * Alle Rechte vorbehalten.  *  *
 * Dieses Programm und alle mitgelieferten Sachen (Dokumentationen, Beispiele,  * Konfigurationen,
 * etc.) duerfen nur unter den Bedingungen der  * Faktor-Zehn-Community Lizenzvereinbarung - Version
 * 0.1 (vor Gruendung Community)  * genutzt werden, die Bestandteil der Auslieferung ist und auch
 * unter  *   http://www.faktorips.org/legal/cl-v01.html  * eingesehen werden kann.  *  *
 * Mitwirkende:  *   Faktor Zehn GmbH - initial API and implementation - http://www.faktorzehn.de  *  
 **************************************************************************************************/

package org.faktorips.devtools.core.builder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.internal.ole.win32.ISpecifyPropertyPages;
import org.faktorips.devtools.core.AbstractIpsPluginTest;
import org.faktorips.devtools.core.IpsPlugin;
import org.faktorips.devtools.core.internal.model.IpsArchiveEntry;
import org.faktorips.devtools.core.internal.model.IpsModel;
import org.faktorips.devtools.core.internal.model.IpsObjectPath;
import org.faktorips.devtools.core.internal.model.IpsProject;
import org.faktorips.devtools.core.model.CreateIpsArchiveOperation;
import org.faktorips.devtools.core.model.IIpsArtefactBuilder;
import org.faktorips.devtools.core.model.IIpsObject;
import org.faktorips.devtools.core.model.IIpsObjectPath;
import org.faktorips.devtools.core.model.IIpsObjectPathEntry;
import org.faktorips.devtools.core.model.IIpsPackageFragment;
import org.faktorips.devtools.core.model.IIpsPackageFragmentRoot;
import org.faktorips.devtools.core.model.IIpsProject;
import org.faktorips.devtools.core.model.IIpsProjectProperties;
import org.faktorips.devtools.core.model.IIpsSrcFile;
import org.faktorips.devtools.core.model.IpsObjectType;
import org.faktorips.devtools.core.model.pctype.AttributeType;
import org.faktorips.devtools.core.model.pctype.IAttribute;
import org.faktorips.devtools.core.model.pctype.IPolicyCmptType;
import org.faktorips.devtools.core.model.product.IProductCmpt;
import org.faktorips.util.message.MessageList;

/**
 * A common base class for builder tests.
 * 
 * @author Jan Ortmann
 */
// IMPORTANT: in the test methods the test builder set has to be set to the model after the
// properties have been set otherwise the builder set will be removed since the setProperties method
// causes a clean to the builder set map of the ips model. Hence when the model is requested
// for the builder set it will look registered builder sets at the extension point and
// won't find the test builder set
public class IpsBuilderTest extends AbstractIpsPluginTest {

    protected IIpsProject ipsProject;
    protected IIpsPackageFragmentRoot root;

    public IpsBuilderTest() {
        super();
    }

    public IpsBuilderTest(String name) {
        super(name);
    }

    protected void setUp() throws Exception {
        super.setUp();
        ipsProject = this.newIpsProject("TestProject");
        root = ipsProject.getIpsPackageFragmentRoots()[0];
    }

    public void testIsFullBuildTriggeredAfterChangesToIpsArchiveOnObjectPath() throws CoreException {
        IFile archive = ipsProject.getProject().getFile("archive.ipsar");
        IIpsProject project2 = newIpsProject("Project2");
        CreateIpsArchiveOperation op = new CreateIpsArchiveOperation(project2, archive.getLocation().toFile());
        op.run(null);
        archive.refreshLocal(1, null);
        assertTrue(archive.exists());
        
        IIpsObjectPath path = ipsProject.getIpsObjectPath();
        path.newArchiveEntry(archive);
        ipsProject.setIpsObjectPath(path);
        
        AssertThatFullBuildIsTriggeredBuilder builder = new AssertThatFullBuildIsTriggeredBuilder();
        setTestArtefactBuilder(ipsProject, builder);
        builder.called = false;
        ipsProject.getProject().build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
        assertTrue(builder.called);
        
        builder.buildKind = -1;
        builder.called = false;
        archive.touch(null);
        ipsProject.getProject().build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
        assertTrue(builder.called);
        assertEquals(IncrementalProjectBuilder.FULL_BUILD, builder.buildKind);
    }

    public void testIsFullBuildTriggeredAfterChangesToIpsProjectFile() throws CoreException {
        ipsProject.getProject().build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
        AssertThatFullBuildIsTriggeredBuilder builder = new AssertThatFullBuildIsTriggeredBuilder();
        setTestArtefactBuilder(ipsProject, builder); // this changes the properties file!
        builder.buildKind = -1;
        builder.called = false;
        ipsProject.getProject().build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
        assertTrue(builder.called);
        assertEquals(IncrementalProjectBuilder.FULL_BUILD, builder.buildKind);
    }
    
    public void testMarkerHandling() throws Exception {
        IPolicyCmptType pcType = newPolicyCmptType(root, "TestPolicy");
        pcType.setSupertype("unknownSupertype");
        pcType.getIpsSrcFile().save(true, null);
        MessageList msgList = pcType.validate();
        int numOfMsg = msgList.getNoOfMessages();
        assertTrue(numOfMsg > 0);
        ipsProject.getProject().build(IncrementalProjectBuilder.INCREMENTAL_BUILD, new NullProgressMonitor());
        IResource resource = pcType.getEnclosingResource();
        IMarker[] markers = resource.findMarkers(IpsPlugin.PROBLEM_MARKER, true, IResource.DEPTH_INFINITE);
        assertTrue(markers.length > 0);
        assertEquals(msgList.getMessage(0).getText(), (String)markers[0].getAttribute(IMarker.MESSAGE));
        assertEquals(IMarker.SEVERITY_ERROR, markers[0].getAttribute(IMarker.SEVERITY, -1));

        // test if marker got's deleted if the problem is fixed.
        pcType.setSupertype("");
        pcType.getIpsSrcFile().save(true, null);
        msgList = pcType.validate();
        assertTrue(msgList.getNoOfMessages() < numOfMsg);
        ipsProject.getProject().build(IncrementalProjectBuilder.INCREMENTAL_BUILD, new NullProgressMonitor());
        resource = pcType.getEnclosingResource();
        markers = resource.findMarkers(IpsPlugin.PROBLEM_MARKER, true, IResource.DEPTH_INFINITE);
        assertEquals(msgList.getNoOfMessages(), markers.length);
    }

    public void testMarkerForNotParsableIpsSrcFiles() throws CoreException{
        IFile file = ((IContainer)root.getCorrespondingResource()).getFile(new Path("test." + IpsObjectType.POLICY_CMPT_TYPE.getFileExtension()));
        file.create(new ByteArrayInputStream("".getBytes()), true, null);
        ipsProject.getProject().build(IncrementalProjectBuilder.INCREMENTAL_BUILD, new NullProgressMonitor());
        IMarker[] markers = file.findMarkers(IMarker.PROBLEM, true, 0);
        boolean isMessageThere = false;
        for (int i = 0; i < markers.length; i++) {
            String msg = (String)markers[i].getAttribute(IMarker.MESSAGE);
            if(msg.equals(Messages.IpsBuilder_ipsSrcFileNotParsable)){
                isMessageThere = true;
            }
        }
        assertTrue("The expected message could not be found", isMessageThere);
        
    }
    
    public void testRemoveResource() throws CoreException {
        TestRemoveIpsArtefactBuilder builder = new TestRemoveIpsArtefactBuilder();

        IIpsProjectProperties props = ipsProject.getProperties();
        props.setBuilderSetId(TestIpsArtefactBuilderSet.ID);
        ipsProject.setProperties(props);
        ((IpsModel)ipsProject.getIpsModel()).setIpsArtefactBuilderSet(ipsProject, new TestIpsArtefactBuilderSet(
                new IIpsArtefactBuilder[] { builder }));

        IIpsObject ipsObject = this.newIpsObject(ipsProject, IpsObjectType.POLICY_CMPT_TYPE, "IpsObjectToRemove");
        ipsProject.getProject().build(IncrementalProjectBuilder.INCREMENTAL_BUILD, new NullProgressMonitor());
        assertTrue(builder.buildCalled);
        ipsObject.getIpsSrcFile().getCorrespondingFile().delete(true, null);
        ipsProject.getProject().build(IncrementalProjectBuilder.INCREMENTAL_BUILD, new NullProgressMonitor());
        assertTrue(builder.deleteCalled);
    }

    // TODO pk
    public void testDependencyGraphWithMultipleDelete() {

    }

    public void testDependencyGraphWithReferencingProjects() throws Exception {

        IPolicyCmptType a = newPolicyCmptType(ipsProject, "A");
        IIpsProject projectB = createSubProject(ipsProject, "projectB");
        IPolicyCmptType b = newPolicyCmptType(projectB, "B");
        IIpsProject projectC = createSubProject(projectB, "projectC");
        IPolicyCmptType c = newPolicyCmptType(projectC, "C");
        b.setSupertype(a.getQualifiedName());
        c.setSupertype(b.getQualifiedName());

        TestDependencyIpsArtefactBuilder builder = createTestBuilderForProject(ipsProject);
        // the project needs to have its own builder set otherwise the project is considered
        // invalid since there is no builder set found for the builder set id defined in the
        // project properties
        createTestBuilderForProject(projectB);
        createTestBuilderForProject(projectC);

        ipsProject.getProject().build(IncrementalProjectBuilder.INCREMENTAL_BUILD, new NullProgressMonitor());

        projectB.getProject().build(IncrementalProjectBuilder.INCREMENTAL_BUILD, new NullProgressMonitor());

        projectC.getProject().build(IncrementalProjectBuilder.INCREMENTAL_BUILD, new NullProgressMonitor());

        List buildObjects = builder.getBuiltIpsObjects();
        assertTrue(buildObjects.contains(a));

        builder.clear();
        IAttribute attrA = a.newAttribute();
        attrA.setName("AttrA");
        attrA.setAttributeType(AttributeType.CHANGEABLE);
        attrA.setDatatype("String");
        a.getIpsSrcFile().save(true, null);

        ipsProject.getProject().build(IncrementalProjectBuilder.INCREMENTAL_BUILD, new NullProgressMonitor());

        assertTrue(buildObjects.contains(a));
        assertTrue(buildObjects.contains(b));
        assertTrue(buildObjects.contains(c));

        builder.clear();
        attrA = a.newAttribute();
        attrA.setName("attrB");
        attrA.setAttributeType(AttributeType.CHANGEABLE);
        attrA.setDatatype("String");
        a.getIpsSrcFile().save(true, null);

        ((IpsProject)projectC).getIpsProjectPropertiesFile().delete(true, null);
        ipsProject.getProject().build(IncrementalProjectBuilder.INCREMENTAL_BUILD, new NullProgressMonitor());

        assertTrue(buildObjects.contains(a));
        assertTrue(buildObjects.contains(b));
        assertFalse(buildObjects.contains(c));
    }

    private IIpsProject createSubProject(IIpsProject superProject, String projectName) throws CoreException {
        IIpsProject subProject = newIpsProject(projectName);
        // set the reference from the ips project to the referenced project
        IIpsObjectPath path = subProject.getIpsObjectPath();
        path.newIpsProjectRefEntry(superProject);
        IProjectDescription description = subProject.getProject().getDescription();
        description.setReferencedProjects(new IProject[] { superProject.getProject() });
        subProject.getProject().setDescription(description, new NullProgressMonitor());
        subProject.setIpsObjectPath(path);
        return subProject;
    }

    private TestDependencyIpsArtefactBuilder createTestBuilderForProject(IIpsProject project) throws CoreException {
        IIpsProjectProperties props = project.getProperties();
        props.setBuilderSetId(TestIpsArtefactBuilderSet.ID);
        project.setProperties(props);
        TestDependencyIpsArtefactBuilder builder = new TestDependencyIpsArtefactBuilder();
        ((IpsModel)project.getIpsModel()).setIpsArtefactBuilderSet(project, new TestIpsArtefactBuilderSet(
                new IIpsArtefactBuilder[] { builder }));
        return builder;
    }

    public void testDependencyGraph() throws CoreException {

        IIpsPackageFragment frag = root.createPackageFragment("dependency", true, null);

        IPolicyCmptType a = (IPolicyCmptType)newIpsObject(frag, IpsObjectType.POLICY_CMPT_TYPE, "A");
        IPolicyCmptType b = (IPolicyCmptType)newIpsObject(frag, IpsObjectType.POLICY_CMPT_TYPE, "B");
        b.setSupertype(a.getQualifiedName());
        IPolicyCmptType c = (IPolicyCmptType)newIpsObject(frag, IpsObjectType.POLICY_CMPT_TYPE, "C");
        c.newRelation().setTarget(a.getQualifiedName());
        IProductCmpt aProduct = (IProductCmpt)newIpsObject(frag, IpsObjectType.PRODUCT_CMPT, "AProduct");
        aProduct.setPolicyCmptType(a.getQualifiedName());
        IPolicyCmptType d = (IPolicyCmptType)newIpsObject(frag, IpsObjectType.POLICY_CMPT_TYPE, "D");
        a.newRelation().setTarget(d.getQualifiedName());

        TestDependencyIpsArtefactBuilder builder = createTestBuilderForProject(ipsProject);

        // after this incremental build the TestDependencyIpsArtefactBuilder is expected to contain
        // all new IpsObjects in
        // its build list.
        ipsProject.getProject().build(IncrementalProjectBuilder.INCREMENTAL_BUILD, new NullProgressMonitor());
        List builtIpsObjects = builder.getBuiltIpsObjects();
        assertTrue(builtIpsObjects.contains(a));
        assertTrue(builtIpsObjects.contains(b));
        assertTrue(builtIpsObjects.contains(c));
        assertTrue(builtIpsObjects.contains(d));
        assertTrue(builtIpsObjects.contains(aProduct));

        builder.clear();
        // after this second build no IpsObjects are expected in the build list since nothing has
        // changed since the last build
        ipsProject.getProject().build(IncrementalProjectBuilder.INCREMENTAL_BUILD, new NullProgressMonitor());
        assertTrue(builder.getBuiltIpsObjects().isEmpty());

        // since the PolicyCmpt b has been deleted after this build the
        // TestDependencyIpsArtefactBuilder is expected to contain
        // all dependent IpsObjects
        d.getIpsSrcFile().getCorrespondingResource().delete(true, null);
        ipsProject.getProject().build(IncrementalProjectBuilder.INCREMENTAL_BUILD, new NullProgressMonitor());
        builtIpsObjects = builder.getBuiltIpsObjects();
        assertTrue(builtIpsObjects.contains(a));
        assertTrue(builtIpsObjects.contains(aProduct));

        // recreate d. All dependants are expected to be rebuilt
        d = (IPolicyCmptType)newIpsObject(frag, IpsObjectType.POLICY_CMPT_TYPE, "D");
        ipsProject.getProject().build(IncrementalProjectBuilder.INCREMENTAL_BUILD, new NullProgressMonitor());
        builtIpsObjects = builder.getBuiltIpsObjects();
        assertTrue(builtIpsObjects.contains(a));
        assertTrue(builtIpsObjects.contains(aProduct));

        // delete d and dependants. The IpsBuilder has to make sure to only build the existing
        // IpsObjects though the graph still contains the dependency chain of the deleted IpsOjects
        // during the build cycle
        d.getIpsSrcFile().getCorrespondingResource().delete(true, null);
        a.getIpsSrcFile().getCorrespondingResource().delete(true, null);
        b.getIpsSrcFile().getCorrespondingResource().delete(true, null);
        c.getIpsSrcFile().getCorrespondingResource().delete(true, null);
        ipsProject.getProject().build(IncrementalProjectBuilder.INCREMENTAL_BUILD, new NullProgressMonitor());
    }

    public void testCleanBuild() throws CoreException{
        newPolicyCmptType(ipsProject, "mycompany.motor.MotorPolicy");
        IFile archiveFile = ipsProject.getProject().getFile("test.ipsar");
        archiveFile.getWorkspace().build(IncrementalProjectBuilder.FULL_BUILD, null);
        
        File file = archiveFile.getLocation().toFile();
        CreateIpsArchiveOperation operation = new CreateIpsArchiveOperation(ipsProject.getIpsPackageFragmentRoots(), file);
        operation.setInclJavaBinaries(true);
        operation.setInclJavaSources(true);
        operation.run(null);

        IIpsProject project2 = newIpsProject("TestProject2");
        IFile archiveFile2 = project2.getProject().getFile(archiveFile.getLocation());

        IpsArchiveEntry archiveEntry = new IpsArchiveEntry((IpsObjectPath)project2.getIpsObjectPath());
        archiveEntry.setArchiveFile(archiveFile2);
        IIpsObjectPathEntry[] entries = project2.getIpsObjectPath().getEntries();
        IIpsObjectPathEntry[] newEntries = new IIpsObjectPathEntry[entries.length + 1];
        System.arraycopy(entries, 0, newEntries, 0, entries.length);
        newEntries[newEntries.length - 1] = archiveEntry;
        IIpsObjectPath newPath = project2.getIpsObjectPath();
        newPath.setEntries(newEntries);
        project2.setIpsObjectPath(newPath);
        
        project2.getProject().build(IncrementalProjectBuilder.CLEAN_BUILD, null);

    }
    
    private void setTestArtefactBuilder(IIpsProject project, IIpsArtefactBuilder builder) throws CoreException {
        IIpsProjectProperties props = project.getProperties();
        props.setBuilderSetId(TestIpsArtefactBuilderSet.ID);
        project.setProperties(props);
        ((IpsModel)project.getIpsModel()).setIpsArtefactBuilderSet(project, new TestIpsArtefactBuilderSet(
                new IIpsArtefactBuilder[] { builder }));
    }
    
    class AssertThatFullBuildIsTriggeredBuilder extends AbstractArtefactBuilder {

        boolean called = false;
        int buildKind = -1;
        
        /**
         * @param builderSet
         */
        public AssertThatFullBuildIsTriggeredBuilder() {
            super(new TestIpsArtefactBuilderSet());
        }
        
        public void beforeBuildProcess(IIpsProject project, int buildKind) throws CoreException {
            called = true;
            this.buildKind = buildKind;
        }


        /**
         * {@inheritDoc}
         */
        public void build(IIpsSrcFile ipsSrcFile) throws CoreException {
        }

        /**
         * {@inheritDoc}
         */
        public void delete(IIpsSrcFile ipsSrcFile) throws CoreException {
        }

        /**
         * {@inheritDoc}
         */
        public String getName() {
            return "AssertThatFullBuildIsTriggeredBuilder";
        }

        /**
         * {@inheritDoc}
         */
        public boolean isBuilderFor(IIpsSrcFile ipsSrcFile) throws CoreException {
            return false;
        }
        
    }

    private static class TestRemoveIpsArtefactBuilder extends AbstractArtefactBuilder {

        /**
         * @param builderSet
         */
        public TestRemoveIpsArtefactBuilder() {
            super(new TestIpsArtefactBuilderSet());
        }

        private boolean buildCalled = false;
        private boolean deleteCalled = false;

        /**
         * {@inheritDoc}
         */
        public String getName() {
            return "TestRemoveIpsArtefactBuilder";
        }

        public void build(IIpsSrcFile ipsSrcFile) throws CoreException {
            buildCalled = true;
        }

        public boolean isBuilderFor(IIpsSrcFile ipsSrcFile) {
            return true;
        }

        public void delete(IIpsSrcFile ipsSrcFile) throws CoreException {
            deleteCalled = true;
        }

    }

    private static class TestDependencyIpsArtefactBuilder extends AbstractArtefactBuilder {

        /**
         * @param builderSet
         */
        public TestDependencyIpsArtefactBuilder() {
            super(new TestIpsArtefactBuilderSet());
        }

        private List builtIpsObjects = new ArrayList();

        public List getBuiltIpsObjects() {
            return builtIpsObjects;
        }

        public void clear() {
            builtIpsObjects.clear();
        }

        public void build(IIpsSrcFile ipsSrcFile) throws CoreException {
            builtIpsObjects.add(ipsSrcFile.getIpsObject());
        }

        public boolean isBuilderFor(IIpsSrcFile ipsSrcFile) throws CoreException {
            return ipsSrcFile.getIpsObjectType().equals(IpsObjectType.POLICY_CMPT_TYPE)
                    || ipsSrcFile.getIpsObjectType().equals(IpsObjectType.PRODUCT_CMPT);
        }

        public void delete(IIpsSrcFile ipsSrcFile) throws CoreException {
        }

        /**
         * {@inheritDoc}
         */
        public String getName() {
            return "TestDependencyIpsArtefactBuilder";
        }

    }

}
