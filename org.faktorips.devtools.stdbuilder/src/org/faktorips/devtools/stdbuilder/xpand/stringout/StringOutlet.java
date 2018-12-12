/*******************************************************************************
 * Copyright (c) Faktor Zehn AG. <http://www.faktorzehn.org>
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version
 * 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and
 * restrictions as well as the possibility of alternative license terms.
 *******************************************************************************/

package org.faktorips.devtools.stdbuilder.xpand.stringout;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.xpand2.output.FileHandle;
import org.eclipse.xpand2.output.Outlet;
import org.eclipse.xpand2.output.PostProcessor;
import org.eclipse.xpand2.output.VetoException;
import org.eclipse.xpand2.output.VetoStrategy;
import org.eclipse.xpand2.output.VetoStrategy2;
import org.faktorips.devtools.stdbuilder.xpand.XpandBuilder;

/**
 * This implementation of {@link Outlet} handles String output instead of file outputs. We use the
 * String output to merge and format the generated code with the existing framework. However it
 * could be a very good option to implement a {@link PostProcessor} instead to handle the merging
 * and formatting.
 * 
 * @deprecated since 3.22 because {@link XpandBuilder} is deprecated and this is only used for xpand
 */
@Deprecated
public class StringOutlet extends Outlet {

    private int filesCreated = 0;

    private int filesWrittenAndClosed = 0;

    private final Map<IPath, StringFileHandle> stringHandles = new HashMap<IPath, StringFileHandle>();

    public StringOutlet() {
        super(null);
    }

    public StringOutlet(String encoding, String name) {
        super(false, encoding, name, true, null);
    }

    @Override
    public String getPath() {
        return null;
    }

    @Override
    public void setPath(final String path) {
        // do nothing
    }

    @Override
    public FileHandle createFileHandle(final String filePath) throws VetoException {
        StringFileHandle stringHandle = new StringFileHandle(this, filePath, getFileEncoding());
        for (VetoStrategy vetoStrategy : vetoStrategies) {
            if (vetoStrategy instanceof VetoStrategy2) {
                if (((VetoStrategy2)vetoStrategy).hasVetoBeforeOpen(stringHandle)) {
                    throw new VetoException(filePath);
                }
            }
        }
        incFilesCreated();
        stringHandles.put(new Path(filePath), stringHandle);
        return stringHandle;
    }

    @Override
    public void addPostprocessor(final PostProcessor b) {
        postprocessors.add(b);
    }

    @Override
    public void addVetoStrategy(final VetoStrategy b) {
        vetoStrategies.add(b);
    }

    @Override
    public void beforeWriteAndClose(final FileHandle impl) {
        for (final Iterator<PostProcessor> iter = postprocessors.iterator(); iter.hasNext();) {
            final PostProcessor b = iter.next();
            b.beforeWriteAndClose(impl);
        }
    }

    @Override
    public void afterClose(final FileHandle impl) {
        incFilesWrittenAndClosed();
        for (final Iterator<PostProcessor> iter = postprocessors.iterator(); iter.hasNext();) {
            final PostProcessor b = iter.next();
            b.afterClose(impl);
        }
    }

    @Override
    public int getFilesCreated() {
        return filesCreated;
    }

    @Override
    public int getFilesWrittenAndClosed() {
        return filesWrittenAndClosed;
    }

    /**
     * @return Returns the stringHandles.
     */
    public String getContent(IPath path) {
        StringFileHandle stringFileHandle = stringHandles.get(path);
        if (stringFileHandle != null) {
            return stringFileHandle.getOutput();
        } else {
            return null;
        }
    }

    /**
     * Clear the internally cached string handles. After calling this method you cannot access any
     * previously stored output!
     */
    public void clear() {
        stringHandles.clear();
    }

}
