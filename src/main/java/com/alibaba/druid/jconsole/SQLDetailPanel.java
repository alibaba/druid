package com.alibaba.druid.jconsole;

import javax.swing.JPanel;

public class SQLDetailPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private Object[]          row;

    public SQLDetailPanel(Object[] row){
        this.row = row;
    }

    public Object[] getRow() {
        return row;
    }
}
