/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.xt.gt.ui.table.renderer;

import com.xt.gt.ui.UIException;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * 此类用于在标注中表示采用默认的表呈现器，不能用于其他用途。
 * @author albert
 */
final public class NullTableCellRenderer implements TableCellRenderer {

    private NullTableCellRenderer() {
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        throw new UIException("此 TableCellRenderer 只能作为标注的空标识使用。");
    }

}
