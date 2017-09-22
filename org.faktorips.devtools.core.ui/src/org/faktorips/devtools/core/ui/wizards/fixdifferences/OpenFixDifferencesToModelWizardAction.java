/*******************************************************************************
 * Copyright (c) Faktor Zehn AG. <http://www.faktorzehn.org>
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version
 * 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and
 * restrictions as well as the possibility of alternative license terms.
 *******************************************************************************/

package org.faktorips.devtools.core.ui.wizards.fixdifferences;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.actions.ActionDelegate;
import org.faktorips.devtools.core.IpsPlugin;
import org.faktorips.devtools.core.model.IIpsElement;
import org.faktorips.devtools.core.model.ipsobject.IFixDifferencesToModelSupport;
import org.faktorips.devtools.core.model.ipsobject.IIpsSrcFile;
import org.faktorips.devtools.core.model.ipsproject.IIpsPackageFragment;
import org.faktorips.devtools.core.model.ipsproject.IIpsPackageFragmentRoot;
import org.faktorips.devtools.core.model.ipsproject.IIpsProject;
import org.faktorips.devtools.core.ui.IpsUIPlugin;
import org.faktorips.util.message.MessageList;

/**
 * 
 * @author Daniel Hohenberger
 */
public class OpenFixDifferencesToModelWizardAction extends ActionDelegate implements IWorkbenchWindowActionDelegate,
        IObjectActionDelegate {
    private IWorkbenchWindow window;
    // the last selection
    private ISelection selection;

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        // nothing to do
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(IWorkbenchWindow window) {
        this.window = window;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run(IAction action) {
        // save dirty editors
        if (!IpsUIPlugin.getDefault().saveAllEditors()) {
            return;
        }

        Set<IFixDifferencesToModelSupport> ipsElementsToFix = findObjectsToFix();
        FixDifferencesToModelWizard wizard = new FixDifferencesToModelWizard(ipsElementsToFix);
        wizard.init(window.getWorkbench(), getCurrentSelection());
        WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
        dialog.open();
    }

    private IStructuredSelection getCurrentSelection() {
        if (window != null) {
            ISelection newSelection = window.getSelectionService().getSelection();
            if (newSelection instanceof IStructuredSelection) {
                return (IStructuredSelection)newSelection;
            }
            IWorkbenchPart part = window.getPartService().getActivePart();
            if (part instanceof IEditorPart) {
                IEditorInput input = ((IEditorPart)part).getEditorInput();
                if (input instanceof IFileEditorInput) {
                    return new StructuredSelection(((IFileEditorInput)input).getFile());
                }
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        if (selection.isEmpty()) {
            action.setEnabled(false);
            return;
        }
        this.selection = selection;
    }

    private Set<IFixDifferencesToModelSupport> findObjectsToFix() {
        Set<IFixDifferencesToModelSupport> ipsElementsToFix = new HashSet<IFixDifferencesToModelSupport>();
        if (selection instanceof IStructuredSelection) {
            try {
                IStructuredSelection sel = (IStructuredSelection)selection;
                for (Iterator<?> iter = sel.iterator(); iter.hasNext();) {
                    Object selected = iter.next();
                    addElementToFix(ipsElementsToFix, selected);
                }
            } catch (CoreException e) {
                // don't disturb
                IpsPlugin.log(e);
            }
        }
        return ipsElementsToFix;
    }

    private void addElementToFix(Set<IFixDifferencesToModelSupport> ipsElementsToFix, Object selected)
            throws CoreException {
        if (selected instanceof IJavaProject) {
            IIpsProject project = getIpsProject((IJavaProject)selected);
            addIpsElements(project, ipsElementsToFix);
        } else if (selected instanceof IIpsProject) {
            addIpsElements((IIpsProject)selected, ipsElementsToFix);
        } else if (selected instanceof IPackageFragmentRoot) {
            IIpsPackageFragmentRoot root = getIpsProject(((IPackageFragmentRoot)selected).getJavaProject())
                    .findIpsPackageFragmentRoot(((IPackageFragmentRoot)selected).getElementName());
            addIpsElements(root, ipsElementsToFix);
        } else if (selected instanceof IIpsPackageFragmentRoot) {
            addIpsElements((IIpsPackageFragmentRoot)selected, ipsElementsToFix);
        } else if (selected instanceof IPackageFragment) {
            IIpsProject project = getIpsProject(((IPackageFragment)selected).getJavaProject());
            IIpsPackageFragmentRoot[] roots = project.getIpsPackageFragmentRoots();
            for (IIpsPackageFragmentRoot root : roots) {
                IIpsPackageFragment pack = root.getIpsPackageFragment(((IPackageFragment)selected).getElementName());
                addIpsElements(pack, ipsElementsToFix);
            }
        } else if (selected instanceof IIpsPackageFragment) {
            addIpsElements((IIpsPackageFragment)selected, ipsElementsToFix);
        } else if (selected instanceof IIpsSrcFile) {
            addElementToFix(ipsElementsToFix, ((IIpsSrcFile)selected).getIpsObject());
        } else if (selected instanceof IFixDifferencesToModelSupport) {
            IFixDifferencesToModelSupport ipsElementToFix = (IFixDifferencesToModelSupport)selected;
            addIpsElement(ipsElementToFix, ipsElementsToFix);
        } else if (selected instanceof IResource) {
            Object objToAdd = IpsPlugin.getDefault().getIpsModel().getIpsElement((IResource)selected);
            if (objToAdd instanceof IIpsSrcFile) {
                objToAdd = ((IIpsSrcFile)objToAdd).getIpsObject();
            }
            if (objToAdd != null) {
                addElementToFix(ipsElementsToFix, objToAdd);
            }
        }
    }

    private IIpsProject getIpsProject(IJavaProject jProject) {
        return IpsPlugin.getDefault().getIpsModel().getIpsProject(jProject.getProject());
    }

    private void addIpsElements(IIpsProject element, Set<IFixDifferencesToModelSupport> ipsElementsToFix)
            throws CoreException {
        if (element == null) {
            return;
        }
        if (!element.exists()) {
            return;
        }
        IIpsPackageFragmentRoot[] roots = element.getIpsPackageFragmentRoots();
        for (IIpsPackageFragmentRoot root : roots) {
            IIpsPackageFragment[] packs = root.getIpsPackageFragments();
            for (IIpsPackageFragment pack : packs) {
                addIpsElements(pack, ipsElementsToFix);
            }
        }
    }

    private void addIpsElements(IIpsPackageFragmentRoot element, Set<IFixDifferencesToModelSupport> ipsElementsToFix)
            throws CoreException {
        if (element == null) {
            return;
        }
        if (!element.exists()) {
            return;
        }
        IIpsPackageFragment[] packs = (element).getIpsPackageFragments();
        for (IIpsPackageFragment pack : packs) {
            addIpsElements(pack, ipsElementsToFix);
        }
    }

    private void addIpsElements(IIpsPackageFragment pack, Set<IFixDifferencesToModelSupport> ipsElementsToFix)
            throws CoreException {
        IIpsElement[] elements = pack.getChildren();
        for (IIpsElement element2 : elements) {
            IIpsElement element = element2;
            if (element instanceof IIpsSrcFile) {
                element = ((IIpsSrcFile)element).getIpsObject();
            }
            if (element instanceof IFixDifferencesToModelSupport) {
                IFixDifferencesToModelSupport ipsElementToFix = (IFixDifferencesToModelSupport)element;
                addIpsElement(ipsElementToFix, ipsElementsToFix);
            } else if (element instanceof IIpsPackageFragment) {
                addIpsElements((IIpsPackageFragment)element, ipsElementsToFix);
            }
        }
        // add all elements in child packages
        IIpsPackageFragment[] childIpsPackageFragments = pack.getChildIpsPackageFragments();
        for (IIpsPackageFragment childIpsPackageFragment : childIpsPackageFragments) {
            addIpsElements(childIpsPackageFragment, ipsElementsToFix);
        }
    }

    private void addIpsElement(IFixDifferencesToModelSupport ipsElementToFix,
            Set<IFixDifferencesToModelSupport> ipsElementsToFix) throws CoreException {
        IIpsProject ipsProject = ipsElementToFix.getIpsSrcFile().getIpsProject();
        MessageList msgListProperties = ipsProject.validate();
        if (msgListProperties.containsErrorMsg()) {
            return;
        }
        if (ipsElementToFix.containsDifferenceToModel(ipsProject)) {
            ipsElementsToFix.add(ipsElementToFix);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        window = targetPart.getSite().getWorkbenchWindow();
    }

}
