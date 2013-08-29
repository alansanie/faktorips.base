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

package org.faktorips.fl.operations;

import org.faktorips.codegen.JavaCodeFragment;
import org.faktorips.datatype.Datatype;
import org.faktorips.fl.AbstractCompilationResult;
import org.faktorips.fl.BinaryOperation;
import org.faktorips.fl.CompilationResult;
import org.faktorips.fl.CompilationResultImpl;
import org.faktorips.fl.Operation;

/**
 * Abstract implementation of {@link BinaryOperation} for {@link JavaCodeFragment Java code}
 * generating operations.
 */
public abstract class AbstractBinaryJavaOperation extends AbstractBinaryOperation<JavaCodeFragment> {

    /**
     * Creates a new binary operation for the indicated {@link Operation}.
     */
    public AbstractBinaryJavaOperation(Operation operation) {
        super(operation);
    }

    /**
     * Creates a new binary operation for the indicated left hand side and right hand side
     * {@link Datatype data types}.
     */
    public AbstractBinaryJavaOperation(String operator, Datatype lhs, Datatype rhs) {
        super(operator, lhs, rhs);
    }

    public AbstractCompilationResult<JavaCodeFragment> generate(CompilationResult<JavaCodeFragment> lhs,
            CompilationResult<JavaCodeFragment> rhs) {
        CompilationResultImpl result = generate((CompilationResultImpl)lhs, (CompilationResultImpl)rhs);
        result.addIdentifiersUsed(((CompilationResultImpl)lhs).getIdentifiersUsedAsSet());
        result.addIdentifiersUsed(((CompilationResultImpl)rhs).getIdentifiersUsedAsSet());
        return result;
    }

    /**
     * Generates the combined {@link CompilationResult} from the given operands.
     * 
     * @param lhs the left hand side operand
     * @param rhs the right hand side operand
     * @return the given operands combined with this operation's operator
     */
    public abstract CompilationResultImpl generate(CompilationResultImpl lhs, CompilationResultImpl rhs);

}
