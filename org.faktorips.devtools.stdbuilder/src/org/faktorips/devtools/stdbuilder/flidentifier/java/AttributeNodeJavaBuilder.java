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

package org.faktorips.devtools.stdbuilder.flidentifier.java;

import org.eclipse.osgi.util.NLS;
import org.faktorips.codegen.CodeFragment;
import org.faktorips.codegen.JavaCodeFragment;
import org.faktorips.datatype.Datatype;
import org.faktorips.devtools.core.builder.Messages;
import org.faktorips.devtools.core.builder.flidentifier.IdentifierNodeBuilderFactory;
import org.faktorips.devtools.core.builder.flidentifier.ast.AttributeNode;
import org.faktorips.devtools.core.builder.flidentifier.ast.IdentifierNode;
import org.faktorips.devtools.core.builder.flidentifier.ast.InvalidIdentifierNode;
import org.faktorips.devtools.core.model.pctype.IPolicyCmptType;
import org.faktorips.devtools.core.model.pctype.IPolicyCmptTypeAttribute;
import org.faktorips.devtools.core.model.productcmpttype.IProductCmptTypeAttribute;
import org.faktorips.devtools.core.model.type.IAttribute;
import org.faktorips.devtools.stdbuilder.StandardBuilderSet;
import org.faktorips.devtools.stdbuilder.flidentifier.AbstractIdentifierJavaBuilder;
import org.faktorips.devtools.stdbuilder.xpand.policycmpt.model.XPolicyAttribute;
import org.faktorips.devtools.stdbuilder.xpand.policycmpt.model.XPolicyCmptClass;
import org.faktorips.devtools.stdbuilder.xpand.productcmpt.model.XProductAttribute;
import org.faktorips.devtools.stdbuilder.xpand.productcmpt.model.XProductCmptClass;
import org.faktorips.fl.CompilationResult;
import org.faktorips.fl.CompilationResultImpl;
import org.faktorips.fl.ExprCompiler;
import org.faktorips.util.message.Message;

public class AttributeNodeJavaBuilder extends AbstractIdentifierJavaBuilder<JavaCodeFragment> {

    public AttributeNodeJavaBuilder(IdentifierNodeBuilderFactory<JavaCodeFragment> nodeBuilderFactory,
            StandardBuilderSet builderSet) {
        super(nodeBuilderFactory, builderSet);
    }

    @Override
    protected CompilationResult<JavaCodeFragment> getCompilationResult(IdentifierNode identifierNode,
            CompilationResult<CodeFragment> contextCompilationResult) {
        AttributeNode node = (AttributeNode)identifierNode;
        IAttribute attribute = node.getAttribute();
        Datatype attrDatatype = node.getDatatype();
        Datatype contextDatatype = contextCompilationResult.getDatatype();

        if (isIdentifierAllowed(attribute)) {
            String text = NLS.bind(Messages.AbstractParameterIdentifierResolver_msgIdentifierNotAllowed,
                    attribute.getName());
            return new CompilationResultImpl(Message.newError(ExprCompiler.UNDEFINED_IDENTIFIER, text));
        }
        JavaCodeFragment javaCodeFragment = new JavaCodeFragment();
        String parameterAttributGetterName = getAttributeGetterName(attribute, attrDatatype, contextDatatype);
        javaCodeFragment.append('.' + parameterAttributGetterName + "()"); //$NON-NLS-1$
        return new CompilationResultImpl(javaCodeFragment, attrDatatype);
    }

    @Override
    protected CompilationResult<JavaCodeFragment> getErrorCompilationResult(InvalidIdentifierNode invalidIdentifierNode) {
        return new CompilationResultImpl(invalidIdentifierNode.getMessage());
    }

    protected String getAttributeGetterName(IAttribute attribute, Datatype attrDatatype, Datatype contextDatatype) {
        boolean isDefaultValueAccess = isDefaultValueAccess(contextDatatype, attribute.getName());
        String parameterAttributGetterName = isDefaultValueAccess ? getParameterAttributDefaultValueGetterName(attribute)
                : getParameterAttributGetterName(attribute);
        return parameterAttributGetterName;
    }

    private boolean isDefaultValueAccess(Datatype datatype, String attributeName) {
        return datatype instanceof IPolicyCmptType && attributeName.endsWith(DEFAULT_VALUE_SUFFIX);
    }

    private String getParameterAttributGetterName(IAttribute attribute) {
        if (attribute instanceof IPolicyCmptTypeAttribute) {
            XPolicyAttribute xPolicyAttribute = getModelNode(attribute, XPolicyAttribute.class);
            return xPolicyAttribute.getMethodNameGetter();
        }
        if (attribute instanceof IProductCmptTypeAttribute) {
            XProductAttribute xProductAttribute = getModelNode(attribute, XProductAttribute.class);
            if (xProductAttribute.isChangingOverTime()) {
                return xProductAttribute.getMethodNameGetter();
            } else {
                XProductCmptClass xProductCmptClass = getModelNode(attribute.getType(), XProductCmptClass.class);
                return xProductCmptClass.getMethodNameGetProductCmpt() + "()."
                        + xProductAttribute.getMethodNameGetter();
            }
        }
        return null;
    }

    private String getParameterAttributDefaultValueGetterName(IAttribute attribute) {
        XPolicyCmptClass xPolicyCmptClass = getModelNode(attribute.getType(), XPolicyCmptClass.class);
        XPolicyAttribute xPolicyAttribute = getModelNode(attribute, XPolicyAttribute.class);
        return xPolicyCmptClass.getMethodNameGetProductCmptGeneration() + "()."
                + xPolicyAttribute.getMethodNameGetDefaultValue();
    }

}
