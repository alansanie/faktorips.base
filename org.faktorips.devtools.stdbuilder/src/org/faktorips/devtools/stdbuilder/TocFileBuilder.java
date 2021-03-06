/*******************************************************************************
 * Copyright (c) Faktor Zehn AG. <http://www.faktorzehn.org>
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version
 * 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and
 * restrictions as well as the possibility of alternative license terms.
 *******************************************************************************/

package org.faktorips.devtools.stdbuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.TransformerException;

import com.google.common.collect.ImmutableSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.faktorips.devtools.core.IpsPlugin;
import org.faktorips.devtools.core.IpsStatus;
import org.faktorips.devtools.core.builder.AbstractArtefactBuilder;
import org.faktorips.devtools.core.builder.ComplianceCheck;
import org.faktorips.devtools.core.internal.model.ipsproject.IpsPackageFragmentRoot;
import org.faktorips.devtools.core.model.IVersion;
import org.faktorips.devtools.core.model.enums.IEnumContent;
import org.faktorips.devtools.core.model.enums.IEnumType;
import org.faktorips.devtools.core.model.ipsobject.IIpsObject;
import org.faktorips.devtools.core.model.ipsobject.IIpsObjectGeneration;
import org.faktorips.devtools.core.model.ipsobject.IIpsSrcFile;
import org.faktorips.devtools.core.model.ipsobject.IpsObjectType;
import org.faktorips.devtools.core.model.ipsproject.IIpsArtefactBuilderSet;
import org.faktorips.devtools.core.model.ipsproject.IIpsPackageFragmentRoot;
import org.faktorips.devtools.core.model.ipsproject.IIpsProject;
import org.faktorips.devtools.core.model.pctype.IPolicyCmptType;
import org.faktorips.devtools.core.model.productcmpt.IProductCmpt;
import org.faktorips.devtools.core.model.productcmpt.IProductCmptGeneration;
import org.faktorips.devtools.core.model.productcmpt.IProductCmptKind;
import org.faktorips.devtools.core.model.productcmpttype.IProductCmptType;
import org.faktorips.devtools.core.model.tablecontents.ITableContents;
import org.faktorips.devtools.core.model.tablestructure.ITableStructure;
import org.faktorips.devtools.core.model.testcase.ITestCase;
import org.faktorips.devtools.core.model.testcasetype.ITestCaseType;
import org.faktorips.devtools.core.util.XmlUtil;
import org.faktorips.devtools.stdbuilder.enumtype.EnumContentBuilder;
import org.faktorips.devtools.stdbuilder.enumtype.EnumXmlAdapterBuilder;
import org.faktorips.devtools.stdbuilder.productcmpt.ProductCmptXMLBuilder;
import org.faktorips.devtools.stdbuilder.table.TableContentBuilder;
import org.faktorips.devtools.stdbuilder.testcase.TestCaseBuilder;
import org.faktorips.devtools.stdbuilder.testcasetype.TestCaseTypeClassBuilder;
import org.faktorips.devtools.stdbuilder.xmodel.GeneratorConfig;
import org.faktorips.runtime.internal.DateTime;
import org.faktorips.runtime.internal.toc.EnumContentTocEntry;
import org.faktorips.runtime.internal.toc.EnumXmlAdapterTocEntry;
import org.faktorips.runtime.internal.toc.GenerationTocEntry;
import org.faktorips.runtime.internal.toc.PolicyCmptTypeTocEntry;
import org.faktorips.runtime.internal.toc.ProductCmptTocEntry;
import org.faktorips.runtime.internal.toc.ProductCmptTypeTocEntry;
import org.faktorips.runtime.internal.toc.TableContentTocEntry;
import org.faktorips.runtime.internal.toc.TestCaseTocEntry;
import org.faktorips.runtime.internal.toc.TocEntryObject;
import org.faktorips.util.StringUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * 
 * @author Jan Ortmann
 */
public class TocFileBuilder extends AbstractArtefactBuilder {

    private static final Set<IpsObjectType> SUPPORTED_TYPES = ImmutableSet.of(IpsObjectType.PRODUCT_CMPT,
            IpsObjectType.TABLE_CONTENTS, IpsObjectType.TEST_CASE, IpsObjectType.ENUM_CONTENT, IpsObjectType.ENUM_TYPE,
            IpsObjectType.POLICY_CMPT_TYPE, IpsObjectType.PRODUCT_CMPT_TYPE);

    // a map that contains the table of contents objects (value) for each table of contents file.
    private Map<IFile, TableOfContent> tocFileMap = new HashMap<IFile, TableOfContent>();

