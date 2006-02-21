package org.faktorips.devtools.core.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.osgi.util.NLS;
import org.faktorips.datatype.ValueDatatype;
import org.faktorips.util.message.Message;
import org.faktorips.util.message.MessageList;
import org.faktorips.values.EnumType;
import org.faktorips.values.EnumValue;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * EnumValueEnumSet realizes a Valueset of Strings dupplicate values are not prohibeted, but in the responsibility of te
 * user of the class.
 * 
 * @author Andy R�sch
 */
public class EnumValueSet extends ValueSet {

    /**
     * Prefix for all message codes of this class.
     */
    public final static String MSGCODE_PREFIX = "ENUMVALUESET-";

    /**
     * Validation message code to indicate that a value in this value set is duplicate.
     */
    public final static String MSGCODE_DUPLICATE_VALUE = MSGCODE_PREFIX + "DuplicateValue";

    
    public static final String XML_TAG = "Enum";
    
    private static final String XML_VALUE = "Value";
    private static final String XML_ATTRIBUTEVALUE = "value";

    private ArrayList elements = new ArrayList();

    /**
     * Creates an EnumValueSet with date of the given xml element.
     * 
     * @param element The enum xml element. 
     */
    static EnumValueSet createEnumFromXml(Element element) {
        NodeList nl = element.getElementsByTagName(XML_VALUE);
        EnumValueSet enumSet = new EnumValueSet();
        for (int i = 0; i < nl.getLength(); i++) {
            if (nl.item(i) instanceof Element) {
                Element valueElement = (Element)nl.item(i);
                String value = valueElement.getAttribute(XML_ATTRIBUTEVALUE);
                enumSet.addValue(value);
            }
        }
        return enumSet;
    }

    /**
     * Creates a new enum value set with all values from the given enum type.
     * 
     * @throws NullPointerException if enumType is <code>null</code>.
     */
    public EnumValueSet(EnumType enumType) {
        EnumValue[] all = enumType.getValues();
        for (int i = 0; i < all.length; i++) {
            addValue(all[i].getId());
        }
    }

    /**
     * Copy constructor.
     * 
     * @throws NullPointerException if enumType is <code>null</code>.
     */
    public EnumValueSet(EnumValueSet valueSet) {
        this();
        for (Iterator it=valueSet.elements.iterator(); it.hasNext();) {
            addValue((String)it.next());
        }
    }

    public EnumValueSet() {
    }
    
    /**
     * Overridden.
     */
    public ValueSet copy() {
        return new EnumValueSet(this);
    }

    /**
     * Returns an array of all Elements in the EnunValueSet
     */
    public String[] getElements() {
        return (String[])elements.toArray(new String[elements.size()]);
    }

