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

package org.faktorips.devtools.core.ui.controls;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.faktorips.devtools.core.ui.IDataChangeableReadWriteAccess;
import org.faktorips.devtools.core.ui.UIToolkit;

public class InfoLabel extends Composite implements IDataChangeableReadWriteAccess {

    private Label hintImage;
    private Text hintText;

    public InfoLabel(Composite parent) {
        super(parent, SWT.NONE);
        createControls();
    }

    private void createControls() {
        UIToolkit uiToolkit = new UIToolkit(null);

        GridLayout layout = new GridLayout(2, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.horizontalSpacing = 10;
        setLayout(layout);
        setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        hintImage = uiToolkit.createLabel(this, StringUtils.EMPTY);

        GridData imageGridData = new GridData(SWT.LEFT, SWT.TOP, false, false);
        imageGridData.widthHint = 16;
        hintImage.setLayoutData(imageGridData);

        hintText = uiToolkit.createText(this, SWT.WRAP | SWT.MULTI | SWT.READ_ONLY);
        GridData textGridData = new GridData(SWT.FILL, SWT.FILL, true, false);
        textGridData.widthHint = 100;
        textGridData.heightHint = 100;
        hintText.setLayoutData(textGridData);
    }

    public void setInfoText(String text) {
        if (StringUtils.isNotEmpty(text)) {
            hintImage.setImage(JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_INFO));
            hintText.setText(text);
        } else {
            hintImage.setImage(null);
            hintText.setText(StringUtils.EMPTY);
        }
    }

    @Override
    public boolean isDataChangeable() {
        return false;
    }

    @Override
    public void setDataChangeable(boolean changeable) {
        // do nothing, this label is never changeable
    }

}
