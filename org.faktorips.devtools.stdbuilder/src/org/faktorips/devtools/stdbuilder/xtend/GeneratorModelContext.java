/*******************************************************************************
 * Copyright (c) Faktor Zehn AG. <http://www.faktorzehn.org>
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version
 * 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and
 * restrictions as well as the possibility of alternative license terms.
 *******************************************************************************/

package org.faktorips.devtools.stdbuilder.xtend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.xtend.expression.ResourceManager;
import org.faktorips.devtools.core.builder.AbstractBuilderSet;
import org.faktorips.devtools.core.builder.IJavaPackageStructure;
import org.faktorips.devtools.core.builder.naming.JavaClassNaming;
import org.faktorips.devtools.core.model.ipsproject.IIpsArtefactBuilderSet;
import org.faktorips.devtools.core.model.ipsproject.IIpsArtefactBuilderSetConfig;
import org.faktorips.devtools.core.model.ipsproject.IIpsProject;
import org.faktorips.devtools.core.model.ipsproject.IIpsSrcFolderEntry;
import org.faktorips.devtools.stdbuilder.AnnotatedJavaElementType;
import org.faktorips.devtools.stdbuilder.AnnotationGeneratorBuilder;
import org.faktorips.devtools.stdbuilder.IAnnotationGenerator;
import org.faktorips.devtools.stdbuilder.StandardBuilderSet;
import org.faktorips.devtools.stdbuilder.StandardBuilderSet.FormulaCompiling;
import org.faktorips.devtools.stdbuilder.xmodel.AbstractGeneratorModelNode;
import org.faktorips.devtools.stdbuilder.xmodel.IGeneratedJavaElement;
import org.faktorips.devtools.stdbuilder.xmodel.ImportHandler;
import org.faktorips.devtools.stdbuilder.xmodel.ImportStatement;
import org.faktorips.runtime.internal.AbstractJaxbModelObject;
import org.faktorips.runtime.internal.AbstractModelObject;
import org.faktorips.runtime.internal.ProductComponent;

/**
 * This class holds all the context information needed to generate the java code with our XPAND
 * builder framework. Context information are for example the java class naming or the builder
 * configuration.
 * <p>
 * The import handler for a single file build is also stored in this context and need to be reseted
 * for every new file. In fact this is not the optimum but ok for the moment. To be thread safe the
 * import handler is stored as {@link ThreadLocal} variable.
 * 
 * 
 * @author widmaier
 */
public class GeneratorModelContext {

    private final JavaClassNaming javaClassNaming;

    /**
     * The import handler holds the import statements for a single file. However this context is the
     * same for all file generations. Because every file is generated sequentially in one thread we
     * could reuse a {@link ThreadLocal} variable in this model context. Every new file have to
     * clear its {@link ImportHandler} before starting generation.
     */
    private final ThreadLocal<ImportHandler> importHandlerThreadLocal = new ThreadLocal<ImportHandler>();

    private final ThreadLocal<GeneratorModelCaches> generatorModelCacheThreadLocal = new ThreadLocal<GeneratorModelCaches>();

    private final ThreadLocal<LinkedHashMap<AbstractGeneratorModelNode, List<IGeneratedJavaElement>>> generatedJavaElements = new ThreadLocal<LinkedHashMap<AbstractGeneratorModelNode, List<IGeneratedJavaElement>>>();

    private final ThreadLocal<ResourceManager> resourceManager = new ThreadLocal<ResourceManager>();

    private final IIpsArtefactBuilderSetConfig config;

    private final Map<AnnotatedJavaElementType, List<IAnnotationGenerator>> annotationGeneratorMap;

    private final IJavaPackageStructure javaPackageStructure;

    private final IIpsProject ipsProject;

    public GeneratorModelContext(IIpsArtefactBuilderSetConfig config, IJavaPackageStructure javaPackageStructure,
            IIpsProject ipsProject) {
        this(config, javaPackageStructure, new HashMap<AnnotatedJavaElementType, List<IAnnotationGenerator>>(),
                ipsProject);
        annotationGeneratorMap.putAll(new AnnotationGeneratorBuilder(ipsProject).createAnnotationGenerators());
    }

    public GeneratorModelContext(IIpsArtefactBuilderSetConfig config, IJavaPackageStructure javaPackageStructure,
            Map<AnnotatedJavaElementType, List<IAnnotationGenerator>> annotationGeneratorMap, IIpsProject ipsProject) {
        this.config = config;
        this.javaPackageStructure = javaPackageStructure;
        this.annotationGeneratorMap = annotationGeneratorMap;
        this.ipsProject = ipsProject;
        this.javaClassNaming = new JavaClassNaming(javaPackageStructure, true);
    }

