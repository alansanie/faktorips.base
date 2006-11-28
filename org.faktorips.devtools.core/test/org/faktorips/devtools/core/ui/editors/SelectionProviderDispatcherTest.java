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

package org.faktorips.devtools.core.ui.editors;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;

import junit.framework.TestCase;

public class SelectionProviderDispatcherTest extends TestCase {

    private TestSelection selection1;
    private TestSelection selection2;
    
    private TestSelectionProvider selectionProvider1;
    private TestSelectionProvider selectionProvider2;
    
    private TestSelectionProviderActivation providerActivation1;
    private TestSelectionProviderActivation providerActivation2;
    
    private TestSelectionChangeListener listener1;
    private TestSelectionChangeListener listener2;
    
    private SelectionProviderDispatcher dispatcher;
    
    public void setUp(){
        selection1 = new TestSelection();
        selection2 = new TestSelection();
        
        selectionProvider1 = new TestSelectionProvider();
        selectionProvider2 = new TestSelectionProvider();
        
        selectionProvider1.selection = selection1;
        selectionProvider2.selection = selection2;
        
        providerActivation1 = new TestSelectionProviderActivation();
        providerActivation2 = new TestSelectionProviderActivation();

        providerActivation1.selectionProvider = selectionProvider1;
        providerActivation2.selectionProvider = selectionProvider2;
        
        dispatcher = new SelectionProviderDispatcher();
        dispatcher.addSelectionProviderActivation(providerActivation1);
        dispatcher.addSelectionProviderActivation(providerActivation2);

        listener1 = new TestSelectionChangeListener();
        listener2 = new TestSelectionChangeListener();
        
        dispatcher.addSelectionChangedListener(listener1);
        dispatcher.addSelectionChangedListener(listener2);
    }
    
    public void testGetSelection(){
        
        
        providerActivation1.activated = true;
        ISelection currentSelection = dispatcher.getSelection();
        assertEquals(selection1, currentSelection);
        
        providerActivation1.activated = false;
        currentSelection = dispatcher.getSelection();
        assertNull(currentSelection);

        providerActivation2.activated = true;
        currentSelection = dispatcher.getSelection();
        assertEquals(selection2, currentSelection);
    }
    
    public void testSetSelection(){
        
        TestSelection selection3 = new TestSelection();
        providerActivation1.activated = true;
        dispatcher.setSelection(selection3);
        
        assertEquals(selection3, selectionProvider1.getSelection());
        assertEquals(selection2, selectionProvider2.getSelection());
        
        selectionProvider1.selection = selection1;
        selectionProvider2.selection = selection2;
        
        providerActivation1.activated = false;
        providerActivation2.activated = true;
        
        dispatcher.setSelection(selection3);

        assertEquals(selection3, selectionProvider2.getSelection());
        assertEquals(selection1, selectionProvider1.getSelection());
        
    }
    
    public void testSelectionChanged(){
        dispatcher.selectionChanged(new SelectionChangedEvent(selectionProvider1, selection1));
        assertTrue(listener1.informed);
        assertTrue(listener2.informed);
    }
    
    
    private class TestSelectionProviderActivation implements ISelectionProviderActivation{

        public ISelectionProvider selectionProvider;
        
        public boolean activated = false;
        
        public ISelectionProvider getSelectionProvider() {
            return selectionProvider;
        }

        public boolean isActivated() {
            return activated;
        }
    }
    
    
    private class TestSelectionProvider implements ISelectionProvider{

        public ISelection selection;
        
        public void addSelectionChangedListener(ISelectionChangedListener listener) {
        }

        public ISelection getSelection() {
            return selection;
        }

        public void removeSelectionChangedListener(ISelectionChangedListener listener) {
        }

        public void setSelection(ISelection selection) {
            this.selection = selection;
        }
    }
    
    
    private class TestSelection implements ISelection{

        public boolean isEmpty() {
            return false;
        }
        
    }
    
    
    private class TestSelectionChangeListener implements ISelectionChangedListener{

        public boolean informed = false;
        
        public void selectionChanged(SelectionChangedEvent event) {
            informed = true;
        }
    }
}
