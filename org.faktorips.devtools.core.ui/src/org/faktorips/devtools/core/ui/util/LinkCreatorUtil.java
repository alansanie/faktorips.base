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

package org.faktorips.devtools.core.ui.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.faktorips.devtools.core.IpsPlugin;
import org.faktorips.devtools.core.exception.CoreRuntimeException;
import org.faktorips.devtools.core.internal.model.productcmpt.IProductCmptLinkContainer;
import org.faktorips.devtools.core.model.ipsobject.IIpsSrcFile;
import org.faktorips.devtools.core.model.ipsproject.IIpsProject;
import org.faktorips.devtools.core.model.productcmpt.IProductCmpt;
import org.faktorips.devtools.core.model.productcmpt.IProductCmptGeneration;
import org.faktorips.devtools.core.model.productcmpt.IProductCmptLink;
import org.faktorips.devtools.core.model.productcmpt.treestructure.IProductCmptReference;
import org.faktorips.devtools.core.model.productcmpt.treestructure.IProductCmptStructureReference;
import org.faktorips.devtools.core.model.productcmpt.treestructure.IProductCmptTypeAssociationReference;
import org.faktorips.devtools.core.model.productcmpttype.IProductCmptType;
import org.faktorips.devtools.core.model.productcmpttype.IProductCmptTypeAssociation;
import org.faktorips.devtools.core.model.type.IAssociation;
import org.faktorips.devtools.core.ui.IpsUIPlugin;
import org.faktorips.devtools.core.ui.Messages;
import org.faktorips.devtools.core.ui.views.productstructureexplorer.AssociationSelectionDialog;

/**
 * This class provides several ways to create a links in a product component.
 * 
 * @author dirmeier
 */
public class LinkCreatorUtil {

    private final boolean autoSave;

    public LinkCreatorUtil(boolean autoSave) {
        this.autoSave = autoSave;
    }

    public boolean canCreateLinks(IProductCmptStructureReference target, List<IProductCmpt> draggedCmpts)
            throws CoreException {
        if (target instanceof IProductCmptReference) {
            // product cmpt reference in product structure view
            IProductCmptReference reference = (IProductCmptReference)target;
            return processProductCmptReference(draggedCmpts, reference, false);
        } else if (target instanceof IProductCmptTypeAssociationReference) {
            IProductCmptTypeAssociationReference reference = (IProductCmptTypeAssociationReference)target;
            return processAssociationReference(draggedCmpts, reference, false);
        } else {
            return false;
        }
    }

    public boolean createLinks(List<IProductCmpt> droppedCmpts, IProductCmptStructureReference target) {
        boolean haveToSave = autoSave;
        try {
            boolean result;
            IIpsSrcFile ipsSrcFile;
            if (target instanceof IProductCmptReference) {
                IProductCmptReference cmptReference = (IProductCmptReference)target;
                ipsSrcFile = cmptReference.getWrappedIpsObject().getIpsSrcFile();
                haveToSave &= !ipsSrcFile.isDirty();
                result = processProductCmptReference(droppedCmpts, cmptReference, true);
            } else if (target instanceof IProductCmptTypeAssociationReference) {
                IProductCmptTypeAssociationReference relationReference = (IProductCmptTypeAssociationReference)target;
                ipsSrcFile = relationReference.getParent().getWrappedIpsObject().getIpsSrcFile();
                haveToSave &= !ipsSrcFile.isDirty();
                result = processAssociationReference(droppedCmpts, relationReference, true);
            } else {
                return false;
            }
            if (result && haveToSave) {
                ipsSrcFile.save(false, null);
            }
            return result;
        } catch (CoreException e) {
            IpsPlugin.log(e);
            return false;
        }
    }