    /**
     * Resetting the builder context for starting a new build process with clean context
     * information.
     * 
     * @param packageOfArtifacts The package of the source file to be generated to handle the
     *            correct import statements
     */
    public void resetContext(String packageOfArtifacts) {
        importHandlerThreadLocal.set(new ImportHandler(packageOfArtifacts));
        generatorModelCacheThreadLocal.set(new GeneratorModelCaches());
        generatedJavaElements.set(new LinkedHashMap<AbstractGeneratorModelNode, List<IGeneratedJavaElement>>());
    }

    IIpsArtefactBuilderSetConfig getConfig() {
        return config;
    }

    /**
     * Returns the thread local import handler. The import handler stores all import statements
     * needed in the generated class file.
     * <p>
     * The import handler is stored as {@link ThreadLocal} variable to have the ability to generate
     * different files in different threads
     * <p>
     * To be able to use the generator model nodes also if no build process is running, this method
     * would return a new import handler in the case there is no import handler yet.
     * 
     * @return The thread local import handler
     */
    public ImportHandler getImportHandler() {
        ImportHandler importHandler = importHandlerThreadLocal.get();
        if (importHandler != null) {
            return importHandler;
        } else {
            return new ImportHandler(StringUtils.EMPTY);
        }
    }

    /**
     * Sets the thread local import handler. The import handler stores all import statements needed
     * in the generated class file.
     * <p>
     * The import handler is stored as {@link ThreadLocal} variable to have the ability to generate
     * different files in different threads
     * 
     * @param importHandler The thread local import handler
     */
    protected void setImportHandler(ImportHandler importHandler) {
        this.importHandlerThreadLocal.set(importHandler);
    }

    /**
     * Returns the thread local generator model cache. The generator model cache stores all cached
     * object references that may change on any time.
     * <p>
     * The generator model cache is stored as {@link ThreadLocal} variable to have the ability to
     * generate different files in different threads
     * 
     * @return The thread local generator model cache
     */
    public GeneratorModelCaches getGeneratorModelCache() {
        return generatorModelCacheThreadLocal.get();
    }

    /**
     * Getting the set of collected import statements.
     * 
     * @return Returns the imports.
     */
    public Set<ImportStatement> getImports() {
        return getImportHandler().getImports();
    }

    /**
     * Adds a new import. The import statement should be the full qualified name of a class.
     * 
     * @param importStatement The full qualified name of a class that should be imported.
     * @return the qualified or unqualified class name depending on whether it is required.
     * @see ImportHandler#addImportAndReturnClassName(String)
     */
    public String addImport(String importStatement) {
        return getImportHandler().addImportAndReturnClassName(importStatement);
    }

    public boolean removeImport(String importStatement) {
        return getImportHandler().remove(importStatement);
    }

    /**
     * Returns the thread local generated artifacts map that maps a
     * {@link AbstractGeneratorModelNode} to a list of generated java elements.
     * <p>
     * The map is stored as {@link ThreadLocal} variable to have the ability to generate different
     * files in different threads.
     * 
     * @return The thread local map holding the generated java elements for the generator model
     *         nodes
     */
    private LinkedHashMap<AbstractGeneratorModelNode, List<IGeneratedJavaElement>> getGeneratedJavaElementsMap() {
        return generatedJavaElements.get();
    }

    /**
     * Add a generated java element to the list of generated elements for the specified generator
     * model node
     * 
     * @param node The generator model node that generates the specified element
     * @param element the generated java element
     */
    public void addGeneratedJavaElement(AbstractGeneratorModelNode node, IGeneratedJavaElement element) {
        List<IGeneratedJavaElement> list = getGeneratedJavaElements(node);
        list.add(element);
    }

    /**
     * Returns the list of generated java elements that is stored for the specified
     * {@link AbstractGeneratorModelNode}. The list is stored in a {@link ThreadLocal}.
     * 
     * @param node The generator model node for which you want to have the generated artifacts
     * @return The list of generated java elements for the specified generator model nodes
     */
    public List<IGeneratedJavaElement> getGeneratedJavaElements(AbstractGeneratorModelNode node) {
        List<IGeneratedJavaElement> list = getGeneratedJavaElementsMap().get(node);
        if (list == null) {
            list = new ArrayList<IGeneratedJavaElement>();
            getGeneratedJavaElementsMap().put(node, list);
        }
        return list;
    }

