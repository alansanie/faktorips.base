/*******************************************************************************
 * Copyright (c) 2005-2011 Faktor Zehn AG und andere.
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

package org.faktorips.devtools.core.ui.wizards.productcmpt;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.faktorips.devtools.core.IpsPlugin;
import org.faktorips.devtools.core.ui.UIToolkit;
import org.faktorips.devtools.core.ui.binding.BindingContext;
import org.faktorips.devtools.core.ui.controller.fields.IpsProjectRefField;
import org.faktorips.devtools.core.ui.controls.IpsProjectRefControl;
import org.faktorips.util.message.MessageList;

public class TypeSelectionPage extends WizardPage {

    private final ResourceManager resourManager;

    private final NewProductCmptPMO pmo;

    private final BindingContext bindingContext;

    private TypeSelectionUpdater typeSelectionUpdater;

    private IpsProjectRefControl ipsProjectRefControl;

    private TypeSelectionComposite typeSelectionComposite;

    public TypeSelectionPage(NewProductCmptPMO pmo) {
        super("New Product Component");
        this.pmo = pmo;
        setTitle("Which kind of product component do you want to create?");
        resourManager = new LocalResourceManager(JFaceResources.getResources());
        bindingContext = new BindingContext();
        pmo.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (isCurrentPage()) {
                    getContainer().updateButtons();
                }
            }
        });
    }

    @Override
    public void createControl(Composite parent) {
        UIToolkit toolkit = new UIToolkit(null);
        Composite composite = toolkit.createGridComposite(parent, 1, false, true);
        GridLayout layout = (GridLayout)composite.getLayout();
        layout.verticalSpacing = 10;

        Composite twoColumnComposite = toolkit.createLabelEditColumnComposite(composite);

        // Select Project
        toolkit.createLabel(twoColumnComposite, "Project:");
        ipsProjectRefControl = toolkit.createIpsProjectRefControl(twoColumnComposite);

        toolkit.createHorizonzalLine(composite);

        typeSelectionComposite = new TypeSelectionComposite(composite, toolkit);
        typeSelectionComposite.setTitle("Type:");

        setControl(composite);

        bindControls();
    }

    void bindControls() {
        IpsProjectRefField ipsProjectRefField = new IpsProjectRefField(ipsProjectRefControl);

        bindingContext.bindContent(ipsProjectRefField, pmo, NewProductCmptPMO.PROPERTY_IPS_PROJECT);

        typeSelectionComposite.addDoubleClickListener(new DoubleClickListener(this));
        typeSelectionUpdater = new TypeSelectionUpdater(this, pmo);
        pmo.addPropertyChangeListener(typeSelectionUpdater);

        bindingContext.bindContent(typeSelectionComposite.getListViewerField(), pmo,
                NewProductCmptPMO.PROPERTY_SELECTED_BASE_TYPE);

        typeSelectionUpdater.updateUI();
        bindingContext.updateUI();

    }

    @Override
    public void dispose() {
        super.dispose();
        resourManager.dispose();
        bindingContext.dispose();
        if (typeSelectionUpdater != null) {
            pmo.removePropertyChangeListener(typeSelectionUpdater);
        }
    }

    private static class TypeSelectionUpdater extends UiUpdater {

        public TypeSelectionUpdater(TypeSelectionPage page, NewProductCmptPMO pmo) {
            super(page, pmo);
        }

        /**
         * @return Returns the page.
         */
        @Override
        public TypeSelectionPage getPage() {
            return (TypeSelectionPage)super.getPage();
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(NewProductCmptPMO.PROPERTY_IPS_PROJECT)) {
                updateListViewer();
            }
            if (NewProductCmptPMO.PROPERTY_SELECTED_BASE_TYPE.equals(evt.getPropertyName())) {
                updateLabelAndDescription();
            }
            super.propertyChange(evt);
        }

        @Override
        public void updateUI() {
            super.updateUI();
            updateListViewer();
            updateLabelAndDescription();
        }

        private void updateListViewer() {
            getPage().typeSelectionComposite.setListInput(getPmo().getBaseTypes());
        }

        private void updateLabelAndDescription() {
            if (getPmo().getSelectedBaseType() == null) {
                getPage().typeSelectionComposite.setDescriptionTitle(StringUtils.EMPTY);
                getPage().typeSelectionComposite.setDescription(StringUtils.EMPTY);
            } else {
                getPage().typeSelectionComposite.setDescriptionTitle(IpsPlugin.getMultiLanguageSupport()
                        .getLocalizedLabel(getPmo().getSelectedBaseType()));
                getPage().typeSelectionComposite.setDescription(IpsPlugin.getMultiLanguageSupport()
                        .getLocalizedDescription(getPmo().getSelectedBaseType()));
            }
        }

        @Override
        protected MessageList validatePage() {
            MessageList messageList = getPmo().getValidator().validateTypeSelection();
            return messageList;
        }

    }

    private static class DoubleClickListener implements IDoubleClickListener {

        private final TypeSelectionPage page;

        public DoubleClickListener(TypeSelectionPage page) {
            this.page = page;
        }

        @Override
        public void doubleClick(DoubleClickEvent event) {
            if (page.canFlipToNextPage()) {
                page.getWizard().getContainer().showPage(page.getNextPage());
            }

        }
    }

}
