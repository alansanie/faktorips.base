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

package org.faktorips.devtools.core.ui.preferencepages;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.faktorips.devtools.core.model.ipsproject.IIpsSrcFolderEntry;
import org.faktorips.devtools.core.ui.UIToolkit;
import org.faktorips.devtools.core.ui.controls.FolderSelectionControl;

/**
 * Dialog for editing output folders of IPS source folder entries 
 * @author Roman Grutza
 */
public class OutputFolderEditDialog extends StatusDialog {

    private FolderSelectionControl folderSelectionControl;
    private Button buttonDefaultFolderSelected;
    private Button buttonCustomFolderSelected;
    
    private boolean customFolderSelected = false;
    private IContainer selectedFolder;
    private IIpsObjectPathEntryAttribute attribute;
    private IIpsSrcFolderEntry srcFolderEntry;    

    /**
     * @param parent Composite
     * @param srcfolderEntry parent entry for which to alter an attribute
     * @param the attribute to be changed
     */
    public OutputFolderEditDialog(Shell parent, IIpsSrcFolderEntry srcFolderEntry, IIpsObjectPathEntryAttribute attribute) {
        super(parent);
        this.setTitle(Messages.OutputFolderEditDialog_dialog_title);
        this.setHelpAvailable(false);
        this.attribute = attribute;
        this.srcFolderEntry = srcFolderEntry;
        
        if (! (attribute.isFolderForDerivedSources() || attribute.isFolderForMergableSources())) {
            throw new IllegalArgumentException("Attribute is not of type output folder."); //$NON-NLS-1$
        }
    }


    /**
     * {@inheritDoc}
     */
    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);

        Group group = new Group(parent, SWT.NONE);
        
        buttonDefaultFolderSelected = new Button(group, SWT.RADIO);
        String defaultOutputFolder = (getDefaultOutputFolder() != null) ? " ("  + getDefaultOutputFolder().getName() + ")" : ""; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        buttonDefaultFolderSelected.setText(Messages.OutputFolderEditDialog_use_default_label + defaultOutputFolder);
        buttonCustomFolderSelected = new Button(group, SWT.RADIO);
        buttonCustomFolderSelected.setText(Messages.OutputFolderEditDialog_use_sepcific_label);

        folderSelectionControl = new FolderSelectionControl(group, new UIToolkit(null), Messages.OutputFolderEditDialog_button_title_browse);
        folderSelectionControl.setRoot(srcFolderEntry.getIpsProject().getProject());

        // initialize specific output folder
        if (srcFolderEntry.getIpsObjectPath().isOutputDefinedPerSrcFolder()) {
            folderSelectionControl.setFolder(getSpecificFolder());
            selectedFolder = getSpecificFolder();    
        }
        
        if (selectedFolder != null) {
            // entry specific folder
            buttonCustomFolderSelected.setSelection(true);
            buttonDefaultFolderSelected.setSelection(false);
            folderSelectionControl.setEnabled(true);
            folderSelectionControl.setFolder(selectedFolder);
        }
        else {
            // default IPS object path folders are used
            buttonCustomFolderSelected.setSelection(false);
            buttonDefaultFolderSelected.setSelection(true);
            folderSelectionControl.setEnabled(false);            
        }
        
        GridLayout layout = new GridLayout(1, true);
        layout.verticalSpacing = 10;
        
        buttonDefaultFolderSelected.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent e) { /* nothing to do*/ }

            public void widgetSelected(SelectionEvent e) {
                folderSelectionControl.setEnabled(false);
                customFolderSelected = false;
            }
        });
        
        buttonCustomFolderSelected.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent e) { /* nothing to do*/ }

            public void widgetSelected(SelectionEvent e) {
                folderSelectionControl.setEnabled(true);
                customFolderSelected = true;
            }
        });
        
        group.setLayout(layout);
        
        return composite;
        
    }

    /**
     * Get the dialog's result
     * @return selected folder, or null if none was selected
     */
    public IContainer getSelectedFolder() {
        IContainer resultContainer = null;
        
        if (customFolderSelected) {
            resultContainer = folderSelectionControl.getFolder();
        }
        else  {
            resultContainer = getDefaultOutputFolder();
        }

        return resultContainer; 
    }

    
    private IFolder getDefaultOutputFolder() {
        
        IFolder outputFolder;
        if (attribute.isFolderForDerivedSources()) {
            outputFolder = srcFolderEntry.getIpsObjectPath().getOutputFolderForDerivedSources();
        } else {
            outputFolder = srcFolderEntry.getIpsObjectPath().getOutputFolderForMergableSources();
        }
        return outputFolder;
    }
    
    private IFolder getSpecificFolder() {
        if (attribute.isFolderForDerivedSources()) {
            return srcFolderEntry.getSpecificOutputFolderForDerivedJavaFiles();
        } else {
            return srcFolderEntry.getSpecificOutputFolderForMergableJavaFiles();
        }
    }
    
}
