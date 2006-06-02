/*******************************************************************************
 * Copyright (c) 2005,2006 Faktor Zehn GmbH und andere.
 *
 * Alle Rechte vorbehalten.
 *
 * Dieses Programm und alle mitgelieferten Sachen (Dokumentationen, Beispiele,
 * Konfigurationen, etc.) duerfen nur unter den Bedingungen der 
 * Faktor-Zehn-Community Lizenzvereinbarung - Version 0.1 (vor Gruendung Community) 
 * genutzt werden, die Bestandteil der Auslieferung ist und auch unter
 *   http://www.faktorips.org/legal/cl-v01.html
 * eingesehen werden kann.
 *
 * Mitwirkende:
 *   Faktor Zehn GmbH - initial API and implementation - http://www.faktorzehn.de
 *
 *******************************************************************************/

package org.faktorips.devtools.core.ui.controller.fields;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.faktorips.devtools.core.ui.controls.AbstractCheckbox;


/**
 *
 */
public class CheckboxField extends DefaultEditField {
    
    private AbstractCheckbox checkbox;
    
    public CheckboxField(AbstractCheckbox checkbox) {
        this.checkbox = checkbox;
    }

    /** 
     * Overridden method.
     * @see org.faktorips.devtools.core.ui.controller.EditField#getControl()
     */
    public Control getControl() {
        return checkbox;
    }
    
    public AbstractCheckbox getCheckbox() {
        return checkbox;
    }

    /** 
     * Overridden method.
     * @see org.faktorips.devtools.core.ui.controller.EditField#getValue()
     */
    public Object getValue() {
        return new Boolean(checkbox.isChecked());
    }

    /** 
     * Overridden method.
     * @see org.faktorips.devtools.core.ui.controller.EditField#setValue(java.lang.Object)
     */
    public void setValue(Object newValue) {
        checkbox.setChecked(((Boolean)newValue).booleanValue());
    }

    /** 
     * Overridden method.
     * @see org.faktorips.devtools.core.ui.controller.EditField#getText()
     */
    public String getText() {
        return checkbox.isChecked()?"true":"false"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    /** 
     * Overridden method.
     * @see org.faktorips.devtools.core.ui.controller.EditField#setText(java.lang.String)
     */
    public void setText(String newText) {
        checkbox.setChecked(Boolean.valueOf(newText).booleanValue());
    }

    /** 
     * Overridden method.
     * @see org.faktorips.devtools.core.ui.controller.EditField#insertText(java.lang.String)
     */
    public void insertText(String text) {
        // nothing to do
    }

    /** 
     * Overridden method.
     * @see org.faktorips.devtools.core.ui.controller.EditField#selectAll()
     */
    public void selectAll() {
        // nothing to do
    }

    /** 
     * Overridden method.
     * @see org.faktorips.devtools.core.ui.controller.fields.DefaultEditField#addListenerToControl()
     */
    protected void addListenerToControl() {
        checkbox.getButton().addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent e) {
                notifyChangeListeners(new FieldValueChangedEvent(CheckboxField.this));
            }

            public void widgetDefaultSelected(SelectionEvent e) {
                // nothing to do
            }
            
        });
    }

}
