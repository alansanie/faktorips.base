package org.faktorips.devtools.core.internal.model.tablestructure;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.faktorips.devtools.core.internal.model.IpsObject;
import org.faktorips.devtools.core.model.IIpsElement;
import org.faktorips.devtools.core.model.IIpsObjectPart;
import org.faktorips.devtools.core.model.IpsObjectType;
import org.faktorips.devtools.core.model.IIpsPackageFragment;
import org.faktorips.devtools.core.model.IIpsSrcFile;
import org.faktorips.devtools.core.model.tablestructure.IColumn;
import org.faktorips.devtools.core.model.tablestructure.IColumnRange;
import org.faktorips.devtools.core.model.tablestructure.IForeignKey;
import org.faktorips.devtools.core.model.tablestructure.IKey;
import org.faktorips.devtools.core.model.tablestructure.IKeyItem;
import org.faktorips.devtools.core.model.tablestructure.ITableAccessFunction;
import org.faktorips.devtools.core.model.tablestructure.ITableStructure;
import org.faktorips.devtools.core.model.tablestructure.IUniqueKey;
import org.faktorips.devtools.core.util.ListElementMover;
import org.faktorips.util.ArgumentCheck;
import org.w3c.dom.Element;


/**
 *
 */
public class TableStructure extends IpsObject implements ITableStructure {
    
    
    private List columns = new ArrayList(2);
    private List ranges = new ArrayList(0);
    private List uniqueKeys = new ArrayList(1);
    private List foreignKeys = new ArrayList(0);
    
    public TableStructure(IIpsSrcFile file) {
        super(file);
    }
    
    /**
     * Constructor for testing purposes.
     */
    protected TableStructure() {
        super();
    }
    
    /**
     * Overridden IMethod.
     *
     * @see org.faktorips.devtools.core.model.IIpsElement#getChildren()
     */
    public IIpsElement[] getChildren() {
        int numOfChildren = getNumOfColumns()
        	+ getNumOfRanges()
        	+ getNumOfUniqueKeys()
        	+ getNumOfForeignKeys();
	    IIpsElement[] childrenArray = new IIpsElement[numOfChildren];
	    List childrenList = new ArrayList(numOfChildren);
	    childrenList.addAll(columns);
	    childrenList.addAll(ranges);
	    childrenList.addAll(uniqueKeys);
	    childrenList.addAll(foreignKeys);
	    childrenList.toArray(childrenArray);
	    return childrenArray;
    }
    
    /** 
     * Overridden method.
     * @see org.faktorips.devtools.core.model.tablestructure.ITableStructure#getColumns()
     */
    public IColumn[] getColumns() {
        IColumn[] c = new IColumn[columns.size()];
        columns.toArray(c);
        return c;
    }
    
    /**
     * Overridden method.
     * @see org.faktorips.devtools.core.model.tablestructure.ITableStructure#getColumn(java.lang.String)
     */
    public IColumn getColumn(String name) {
        for (Iterator it=columns.iterator(); it.hasNext();) {
            IColumn column = (IColumn)it.next();
            if (column.getName().equals(name)) {
                return column;
            }
        }
        return null;
    }
    
    /** 
     * Overridden method.
     * @see org.faktorips.devtools.core.model.tablestructure.ITableStructure#getNumOfColumns()
     */
    public int getNumOfColumns() {
        return columns.size();
    }
    
    /** 
     * Overridden method.
     * @see org.faktorips.devtools.core.model.tablestructure.ITableStructure#newColumn()
     */
    public IColumn newColumn() {
        IColumn newColumn = newColumnInternal(getNextPartId());
        updateSrcFile();
        return newColumn;
    }
    
    /**
     * Overridden method.
     * @see org.faktorips.devtools.core.model.tablestructure.ITableStructure#moveColumns(int[], boolean)
     */
    public int[] moveColumns(int[] indexes, boolean up) {
        ListElementMover mover = new ListElementMover(columns);
        return mover.move(indexes, up);
    }
    
    private IColumn newColumnInternal(int id) {
        IColumn newColumn = new Column(this, id);
        columns.add(newColumn);
        return newColumn;
    }

    void removeColumn(IColumn column) {
        columns.remove(column);
        updateSrcFile();
    }
    
    int getColumnIndex(IColumn column) {
        for (int i=0; i<columns.size(); i++) {
            if (columns.get(i)==column) {
                return i;
            }
        }
        throw new RuntimeException("Can't get index for column " + column);
    }
    
