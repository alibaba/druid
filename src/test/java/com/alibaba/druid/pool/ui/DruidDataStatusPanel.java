/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

    private final String[]                fieldNames       = new String[] { //
                                                           "CreateCount", "CreateErrorCount", "CreateTimespanMillis",
            "CreateTimespanNano", "DestroyCount", //
            "ConnectCount", "ConnectErrorCount", "CloseCount", "RecycleCount", "ActiveCount", //
            "PoolingCount", "IdleCheckCount", "UI_GettingCount", "UI_GetCount", "UI_ReleaseCount" //
            , "UI_ExecutingCount" //
                                                           };

    public DruidDataStatusPanel(){

        this.setLayout(new GridLayout(6, 6));

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
