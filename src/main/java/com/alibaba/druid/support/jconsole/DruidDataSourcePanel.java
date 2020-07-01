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
package com.alibaba.druid.support.jconsole;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;

import com.alibaba.druid.support.jconsole.model.DruidTableCellRenderer;
import com.alibaba.druid.support.jconsole.model.DruidTableModel;
import com.alibaba.druid.support.jconsole.util.TableDataProcessor;
import com.alibaba.druid.support.jconsole.util.TableDataProcessor.ColumnData;

/**
 * service的请求地址：/datasource.json
 * 
 * 请求service返回的json格式：
 * <pre>
 * {"ResultCode":1,
 *      "Content":
 *          [{"Identity":31375837,"Name":"DataSource-31375837","DbType":"mysql",
 *          "DriverClassName":"com.mysql.jdbc.Driver",
 *          "URL":"jdbc:mysql://localhost:3306/dragoon_v25_masterdb","UserName":"root",
 *          "FilterClassNames":["com.alibaba.druid.filter.stat.StatFilter"],
 *          "WaitThreadCount":0,"NotEmptyWaitCount":0,"NotEmptyWaitMillis":0,"PoolingCount":1,
 *          "PoolingPeak":0,"ActiveCount":0,"ActivePeak":1,"ActivePeakTime":"Tue Aug 21 21:22:33 CST 2012",
 *          "InitialSize":1,"MinIdle":1,"MaxActive":20,"QueryTimeout":0,"TransactionQueryTimeout":0,
 *          "LoginTimeout":0,"ValidConnectionCheckerClassName":"com.alibaba.druid.pool.vendor.MySqlValidConnectionChecker",
 *          "ExceptionSorterClassName":"com.alibaba.druid.pool.vendor.MySqlExceptionSorter",
 *          "TestOnBorrow":false,"TestOnReturn":false,"TestWhileIdle":true,"DefaultAutoCommit":true,
 *          "DefaultReadOnly":true,"LogicConnectCount":93,"LogicCloseCount":93,"LogicConnectErrorCount":0,
 *          "PhysicalConnectCount":1,"PhysicalCloseCount":0,"PhysicalConnectErrorCount":0,
 *          "ExecuteCount":93,"ErrorCount":0,"CommitCount":0,"RollbackCount":0,"PSCacheAccessCount":31,
 *          "PSCacheHitCount":30,"PSCacheMissCount":1,"StartTransactionCount":0,
 *          "TransactionHistogram":[0,0,0,0,0,0],"ConnectionHoldTimeHistogram":[0,0,48,45,0,0,0,0],
 *          "RemoveAbandoned":false
 *          }]
 * }
 * </pre>
 * 
 * @author yunnysunny [yunnysunny@gmail.com]
 * */
public class DruidDataSourcePanel extends DruidPanel {

    private static final long   serialVersionUID  = 1L;
    private static final String REQUEST_URL       = "/datasource.json";
    private static final String KEY_WORD_IDENTITY = "Identity";
    private JPanel              contentPanel;

    public DruidDataSourcePanel(){
        super();
        this.url = REQUEST_URL;

    }

    private void addTable(ColumnData columnData) {
        ArrayList<ArrayList<LinkedHashMap<String, Object>>> data = columnData.getTableData();

        int i = 0;
        ArrayList<String> ids = columnData.getNames();

        for (ArrayList<LinkedHashMap<String, Object>> listNow : data) {
            JTable table = new JTable();
            tableModel = new DruidTableModel(listNow);
            table.setModel(tableModel);

            String id = ids.get(i);
            JPanel panelNow = new JPanel(new BorderLayout());
            panelNow.setBorder((TitledBorder) BorderFactory.createTitledBorder(KEY_WORD_IDENTITY + ":" + id));
            contentPanel.add(panelNow);

            panelNow.add(table.getTableHeader(), BorderLayout.NORTH);
            panelNow.add(table);
            table.getColumnModel().getColumn(0).setCellRenderer(new DruidTableCellRenderer());

            i++;
        }
    }

    @Override
    protected void tableDataProcess(ArrayList<LinkedHashMap<String, Object>> data) {
        ColumnData columnData = TableDataProcessor.multiRow2Col(data, KEY_WORD_IDENTITY);
        contentPanel = new JPanel(new GridLayout(0, 1));
        addTable(columnData);

        scrollPane.setViewportView(contentPanel);
    }
}
