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

import com.alibaba.druid.support.jconsole.model.ColumnGroup;
import com.alibaba.druid.support.jconsole.model.DruidTableModel;
import com.alibaba.druid.support.jconsole.model.GroupableTableHeader;
import com.alibaba.druid.support.jconsole.model.GroupableTableHeaderUI;
import com.alibaba.druid.support.jconsole.model.RowHeaderTable;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

/**
 * 请求地址 /sql.json
 * 
 * 返回数据格式：
 * <pre>
 * {
    "ResultCode": 1,
    "Content": [
        {
            "BatchSizeMax": 0,
            "BatchSizeTotal": 0,
            "ConcurrentMax": 1,
            "DbType": "h2",
            "EffectedRowCount": 18,
            "EffectedRowCountHistogram": [
                0,6,0,0,0,0
            ],
            "EffectedRowCountMax": 4,
            "ErrorCount": 0,
            "ExecuteAndResultHoldTimeHistogram": [
                5,0,1,0,0,0,0,0
            ],
            "ExecuteAndResultSetHoldTime": 0,
            "ExecuteCount": 6,
            "FetchRowCount": 0,
            "FetchRowCountHistogram": [
                6,0,0,0,0,0
            ],
            "FetchRowCountMax": 0,
            "Histogram": [
                5,0,1,0,0,0,0,0
            ],
            "ID": 9,
            "InTransactionCount": 6,
            "LastSlowParameters": "[4]",
            "LastTime": "2012-08-22 02:57:56",
            "MaxTimespan": 18,
            "MaxTimespanOccurTime": "2012-08-22 12:38:56",
            "ResultSetHoldTime": 0,
            "RunningCount": 0,
            "SQL": "delete from acct_group_permission where group_id=?",
            "TotalTime": 21
        },
     ]
  }
  </pre>
  
  @author yunnysunny [yunnysunny@gmail.com]
 * */
public class DruidSQLPanel extends DruidPanel {

    private static final long              serialVersionUID               = 1L;
    private static final String            REQUEST_URL                    = "/sql.json";
    private static final ArrayList<String> SHOW_LIST                      = new ArrayList<String>() {

                                                                              private static final long serialVersionUID = 1L;

                                                                              {
                                                                                  add("SQL");
                                                                                  add("ExecuteCount");
                                                                                  add("TotalTime");
                                                                                  add("InTransactionCount");
                                                                                  add("ErrorCount");
                                                                                  add("EffectedRowCount");
                                                                                  add("FetchRowCount");
                                                                                  add("RunningCount");
                                                                                  add("ConcurrentMax");
                                                                                  add("Histogram");
                                                                                  add("EffectedRowCountHistogram");
                                                                                  add("ExecuteAndResultHoldTimeHistogram");
                                                                                  add("FetchRowCountHistogram");
                                                                              }

                                                                          };
    private static final ArrayList<String> REAL_SHOW_LIST                 = new ArrayList<String>() {

                                                                              private static final long serialVersionUID = 1L;

                                                                              {
                                                                                  add("SQL");
                                                                                  add("ExecuteCount");
                                                                                  add("TotalTime");
                                                                                  add("InTransactionCount");
                                                                                  add("ErrorCount");
                                                                                  add("EffectedRowCount");
                                                                                  add("FetchRowCount");
                                                                                  add("RunningCount");
                                                                                  add("ConcurrentMax");
                                                                                  add("Histogram");
                                                                                  add("EffectedRowCountHistogram");
                                                                                  add("ExecuteAndResultHoldTimeHistogram");
                                                                                  add("FetchRowCountHistogram");
                                                                              }
                                                                          };
    private static final String            HISTOGRAM                      = "Histogram";
    private static final String            Effected_RowCount_HISOGRAM     = "EffectedRowCountHistogram";
    private static final String            ExecuteAndResult_Hold_HISOGRAM = "ExecuteAndResultHoldTimeHistogram";
    private static final String            FetchRowCount_HISOGRAM         = "FetchRowCountHistogram";
    private static final ArrayList<String> ARRAY_DATA_MAP                 = new ArrayList<String>() {

                                                                              private static final long serialVersionUID = 1L;
                                                                              {
                                                                                  add(HISTOGRAM);
                                                                                  add(Effected_RowCount_HISOGRAM);
                                                                                  add(ExecuteAndResult_Hold_HISOGRAM);
                                                                                  add(FetchRowCount_HISOGRAM);
                                                                              }
                                                                          };
    private static final int               FIST_LIST_OFFSET               = 9;

    private ColumnGroup                    groupHistogram;
    private ColumnGroup                    groupEffectedRowCountHistogram;
    private ColumnGroup                    groupExecuteAndResultHoldTimeHistogram;
    private ColumnGroup                    groupFetchRowCountHistogram;
    private ArrayList<Integer>             listHistogram;
    private ArrayList<Integer>             listEffectedRowCountHistogram;
    private ArrayList<Integer>             listExecuteAndResultHoldTimeHistogram;
    private ArrayList<Integer>             listFetchRowCountHistogram;
    private ArrayList<String>              ids;
    private static final String            JSON_ID_NAME                   = "ID";

    public DruidSQLPanel(){
        super();
        this.url = REQUEST_URL;
    }