    /**
     * Overridden method.
     * @see org.faktorips.devtools.core.model.tablestructure.ITableStructure#getRanges()
     */ 
    public IColumnRange[] getRanges() {
        IColumnRange[] c = new IColumnRange[ranges.size()];
        ranges.toArray(c);
        return c;
    }
    
    /**
     * Overridden method.
     * @see org.faktorips.devtools.core.model.tablestructure.ITableStructure#getRange(java.lang.String)
     */
    public IColumnRange getRange(String name) {
        for (Iterator it=ranges.iterator(); it.hasNext();) {
            IColumnRange range = (IColumnRange)it.next();
            if (range.getName().equals(name)) {
                return range;
            }
        }
        return null;
    }
    
    /**
     * Overridden method.
     * @see org.faktorips.devtools.core.model.tablestructure.ITableStructure#getNumOfRanges()
     */ 
    public int getNumOfRanges() {
        return ranges.size();
    }
    
    /** 
     * Overridden method.
     * @see org.faktorips.devtools.core.model.tablestructure.ITableStructure#newColumn()
     */
    public IColumnRange newRange() {
        IColumnRange newRange = newColumnRangeInternal(getNextPartId());
        updateSrcFile();
        return newRange;
    }
    
    /**
     * Overridden method.
     * @see org.faktorips.devtools.core.model.tablestructure.ITableStructure#moveRanges(int[], boolean)
     */
    public int[] moveRanges(int[] indexes, boolean up) {
        ListElementMover mover = new ListElementMover(ranges);
        return mover.move(indexes, up);
    }
    
    private IColumnRange newColumnRangeInternal(int id) {
        IColumnRange newRange = new ColumnRange(this, id);
        ranges.add(newRange);
        return newRange;
    }

    void removeRange(IColumnRange range) {
        ranges.remove(range);
        updateSrcFile();
    }
    
    /**
     * Overridden method.
     * @see org.faktorips.devtools.core.model.tablestructure.ITableStructure#getUniqueKeys()
     */ 
    public IUniqueKey[] getUniqueKeys() {
        IUniqueKey[] keys = new IUniqueKey[uniqueKeys.size()];
        uniqueKeys.toArray(keys);
        return keys;
    }
    
    /**
     * Overridden method.
     * @see org.faktorips.devtools.core.model.tablestructure.ITableStructure#getUniqueKey(java.lang.String)
     */
    public IUniqueKey getUniqueKey(String name) {
        for (Iterator it=uniqueKeys.iterator(); it.hasNext();) {
            IUniqueKey key = (IUniqueKey)it.next();
            if (key.getName().equals(name)) {
                return key;
            }
        }
        return null;
    }
    
    /**
     * Overridden method.
     * @see org.faktorips.devtools.core.model.tablestructure.ITableStructure#getNumOfUniqueKeys()
     */
    public int getNumOfUniqueKeys() {
        return uniqueKeys.size();
    }
    
    /**
     * Overridden method.
     * @see org.faktorips.devtools.core.model.tablestructure.ITableStructure#newUniqueKey()
     */ 
    public IUniqueKey newUniqueKey() {
        IUniqueKey newUniqueKey = newUniqueKeyInternal(getNextPartId());
        updateSrcFile();
        return newUniqueKey;
    }

    /**
     * Overridden method.
     * @see org.faktorips.devtools.core.model.tablestructure.ITableStructure#moveUniqueKeys(int[], boolean)
     */
    public int[] moveUniqueKeys(int[] indexes, boolean up) {
        ListElementMover mover = new ListElementMover(uniqueKeys);
        return mover.move(indexes, up);
    }
    
    private IUniqueKey newUniqueKeyInternal(int id) {
        IUniqueKey newUniqueKey = new UniqueKey(this, id);
        uniqueKeys.add(newUniqueKey);
        return newUniqueKey;
    }

    void removeUniqueKey(IUniqueKey key) {
        uniqueKeys.remove(key);
        updateSrcFile();
    }
    
    /**
     * Overridden method.
     * @see org.faktorips.devtools.core.model.tablestructure.ITableStructure#getForeignKeys()
     */
    public IForeignKey[] getForeignKeys() {
        IForeignKey[] keys = new IForeignKey[foreignKeys.size()];
        foreignKeys.toArray(keys);
        return keys;
    }
    
