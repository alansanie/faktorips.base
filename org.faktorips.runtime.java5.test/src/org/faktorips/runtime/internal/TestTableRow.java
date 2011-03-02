/*******************************************************************************
 * Copyright (c) 2005-2010 Faktor Zehn AG und andere.
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

package org.faktorips.runtime.internal;

import org.faktorips.values.Decimal;

public class TestTableRow {

    private final String company;

    private final Integer gender;

    private final Decimal rate;

    public TestTableRow(String company, Integer gender, Decimal rate) {
        this.company = company;
        this.gender = gender;
        this.rate = rate;
    }

    public String getCompany() {
        return company;
    }

    public Integer getGender() {
        return gender;
    }

    public Decimal getRate() {
        return rate;
    }

}
