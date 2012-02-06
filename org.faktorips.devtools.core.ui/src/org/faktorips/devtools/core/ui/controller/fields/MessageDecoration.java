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

package org.faktorips.devtools.core.ui.controller.fields;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.widgets.Section;
import org.faktorips.util.message.Message;
import org.faktorips.util.message.MessageList;

/**
 * This class uses a {@link ControlDecoration} to paint a marker next to a {@link Control} to
 * indicate a problem. The problem is triggered by setting a {@link MessageList}.
 * 
 * @author dirmeier
 */
public class MessageDecoration {

    private ControlDecoration controlDecoration;

    /**
     * Installing a {@link ControlDecoration} to the given control to the specified position.
     * 
     * @param control The control used to paint the message decoration to.
     * @param position The position of the decoration
     * @see ControlDecoration
     */
    public MessageDecoration(Control control, int position) {
        controlDecoration = new ControlDecoration(control, position, getSection(control.getParent()));
    }

    /**
     * Setting a message list for this decoration. The first message with the highest severity is
     * displayed as marker decoration with an hover tooltip. The message list may be empty or null
     * to hide the decoration.
     * 
     * @param list The message list to decorate the control or null to hide the decoration
     */
    public void setMessageList(MessageList list) {
        if (list != null) {
            FieldDecoration decoration = FieldDecorationRegistry.getDefault().getFieldDecoration(
                    mapSeverityToFieldDecoration(list.getSeverity()));
            if (decoration != null) {
                controlDecoration.setImage(decoration.getImage());
                Message messageWithHighestSeverity = list.getMessageWithHighestSeverity();
                controlDecoration.setDescriptionText(messageWithHighestSeverity != null ? messageWithHighestSeverity
                        .getText() : decoration.getDescription());
                controlDecoration.show();
                return;
            }
        }
        controlDecoration.hide();
    }

    /**
     * Searching for a section where this control is painted in. We need to to get this section for
     * not painting the marker if the section may be invisible (closed).
     * 
     * @param composite The composite where to start searching for a section.
     * @return A Section which is the parent of the composite (recoursive) or null if there is no
     *         section.
     */
    private Section getSection(Composite composite) {
        if (composite instanceof Section) {
            return (Section)composite;
        } else if (composite != null) {
            return getSection(composite.getParent());
        } else {
            return null;
        }
    }

    /**
     * Mapping the severity of {@link Message} to the {@link FieldDecorationRegistry} id.
     * 
     * @param severity The {@link Message} severity we want to map
     * 
     * @return the id of the {@link FieldDecoration}
     */
    private String mapSeverityToFieldDecoration(int severity) {
        switch (severity) {
            case Message.ERROR:
                return FieldDecorationRegistry.DEC_ERROR;
            case Message.WARNING:
                return FieldDecorationRegistry.DEC_WARNING;
            case Message.INFO:
                return FieldDecorationRegistry.DEC_INFORMATION;
            default:
                return null;
        }
    }

}