    /**
     * Overridden method.
     * @see org.faktorips.devtools.core.model.tablestructure.ITableStructure#getForeignKey(java.lang.String)
     */
    public IForeignKey getForeignKey(String name) {
        for (Iterator it=foreignKeys.iterator(); it.hasNext();) {
            IForeignKey key = (IForeignKey)it.next();
            if (key.getName().equals(name)) {
                return key;
            }
        }
        return null;
    }
    
    /**
     * Overridden method.
     * @see org.faktorips.devtools.core.model.tablestructure.ITableStructure#getNumOfForeignKeys()
     */
    public int getNumOfForeignKeys() {
        return foreignKeys.size();
    }
    
    /**
     * Overridden method.
     * @see org.faktorips.devtools.core.model.tablestructure.ITableStructure#newUniqueKey()
     */ 
    public IForeignKey newForeignKey() {
        IForeignKey newForeignKey = newForeignKeyInternal(getNextPartId());
        updateSrcFile();
        return newForeignKey;
    }

    /**
     * Overridden method.
     * @see org.faktorips.devtools.core.model.tablestructure.ITableStructure#moveForeignKeys(int[], boolean)
     */
    public int[] moveForeignKeys(int[] indexes, boolean up) {
        ListElementMover mover = new ListElementMover(foreignKeys);
        return mover.move(indexes, up);
    }
    
    private IForeignKey newForeignKeyInternal(int id) {
        IForeignKey newForeignKey = new ForeignKey(this, id);
        foreignKeys.add(newForeignKey);
        return newForeignKey;
    }
    
    void removeForeignKey(IForeignKey key) {
        foreignKeys.remove(key);
        updateSrcFile();
    }
    
    /** 
     * Overridden method.
     * @see org.faktorips.devtools.core.model.IIpsObject#getIpsObjectType()
     */
    public IpsObjectType getIpsObjectType() {
        return IpsObjectType.TABLE_STRUCTURE;
    }
    
    /** 
     * Overridden method.
     * 
     * @throws CoreException if type can't be determined
     * @see org.faktorips.devtools.core.model.IIpsObject#getJavaType(int)
     */
    public IType getJavaType(int kind) throws CoreException {
        return getJavaType(getIpsPackageFragment(), getName(), kind);
    }

    /**
     * Returns the Java type that correspond to the IPS object identified by
     * the IPS package fragment and the given name and the given kind.
     * 
     * @throws CoreException if the type can't be determined.
     */
    public final static IType getJavaType(
            IIpsPackageFragment ipsPack, 
            String tableStructureName, 
            int kind) throws CoreException {
        
        switch (kind) {
            case JAVA_TABLE_IMPLEMENTATION_TYPE:
            {
        	    IPackageFragment pack = ipsPack.getJavaPackageFragment(IIpsPackageFragment.JAVA_PACK_IMPLEMENTATION);
                String javaTypeName = StringUtils.capitalise(tableStructureName);
        	    ICompilationUnit cu = pack.getCompilationUnit(javaTypeName + ".java");
        	    return cu.getType(javaTypeName);
            }
        	case JAVA_TABLE_ROW_IMPL_TYPE:
        	{
        	    IPackageFragment pack = ipsPack.getJavaPackageFragment(IIpsPackageFragment.JAVA_PACK_IMPLEMENTATION);
                String javaTypeName = StringUtils.capitalise(tableStructureName + "Row");
        	    ICompilationUnit cu = pack.getCompilationUnit(javaTypeName + ".java");
        	    return cu.getType(javaTypeName);
        	}
            default:
                throw new IllegalArgumentException("Unexpected value for parameter kind: " + kind);
        }
    }

    
    /** 
     * Overridden method.
     * @see org.faktorips.devtools.core.model.IIpsObject#getAllJavaTypes()
     */
    public IType[] getAllJavaTypes() {
        return new IType[0];
    }

    /**
     * Overridden.
     */
    public boolean hasRange(String name) {
        return getRange(name) != null;
    }

    /**
     * Overridden.
     */
    public boolean hasColumn(String name) {
        return getColumn(name) != null;
    }

    /**
     * Overridden IMethod.
     */
    public ITableAccessFunction[] getAccessFunctions() {
        if (getUniqueKeys().length==0) {
            return new ITableAccessFunction[0];
        }
        List functions = new ArrayList();
        IUniqueKey key = getUniqueKeys()[0];
        IColumn[] columns = getColumnsNotInKey(key);
        for (int i = 0; i < columns.length; i++) {
            functions.add(createFunction(i, key, columns[i]));
        }
        return (ITableAccessFunction[])functions.toArray(new ITableAccessFunction[functions.size()]);
    }
    
