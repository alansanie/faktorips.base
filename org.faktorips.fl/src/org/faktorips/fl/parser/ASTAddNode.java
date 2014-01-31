/*******************************************************************************
 * Copyright (c) Faktor Zehn AG. <http://www.faktorzehn.org>
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version
 * 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and
 * restrictions as well as the possibility of alternative license terms.
 *******************************************************************************/

/* Generated By:JJTree: Do not edit this line. ASTAddNode.java */

package org.faktorips.fl.parser;

public class ASTAddNode extends SimpleNode {
    public ASTAddNode(int id) {
        super(id);
    }

    public ASTAddNode(FlParser p, int id) {
        super(p, id);
    }

    /** Accept the visitor. **/
    @Override
    public Object jjtAccept(FlParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
