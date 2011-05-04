/*******************************************************************************
 * Copyright (c) 2005-2011 Faktor Zehn AG und andere.
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

package org.faktorips.runtime.testrepository.test;

import org.faktorips.runtime.internal.XmlUtil;
import org.faktorips.runtime.test.IpsTestCase2;
import org.faktorips.runtime.test.IpsTestResult;
import org.w3c.dom.Element;

/**
 * 
 * @author Jan Ortmann
 */
public class TestPremiumCalculation extends IpsTestCase2 {

    private String inputSumInsured;
    private String expResultPremium;

    public TestPremiumCalculation(String qName) {
        super(qName);
    }

    @Override
    protected void initInputFromXml(Element inputEl) {
        Element el = XmlUtil.getFirstElement(inputEl);
        inputSumInsured = el.getAttribute("value");
    }

    @Override
    protected void initExpectedResultFromXml(Element resultEl) {
        Element el = XmlUtil.getFirstElement(resultEl);
        expResultPremium = el.getAttribute("value");
    }

    public String getExpResultPremium() {
        return expResultPremium;
    }

    public String getInputSumInsured() {
        return inputSumInsured;
    }

    @Override
    public void executeBusinessLogic() throws Exception {
        // do nothing
    }

    @Override
    public void executeAsserts(IpsTestResult result) throws Exception {
        // do nothing
    }

    public void testDummy() {
        // do nothing
    }

}