    /**
     * Returns the thread local resource manager. If there is no resource manager yet this method
     * would create a new one (lazy loading).
     * <p>
     * The resource manager needs to be thread local because the resources seems to be stateful in
     * XPAND. That means a resource may have the state of the current template evaluation. Hence if
     * you use the same resource manager in two threads both threads would use the same resource and
     * we get a concurrent modification exception.
     * 
     * @return The thread local resource manager
     */
    public ResourceManager getResourceManager() {
        ResourceManager localResourceManager = resourceManager.get();
        if (localResourceManager == null) {
            localResourceManager = new OptimizedResourceManager();
            resourceManager.set(localResourceManager);
        }
        return localResourceManager;
    }

    public Locale getLanguageUsedInGeneratedSourceCode() {
        String localeString = getConfig().getPropertyValueAsString(StandardBuilderSet.CONFIG_PROPERTY_GENERATOR_LOCALE);
        if (localeString == null) {
            return Locale.ENGLISH;
        }
        return AbstractBuilderSet.getLocale(localeString);
    }

    /**
     * Returns the list of annotation generators for the given type. This method never returns null.
     * If there is no annotation generator for the specified type an empty list will be returned.
     * 
     * @param type The {@link AnnotatedJavaElementType} you want to get the generators for
     * @return the list of {@link IAnnotationGenerator annotation generators} or an empty list if
     *         there is none
     */
    public List<IAnnotationGenerator> getAnnotationGenerator(AnnotatedJavaElementType type) {
        List<IAnnotationGenerator> result = annotationGeneratorMap.get(type);
        if (result == null) {
            result = new ArrayList<IAnnotationGenerator>();
        }
        return result;
    }

    public JavaClassNaming getJavaClassNaming() {
        return javaClassNaming;
    }

    public String getValidationMessageBundleBaseName(IIpsSrcFolderEntry entry) {
        String baseName = javaPackageStructure.getBasePackageName(entry, true, false) + "."
                + entry.getValidationMessagesBundle();
        return baseName;
    }

    public boolean isGenerateChangeSupport() {
        return config.getPropertyValueAsBoolean(StandardBuilderSet.CONFIG_PROPERTY_GENERATE_CHANGELISTENER)
                .booleanValue();
    }

    public FormulaCompiling getFormulaCompiling() {
        String kind = getConfig().getPropertyValueAsString(StandardBuilderSet.CONFIG_PROPERTY_FORMULA_COMPILING);
        try {
            return FormulaCompiling.valueOf(kind);
            // CSOFF: IllegalCatch
        } catch (Exception e) {
            // CSON: IllegalCatch
            // if value is not set correctly we use Both as default value
            return FormulaCompiling.Both;
        }
    }

    /**
     * Returns whether to generate camel case constant names with underscore separator or without.
     * For example if this property is true, the constant for the property
     * checkAnythingAndDoSomething would be generated as CHECK_ANYTHING_AND_DO_SOMETHING, if the
     * property is false the constant name would be CHECKANYTHINGANDDOSOMETHING.
     * 
     * @see StandardBuilderSet#CONFIG_PROPERTY_CAMELCASE_SEPARATED
     */
    public boolean isGenerateSeparatedCamelCase() {
        Boolean propertyValueAsBoolean = getConfig()
                .getPropertyValueAsBoolean(StandardBuilderSet.CONFIG_PROPERTY_CAMELCASE_SEPARATED);
        return propertyValueAsBoolean == null ? false : propertyValueAsBoolean.booleanValue();
    }

    public boolean isGenerateDeltaSupport() {
        Boolean propertyValueAsBoolean = getConfig()
                .getPropertyValueAsBoolean(StandardBuilderSet.CONFIG_PROPERTY_GENERATE_DELTA_SUPPORT);
        return propertyValueAsBoolean == null ? false : propertyValueAsBoolean;
    }

    public boolean isGenerateCopySupport() {
        Boolean propertyValueAsBoolean = getConfig()
                .getPropertyValueAsBoolean(StandardBuilderSet.CONFIG_PROPERTY_GENERATE_COPY_SUPPORT);
        return propertyValueAsBoolean == null ? false : propertyValueAsBoolean;
    }

    public boolean isGenerateVisitorSupport() {
        Boolean propertyValueAsBoolean = getConfig()
                .getPropertyValueAsBoolean(StandardBuilderSet.CONFIG_PROPERTY_GENERATE_VISITOR_SUPPORT);
        return propertyValueAsBoolean == null ? false : propertyValueAsBoolean;
    }

