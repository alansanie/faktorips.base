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

package org.faktorips.devtools.core.ui.controlfactories;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.faktorips.datatype.Datatype;
import org.faktorips.datatype.PrimitiveBooleanDatatype;
import org.faktorips.datatype.ValueDatatype;
import org.faktorips.datatype.classtypes.BooleanDatatype;
import org.faktorips.devtools.core.IpsPlugin;
import org.faktorips.devtools.core.model.ipsproject.IIpsProject;
import org.faktorips.devtools.core.model.valueset.IEnumValueSet;
import org.faktorips.devtools.core.model.valueset.IValueSet;
import org.faktorips.devtools.core.ui.IpsUIPlugin;
import org.faktorips.devtools.core.ui.UIToolkit;
import org.faktorips.devtools.core.ui.ValueDatatypeControlFactory;
import org.faktorips.devtools.core.ui.controller.EditField;
import org.faktorips.devtools.core.ui.controller.fields.RadioButtonGroupField;
import org.faktorips.devtools.core.ui.controls.RadioButtonGroup;
import org.faktorips.devtools.core.ui.table.ComboCellEditor;
import org.faktorips.devtools.core.ui.table.IpsCellEditor;
import org.faktorips.devtools.core.ui.table.TableViewerTraversalStrategy;

/**
 * A control factory for the data types boolean and primitive boolean.
 * 
 * @author Joerg Ortmann
 */
public class BooleanControlFactory extends ValueDatatypeControlFactory {

    public BooleanControlFactory() {
        super();
    }

    @Override
    public boolean isFactoryFor(ValueDatatype datatype) {
        return Datatype.BOOLEAN.equals(datatype) || Datatype.PRIMITIVE_BOOLEAN.equals(datatype);
    }

    @Override
    public EditField<String> createEditField(UIToolkit toolkit,
            Composite parent,
            ValueDatatype datatype,
            IValueSet valueSet,
            IIpsProject ipsProject) {
        Composite comp = (Composite)createControl(toolkit, parent, datatype, valueSet, ipsProject);
        // We know that the returned value contains a RadioButtonGroup<String>, but we can't cast
        // safely, hence we ignore the warning.
        @SuppressWarnings("unchecked")
        RadioButtonGroup<String> radioButtonGroup = (RadioButtonGroup<String>)comp.getData();
        if (valueSet instanceof IEnumValueSet) {
            for (Button b : radioButtonGroup.getRadioButtons()) {
                b.setEnabled(false);
            }
            for (String value : ((IEnumValueSet)valueSet).getValues()) {
                for (Button b : radioButtonGroup.getRadioButtons()) {
                    if (b.getText()
                            .equals(IpsUIPlugin.getDefault().getDatatypeFormatter().formatValue(datatype, value))) {
                        b.setEnabled(true);
                    }
                }
            }
        }
        RadioButtonGroupField<String> groupField = new RadioButtonGroupField<String>(radioButtonGroup);
        groupField.setNullOption(IpsPlugin.getDefault().getIpsPreferences().getNullPresentation());
        return groupField;

    }

    @Override
    public Control createControl(UIToolkit toolkit,
            Composite parent,
            ValueDatatype datatype,
            IValueSet valueSet,
            IIpsProject ipsProject) {
        return toolkit.createRadioSetForBoolean(parent, !datatype.isPrimitive(), getTrueValue(), getFalseValue());
    }

    private Combo createComboControl(UIToolkit toolkit, Composite parent, ValueDatatype datatype) {
        return toolkit.createComboForBoolean(parent, !datatype.isPrimitive(), getTrueValue(), getFalseValue());
    }

    /**
     * @deprecated use
     *             {@link #createTableCellEditor(UIToolkit, ValueDatatype, IValueSet, TableViewer, int, IIpsProject)}
     *             instead.
     */
    @Deprecated
    @Override
    public IpsCellEditor createCellEditor(UIToolkit toolkit,
            ValueDatatype dataType,
            IValueSet valueSet,
            TableViewer tableViewer,
            int columnIndex,
            IIpsProject ipsProject) {

        return createTableCellEditor(toolkit, dataType, valueSet, tableViewer, columnIndex, ipsProject);
    }

    /**
     * Creates a <code>ComboCellEditor</code> containing a <code>Combo</code> using
     * {@link #createControl(UIToolkit, Composite, ValueDatatype, IValueSet, IIpsProject)}.
     */
    @Override
    public IpsCellEditor createTableCellEditor(UIToolkit toolkit,
            ValueDatatype dataType,
            IValueSet valueSet,
            TableViewer tableViewer,
            int columnIndex,
            IIpsProject ipsProject) {
        IpsCellEditor cellEditor = createComboCellEditor(toolkit, dataType, tableViewer.getTable());
        TableViewerTraversalStrategy strat = new TableViewerTraversalStrategy(cellEditor, tableViewer, columnIndex);
        strat.setRowCreating(true);
        cellEditor.setTraversalStrategy(strat);
        return cellEditor;
    }

    private IpsCellEditor createComboCellEditor(UIToolkit toolkit, ValueDatatype dataType, Composite parent) {
        Combo comboControl = createComboControl(toolkit, parent, dataType);
        IpsCellEditor tableCellEditor = new ComboCellEditor(comboControl);
        // stores the boolean datatype object as data object in the combo,
        // to indicate that the to be displayed data will be mapped as boolean
        if (Datatype.PRIMITIVE_BOOLEAN.equals(dataType)) {
            comboControl.setData(new PrimitiveBooleanDatatype());
        } else {
            comboControl.setData(new BooleanDatatype());
        }
        return tableCellEditor;
    }

    @Override
    public int getDefaultAlignment() {
        return SWT.LEFT;
    }

    public static String getTrueValue() {
        return IpsUIPlugin.getDefault().getDatatypeFormatter()
                .formatValue(Datatype.PRIMITIVE_BOOLEAN, Boolean.toString(true));
    }

    public static String getFalseValue() {
        return IpsUIPlugin.getDefault().getDatatypeFormatter()
                .formatValue(Datatype.PRIMITIVE_BOOLEAN, Boolean.toString(false));
    }
}
