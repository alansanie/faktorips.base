/*******************************************************************************
 * Copyright (c) 2005-2012 Faktor Zehn AG und andere.
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

package org.faktorips.devtools.core.builder.flidentifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.faktorips.datatype.ListOfTypeDatatype;
import org.faktorips.devtools.core.builder.flidentifier.ast.AssociationNode;
import org.faktorips.devtools.core.builder.flidentifier.ast.IndexBasedAssociationNode;
import org.faktorips.devtools.core.builder.flidentifier.ast.InvalidIdentifierNode;
import org.faktorips.devtools.core.builder.flidentifier.ast.QualifiedAssociationNode;
import org.faktorips.devtools.core.model.ipsobject.IIpsSrcFile;
import org.faktorips.devtools.core.model.pctype.IPolicyCmptType;
import org.faktorips.devtools.core.model.productcmpt.IProductCmpt;
import org.faktorips.devtools.core.model.type.IAssociation;
import org.faktorips.devtools.core.model.type.IType;
import org.faktorips.fl.ExprCompiler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AssociationParserTest extends AbstractParserTest {

    private static final String RUNTIME_ID = "RuntimeID";

    private static final String MY_ASSOCIATION = "myAssociation";

    private static final String ANY_ASSOCIATION = "anyAssociation";

    private static final int MY_INDEX = 12;

    private static final String MY_QUALIFIER = "myQualifier";

    private static final String INDEX_BASED_ASSOCIATION = MY_ASSOCIATION + "[" + MY_INDEX + "]";

    private static final String QUALIFIED_ASSOCIATION = MY_ASSOCIATION + "[\"" + MY_QUALIFIER + "\"]";

    @Mock
    private IAssociation assciation;

    @Mock
    private IPolicyCmptType policyCmptType;

    @Mock
    private IType targetType;

    private AssociationParser associationParser;

    @Before
    public void createAssociationParser() throws Exception {
        associationParser = new AssociationParser(getExpression(), getIpsProject());
    }

    @Before
    public void mockAssociation() throws Exception {
        when(policyCmptType.findAssociation(MY_ASSOCIATION, getIpsProject())).thenReturn(assciation);
        when(assciation.getName()).thenReturn(MY_ASSOCIATION);
        when(assciation.findTarget(getIpsProject())).thenReturn(targetType);
    }

    @Test
    public void testParse_findAssociation1To1() throws Exception {
        when(assciation.is1ToMany()).thenReturn(false);

        AssociationNode node = (AssociationNode)associationParser.parse(MY_ASSOCIATION, policyCmptType);

        assertEquals(assciation, node.getAssociation());
        assertEquals(targetType, node.getDatatype());
    }

    @Test
    public void testParse_findAssociation1ToMany() throws Exception {
        when(assciation.is1ToMany()).thenReturn(true);

        AssociationNode node = (AssociationNode)associationParser.parse(MY_ASSOCIATION, policyCmptType);

        assertEquals(assciation, node.getAssociation());
        assertEquals(new ListOfTypeDatatype(targetType), node.getDatatype());
    }

    @Test
    public void testParse_findAssociation1To1FromMany() throws Exception {
        when(assciation.is1ToMany()).thenReturn(false);

        AssociationNode node = (AssociationNode)associationParser.parse(MY_ASSOCIATION, new ListOfTypeDatatype(
                policyCmptType));

        assertEquals(assciation, node.getAssociation());
        assertEquals(new ListOfTypeDatatype(targetType), node.getDatatype());
    }

    @Test
    public void testParse_findAssociation1ToManyFromMany() throws Exception {
        when(assciation.is1ToMany()).thenReturn(true);

        AssociationNode node = (AssociationNode)associationParser.parse(MY_ASSOCIATION, new ListOfTypeDatatype(
                policyCmptType));

        assertEquals(assciation, node.getAssociation());
        assertEquals(new ListOfTypeDatatype(targetType), node.getDatatype());
    }

    private IPolicyCmptType initSourceFile() throws Exception {
        return initSourceFile(RUNTIME_ID);
    }

    private IPolicyCmptType initSourceFileNoRuntimeID() throws Exception {
        return initSourceFile(null);
    }

    private IPolicyCmptType initSourceFile(String runtimeID) throws Exception {
        IPolicyCmptType type = mock(IPolicyCmptType.class);
        IProductCmpt productCmpt = mock(IProductCmpt.class);
        when(assciation.findTarget(getIpsProject())).thenReturn(type);
        when(type.findProductCmptType(getIpsProject())).thenReturn(getProductCmptType());
        IIpsSrcFile sourceFile = mock(IIpsSrcFile.class);
        IIpsSrcFile[] ipsSourceFiles = new IIpsSrcFile[] { sourceFile };
        when(getIpsProject().findAllProductCmptSrcFiles(getProductCmptType(), true)).thenReturn(ipsSourceFiles);
        when(sourceFile.getIpsObjectName()).thenReturn(MY_QUALIFIER);
        when(sourceFile.getIpsObject()).thenReturn(productCmpt);
        when(productCmpt.getRuntimeId()).thenReturn(runtimeID);
        when(productCmpt.findPolicyCmptType(getIpsProject())).thenReturn(policyCmptType);
        return type;
    }

    @Test
    public void testParse_findAssociationQualified1To1() throws Exception {
        when(assciation.is1ToMany()).thenReturn(true);
        when(assciation.is1ToManyIgnoringQualifier()).thenReturn(false);
        initSourceFile();

        QualifiedAssociationNode node = (QualifiedAssociationNode)associationParser.parse(QUALIFIED_ASSOCIATION,
                policyCmptType);

        assertEquals(assciation, node.getAssociation());
        assertEquals(RUNTIME_ID, node.getRuntimeID());
        assertEquals(policyCmptType, node.getDatatype());
    }

    @Test
    public void testParse_findAssociationQualified1ToMany() throws Exception {
        when(assciation.is1ToMany()).thenReturn(true);
        when(assciation.is1ToManyIgnoringQualifier()).thenReturn(true);
        IPolicyCmptType type = initSourceFile();

        QualifiedAssociationNode node = (QualifiedAssociationNode)associationParser.parse(QUALIFIED_ASSOCIATION,
                policyCmptType);

        assertEquals(assciation, node.getAssociation());
        assertEquals(RUNTIME_ID, node.getRuntimeID());
        assertEquals(new ListOfTypeDatatype(type), node.getDatatype());
    }

    @Test
    public void testParse_findAssociationQualified1To1FromMany() throws Exception {
        when(assciation.is1ToMany()).thenReturn(true);
        when(assciation.is1ToManyIgnoringQualifier()).thenReturn(false);
        IPolicyCmptType type = initSourceFile();

        QualifiedAssociationNode node = (QualifiedAssociationNode)associationParser.parse(QUALIFIED_ASSOCIATION,
                new ListOfTypeDatatype(policyCmptType));

        assertEquals(assciation, node.getAssociation());
        assertEquals(RUNTIME_ID, node.getRuntimeID());
        assertEquals(new ListOfTypeDatatype(type), node.getDatatype());
    }

    @Test
    public void testParse_findAssociationQualified_NoRuntimeID() throws Exception {
        when(assciation.is1ToMany()).thenReturn(true);
        when(assciation.is1ToManyIgnoringQualifier()).thenReturn(false);
        initSourceFileNoRuntimeID();

        InvalidIdentifierNode node = (InvalidIdentifierNode)associationParser.parse(QUALIFIED_ASSOCIATION,
                policyCmptType);
        assertEquals(ExprCompiler.UNKNOWN_QUALIFIER, node.getMessage().getCode());
    }

    @Test
    public void testParse_findAssociationIndex() throws Exception {
        IndexBasedAssociationNode node = (IndexBasedAssociationNode)associationParser.parse(INDEX_BASED_ASSOCIATION,
                policyCmptType);

        assertEquals(assciation, node.getAssociation());
        assertEquals(MY_INDEX, node.getIndex());
        assertEquals(targetType, node.getDatatype());
    }

    @Test
    public void testParse_invalidAssociationTo1Index() throws Exception {
        when(assciation.is1To1()).thenReturn(true);

        InvalidIdentifierNode node = (InvalidIdentifierNode)associationParser.parse(INDEX_BASED_ASSOCIATION,
                new ListOfTypeDatatype(policyCmptType));

        assertEquals(ExprCompiler.NO_INDEX_FOR_1TO1_ASSOCIATION, node.getMessage().getCode());
    }

    @Test
    public void testParse_associationInvalidIndex() throws Exception {
        InvalidIdentifierNode node = (InvalidIdentifierNode)associationParser.parse(MY_ASSOCIATION + "[asd]",
                new ListOfTypeDatatype(policyCmptType));

        assertEquals(ExprCompiler.UNKNOWN_QUALIFIER, node.getMessage().getCode());
    }

    @Test
    public void testParse_findAssociationIndexFromList() throws Exception {
        IndexBasedAssociationNode node = (IndexBasedAssociationNode)associationParser.parse(INDEX_BASED_ASSOCIATION,
                new ListOfTypeDatatype(policyCmptType));

        assertEquals(assciation, node.getAssociation());
        assertEquals(MY_INDEX, node.getIndex());
        assertEquals(targetType, node.getDatatype());
    }

    @Test
    public void testParse_findAssociation1ToManyIndexedFromList() throws Exception {
        when(assciation.is1ToMany()).thenReturn(true);
        when(assciation.is1ToManyIgnoringQualifier()).thenReturn(true);

        IndexBasedAssociationNode node = (IndexBasedAssociationNode)associationParser.parse(INDEX_BASED_ASSOCIATION,
                new ListOfTypeDatatype(policyCmptType));

        assertEquals(assciation, node.getAssociation());
        assertEquals(MY_INDEX, node.getIndex());
        assertEquals(targetType, node.getDatatype());
    }

    @Test
    public void testParse_wrongType() throws Exception {
        AssociationNode node = (AssociationNode)associationParser.parse(MY_ASSOCIATION, getProductCmptType());

        assertNull(node);
    }

    @Test
    public void testParse_findNoAssociation() throws Exception {
        AssociationNode node = (AssociationNode)associationParser.parse(ANY_ASSOCIATION, getProductCmptType());

        assertNull(node);
    }

}
