/*******************************************************************************
 * Copyright (c) 2005-2010 Faktor Zehn AG und andere.
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

package org.faktorips.devtools.core.refactor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RefactoringParticipant;
import org.eclipse.ltk.core.refactoring.participants.SharableParticipants;
import org.faktorips.abstracttest.AbstractIpsRefactoringTest;
import org.faktorips.devtools.core.model.IIpsElement;
import org.junit.Before;
import org.junit.Test;

public class IpsRefactoringProcessorTest extends AbstractIpsRefactoringTest {

    private TestProcessor testProcessor;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        testProcessor = new TestProcessor(policyCmptType);
    }

    @Test
    public void testCheckInitialConditionsValid() throws OperationCanceledException, CoreException {
        RefactoringStatus status = testProcessor.checkInitialConditions(new NullProgressMonitor());
        assertFalse(status.hasError());
    }

    @Test
    public void testCheckInitialConditionsInvalid() throws OperationCanceledException, CoreException {
        policyCmptType.getIpsSrcFile().getCorrespondingResource().delete(true, null);
        RefactoringStatus status = testProcessor.checkInitialConditions(new NullProgressMonitor());
        assertTrue(status.hasError());
    }

    @Test
    public void testCheckFinalConditions() throws OperationCanceledException, CoreException {
        TestProcessor testProcessorSpy = spy(testProcessor);

        testProcessorSpy.checkFinalConditions(new NullProgressMonitor(), new CheckConditionsContext());

        verify(testProcessorSpy).validateUserInput(any(IProgressMonitor.class));
    }

    // Public so it can be accessed by Mockito
    public static class TestProcessor extends IpsRefactoringProcessor {

        protected TestProcessor(IIpsElement ipsElement) {
            super(ipsElement);
        }

        @Override
        protected void addIpsSrcFiles() throws CoreException {

        }

        @Override
        protected void refactorIpsModel(IProgressMonitor pm) throws CoreException {

        }

        @Override
        public String getIdentifier() {
            return null;
        }

        @Override
        public String getProcessorName() {
            return null;
        }

        @Override
        public RefactoringParticipant[] loadParticipants(RefactoringStatus status,
                SharableParticipants sharedParticipants) throws CoreException {

            return null;
        }

        @Override
        public RefactoringStatus validateUserInput(IProgressMonitor pm) throws CoreException {
            return null;
        }

        @Override
        public boolean isSourceFilesSavedRequired() {
            return false;
        }

    }

}
