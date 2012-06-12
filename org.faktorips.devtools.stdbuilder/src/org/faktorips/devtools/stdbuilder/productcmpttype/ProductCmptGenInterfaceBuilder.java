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

package org.faktorips.devtools.stdbuilder.productcmpttype;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.faktorips.codegen.DatatypeHelper;
import org.faktorips.codegen.JavaCodeFragmentBuilder;
import org.faktorips.devtools.core.builder.naming.JavaClassNaming;
import org.faktorips.devtools.core.model.ipsobject.IIpsSrcFile;
import org.faktorips.devtools.core.model.pctype.IPolicyCmptType;
import org.faktorips.devtools.core.model.pctype.IPolicyCmptTypeAttribute;
import org.faktorips.devtools.core.model.productcmpttype.IProductCmptType;
import org.faktorips.devtools.core.model.productcmpttype.IProductCmptTypeAssociation;
import org.faktorips.devtools.core.model.productcmpttype.IProductCmptTypeMethod;
import org.faktorips.devtools.core.model.productcmpttype.ITableStructureUsage;
import org.faktorips.devtools.core.model.type.IAssociation;
import org.faktorips.devtools.core.model.type.IMethod;
import org.faktorips.devtools.stdbuilder.StandardBuilderSet;
import org.faktorips.devtools.stdbuilder.policycmpttype.GenPolicyCmptType;
import org.faktorips.devtools.stdbuilder.productcmpttype.association.GenProdAssociation;
import org.faktorips.devtools.stdbuilder.productcmpttype.method.GenProductCmptTypeMethod;
import org.faktorips.runtime.IProductComponentGeneration;
import org.faktorips.util.LocalizedStringsSet;
import org.faktorips.util.StringUtil;

/**
 * Builder that generates Java source files (compilation units) containing the source code for the
 * published interface of a product component generation.
 * 
 * @author Jan Ortmann
 */
public class ProductCmptGenInterfaceBuilder extends BaseProductCmptTypeBuilder {

    private ProductCmptInterfaceBuilder productCmptInterfaceBuilder;

    public ProductCmptGenInterfaceBuilder(StandardBuilderSet builderSet) {
        super(builderSet, new LocalizedStringsSet(ProductCmptGenInterfaceBuilder.class));
        setMergeEnabled(true);
        setJavaClassNaming(new JavaClassNaming(generatesInterface(), isBuildingPublishedSourceFile(),
                !buildsDerivedArtefacts()) {

            @Override
            public String getUnqualifiedClassName(IIpsSrcFile ipsSrcFile) {
                String name = ipsSrcFile.getIpsObjectName() + getAbbreviationForGenerationConcept(ipsSrcFile);
                return getJavaNamingConvention().getPublishedInterfaceName(name);
            }

        });

    }

    public ProductCmptInterfaceBuilder getProductCmptInterfaceBuilder() {
        return productCmptInterfaceBuilder;
    }

    public void setProductCmptInterfaceBuilder(ProductCmptInterfaceBuilder productCmptInterfaceBuilder) {
        this.productCmptInterfaceBuilder = productCmptInterfaceBuilder;
    }

    @Override
    protected boolean generatesInterface() {
        return true;
    }

    @Override
    protected String[] getExtendedInterfaces() throws CoreException {
        String javaSupertype = IProductComponentGeneration.class.getName();
        IProductCmptType supertype = (IProductCmptType)getProductCmptType().findSupertype(getIpsProject());
        if (supertype != null) {
            String pack = getPackage(supertype.getIpsSrcFile());
            javaSupertype = StringUtil.qualifiedName(pack, getUnqualifiedClassName(supertype.getIpsSrcFile()));
        }
        return new String[] { javaSupertype };
    }

    @Override
    protected String getSuperclass() throws CoreException {
        return null;
    }

    @Override
    protected void generateTypeJavadoc(JavaCodeFragmentBuilder builder) throws CoreException {
        String generationConceptName = getChangesInTimeNamingConvention(getIpsObject())
                .getGenerationConceptNameSingular(getLanguageUsedInGeneratedSourceCode());
        appendLocalizedJavaDoc("INTERFACE", new String[] { generationConceptName, getProductCmptType().getName() },
                getIpsObject(), builder);
    }

    @Override
    protected void generateOtherCode(JavaCodeFragmentBuilder constantsBuilder,
            JavaCodeFragmentBuilder memberVarsBuilder,
            JavaCodeFragmentBuilder methodsBuilder) throws CoreException {

        generateMethodTypeSafeGetProductCmpt(methodsBuilder);
        if (getPcType() != null && !getPcType().isAbstract()) {
            generateMethodCreatePolicyCmpt(methodsBuilder);
        }
    }

    @Override
    protected boolean isChangingOverTimeContainer() {
        return true;
    }