    public boolean isGenerateToXmlSupport() {
        Boolean propertyValueAsBoolean = getConfig()
                .getPropertyValueAsBoolean(StandardBuilderSet.CONFIG_PROPERTY_TO_XML_SUPPORT);
        return propertyValueAsBoolean == null ? false : propertyValueAsBoolean;
    }

    /**
     * Returns <code>true</code> if the given project is configured to generate published
     * interfaces, <code>false</code> else.
     * <p>
     * If the given project differs from this context's project, this method always asks the current
     * {@link IIpsArtefactBuilderSet} for its {@link IIpsArtefactBuilderSetConfig} and retrieves the
     * value of the generate-published-interfaces setting.
     * <p>
     * If, however, the given project is equal to the project of this context, this method uses the
     * context's own {@link IIpsArtefactBuilderSetConfig}. This is important as the project's
     * {@link IIpsArtefactBuilderSetConfig config} may not be available during initialization of the
     * builder set.
     * 
     * @param ipsProject The project in which the property is configured
     * @return <code>true</code> if the project is configured to generate published interfaces,
     *         <code>false</code> if not.
     */
    public boolean isGeneratePublishedInterfaces(IIpsProject ipsProject) {
        if (this.ipsProject.equals(ipsProject)) {
            return isGeneratePublishedInterfaces(getConfig());
        } else {
            IIpsArtefactBuilderSetConfig configuration = ipsProject.getIpsArtefactBuilderSet().getConfig();
            return isGeneratePublishedInterfaces(configuration);
        }
    }

    private boolean isGeneratePublishedInterfaces(IIpsArtefactBuilderSetConfig config) {
        Boolean propertyValueAsBoolean = config
                .getPropertyValueAsBoolean(StandardBuilderSet.CONFIG_PROPERTY_PUBLISHED_INTERFACES);
        return propertyValueAsBoolean == null ? true : propertyValueAsBoolean.booleanValue();
    }

    public boolean isGenerateSerializablePolicyCmptSupport() {
        Boolean propertyValueAsBoolean = getConfig().getPropertyValueAsBoolean(
                StandardBuilderSet.CONFIG_PROPERTY_GENERATE_SERIALIZABLE_POLICY_CMPTS_SUPPORT);
        return propertyValueAsBoolean == null ? false : propertyValueAsBoolean;
    }

    public boolean isGenerateConvenienceGetters() {
        Boolean propertyValueAsBoolean = getConfig()
                .getPropertyValueAsBoolean(StandardBuilderSet.CONFIG_PROPERTY_GENERATE_CONVENIENCE_GETTERS);
        return propertyValueAsBoolean == null ? true : propertyValueAsBoolean;
    }

    public boolean isGeneratePolicyBuilder() {
        String propertyValue = getConfig()
                .getPropertyValueAsString(StandardBuilderSet.CONFIG_PROPERTY_BUILDER_GENERATOR);
        return (StandardBuilderSet.CONFIG_PROPERTY_BUILDER_GENERATOR_ALL.equals(propertyValue)
                || StandardBuilderSet.CONFIG_PROPERTY_BUILDER_GENERATOR_POLICY.equals(propertyValue));
    }

    public boolean isGenerateProductBuilder() {
        String propertyValue = getConfig()
                .getPropertyValueAsString(StandardBuilderSet.CONFIG_PROPERTY_BUILDER_GENERATOR);
        return (StandardBuilderSet.CONFIG_PROPERTY_BUILDER_GENERATOR_ALL.equals(propertyValue)
                || StandardBuilderSet.CONFIG_PROPERTY_BUILDER_GENERATOR_PRODUCT.equals(propertyValue));
    }

    public String getBaseClassPolicyCmptType() {
        String baseClass = getConfig()
                .getPropertyValueAsString(StandardBuilderSet.CONFIG_PROPERTY_BASE_CLASS_POLICY_CMPT_TYPE);
        return StringUtils.isBlank(baseClass)
                ? getConfig().getPropertyValueAsBoolean(StandardBuilderSet.CONFIG_PROPERTY_GENERATE_JAXB_SUPPORT)
                        ? AbstractJaxbModelObject.class.getName() : AbstractModelObject.class.getName()
                : baseClass;
    }

    public String getBaseClassProductCmptType() {
        String baseClass = getConfig()
                .getPropertyValueAsString(StandardBuilderSet.CONFIG_PROPERTY_BASE_CLASS_PRODUCT_CMPT_TYPE);
        return StringUtils.isBlank(baseClass) ? ProductComponent.class.getName() : baseClass;
    }

}
