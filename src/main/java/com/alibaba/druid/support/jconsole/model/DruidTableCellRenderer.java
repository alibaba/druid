package com.alibaba.druid.support.jconsole.model;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class DruidTableCellRenderer extends JLabel implements TableCellRenderer
{

	private static final long serialVersionUID = 1L;

	// This method is called each time a cell in a column
    // using this renderer needs to be rendered.
    public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column)
    {

        if ((column == 0))
        {
        	setBackground(Color.lightGray);
        }
        this.setOpaque(true);

        if (value != null) {
        	setText(value.toString());
        } else {
        	setText((row + 1)+"");
        }

        // Since the renderer is a component, return itself
        return this;
    }
}