    protected boolean processProductCmptReference(List<IProductCmpt> draggedCmpts,
            IProductCmptReference target,
            boolean createLinks) throws CoreException {
        IpsUIPlugin.getDefault();
        if (!IpsUIPlugin.isEditable(target.getProductCmpt().getIpsSrcFile())) {
            return false;
        }

        IIpsProject ipsProject = target.getProductCmpt().getIpsProject();
        IProductCmptGeneration generation = target.getProductCmpt().getGenerationEffectiveOn(
                target.getStructure().getValidAt());
        IProductCmptType cmptType = target.getProductCmpt().findProductCmptType(ipsProject);
        if (generation == null || cmptType == null) {
            return false;
        }
        List<IProductCmptTypeAssociation> associations = cmptType.findAllNotDerivedAssociations(ipsProject);
        // should only return true if all dragged cmpts are valid
        boolean result = false;
        for (IProductCmpt draggedCmpt : draggedCmpts) {
            List<IProductCmptTypeAssociation> possibleAssos = new ArrayList<IProductCmptTypeAssociation>();
            for (IProductCmptTypeAssociation aAssoziation : associations) {
                if (canCreateValidLink(generation, draggedCmpt, aAssoziation)) {
                    possibleAssos.add(aAssoziation);
                }
            }
            if (possibleAssos.size() > 0) {
                result = true;
            } else if (!createLinks) {
                return false;
            }
            if (createLinks) {
                if (possibleAssos.size() == 1) {
                    IProductCmptTypeAssociation association = possibleAssos.get(0);
                    createLink(association, generation, draggedCmpt.getQualifiedName());
                } else if (possibleAssos.size() > 1) {
                    Object[] selectedAssociations = selectAssociation(draggedCmpt.getQualifiedName(), possibleAssos);
                    if (selectedAssociations != null) {
                        for (Object object : selectedAssociations) {
                            if (object instanceof IProductCmptTypeAssociation) {
                                IProductCmptTypeAssociation association = (IProductCmptTypeAssociation)object;
                                createLink(association, generation, draggedCmpt.getQualifiedName());
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    private boolean canCreateValidLink(IProductCmptGeneration generation,
            IProductCmpt draggedCmpt,
            IProductCmptTypeAssociation aAssoziation) {
        try {
            if (generation == null) {
                return false;
            }
            IProductCmptLinkContainer container;
            if (generation.isContainerFor(aAssoziation)) {
                container = generation;
            } else {
                container = generation.getProductCmpt();
            }
            return container.canCreateValidLink(draggedCmpt, aAssoziation, container.getIpsProject());
        } catch (CoreException e) {
            throw new CoreRuntimeException(e);
        }
    }

    /**
     * Set to protected to override in test class
     */
    protected Object[] selectAssociation(String droppedCmptName, List<IProductCmptTypeAssociation> possibleAssos) {
        Shell shell = Display.getDefault().getActiveShell();
        if (shell == null) {
            shell = new Shell(Display.getDefault());
        }
        //
        SelectionDialog dialog = new AssociationSelectionDialog(shell, new CopyOnWriteArrayList<IAssociation>(
                possibleAssos), NLS.bind(Messages.LinkDropListener_selectAssociation, droppedCmptName));
        dialog.setBlockOnOpen(true);
        dialog.setHelpAvailable(false);
        if (dialog.open() == Window.OK) {
            if (dialog.getResult().length > 0) {
                return dialog.getResult();
            }
        }
        return null;
    }

    protected boolean processAssociationReference(List<IProductCmpt> draggedCmpts,
            IProductCmptTypeAssociationReference target,
            boolean createLink) {
        IProductCmptTypeAssociation association;
        IProductCmptGeneration generation;
        IProductCmpt parentCmpt = ((IProductCmptReference)target.getParent()).getProductCmpt();
        IpsUIPlugin.getDefault();
        if (!IpsUIPlugin.isEditable(parentCmpt.getIpsSrcFile())) {
            return false;
        }
        generation = parentCmpt.getGenerationEffectiveOn(target.getStructure().getValidAt());
        association = target.getAssociation();
        // should only return true if all dragged cmpts are valid
        boolean result = false;
        for (IProductCmpt draggedCmpt : draggedCmpts) {
            if (canCreateValidLink(generation, draggedCmpt, association)) {
                result = true;
                if (createLink) {
                    createLink(association, generation, draggedCmpt.getQualifiedName());
                }
            } else {
                return false;
            }
        }
        return result;
    }

    /**
     * Creates a new link instance for the given association. If the association is defined as
     * changing over time, the link instance will be added to the product component generation.
     * Otherwise it will be added to the product component itself.
     * 
     * @param association the association the new link is an instance of.
     * @param generation the generation currently active in the editor. The new link is not
     *            necessarily added to this generation!
     * @param targetQualifiedName the qualified name of the target product component
     * @return the newly created link instance
     */
    public IProductCmptLink createLink(IProductCmptTypeAssociation association,
            IProductCmptGeneration generation,
            String targetQualifiedName) {
        if (generation != null && association != null && IpsUIPlugin.getDefault().isGenerationEditable(generation)) {
            if (generation.isContainerFor(association)) {
                return createLinkForContainer(targetQualifiedName, generation, association);
            } else {
                return createLinkForContainer(targetQualifiedName, generation.getProductCmpt(), association);
            }
        }
        return null;
    }

    private IProductCmptLink createLinkForContainer(String droppedCmptQName,
            IProductCmptLinkContainer container,
            IAssociation association) {
        IProductCmptLink newLink = container.newLink(association.getName());
        newLink.setTarget(droppedCmptQName);
        newLink.setMaxCardinality(1);
        newLink.setMinCardinality(0);
        return newLink;
    }

}
