package org.faktorips.devtools.stdbuilder.xtend.productcmpt.template

import org.faktorips.devtools.stdbuilder.AnnotatedJavaElementType
import org.faktorips.devtools.stdbuilder.xmodel.policycmpt.XPolicyAttribute


import static extension org.faktorips.devtools.stdbuilder.AnnotatedJavaElementType.*
import static extension org.faktorips.devtools.stdbuilder.xtend.template.ClassNames.*
import static extension org.faktorips.devtools.stdbuilder.xtend.template.CommonGeneratorExtensions.*
import static extension org.faktorips.devtools.stdbuilder.xtend.template.Constants.*
import static org.faktorips.devtools.stdbuilder.xtend.template.MethodNames.*

class DefaultAndAllowedValuesTmpl {

    def package static defaultAndAllowedValuesFields (XPolicyAttribute it) '''
        «IF generateGetAllowedValuesForAndGetDefaultValue»
            «defaultField»
            «allowedValueSetField»
        «ENDIF»
    '''

    def private static defaultField (XPolicyAttribute it) '''
        /**
         * «localizedJDoc("FIELD_DEFAULTVALUE", name)»
         * «getAnnotations(AnnotatedJavaElementType.ELEMENT_JAVA_DOC)»
         * @generated
         */
        private «javaClassName» «field(fieldNameDefaultValue)» = «defaultValueCode»;
    '''

    def private static allowedValueSetField (XPolicyAttribute it) '''
        /**
         * «localizedJDoc(getJavadocKey("FIELD"), name)»
         * «getAnnotations(AnnotatedJavaElementType.ELEMENT_JAVA_DOC)»
         * @generated
         */
        private «valueSetJavaClassName» «field(fieldNameValueSet)»;
    '''

    def package static getter (XPolicyAttribute it) '''
        «IF generateGetAllowedValuesForAndGetDefaultValue»
            «getterDefaultValue»
            «getterAllowedValues»
        «ENDIF»
    '''

    def private static getterDefaultValue (XPolicyAttribute it) '''
        /**
         * «inheritDocOrJavaDocIf(genInterface, "METHOD_GET_DEFAULTVALUE", name)»
         * «getAnnotations(ELEMENT_JAVA_DOC)»
         * @generated
         */
        «getAnnotationsForPublishedInterfaceModifierRelevant(PRODUCT_CMPT_DECL_CLASS_ATTRIBUTE_DEFAULT, genInterface)»
        «overrideAnnotationForPublishedMethodOrIf(!genInterface && published, overrideGetDefaultValue && overwrittenAttribute.productRelevantInHierarchy)»
        public «javaClassName» «method(methodNameGetDefaultValue)»
        «IF genInterface»;«ELSE»
        {
            return «fieldNameDefaultValue»;
        }
        «ENDIF»
    '''

    def private static getterAllowedValues (XPolicyAttribute it) '''
        /**
         * «inheritDocOrJavaDocIf(genInterface, getJavadocKey("METHOD_GET"), name)»
         * «getAnnotations(ELEMENT_JAVA_DOC)»
         * @generated
         */
        «getAnnotationsForPublishedInterfaceModifierRelevant(PRODUCT_CMPT_DECL_CLASS_ATTRIBUTE_ALLOWED_VALUES, genInterface)»
        «overrideAnnotationForPublishedMethodOrIf(!genInterface() && published, overrideGetAllowedValuesFor && overwrittenAttribute.productRelevantInHierarchy)»
        public «valueSetJavaClassName» «method(methodNameGetAllowedValuesFor, IValidationContext, "context")»
        «IF genInterface»;«ELSE»
        {
            return «fieldNameValueSet»;
        }
        «ENDIF»
    '''

    def package static initFromXmlMethodCall (XPolicyAttribute it) '''
        «IF generateGetAllowedValuesForAndGetDefaultValue»
            «methodNameDoInitFromXml»(configMap);
        «ENDIF»
    '''

    def package static initFromXmlMethod (XPolicyAttribute it) '''
        «IF generateGetAllowedValuesForAndGetDefaultValue»
            /**
             * @generated
             */
             private void «method(methodNameDoInitFromXml, Map("String", Element), "configMap")» {
             Element defaultValueElement = configMap.get(«CONFIGURED_DEFAULT_PREFIX»+«policyCmptNode.implClassName».«constantNamePropertyName»);
            if (defaultValueElement != null) {
                String value = «ValueToXmlHelper».«getValueFromElement("defaultValueElement")»;
                «fieldNameDefaultValue» = «getNewInstanceFromExpression("value", "getRepository()")»;
            }
            Element valueSetElement = configMap.get(«CONFIGURED_VALUE_SET_PREFIX»+«policyCmptNode.implClassName».«constantNamePropertyName»);
            if (valueSetElement != null) {
                «IF valueSetUnrestricted»
                    «IF ipsEnum»
                           «UnrestrictedValueSet("?")» unrestrictedValueSet = «ValueToXmlHelper()».«getUnrestrictedValueSet("valueSetElement", XML_TAG_VALUE_SET())»;
                           «fieldNameValueSet» = «newEnumValueSetInstance(getAllEnumValuesCode("getRepository()"), "unrestrictedValueSet.containsNull()")»;
                    «ELSE»
                        «fieldNameValueSet» = «ValueToXmlHelper()».«getUnrestrictedValueSet("valueSetElement", XML_TAG_VALUE_SET)»;
                    «ENDIF»
                «ENDIF»
                «IF valueSetEnum || (valueSetUnrestricted && enumValueSetSupported)»
                    «EnumValues» values = «ValueToXmlHelper».«getEnumValueSetFromElement("valueSetElement", XML_TAG_VALUE_SET)»;
                    if (values != null) {
                        «ArrayList(javaClassUsedForValueSet)» enumValues = new «ArrayList(javaClassUsedForValueSet)»();
                        for (int i = 0; i < values.«getNumberOfValues()»; i++) {
                            enumValues.add(«getValueSetNewInstanceFromExpression("values.getValue(i)", "getRepository()")»);
                        }
                        «fieldNameValueSet» = «newEnumValueSetInstance("enumValues", "values
                                .containsNull()")»;
                    }
                «ENDIF»
                «IF valueSetRange || (valueSetUnrestricted && rangeSupported)»
                    «Range» range = «ValueToXmlHelper».«getRangeFromElement("valueSetElement", XML_TAG_VALUE_SET)»;
                    if (range != null) {
                        «fieldNameValueSet» = «getNewRangeExpression("range.getLower()", "range
                                .getUpper()", "range.getStep()", "range.containsNull()")»;
                    }
                «ENDIF»
            }
        }
         «ENDIF»
    '''

