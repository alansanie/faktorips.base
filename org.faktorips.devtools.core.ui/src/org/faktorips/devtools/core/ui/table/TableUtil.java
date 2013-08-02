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

package org.faktorips.devtools.core.ui.table;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 * A collection of utility methods for Tables.
 */
public class TableUtil {

    private TableUtil() {
        // Prevent instantiation by making the constructor private.
    }

    /**
     * Increases the height of a table row. This might be necessary because otherwise control
     * elements won't be displayed correctly.
     * 
     * @param table the table, which rows needs to be resized
     * @param numOfColumns number of columns of the table
     * @param amount number of pixel to increase the height by
     */
    public static void increaseHeightOfTableRows(Table table, final int numOfColumns, final int amount) {
        // add paint lister to increase the height of the table row.
        Listener paintListener = new PaintListener(amount, numOfColumns);
        table.addListener(SWT.MeasureItem, paintListener);
    }

    private static final class PaintListener implements Listener {
        private final int amount;
        private final int numOfColumns;

        private PaintListener(int amount, int numOfColumns) {
            this.amount = amount;
            this.numOfColumns = numOfColumns;
        }

        @Override
        public void handleEvent(Event event) {
            if (event.type == SWT.MeasureItem) {
                if (numOfColumns == 0) {
                    return;
                }
                TableItem item = (TableItem)event.item;
                // column 0 will be used to determine the height,
                // <code>event.index<code> couldn't be used because it is only available
                // @since 3.2, that's ok because the height is always the same, even if the
                // column contains no text, the height only depends on the font
                String text = getText(item, 0);
                Point size = event.gc.textExtent(text);
                // the height will be increased by amount pixels.
                event.height = Math.max(event.height, size.y + amount);
            }
        }

        String getText(TableItem item, int column) {
            String text = item.getText(column);
            return text;
        }
    }
}