    private ITableAccessFunction createFunction(int id, IUniqueKey key, IColumn column) {
        TableAccessFunction fct = new TableAccessFunction(this, id);
        fct.setAccessedColumn(column.getName());
        fct.setName(getName() + "." + column.getName());
        fct.setType(column.getDatatype());
        StringBuffer description = new StringBuffer("The function retrieves the row identified by "); 
        IKeyItem[] items = key.getKeyItems();
        String[] argTypes = new String[items.length];
        for (int i = 0; i < items.length; i++) {
            argTypes[i] = items[i].getDatatype();
            if (i>0) {
                description.append(", ");
            }
            description.append(items[i].getAccessParameterName());
        }
        fct.setArgTypes(argTypes);
        description.append(" and returns the value of the column " + column.getName());
        fct.setDescription(description.toString());
        return fct;
    }
    
    /**
     * Overridden.
     */
    public IColumn[] getColumnsNotInKey(IKey key) {
        ArgumentCheck.notNull(key);
        List columnsNotInKey = new ArrayList(columns);
        IKeyItem[] items = key.getKeyItems();
        for (int i = 0; i < items.length; i++) {
            IColumn[] columnsInItem = items[i].getColumns();
            for (int j=0; j<columnsInItem.length; j++) {
                columnsNotInKey.remove(columnsInItem[j]);
            }
        }
        return (IColumn[])columnsNotInKey.toArray(new IColumn[columnsNotInKey.size()]);
    }

    /**
     * Overridden IMethod.
     *
     * @see org.faktorips.devtools.core.internal.model.IpsObject#propertiesToXml(org.w3c.dom.Element)
     */
    protected void propertiesToXml(Element newElement) {
        super.propertiesToXml(newElement);
        // nothing else to do
    }

    /**
     * Overridden IMethod.
     *
     * @see org.faktorips.devtools.core.internal.model.IpsObject#initPropertiesFromXml(org.w3c.dom.Element)
     */
    protected void initPropertiesFromXml(Element element, int id) {
        super.initPropertiesFromXml(element, id);
        // nothing else to do
    }

    /**
     * Overridden IMethod.
     *
     * @see org.faktorips.devtools.core.internal.model.IpsObject#reinitPartCollections()
     */
    protected void reinitPartCollections() {
        columns.clear();
        ranges.clear();
        uniqueKeys.clear();
        foreignKeys.clear();
    }

    /**
     * Overridden IMethod.
     *
     * @see org.faktorips.devtools.core.internal.model.IpsObject#reAddPart(org.faktorips.devtools.core.model.IIpsObjectPart)
     */
    protected void reAddPart(IIpsObjectPart part) {
        if (part instanceof IColumn) {
            columns.add(part);
            return;
        } else if (part instanceof IColumnRange) {
            ranges.add(part);
            return;
        } else if (part instanceof IUniqueKey) {
            uniqueKeys.add(part);
            return;
        } else if (part instanceof IForeignKey) {
            foreignKeys.add(part);
            return;
        }
        throw new RuntimeException("Unknown part type" + part.getClass());
    }

    /**
     * Overridden IMethod.
     *
     * @see org.faktorips.devtools.core.internal.model.IpsObject#newPart(java.lang.String, int)
     */
    protected IIpsObjectPart newPart(String xmlTagName, int id) {
        if (xmlTagName.equals(Column.TAG_NAME)) {
            return newColumnInternal(id);
        } else if (xmlTagName.equals(ColumnRange.TAG_NAME)) {
            return newColumnRangeInternal(id);
        } else if (xmlTagName.equals(UniqueKey.TAG_NAME)) {
            return newUniqueKeyInternal(id);
        } else if (xmlTagName.equals(ForeignKey.TAG_NAME)) {
            return newForeignKeyInternal(id);
        }
        throw new RuntimeException("Could not create part for tag name" + xmlTagName);
    }
	/**
	 * {@inheritDoc}
	 */
	public IIpsObjectPart newPart(Class partType) {
        if (partType.equals(IColumn.class)) {
            return newColumnInternal(this.getNextPartId());
        } else if (partType.equals(IColumnRange.class)) {
            return newColumnRangeInternal(this.getNextPartId());
        } else if (partType.equals(IUniqueKey.class)) {
            return newUniqueKeyInternal(this.getNextPartId());
        } else if (partType.equals(IForeignKey.class)) {
            return newForeignKeyInternal(this.getNextPartId());
        }
		throw new IllegalArgumentException("Unknown part type" + partType);
	}
}