    /**
     * Sets the elements to the given elements of the String[]
     */
    public void setElements(String[] elements) {
        this.elements.clear();
        for (int i = 0; i < elements.length; i++) {
            this.elements.add(elements[i]);
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean containsValue(String value, ValueDatatype datatype) {
    	return containsValue(value, datatype, null, null, null);
    }

    /**
     * {@inheritDoc}
     */
    public boolean containsValue(String value, ValueDatatype datatype, MessageList list, Object invalidObject, String invalidProperty) {
        if (!datatype.isParsable(value)) {
        	if (list != null) {
        		String msg = "Value " + value + " is not parsable by datatype " + datatype.getName();
        		addMsg(list, MSGCODE_VALUE_NOT_PARSABLE, msg, invalidObject, invalidProperty);
        	}
            return false;
        }
        
        Object val = datatype.getValue(value);
        for (Iterator it=elements.iterator(); it.hasNext(); ) {
            String each = (String)it.next();
            if (datatype.isParsable(each)) {
                Object eachVal = datatype.getValue(each);
                if (eachVal.equals(val)) {
                    return true;
                }
            }
        }
        if (list != null) {
        	String text = "The value is not included in the enumeration.";
        	addMsg(list, MSGCODE_VALUE_NOT_CONTAINED, text, invalidObject, invalidProperty);
        }
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean containsValueSet(ValueSet subset, ValueDatatype datatype, MessageList list, Object invalidObject, String invalidProperty) {
    	if (!(subset instanceof EnumValueSet)) {
    		if (list != null) {
    			addMsg(list, MSGCODE_TYPE_OF_VALUESET_NOT_MATCHING, "The subset is not an enum value set", invalidObject, invalidProperty);
    		}
    		return false;
    	}
    	
    	EnumValueSet enumSubset = (EnumValueSet)subset;
    	String[] subsetValues = enumSubset.getElements();
    	
    	boolean contains = true;
    	for (int i = 0; i < subsetValues.length && contains; i++) {
			contains = this.containsValue(subsetValues[i], datatype, list, invalidObject, invalidProperty);
		}
		return contains;
	}

    /**
     * {@inheritDoc}
     */
	public boolean containsValueSet(ValueSet subset, ValueDatatype datatype) {
		return containsValueSet(subset, datatype, null, null, null);
	}

	/**
     * Adds the value to the set.
     */
    public void addValue(String val) {
        elements.add(val);
    }

    /**
     * Removes the value at the given index from the value set.
     */
    public void removeValue(int index) {
        elements.remove(index);
    }

    /**
     * Retrieves the value at the given index.
     */
    public String getValue(int index) {
        return (String)elements.get(index);
    }

    /**
     * Sets the value at the given index.
     */
    public void setValue(int index, String value) {
        elements.set(index, value);
    }
    
    /**
     * Returns the number of values in the set.
     */
    public int getNumOfValues() {
        return elements.size();
    }

    /**
     * Creates the xml element for the enum value set.
     * 
     * Overridden IMethod.
     *
     * @see org.faktorips.devtools.core.model.ValueSet#createSubclassElement(org.w3c.dom.Document)
     */
    protected Element createSubclassElement(Document doc) {
        Element tagElement = doc.createElement(XML_TAG);
        for (Iterator iter = elements.iterator(); iter.hasNext();) {
            String element = (String)iter.next();
            Element valueElement = doc.createElement(XML_VALUE);
            valueElement.setAttribute(XML_ATTRIBUTEVALUE, element);
            tagElement.appendChild(valueElement);
        }
        return tagElement;
    }

    /**
     * Overridden IMethod.
     * @see org.faktorips.devtools.core.model.ValueSet#getValueSetType()
     */
    public ValueSetType getValueSetType() {
        return ValueSetType.ENUM;
    }

    /**
     * Overridden IMethod.
     * @see org.faktorips.devtools.core.model.ValueSet#validate(org.faktorips.datatype.ValueDatatype,
     *      org.faktorips.devtools.core.model.ValueSet, org.faktorips.util.message.MessageList)
     */
    public String[] getValuesNotContained(EnumValueSet otherSet) {
        List result = new ArrayList();
        for (int i = 0; i < otherSet.getNumOfValues(); i++) {
            if (!elements.contains(otherSet.getValue(i))) {
                result.add(otherSet.getValue(i));
            }
        }
        return (String[])result.toArray(new String[result.size()]);
    }

    /**
     * Overridden.
     */
    public void validate(ValueDatatype datatype, MessageList list) {
        int numOfValues = elements.size();
        for (int i = 0; i < numOfValues; i++) {
            String value = (String)elements.get(i);
            if (!datatype.isParsable(value)) {
                String msg = NLS.bind("The value {0} is not a {1}.", value, datatype.getName());
                list.add(new Message(MSGCODE_VALUE_NOT_PARSABLE, msg, Message.ERROR, value));
            }
        }
        for (int i = 0; i < numOfValues - 1; i++) {
            String valueOfi = (String)elements.get(i);
            for (int j = i + 1; j < numOfValues; j++) {
                String valueOfj = (String)elements.get(j);
                if (valueOfi.equals(valueOfj)) {
                    String msg = NLS.bind("The value {0} is more than once in the value set.", valueOfi);
                    list.add(new Message(MSGCODE_DUPLICATE_VALUE, msg, Message.ERROR, valueOfi));
                    list.add(new Message(MSGCODE_DUPLICATE_VALUE, msg, Message.ERROR, valueOfj));
                }
            }
        }
    }

    public String toString() {
        return elements.toString();
    }

}