    private Map<IpsObjectType, List<ITocEntryBuilder>> ipsObjectTypeToTocEntryBuilderMap;

    public TocFileBuilder(StandardBuilderSet builderSet) {
        super(builderSet);
        initExtensionBuilders();
    }

    protected void initExtensionBuilders() {
        List<ITocEntryBuilderFactory> tocEntryBuilderFactories = StdBuilderPlugin.getDefault()
                .getTocEntryBuilderFactories();
        ipsObjectTypeToTocEntryBuilderMap = new HashMap<IpsObjectType, List<ITocEntryBuilder>>();
        for (ITocEntryBuilderFactory tocEntryBuilderFactory : tocEntryBuilderFactories) {
            ITocEntryBuilder builder = tocEntryBuilderFactory.createTocEntryBuilder(this);
            IpsObjectType ipsObjectType = builder.getIpsObjectType();
            List<ITocEntryBuilder> builderList = ipsObjectTypeToTocEntryBuilderMap.get(ipsObjectType);
            if (builderList == null) {
                builderList = new ArrayList<ITocEntryBuilder>();
                ipsObjectTypeToTocEntryBuilderMap.put(ipsObjectType, builderList);
            }
            builderList.add(builder);
        }
    }

    @Override
    public StandardBuilderSet getBuilderSet() {
        return (StandardBuilderSet)super.getBuilderSet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "TocFileBuilder"; //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isBuilderFor(IIpsSrcFile ipsSrcFile) throws CoreException {
        IpsObjectType type = ipsSrcFile.getIpsObjectType();
        return SUPPORTED_TYPES.contains(type) || ipsObjectTypeToTocEntryBuilderMap.containsKey(type);
    }

    /**
     * The toc file builder has to remember the modifcation stamp for each toc before the build
     * process starts.
     * 
     * {@inheritDoc}
     */
    @Override
    public void beforeBuildProcess(IIpsProject ipsProject, int buildKind) throws CoreException {
        if (buildKind == IncrementalProjectBuilder.FULL_BUILD) {
            tocFileMap.clear();
        }
        IIpsPackageFragmentRoot[] srcRoots = ipsProject.getSourceIpsPackageFragmentRoots();
        for (IIpsPackageFragmentRoot srcRoot : srcRoots) {
            IpsPackageFragmentRoot root = (IpsPackageFragmentRoot)srcRoot;
            if (buildKind == IncrementalProjectBuilder.FULL_BUILD) {
                getToc(root).clear();
            }
            // next lines are a workaround for a bug in PDE
            // if we create the folder in afterBuildProcess, it is marked in the MANIFEST section
            // for exported packages as not existing (but it's there).
            IFile tocFile = getBuilderSet().getRuntimeRepositoryTocFile(root);
            if (tocFile == null) {
                continue;
            }
            createFolderIfNotThere((IFolder)tocFile.getParent());
        }
    }

    /**
     * Saves the tocs that have been modified during the build.
     * 
     * {@inheritDoc}
     */
    @Override
    public void afterBuildProcess(IIpsProject ipsProject, int buildKind) throws CoreException {
        IIpsPackageFragmentRoot[] srcRoots = ipsProject.getSourceIpsPackageFragmentRoots();
        for (IIpsPackageFragmentRoot srcRoot : srcRoots) {
            IpsPackageFragmentRoot root = (IpsPackageFragmentRoot)srcRoot;
            if (getToc(root).isModified()) {
                saveToc(root);
            }
        }
    }

    /**
     * Saves the repository's table of contents to a file. The table of contents file is needed by
     * the FaktorIPS runtime to load the product components and table data.
     * 
     * @throws CoreException if an error occurs while writing the toc to the file.
     */
    private void saveToc(IIpsPackageFragmentRoot root) throws CoreException {
        IFile tocFile = getBuilderSet().getRuntimeRepositoryTocFile(root);
        if (tocFile == null) {
            return;
        }
        String encoding = root.getIpsProject().getXmlFileCharset();
        if (encoding == null) {
            return;
        }
        String xml = null;
        try {
            Document doc = IpsPlugin.getDefault().getDocumentBuilder().newDocument();
            IVersion<?> version = getIpsProject().getVersionProvider().getProjectVersion();
            Element tocElement = getToc(root).toXml(version, doc);
            doc.appendChild(tocElement);
            xml = XmlUtil.nodeToString(doc, encoding);
        } catch (TransformerException e) {
            throw new CoreException(
                    new IpsStatus("Error transforming product component registry's table of contents to xml.", e)); //$NON-NLS-1$
        }

        boolean newlyCreated = createFileIfNotThere(tocFile);
        if (!newlyCreated) {
            replaceTocFileIfContentHasChanged(root.getIpsProject(), tocFile, xml);
        } else {
            try {
                ByteArrayInputStream inputStream = new ByteArrayInputStream(xml.getBytes(encoding));
                writeToFile(tocFile, inputStream, true, true);
            } catch (UnsupportedEncodingException e1) {
                throw new CoreException(new IpsStatus(e1));
            }
        }
    }

