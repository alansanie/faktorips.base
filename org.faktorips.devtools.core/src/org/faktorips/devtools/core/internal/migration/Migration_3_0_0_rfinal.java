/*******************************************************************************
 * Copyright (c) Faktor Zehn AG. <http://www.faktorzehn.org>
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version
 * 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and
 * restrictions as well as the possibility of alternative license terms.
 *******************************************************************************/

package org.faktorips.devtools.core.internal.migration;

import org.faktorips.devtools.core.model.ipsproject.IIpsProject;

/**
 * Migration from Faktor-IPS Version 3.0.0.rfinal to 3.0.1.rfinal
 * 
 * @author Joerg Ortmann
 */
public class Migration_3_0_0_rfinal extends EmptyMigration {

    public Migration_3_0_0_rfinal(IIpsProject projectToMigrate, String featureId) {
        super(projectToMigrate, featureId);
    }

    @Override
    public String getTargetVersion() {
        return "3.0.1.rfinal"; //$NON-NLS-1$
    }

}
