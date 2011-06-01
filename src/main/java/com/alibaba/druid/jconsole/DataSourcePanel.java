package com.alibaba.druid.jconsole;

import java.awt.Color;
import java.awt.Panel;

import com.sun.tools.jconsole.JConsoleContext;

public class DataSourcePanel extends Panel {

    private static final long serialVersionUID = 1L;

    private JConsoleContext   context;

    public DataSourcePanel(JConsoleContext context){
        this.context = context;
        
        this.setBackground(Color.RED);
    }

    public JConsoleContext getJConsoleContext() {
        return context;
    }
}