    private void replaceTocFileIfContentHasChanged(IIpsProject ipsProject, IFile tocFile, String newContents)
            throws CoreException {
        String oldContents = null;
        String charset = ipsProject.getXmlFileCharset();
        try {
            oldContents = StringUtil.readFromInputStream(tocFile.getContents(), charset);
            // CSOFF: Empty Statement
        } catch (IOException e) {
            // if an error occurs reading the old contents, we just write the new one
            // e.g. an error can occur if the toc file isn't synchronized
        }
        // CSON: Empty Statement
        if (newContents.equals(oldContents)) {
            return;
        }
        InputStream is;
        try {
            is = new ByteArrayInputStream(newContents.getBytes(charset));
        } catch (UnsupportedEncodingException e1) {
            throw new CoreException(new IpsStatus(e1));
        }
        writeToFile(tocFile, is, true, true);
    }

    private TableOfContent getToc(IIpsSrcFile ipsSrcFile) throws CoreException {
        IIpsPackageFragmentRoot root = ipsSrcFile.getIpsObject().getIpsPackageFragment().getRoot();
        return getToc(root);
    }

    /**
     * Returns the product component registry's table of contents for the indicated ips package
     * fragment root.
     * 
     * @throws CoreException if an error occurs while accessing the toc file.
     */
    public TableOfContent getToc(IIpsPackageFragmentRoot root) throws CoreException {
        IIpsArtefactBuilderSet builderSet = root.getIpsProject().getIpsArtefactBuilderSet();
        IFile tocFile = builderSet.getRuntimeRepositoryTocFile(root);
        TableOfContent toc = tocFileMap.get(tocFile);
        if (toc == null) {
            toc = new TableOfContent();
            if (tocFile != null && tocFile.exists()) {
                InputStream is = tocFile.getContents(true);
                Document doc;
                try {
                    DocumentBuilder builder = IpsPlugin.getDefault().getDocumentBuilder();
                    doc = builder.parse(is);
                } catch (IOException ioe) {
                    // can happen if the file is deleted in the filesystem, but the workspace has
                    // not been synchronized
                    // nothing seriuos, we just write the file again
                    doc = null;
                    tocFile.refreshLocal(1, null);
                } catch (SAXException e) {
                    // can happen if the file is deleted in the filesystem, but the workspace has
                    // not been synchronized
                    // nothing seriuos, we just write the file again
                    doc = null;
                    tocFile.refreshLocal(1, null);
                }
                if (doc != null) {
                    Element tocEl = doc.getDocumentElement();
                    toc.initFromXml(tocEl);
                }
            }
            tocFileMap.put(tocFile, toc);
        }
        return toc;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void build(IIpsSrcFile ipsSrcFile) throws CoreException {
        IIpsObject object = null;
        try {
            List<TocEntryObject> entries = new ArrayList<TocEntryObject>();
            object = ipsSrcFile.getIpsObject();
            if (!object.isValid(getIpsProject())) {
                return;
            }
            IpsObjectType type = object.getIpsObjectType();
            if (type.equals(IpsObjectType.PRODUCT_CMPT)) {
                entries.add(createTocEntry((IProductCmpt)object));
            } else if (type.equals(IpsObjectType.TABLE_CONTENTS)) {
                entries.add(createTocEntry((ITableContents)object));
            } else if (type.equals(IpsObjectType.PRODUCT_CMPT_TYPE)) {
                entries.add(createTocEntry((IProductCmptType)object));
            } else if (type.equals(IpsObjectType.POLICY_CMPT_TYPE)) {
                entries.add(createTocEntry((IPolicyCmptType)object));
            } else if (type.equals(IpsObjectType.TEST_CASE)) {
                entries.add(createTocEntry((ITestCase)object));
            } else if (type.equals(IpsObjectType.ENUM_CONTENT)) {
                entries.add(createTocEntry((IEnumContent)object));
            } else if (type.equals(IpsObjectType.ENUM_TYPE)) {
                entries.add(createTocEntry((IEnumType)object));
            } else if (ipsObjectTypeToTocEntryBuilderMap.containsKey(type)) {
                List<ITocEntryBuilder> builderList = ipsObjectTypeToTocEntryBuilderMap.get(type);
                for (ITocEntryBuilder builder : builderList) {
                    entries.addAll(builder.createTocEntries(object));
                }
            } else {
                throw new RuntimeException("Unknown ips object type " + object.getIpsObjectType()); //$NON-NLS-1$
            }
            if (!entries.isEmpty()) {
                for (TocEntryObject entry : entries) {
                    getToc(ipsSrcFile).addOrReplaceTocEntry(entry);
                }
            } else {
                // no toc entry has been newly created, remove the previous toc entry
                getToc(ipsSrcFile).removeEntry(object.getQualifiedNameType());
            }
        } catch (CoreException e) {
            IStatus status = new IpsStatus("Unable to update the runtime repository toc file with the entry for: " //$NON-NLS-1$
                    + object.getQualifiedName(), e);
            throw new CoreException(status);
        }
    }

    public ProductCmptTocEntry createTocEntry(IProductCmpt productCmpt) throws CoreException {
        if (productCmpt.getNumOfGenerations() == 0) {
            return null;
        }
        IProductCmptType pcType = productCmpt.findProductCmptType(productCmpt.getIpsProject());
        if (pcType == null) {
            return null;
        }
        IProductCmptKind kind = productCmpt.getKindId();
        if (kind == null) {
            return null;
        }
        IPath xmlContentRelativeFile = getBuilderSet()
                .getBuilderById(BuilderKindIds.PRODUCT_CMPT_XML, ProductCmptXMLBuilder.class)
                .getXmlContentRelativeFile(productCmpt.getIpsSrcFile());
        String ipsObjectId = productCmpt.getRuntimeId();
        String ipsObjectQName = productCmpt.getQualifiedName();

        String implementationClass = getBuilderSet().getProductCmptBuilder().getImplementationClass(productCmpt);
        String generationImplClass = getBuilderSet().getProductCmptGenImplClassBuilder()
                .getQualifiedClassName(pcType.getIpsSrcFile());
        String kindId = productCmpt.getKindId().getRuntimeId();
        String versionId = productCmpt.getVersionId();
        DateTime validTo = DateTime.createDateOnly(productCmpt.getValidTo());

        ProductCmptTocEntry entry = new ProductCmptTocEntry(ipsObjectId, ipsObjectQName, kindId, versionId,
                xmlContentRelativeFile.toString(), implementationClass, generationImplClass, validTo);
        if (pcType.isChangingOverTime()) {
            createProductCmptGenerationTocEntries(productCmpt, xmlContentRelativeFile, entry);
        }
        return entry;
    }

    private void createProductCmptGenerationTocEntries(IProductCmpt productCmpt,
            IPath xmlContentRelativeFile,
            ProductCmptTocEntry entry) {

        IIpsObjectGeneration[] generations = productCmpt.getGenerationsOrderedByValidDate();
        List<GenerationTocEntry> genEntries = new ArrayList<GenerationTocEntry>(generations.length);
        for (IIpsObjectGeneration generation : generations) {
            DateTime validFrom = DateTime.createDateOnly(generation.getValidFrom());
            if (validFrom == null) {
                continue;
            }
            String generationClassName = getBuilderSet().getProductCmptBuilder()
                    .getImplementationClass((IProductCmptGeneration)generation);
            genEntries.add(
                    new GenerationTocEntry(entry, validFrom, generationClassName, xmlContentRelativeFile.toString()));
        }
        entry.setGenerationEntries(genEntries);
    }

    public TocEntryObject createTocEntry(ITableContents tableContents) throws CoreException {
        ITableStructure tableStructure = tableContents.findTableStructure(getIpsProject());
        if (tableStructure == null) {
            return null;
        }
        TableContentBuilder tableContentBuilder = getBuilderSet().getBuilderById(BuilderKindIds.TABLE_CONTENT,
                TableContentBuilder.class);
        IPath xmlRelativeFile = tableContentBuilder.getXmlContentRelativeFile(tableContents.getIpsSrcFile());
        String tableStructureName = getBuilderSet().getTableBuilder()
                .getQualifiedClassName(tableStructure.getIpsSrcFile());
        TocEntryObject entry = new TableContentTocEntry(tableContents.getQualifiedName(),
                tableContents.getQualifiedName(), xmlRelativeFile.toString(), tableStructureName);
        return entry;
    }

    /**
     * Creates a toc entry for the given test case.
     */
    public TocEntryObject createTocEntry(ITestCase testCase) throws CoreException {
        ITestCaseType type = testCase.findTestCaseType(getIpsProject());
        if (type == null) {
            return null;
        }
        /*
         * generate the object id: the objectId for this element will be the package root name
         * concatenated with the qualified name
         */
        String packageRootName = testCase.getIpsPackageFragment().getRoot().getName();
        String objectId = packageRootName + "." + testCase.getQualifiedName(); //$NON-NLS-1$
        objectId = objectId.replace('.', '/') + "." + IpsObjectType.TEST_CASE.getFileExtension(); //$NON-NLS-1$

        String xmlResourceName = getBuilderSet().getBuilderById(BuilderKindIds.TEST_CASE, TestCaseBuilder.class)
                .getXmlContentRelativeFile(testCase.getIpsSrcFile()).toString();
        String testCaseTypeName = getBuilderSet()
                .getBuilderById(BuilderKindIds.TEST_CASE_TYPE, TestCaseTypeClassBuilder.class)
                .getQualifiedClassName(type);
        TocEntryObject entry = new TestCaseTocEntry(objectId, testCase.getQualifiedName(), xmlResourceName,
                testCaseTypeName);
        return entry;
    }

    /** Creates a toc entry for the given enum content. */
    public TocEntryObject createTocEntry(IEnumContent enumContent) {
        IEnumType enumType = enumContent.findEnumType(enumContent.getIpsProject());
        if (enumType == null) {
            return null;
        }

        /*
         * generate the object id: the objectId for this element will be the package root name
         * concatenated with the qualified name
         */
        String packageRootName = enumContent.getIpsPackageFragment().getRoot().getName();
        String objectId = packageRootName + "." + enumContent.getQualifiedName(); //$NON-NLS-1$
        objectId = objectId.replace('.', '/') + "." + IpsObjectType.ENUM_CONTENT.getFileExtension(); //$NON-NLS-1$

        IPath xmlResourceName = getBuilderSet().getBuilderById(BuilderKindIds.ENUM_CONTENT, EnumContentBuilder.class)
                .getXmlContentRelativeFile(enumContent.getIpsSrcFile());

        String enumTypeName = getBuilderSet().getEnumTypeBuilder().getQualifiedClassName(enumType);
        TocEntryObject entry = new EnumContentTocEntry(objectId, enumContent.getQualifiedName(),
                xmlResourceName.toString(), enumTypeName);
        return entry;
    }

    public TocEntryObject createTocEntry(IEnumType enumType) {
        if (!GeneratorConfig.forIpsObject(enumType).isGenerateJaxbSupport()
                || !ComplianceCheck.isComplianceLevelAtLeast5(getIpsProject())) {
            return null;
        }
        if (enumType.isInextensibleEnum() || enumType.isAbstract()) {
            return null;
        }
        TocEntryObject entry = new EnumXmlAdapterTocEntry(enumType.getQualifiedName(), enumType.getQualifiedName(),
                getBuilderSet().getBuilderById(BuilderKindIds.ENUM_XML_ADAPTER, EnumXmlAdapterBuilder.class)
                        .getQualifiedClassName(enumType));
        return entry;
    }

    /**
     * Creates a toc entry for the given model type.
     */
    public TocEntryObject createTocEntry(IPolicyCmptType type) {
        String id = type.getQualifiedName();
        String javaImplClass = getBuilderSet().getPolicyCmptImplClassBuilder().getQualifiedClassName(type);
        return new PolicyCmptTypeTocEntry(id, type.getQualifiedName(), javaImplClass);
    }

    /**
     * Creates a toc entry for the given model type.
     */
    public TocEntryObject createTocEntry(IProductCmptType type) {
        String id = type.getQualifiedName();
        String javaImplClass = getBuilderSet().getProductCmptImplClassBuilder().getQualifiedClassName(type);
        return new ProductCmptTypeTocEntry(id, type.getQualifiedName(), javaImplClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(IIpsSrcFile ipsSrcFile) throws CoreException {
        TableOfContent toc = getToc(ipsSrcFile.getIpsPackageFragment().getRoot());
        toc.removeEntry(ipsSrcFile.getQualifiedNameType());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean buildsDerivedArtefacts() {
        return true;
    }

    @Override
    public boolean isBuildingInternalArtifacts() {
        return getBuilderSet().isGeneratePublishedInterfaces();
    }
}
