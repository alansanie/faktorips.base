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

package org.faktorips.devtools.core.ui.workbenchadapters;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.faktorips.devtools.core.model.ipsobject.IIpsObjectPart;
import org.faktorips.devtools.core.model.testcase.ITestPolicyCmpt;
import org.faktorips.devtools.core.ui.IpsUIPlugin;
import org.faktorips.devtools.core.ui.OverlayIcons;

public class TestPolicyCmptWorkbenchAdapter extends IpsObjectPartWorkbenchAdapter {

    @Override
    protected ImageDescriptor getImageDescriptor(IIpsObjectPart ipsObjectPart) {
        if (ipsObjectPart instanceof ITestPolicyCmpt) {
            ITestPolicyCmpt testPC = (ITestPolicyCmpt)ipsObjectPart;
            String baseImageName = "PolicyCmptInstance.gif"; //$NON-NLS-1$
            if (testPC.isProductRelevant()) {
                return IpsUIPlugin.getImageHandling().getSharedOverlayImage(baseImageName, OverlayIcons.PRODUCT_OVR,
                        IDecoration.TOP_RIGHT);
            } else {
                return IpsUIPlugin.getImageHandling().getSharedImageDescriptor(baseImageName, true);
            }
        }
        return null;
    }

    @Override
    public ImageDescriptor getDefaultImageDescriptor() {
        return IpsUIPlugin.getImageHandling().getSharedImageDescriptor("PolicyCmptInstance.gif", true); //$NON-NLS-1$
    }

}
