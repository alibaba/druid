package com.alibaba.druid.pool.ui;

import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class DruidDataStatusPanel extends JPanel {

    private static final long             serialVersionUID = 1L;

    private final Map<String, JTextField> fields           = new HashMap<String, JTextField>();

    private final String[]                fieldNames       = new String[] { "CreateCount", "CreateErrorCount", "CreateTimespanMillis", "CreateTimespanNano",
            "DestroyCount", "ConnectCount", "ConnectErrorCount", "CloseCount", "RecycleCount", "ActiveCount", "PoolingCount", };

    public DruidDataStatusPanel(){

        this.setLayout(new GridLayout(4, 6));

        for (String fieldName : fieldNames) {
            JLabel label = new JLabel(fieldName + " : ");
            this.add(label);

            JTextField textField = new JTextField();
            this.add(textField);
            textField.setEditable(false);
            fields.put(fieldName, textField);
        }
    }
    
    public void set(String fieldName, Object value) {
        JTextField textField = fields.get(fieldName);
        textField.setText(String.valueOf(value));
    }

    public Map<String, JTextField> getFields() {
        return fields;
    }

}
