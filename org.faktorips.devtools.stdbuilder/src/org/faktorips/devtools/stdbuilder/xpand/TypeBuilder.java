/*******************************************************************************
 * Copyright (c) Faktor Zehn AG. <http://www.faktorzehn.org>
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version
 * 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and
 * restrictions as well as the possibility of alternative license terms.
 *******************************************************************************/

package org.faktorips.devtools.stdbuilder.xpand;

import java.util.Map;

import org.eclipse.xtend.expression.Variable;
import org.faktorips.devtools.stdbuilder.StandardBuilderSet;
import org.faktorips.devtools.stdbuilder.xpand.model.ModelService;
import org.faktorips.devtools.stdbuilder.xpand.model.XType;
import org.faktorips.util.LocalizedStringsSet;

/**
 * Common abstraction of {@link XpandBuilder} to generate classed of type {@link XType}.
 * 
 * @deprecated Since 3.22 we now use Xtend to generate templates for code generation. Use
 *             {@link XtendTypeBuilder} instead and refactor your templates for Xtend.
 * 
 */
@Deprecated
public abstract class TypeBuilder<T extends XType> extends XpandBuilder<T> {

    private static final String GENERATE_INTERFACE = "generateInterface";
    private final boolean interfaceBuilder;

    public TypeBuilder(boolean interfaceBuilder, StandardBuilderSet builderSet, GeneratorModelContext modelContext,
            ModelService modelService, LocalizedStringsSet localizedStringsSet) {
        super(builderSet, modelContext, modelService, localizedStringsSet);
        this.interfaceBuilder = interfaceBuilder;
    }

    public boolean isInterfaceBuilder() {
        return interfaceBuilder;
    }

    @Override
    public boolean isBuildingPublishedSourceFile() {
        return isInterfaceBuilder() || !getGeneratorModelContext().isGeneratePublishedInterfaces(getIpsProject());
    }

    @Override
    protected Map<String, Variable> getGlobalVars() {
        Map<String, Variable> map = super.getGlobalVars();
        Variable generateInterface = new Variable(GENERATE_INTERFACE, generatesInterface());
        map.put(generateInterface.getName(), generateInterface);
        return map;
    }

    @Override
    protected boolean generatesInterface() {
        return isInterfaceBuilder();
    }

}