/*******************************************************************************
 * Copyright (c) 2005-2009 Faktor Zehn AG und andere.
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

package org.faktorips.devtools.core.model.bf;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.faktorips.devtools.core.IpsPlugin;

/**
 * This enumeration defines the possible type of business function elements. Each instance of a
 * business function element has a reference to its type.
 * 
 * @author Peter Erzberger
 */
public enum BFElementType {

    ACTION_INLINE("inlineAction", Messages.getString("BFElementType.inlineAction"), "OpaqueAction.gif") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        @Override
        public IBFElement newBFElement(IBusinessFunction businessFunction, Point location) {
            return businessFunction.newOpaqueAction(location);
        }
    },
    ACTION_METHODCALL("methodCallAction", Messages.getString("BFElementType.methodCallAction"), "CallOperationAction.gif") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        @Override
        public IBFElement newBFElement(IBusinessFunction businessFunction, Point location) {
            return businessFunction.newMethodCallAction(location);
        }
    },
    ACTION_BUSINESSFUNCTIONCALL("businessFunctionCallAction", Messages.getString("BFElementType.bfCallAction"), "CallBehaviorAction.gif") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        @Override
        public IBFElement newBFElement(IBusinessFunction businessFunction, Point location) {
            return businessFunction.newBusinessFunctionCallAction(location);
        }
    },
    DECISION("decision", Messages.getString("BFElementType.decision"), "DecisionNode.gif") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        @Override
        public IBFElement newBFElement(IBusinessFunction businessFunction, Point location) {
            return businessFunction.newDecision(location);
        }
    },
    DECISION_METHODCALL("methodCallDecision", Messages.getString("BFElementType.methodCallDecision"), "DecisionNode.gif") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        @Override
        public IBFElement newBFElement(IBusinessFunction businessFunction, Point location) {
            return businessFunction.newMethodCallDecision(location);
        }
    },
    MERGE("merge", Messages.getString("BFElementType.merge"), "MergeNode.gif") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        @Override
        public IBFElement newBFElement(IBusinessFunction businessFunction, Point location) {
            return businessFunction.newMerge(location);
        }
    },
    END("end", Messages.getString("BFElementType.end"), "ActivityFinalNode.gif") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        @Override
        public IBFElement newBFElement(IBusinessFunction businessFunction, Point location) {
            return businessFunction.newEnd(location);
        }
    },
    START("start", Messages.getString("BFElementType.start"), "InitialNode.gif") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        @Override
        public IBFElement newBFElement(IBusinessFunction businessFunction, Point location) {
            return businessFunction.newStart(location);
        }
    },
    PARAMETER("parameter", Messages.getString("BFElementType.parameter"), null) { //$NON-NLS-1$ //$NON-NLS-2$
        @Override
        public IBFElement newBFElement(IBusinessFunction businessFunction, Point location) {
            return businessFunction.newParameter();
        }
    };

    private BFElementType(String id, String name, String imageName) {
        this.name = name;
        this.id = id;
        this.imageName = imageName;
    }

    private String name;
    private String id;
    private ImageDescriptor imageDescriptor;
    private String imageName;

    /**
     * Returns the describing name of the business function element type.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the unique id of the business function element type.
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the image descriptor of the image that specific for business function elements of this type.
     */
    public ImageDescriptor getImageDescriptor() {
        if (imageDescriptor == null) {
            imageDescriptor = IpsPlugin.getDefault().getImageDescriptor("/obj16/" + imageName); //$NON-NLS-1$
        }
        return imageDescriptor;
    }

    /**
     * The image of this type.
     */
    public Image getImage() {
        return IpsPlugin.getDefault().getImage(getImageDescriptor());
    }

    /**
     * Creates a new instance of a business function element of this type.
     * 
     * @param businessFunction the business function to which the created type belongs to
     * @param location the graphical location of the display element of this type 
     */
    public abstract IBFElement newBFElement(IBusinessFunction businessFunction, Point location);

    /**
     * Returns the type for the specified id. If none is found and {@link IllegalArgumentException} will be thrown.
     * 
     * @throws IllegalArgumentException if no type is found for the specified id
     */
    public final static BFElementType getType(String id) {
        if (id.equals(ACTION_INLINE.id)) {
            return ACTION_INLINE;
        }
        if (id.equals(ACTION_BUSINESSFUNCTIONCALL.id)) {
            return ACTION_BUSINESSFUNCTIONCALL;
        }
        if (id.equals(ACTION_METHODCALL.id)) {
            return ACTION_METHODCALL;
        }
        if (id.equals(DECISION.id)) {
            return DECISION;
        }
        if (id.equals(DECISION_METHODCALL.id)) {
            return BFElementType.DECISION_METHODCALL;
        }
        if (id.equals(MERGE.id)) {
            return MERGE;
        }
        if (id.equals(END.id)) {
            return END;
        }
        if (id.equals(START.id)) {
            return START;
        }
        if (id.equals(PARAMETER.id)) {
            return PARAMETER;
        }
        throw new IllegalArgumentException("Unexpected type id: " + id); //$NON-NLS-1$
    }
}
