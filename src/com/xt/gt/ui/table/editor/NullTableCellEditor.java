
package com.xt.gt.ui.table.editor;

import com.xt.gt.ui.UIException;
import java.awt.Component;
import java.util.EventObject;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;

/**
 * 此类用于在标注中表示采用默认的表编辑器，不能用于其他用途。
 * @author albert
 */
final public class NullTableCellEditor implements TableCellEditor {

    private NullTableCellEditor() {
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        throw new UIException("此 TableCellEditor 只能作为标注的空标识使用。");
    }

    @Override
    public Object getCellEditorValue() {
        throw new UIException("此 TableCellEditor 只能作为标注的空标识使用。");
    }

    @Override
    public boolean isCellEditable(EventObject anEvent) {
        throw new UIException("此 TableCellEditor 只能作为标注的空标识使用。");
    }

    @Override
    public boolean shouldSelectCell(EventObject anEvent) {
        throw new UIException("此 TableCellEditor 只能作为标注的空标识使用。");
    }

    @Override
    public boolean stopCellEditing() {
        throw new UIException("此 TableCellEditor 只能作为标注的空标识使用。");
    }

    @Override
    public void cancelCellEditing() {
        throw new UIException("此 TableCellEditor 只能作为标注的空标识使用。");
    }

    @Override
    public void addCellEditorListener(CellEditorListener l) {
        throw new UIException("此 TableCellEditor 只能作为标注的空标识使用。");
    }

    @Override
    public void removeCellEditorListener(CellEditorListener l) {
        throw new UIException("此 TableCellEditor 只能作为标注的空标识使用。");
    }

}
