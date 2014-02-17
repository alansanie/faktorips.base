/*******************************************************************************
 * Copyright (c) Faktor Zehn AG. <http://www.faktorzehn.org>
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version
 * 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and
 * restrictions as well as the possibility of alternative license terms.
 *******************************************************************************/
package org.faktorips.devtools.core.ui.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.faktorips.devtools.core.internal.model.ipsproject.SupportedLanguage;
import org.faktorips.devtools.core.model.IIpsElement;
import org.faktorips.devtools.core.model.ipsobject.IIpsObject;
import org.faktorips.devtools.core.model.ipsobject.IIpsObjectPartContainer;
import org.faktorips.devtools.core.model.ipsobject.ILabel;
import org.faktorips.devtools.core.model.ipsobject.ILabeledElement;
import org.faktorips.devtools.core.model.ipsproject.IIpsProject;
import org.faktorips.devtools.core.model.ipsproject.IIpsProjectProperties;
import org.faktorips.util.message.Message;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class UiMessageTest {

    private static final String NAME1 = "name1";

    private static final String LABEL1 = "label1";

    private static final String CAPTION1 = "caption1";

    private static final String CAPTION2 = "caption2";

    @Mock
    private IIpsObjectPartContainer part1;

    @Mock
    private IIpsObjectPartContainer part2;

    @Mock
    private IIpsProject ipsProject;

    private Message message = new Message("ABC", "text", Message.INFO);

    private UiMessage uiMessage = new UiMessage(message);

    @Before
    public void mockProjectAndObjects() {
        IIpsProjectProperties properties = mock(IIpsProjectProperties.class);
        when(properties.getDefaultLanguage()).thenReturn(new SupportedLanguage(Locale.GERMAN));
        when(ipsProject.getReadOnlyProperties()).thenReturn(properties);
        when(part1.getIpsProject()).thenReturn(ipsProject);
        when(part2.getIpsProject()).thenReturn(ipsProject);
        when(part1.getParent()).thenReturn(part2);
    }

    @Test
    public void testGetCaptionName_noCaption() throws Exception {
        String captionName = uiMessage.getCaptionName(part1);

        assertTrue(StringUtils.isEmpty(captionName));
    }

    @Test
    public void testGetCaptionName_directCaption() throws Exception {
        when(part1.getCaption(Locale.GERMAN)).thenReturn(CAPTION1);

        String captionName = uiMessage.getCaptionName(part1);

        assertEquals(CAPTION1, captionName);
    }

    @Test
    public void testGetCaptionName_parentCaption() throws Exception {
        when(part2.getCaption(Locale.GERMAN)).thenReturn(CAPTION2);

        String captionName = uiMessage.getCaptionName(part1);

        assertEquals(CAPTION2, captionName);
    }

    @Test
    public void testGetPrefix() throws Exception {
        String prefix = uiMessage.getPrefix();

        assertTrue(prefix.isEmpty());
    }

    @Test
    public void testGetPrefix_caption() throws Exception {
        message = new Message("ABC", "text", Message.INFO, part1);
        uiMessage = new UiMessage(message);
        when(part2.getCaption(Locale.GERMAN)).thenReturn(CAPTION2);

        String prefix = uiMessage.getPrefix();

        assertEquals(CAPTION2 + UiMessage.SEPERATOR, prefix);
    }

    @Test
    public void testGetPrefix_ipsObject() throws Exception {
        IIpsObject ipsObject = mock(IIpsObject.class);
        message = new Message("ABC", "text", Message.INFO, ipsObject);
        uiMessage = new UiMessage(message);
        when(part2.getCaption(Locale.GERMAN)).thenReturn(CAPTION2);

        String prefix = uiMessage.getPrefix();

        assertTrue(prefix.isEmpty());
    }

    @Test
    public void testGetPrefix_label() throws Exception {
        ILabel label = mock(ILabel.class);
        when(label.getValue()).thenReturn(LABEL1);
        ILabeledElement labeledElement = mock(ILabeledElement.class);
        when(labeledElement.getIpsProject()).thenReturn(ipsProject);
        when(labeledElement.getLabel(Locale.GERMAN)).thenReturn(label);
        message = new Message("ABC", "text", Message.INFO, labeledElement);
        uiMessage = new UiMessage(message);

        String prefix = uiMessage.getPrefix();

        assertEquals(LABEL1 + UiMessage.SEPERATOR, prefix);
    }

    @Test
    public void testGetPrefix_name() throws Exception {
        IIpsElement ipsElement = mock(IIpsElement.class);
        message = new Message("ABC", "text", Message.INFO, ipsElement);
        uiMessage = new UiMessage(message);
        when(ipsElement.getName()).thenReturn(NAME1);

        String prefix = uiMessage.getPrefix();

        assertEquals(NAME1 + UiMessage.SEPERATOR, prefix);
    }

}