    private void addGroupData(String keyNow, int index) {
        // System.out.println("keyNow:"+keyNow+"index:"+index);
        if (HISTOGRAM.equals(keyNow)) {
            listHistogram.add(index);
        } else if (Effected_RowCount_HISOGRAM.equals(keyNow)) {
            listEffectedRowCountHistogram.add(index);
        } else if (ExecuteAndResult_Hold_HISOGRAM.equals(keyNow)) {
            listExecuteAndResultHoldTimeHistogram.add(index);
        } else if (FetchRowCount_HISOGRAM.equals(keyNow)) {
            listFetchRowCountHistogram.add(index);
        }
    }

    /**
     * 数据预处理，将没有用到的数据删除掉，将牵扯到时间分布的数据拆分出来。
     * 
     * @param data 要处理的数据
     * @return 最终的处理结果
     */
    private ArrayList<LinkedHashMap<String, Object>> preProcess(ArrayList<LinkedHashMap<String, Object>> data) {
        groupHistogram = new ColumnGroup(HISTOGRAM);
        groupEffectedRowCountHistogram = new ColumnGroup(Effected_RowCount_HISOGRAM);
        groupExecuteAndResultHoldTimeHistogram = new ColumnGroup(ExecuteAndResult_Hold_HISOGRAM);
        groupFetchRowCountHistogram = new ColumnGroup(FetchRowCount_HISOGRAM);
        listHistogram = new ArrayList<Integer>();
        listEffectedRowCountHistogram = new ArrayList<Integer>();
        listExecuteAndResultHoldTimeHistogram = new ArrayList<Integer>();
        listFetchRowCountHistogram = new ArrayList<Integer>();
        ids = new ArrayList<String>();

        ArrayList<LinkedHashMap<String, Object>> newData = new ArrayList<LinkedHashMap<String, Object>>();
        int dataIndex = 0;
        for (LinkedHashMap<String, Object> dataNow : data) {
            // 先把不需要显示的内容删除掉
            for (Iterator<Entry<String, Object>> it = dataNow.entrySet().iterator(); it.hasNext();) {
                Entry<String, Object> entry = it.next();
                String key = entry.getKey();
                Object value = entry.getValue();
                if (JSON_ID_NAME.equals(key)) {
                    ids.add(value != null ? value.toString() : null);
                }
                if (!SHOW_LIST.contains(key)) {
                    it.remove();
                }
            }
            // if (dataIndex==0)
            // System.out.println(dataNow.toString());
            int offset = FIST_LIST_OFFSET;

            for (String arrayKey : ARRAY_DATA_MAP) {// 把数组数据显示为单个数据
                Object arrayData = dataNow.get(arrayKey);

                if (arrayData instanceof ArrayList<?>) {

                    dataNow.remove(arrayKey);
                    if (dataIndex == 0) {
                        REAL_SHOW_LIST.remove(arrayKey);
                    }
                    @SuppressWarnings("unchecked")
                    ArrayList<Integer> arrayDataList = (ArrayList<Integer>) arrayData;
                    for (int j = 0, len = arrayDataList.size(); j < len; j++) {
                        int a = (j - 1 >= 0) ? (int) Math.pow(10, j - 1) : 0;
                        int b = (int) Math.pow(10, j);
                        String newKey = arrayKey + "-" + a + "~" + b + "ms";

                        dataNow.put(newKey, arrayDataList.get(j));
                        if (dataIndex == 0) {
                            REAL_SHOW_LIST.add(newKey);
                            int index = offset + j;
                            addGroupData(arrayKey, index);
                            if (j == len - 1) {
                                offset = index + 1;
                            }
                        }
                    }
                }

            }// end of foreach ARRAY_DATA_MAP
            dataIndex++;
            newData.add(dataNow);
        }

        return newData;
    }

    private void addTableGroup() {

        TableColumnModel cm = table.getColumnModel();
        // System.out.println(SHOW_LIST.size());
        for (int i : listHistogram) {
            groupHistogram.add(cm.getColumn(i));
        }
        for (int j : listEffectedRowCountHistogram) {
            groupEffectedRowCountHistogram.add(cm.getColumn(j));
        }
        for (int x : listExecuteAndResultHoldTimeHistogram) {
            groupExecuteAndResultHoldTimeHistogram.add(cm.getColumn(x));
        }
        for (int y : listFetchRowCountHistogram) {
            groupFetchRowCountHistogram.add(cm.getColumn(y));
        }

        GroupableTableHeader header = (GroupableTableHeader) table.getTableHeader();
        header.addColumnGroup(groupHistogram);
        header.addColumnGroup(groupEffectedRowCountHistogram);
        header.addColumnGroup(groupExecuteAndResultHoldTimeHistogram);
        header.addColumnGroup(groupFetchRowCountHistogram);
        header.setUI(new GroupableTableHeaderUI());
    }

    @Override
    protected void tableDataProcess(ArrayList<LinkedHashMap<String, Object>> data) {
        table = new JTable() {

            private static final long serialVersionUID = 1L;

            @Override
            protected JTableHeader createDefaultTableHeader() {

                return new GroupableTableHeader(columnModel);
            }
        };
        data = preProcess(data);
        // System.out.println(SHOW_LIST);
        tableModel = new DruidTableModel(data, REAL_SHOW_LIST);
        // System.out.println(data.toString());
        table.setModel(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        addTableGroup();
        table.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {// 点击几次，这里是双击事件
                    int row = table.getSelectedRow();
                    String id = ids.get(row);
                    new DruidSqlDetailFrame(id,conn);
                }
            }
        });

        RowHeaderTable header = new RowHeaderTable(table, 20);
        scrollPane.setRowHeaderView(header);
        scrollPane.setViewportView(table);
        JLabel jb = new JLabel("N", SwingConstants.CENTER);
        jb.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
        scrollPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, jb);
    }
}
