package org.faktorips.devtools.core.model.product;

import org.eclipse.core.runtime.CoreException;
import org.faktorips.devtools.core.model.IIpsObjectPart;
import org.faktorips.devtools.core.model.ValueSet;
import org.faktorips.devtools.core.model.pctype.IAttribute;
import org.faktorips.fl.ExprCompiler;
import org.faktorips.util.message.MessageList;


/**
 * A configuration element is based on an product component type's attribute.
 * <p>
 * For example a policy component could have a constant attribute interestRate.
 * All product components based on that policy component have a matching
 * product attribute that stores the concrete interest rate value.  
 */
public interface IConfigElement extends IIpsObjectPart {
    
	public final static String PROPERTY_TYPE = "type";
    public final static String PROPERTY_PCTYPE_ATTRIBUTE = "pcTypeAttribute";
    public final static String PROPERTY_VALUE = "value";
    
    /**
     * Prefix for all message codes of this class.
     */
    public final static String MSGCODE_PREFIX = "CONFIGELEMENT-";

    /**
     * Validation message code to indicate that the attribute the config element is based can't be found.
     */
    public final static String MSGCODE_UNKNWON_ATTRIBUTE = MSGCODE_PREFIX + "UnknownAttribute";

    /**
     * Validation message code to indicate that the attribute's datatype can't be found and so the 
     * formula's datatype can't be checked against it.
     */
    public final static String MSGCODE_UNKNOWN_DATATYPE_FORMULA = MSGCODE_PREFIX + "UnknownDatatypeFormula";
    
    /**
     * Validation message code to indicate that the attribute's datatype can't be found and so the 
     * value can't be parsed.
     */
    public final static String MSGCODE_UNKNOWN_DATATYPE_VALUE = MSGCODE_PREFIX + "UnknownDatatypeValue";

    /**
     * Validation message code to indicate that the formula's datatype is not compatible with the 
     * one defined by the attribute.
     */
    public final static String MSGCODE_WRONG_FORMULA_DATATYPE = MSGCODE_PREFIX + "WrongFormulaDatatype";
    
    /**
     * Validation message code to indicate that the datatype is not a value datatype.
     */
    public final static String MSGCODE_NOT_A_VALUEDATATYPE = MSGCODE_PREFIX + "NotAValudDatatype";

    /**
     * Validation message code to indicate that the datatype is invalid. (E.g. the definition
     * of a dynamic datatype can be wrong.)
     */
    public final static String MSGCODE_INVALID_DATATYPE = MSGCODE_PREFIX + "InvalidDatatype";
    // TODO test case - untestable at the moment (20.02.2006) because no datatype implements validate()

    /**
     * Validation message code to indicate that the value can't be parsed, it is not an instance
     * of the datatype
     */
    public final static String MSGCODE_VALUE_NOT_PARSABLE = MSGCODE_PREFIX + "ValueNotParsable";

    /**
     * Validation message code to indicate that the value is not contained in the valueset.
     */
    public final static String MSGCODE_VALUE_NOT_IN_VALUESET = MSGCODE_PREFIX + "ValueNotInValueSet";
    
    /**
     * Validation message code to indicate that formula is missing.
     */
    public final static String MSGCODE_MISSING_FORMULA = MSGCODE_PREFIX + "MissingFormula";

    /**
     * Returns the product component generation this config element belongs to.
     */
    public IProductCmpt getProductCmpt();
    
    /**
     * Returns the product component generation this config element belongs to.
     */
    public IProductCmptGeneration getProductCmptGeneration();
    
    /**
     * Returns this element's type.
     */
    public ConfigElementType getType();
    
    /**
     * Sets this element's type.
     */
    public void setType(ConfigElementType newType);
    
    /**
     * Returns the name of the product component type's attribute
     * this element is based on.
     */
    public String getPcTypeAttribute();
    
    /**
     * Sets the name of the product component type's attribute
     * this attribute is based on.
     * 
     * @throws NullPointerException if name is <code>null</code>.
     */
    public void setPcTypeAttribute(String name);
    
    /**
     * Returns the attribute's value. 
     */
    public String getValue();
    
    /**
     * Sets the attribute's value. 
     */
    public void setValue(String newValue);
    
    /**
     * Returns the set of allowed values.
     */
    public ValueSet getValueSet();

    /**
     * Sets the set of allowed values.
     */
    public void setValueSet(ValueSet set);

    /**
     * Returns an expression compiler that can be used to compile the formula.
     * or <code>null</code> if the element does not contain a formula.
     */
    public ExprCompiler getExprCompiler() throws CoreException;
    
    /**
     * Finds the corresponding attribute in the product component type this
     * product component is an instance of.
     * 
     * @return the corresponding attribute or <code>null</code> if no such
     * attribute exists.
     * 
     * @throws CoreException if an exception occurs while searching for the attribute. 
     */
    public IAttribute findPcTypeAttribute() throws CoreException;

   /**
    *  
    * @throws CoreException if an exception occurs while validating the object.
    */
   public MessageList validate() throws CoreException;
    
    
}