    /**
     * Code sample.
     * 
     * <pre>
     * public IHtMotorPolicyType getHtMotorPolicyType();
     * </pre>
     */
    protected void generateMethodTypeSafeGetProductCmpt(JavaCodeFragmentBuilder methodsBuilder) throws CoreException {
        String generationConceptName = getChangesInTimeNamingConvention(getIpsObject())
                .getGenerationConceptNameSingular(getLanguageUsedInGeneratedSourceCode(), true);
        appendLocalizedJavaDoc("METHOD_TYPESAFE_GET_PRODUCT_CMPT", new String[] { getProductCmptType().getName(),
                generationConceptName }, getIpsObject(), methodsBuilder);
        generateSignatureTypeSafeGetProductCmpt(getProductCmptType(), methodsBuilder);
        methodsBuilder.appendln(";");
    }

    /**
     * Code sample.
     * 
     * <pre>
     * public IHtMotorPolicyType getHtMotorPolicyType()
     * </pre>
     */
    protected void generateSignatureTypeSafeGetProductCmpt(IProductCmptType productCmptType,
            JavaCodeFragmentBuilder methodsBuilder) throws CoreException {
        String productCmptInterface = productCmptInterfaceBuilder
                .getQualifiedClassName(productCmptType.getIpsSrcFile());
        String methodName = "get" + StringUtils.capitalize(productCmptType.getName());
        methodsBuilder.signature(java.lang.reflect.Modifier.PUBLIC, productCmptInterface, methodName, new String[] {},
                new String[] {});
    }

    @Override
    protected void generateConstructors(JavaCodeFragmentBuilder builder) throws CoreException {
        // nothing to do, building an interface.
    }

    /**
     * Code sample:
     * 
     * <pre>
     * [javadoc]
     * public IPolicy createPolicy();
     * </pre>
     */
    protected void generateMethodCreatePolicyCmpt(JavaCodeFragmentBuilder methodsBuilder) throws CoreException {
        IPolicyCmptType policyCmptType = getPcType();
        GenPolicyCmptType genPcType = getBuilderSet().getGenerator(policyCmptType);
        String policyCmptTypeName = genPcType.getPolicyCmptTypeName();
        appendLocalizedJavaDoc("METHOD_CREATE_POLICY_CMPT", new String[] { policyCmptTypeName }, getIpsObject(),
                methodsBuilder);
        genPcType.generateSignatureCreatePolicyCmpt(methodsBuilder);
        methodsBuilder.append(';');
    }

    @SuppressWarnings("unused")
    protected void generateCodeForMethod(IPolicyCmptTypeAttribute a,
            DatatypeHelper datatypeHelper,
            JavaCodeFragmentBuilder memberVarsBuilder,
            JavaCodeFragmentBuilder methodsBuilder) throws CoreException {
        // nothing to do, computation methods are not published.
    }

    @Override
    protected void generateCodeForPolicyCmptTypeAttribute(IPolicyCmptTypeAttribute a,
            DatatypeHelper datatypeHelper,
            JavaCodeFragmentBuilder fieldsBuilder,
            JavaCodeFragmentBuilder methodsBuilder) throws CoreException {
        if (a.getModifier().equals(org.faktorips.devtools.core.model.ipsobject.Modifier.PUBLISHED)) {
            super.generateCodeForPolicyCmptTypeAttribute(a, datatypeHelper, fieldsBuilder, methodsBuilder);
        }
    }

    @Override
    protected void generateCodeForNoneDerivedUnionAssociation(IProductCmptTypeAssociation association,
            JavaCodeFragmentBuilder memberVarsBuilder,
            JavaCodeFragmentBuilder methodsBuilder) throws Exception {
        GenProdAssociation generator = getGenerator(association);
        generator.generate(generatesInterface(), getIpsProject(), getMainTypeSection());
    }

    @Override
    protected void generateCodeForDerivedUnionAssociationDefinition(IProductCmptTypeAssociation containerAssociation,
            JavaCodeFragmentBuilder memberVarsBuilder,
            JavaCodeFragmentBuilder methodsBuilder) throws Exception {
        GenProdAssociation generator = getGenerator(containerAssociation);
        generator.generateCodeForDerivedUnionAssociationDefinition(methodsBuilder);
    }

    @Override
    protected void generateCodeForDerivedUnionAssociationImplementation(IProductCmptTypeAssociation derivedUnionAssociation,
            List<IAssociation> implementationAssociations,
            JavaCodeFragmentBuilder memberVarsBuilder,
            JavaCodeFragmentBuilder methodsBuilder) throws Exception {
        // nothing to do
    }

    @Override
    protected void generateCodeForTableUsage(ITableStructureUsage tsu,
            JavaCodeFragmentBuilder fieldsBuilder,
            JavaCodeFragmentBuilder methodsBuilder) throws CoreException {
        // nothing to do, table usage methods are not published.
    }

    @Override
    protected void generateCodeForMethodDefinedInModel(IMethod method, JavaCodeFragmentBuilder methodsBuilder)
            throws CoreException {

        GenProductCmptTypeMethod generator = getBuilderSet().getGenerator(getProductCmptType()).getGenerator(
                (IProductCmptTypeMethod)method);
        if (generator != null) {
            generator.generate(generatesInterface(), getIpsProject(), getMainTypeSection());
        }
    }

    @Override
    public boolean isBuildingPublishedSourceFile() {
        return true;
    }

}
