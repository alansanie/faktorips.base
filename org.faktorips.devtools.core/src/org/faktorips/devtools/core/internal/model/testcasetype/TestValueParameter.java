/*******************************************************************************
 * Copyright (c) 2005,2006 Faktor Zehn GmbH und andere.
 *
 * Alle Rechte vorbehalten.
 *
 * Dieses Programm und alle mitgelieferten Sachen (Dokumentationen, Beispiele,
 * Konfigurationen, etc.) dürfen nur unter den Bedingungen der 
 * Faktor-Zehn-Community Lizenzvereinbarung – Version 0.1 (vor Gründung Community) 
 * genutzt werden, die Bestandteil der Auslieferung ist und auch unter
 *   http://www.faktorips.org/legal/cl-v01.html
 * eingesehen werden kann.
 *
 * Mitwirkende:
 *   Faktor Zehn GmbH - initial API and implementation 
 *
 *******************************************************************************/

package org.faktorips.devtools.core.internal.model.testcasetype;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.faktorips.datatype.ValueDatatype;
import org.faktorips.devtools.core.IpsPlugin;
import org.faktorips.devtools.core.model.IIpsElement;
import org.faktorips.devtools.core.model.ipsobject.IIpsObject;
import org.faktorips.devtools.core.model.ipsobject.IIpsObjectPart;
import org.faktorips.devtools.core.model.ipsproject.IIpsProject;
import org.faktorips.devtools.core.model.testcasetype.ITestParameter;
import org.faktorips.devtools.core.model.testcasetype.ITestValueParameter;
import org.faktorips.devtools.core.model.testcasetype.TestParameterType;
import org.faktorips.util.ArgumentCheck;
import org.faktorips.util.message.Message;
import org.faktorips.util.message.MessageList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Test value parameter class. Defines a test value for a specific test case type.
 * @author Joerg Ortmann
 */
public class TestValueParameter extends TestParameter implements
		ITestValueParameter {

	final static String TAG_NAME = "ValueParameter"; //$NON-NLS-1$
	
	private String datatype = ""; //$NON-NLS-1$
	
	/**
	 * @param parent
	 * @param id
	 */
	public TestValueParameter(IIpsObject parent, int id) {
		super(parent, id);
	}

	/**
	 * @param parent
	 * @param id
	 */
	public TestValueParameter(IIpsObjectPart parent, int id) {
		super(parent, id);
	}

    /**
     * {@inheritDoc}
     */
    public String getDatatype() {
        return getValueDatatype();
    }

    /**
     * {@inheritDoc}
     */
    public void setDatatype(String datatype) {
        setValueDatatype(datatype);
    }
    
    /**
     * {@inheritDoc}
     */
    public void setTestParameterType(TestParameterType testParameterType) {
        // a test value parameter supports only input type or expected result type
        ArgumentCheck.isTrue(testParameterType.equals(TestParameterType.INPUT)
                || testParameterType.equals(TestParameterType.EXPECTED_RESULT));
        TestParameterType oldType = this.type;
        this.type = testParameterType;
        valueChanged(oldType, testParameterType);
    }
    
	/**
	 * {@inheritDoc}
	 */
	public String getValueDatatype() {
		return datatype;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setValueDatatype(String datatypeId) {
        String oldDatatype = this.datatype;
		datatype = datatypeId;
        valueChanged(oldDatatype, datatypeId);
	}

	/**
	 * {@inheritDoc}
	 */
	public ValueDatatype findValueDatatype(IIpsProject ipsProject) throws CoreException {
		return ipsProject.findValueDatatype(datatype);
	}

    /**
     * {@inheritDoc}
     */
    protected Element createElement(Document doc) {
        return doc.createElement(TAG_NAME);
    }

    /**
     * {@inheritDoc}
     */
	protected void initPropertiesFromXml(Element element, Integer id) {
		super.initPropertiesFromXml(element, id);
		datatype = element.getAttribute(PROPERTY_VALUEDATATYPE);
	}

    /**
     * {@inheritDoc}
     */
	protected void propertiesToXml(Element element) {
		super.propertiesToXml(element);
		element.setAttribute(PROPERTY_VALUEDATATYPE, datatype);
	}

	/**
     * {@inheritDoc}
	 */
    public Image getImage() {
        return IpsPlugin.getDefault().getImage("Datatype.gif"); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    public ITestParameter getRootParameter() {
        // no childs are supported, the test value parameter is always a root element
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isRoot() {
        // no childs are supported, the test value parameter is always a root element        
        return true;
    }

    /**
     * {@inheritDoc}
     */
    protected void validateThis(MessageList list, IIpsProject ipsProject) throws CoreException {
        super.validateThis(list, ipsProject);
        ValueDatatype datatype = findValueDatatype(ipsProject);
        if (datatype==null) {
            String text = NLS.bind(Messages.TestValueParameter_ValidateError_ValueDatatypeNotFound, getDatatype());
            Message msg = new Message(MSGCODE_VALUEDATATYPE_NOT_FOUND, text, Message.ERROR, this, PROPERTY_VALUEDATATYPE);
            list.add(msg);
        }
        
        // check the correct type
        if (isCombinedParameter() || (! isInputOrCombinedParameter()&& ! isExpextedResultOrCombinedParameter())){
            String text = NLS.bind(Messages.TestValueParameter_ValidationError_TypeNotAllowed, type.getName(), name);
            Message msg = new Message(MSGCODE_WRONG_TYPE, text, Message.ERROR, this, PROPERTY_TEST_PARAMETER_TYPE);
            list.add(msg);
        }
    }

    /**
     * {@inheritDoc}
     */
    public IIpsElement[] getChildren() {
        return new IIpsElement[0];
    }

    /**
     * {@inheritDoc}
     */
    protected void reinitPartCollections() {
    }

    /**
     * {@inheritDoc}
     */
    protected void reAddPart(IIpsObjectPart part) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    protected void removePart(IIpsObjectPart part) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    protected IIpsObjectPart newPart(Element xmlTag, int id) {
        return null;
    }
}