    def package static writeAttributeToXmlMethodCall (XPolicyAttribute it) '''
        «IF generateGetAllowedValuesForAndGetDefaultValue»
            «methodNameWriteToXml»(element);
        «ENDIF»
    '''

    def package static writeAttributeToXmlMethod (XPolicyAttribute it) '''
        «IF generateGetAllowedValuesForAndGetDefaultValue»
            /**
             * @generated
             */
            private void «method(methodNameWriteToXml, Element, "element")» {
                Element defaultValueElement = «ValueToXmlHelper».«addValueAndReturnElement(toStringExpression, "element", XML_TAG_CONFIGURED_DEFAULT)»;
                defaultValueElement.setAttribute(«XML_ATTRIBUTE_ATTRIBUTE», «policyCmptNode.implClassName».«constantNamePropertyName»);

                Element configuredValueSetElement = element.getOwnerDocument().createElement(«XML_TAG_CONFIGURED_VALUE_SET»);
                Element valueSetElement = element.getOwnerDocument().createElement(«XML_TAG_VALUE_SET»);
                «IF valueSetUnrestricted»
                    if («fieldNameValueSet» instanceof «UnrestrictedValueSet("?")») {
                        Element valueElement = element.getOwnerDocument().createElement(«XML_TAG_ALL_VALUES»);
                        valueElement.setAttribute(«XML_ATTRIBUTE_CONTAINS_NULL», Boolean.toString(«fieldNameValueSet».«containsNull»));
                        valueSetElement.appendChild(valueElement);
                    }
                «ENDIF»
                «IF valueSetUnrestricted && rangeSupported»
                    if («fieldNameValueSet» instanceof «qnameRange("?")») {
                        «qnameRange(javaClassQualifiedNameUsedForValueSet)» range = («qnameRange(javaClassQualifiedNameUsedForValueSet)»)«fieldNameValueSet»;
                        «writeRange("range", it)»
                    }
                «ELSEIF valueSetRange»
                        «writeRange(fieldNameValueSet, it)»
                «ENDIF»
                «IF valueSetUnrestricted && enumValueSetSupported»
                    if («fieldNameValueSet» instanceof «OrderedValueSet("?")») {
                        «writeEnumValueSet»
                    }
                «ELSEIF valueSetEnum»
                           «writeEnumValueSet»
                «ENDIF»
                configuredValueSetElement.setAttribute(«XML_ATTRIBUTE_ATTRIBUTE», «policyCmptNode.implClassName».«constantNamePropertyName»);
                configuredValueSetElement.appendChild(valueSetElement);
                element.appendChild(configuredValueSetElement);
            }
        «ENDIF»
    '''


    def private static writeRange(String rangeVar, XPolicyAttribute it) '''
        Element valueSetValuesElement = element.getOwnerDocument().createElement(«XML_TAG_RANGE»);
        valueSetValuesElement.setAttribute(«XML_ATTRIBUTE_CONTAINS_NULL», Boolean.toString(«fieldNameValueSet».«containsNull»));
        «ValueToXmlHelper».«addValueToElement(getToStringExpression(rangeVar + ".getLowerBound()"), "valueSetValuesElement", XML_TAG_LOWER_BOUND)»;
        «ValueToXmlHelper».«addValueToElement(getToStringExpression(rangeVar + ".getUpperBound()"), "valueSetValuesElement", XML_TAG_UPPER_BOUND)»;
        «ValueToXmlHelper».«addValueToElement(getToStringExpression(rangeVar + ".getStep()"), "valueSetValuesElement", XML_TAG_STEP)»;
        valueSetElement.appendChild(valueSetValuesElement);
    '''

    def private static writeEnumValueSet (XPolicyAttribute it) '''
        Element valueSetValuesElement = element.getOwnerDocument().createElement(«XML_TAG_ENUM»);
        for («javaClassQualifiedName» value : «fieldNameValueSet».getValues(false)) {
            Element valueElement = element.getOwnerDocument().createElement(«XML_TAG_VALUE»);
            «ValueToXmlHelper».«addValueToElement(getToStringExpression("value"),"valueElement", XML_TAG_DATA)»;
            valueSetValuesElement.appendChild(valueElement);
        }
        valueSetElement.appendChild(valueSetValuesElement);
    '''

}