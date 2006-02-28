package org.faktorips.devtools.core.ui.editors.productcmpt;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.faktorips.devtools.core.model.Described;
import org.faktorips.devtools.core.model.pctype.IValidationRule;
import org.faktorips.devtools.core.model.product.IProductCmpt;
import org.faktorips.devtools.core.ui.UIToolkit;
import org.faktorips.devtools.core.ui.editors.DescriptionSection;
import org.faktorips.devtools.core.ui.editors.IpsObjectEditor;
import org.faktorips.devtools.core.ui.editors.IpsObjectEditorPage;


/**
 * A page to display all rules aplied to one product component.
 */
public class RulesPage extends IpsObjectEditorPage {
    
    final static String PAGE_ID = "Rules"; //$NON-NLS-1$

    public RulesPage(IpsObjectEditor editor) {
        super(editor, PAGE_ID, Messages.RulesPage_title);
    }

    /**
     * Get owning editor.
     */
    ProductCmptEditor getProductCmptEditor() {
        return (ProductCmptEditor)getEditor();
    }

    /**
     * Get Product which is parent of the generations
     */
    IProductCmpt getProductCmpt() {
        return getProductCmptEditor().getProductCmpt(); 
    }
    
    /**
     * {@inheritDoc}
     */
    protected void createPageContent(Composite formBody, UIToolkit toolkit) {
		GridLayout layout = new GridLayout(2, true);
		formBody.setLayout(layout);
		
		final RulesSection rulesSection 
			= new RulesSection(this, formBody, toolkit);
		final DescriptionSection descSection = new DescriptionSection(getProductCmpt(), formBody, toolkit);
		rulesSection.addSelectionChangedListener(new ISelectionChangedListener() {
			private final Described EMPTY = new Described() {
			
				public String getDescription() {
					return ""; //$NON-NLS-1$
				}
			
				public void setDescription(String newDescription) {
					// dont do anything.
				}
			};
            public void selectionChanged(SelectionChangedEvent event) {
            	Object selected = ((IStructuredSelection)event.getSelection()).getFirstElement();
            	if (selected instanceof IValidationRule) {
            		descSection.setDescribedObject((IValidationRule)selected);
            	}
            	else {
            		descSection.setDescribedObject(EMPTY);
            	}
            }
		    
		});
    }
}
