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
package com.alibaba.druid.support.jconsole.util;

import com.alibaba.druid.stat.DruidStatService;
import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 表格数据处理类
 * 
 * @author yunnysunny [yunnysunny@gmail.com]
 */
public final class TableDataProcessor {

    /** 名称列的列名. */
    private static final String COLUMN_KEY_NAME       = "名称";

    /** 内容列的列名. */
    private static final String COLUMN_VALUE_NAME     = "值";

    /** 返回码在json中的键名 */
    private static final String RESP_JSON_RESULT_KEY  = "ResultCode";
    /** 内容在json中的键名 */
    private static final String RESP_JSON_CONTENT_KEY = "Content";
    /** 成功的返回码 */
    protected static final int  RESP_SUCCESS_RESULT   = 1;
    private final static Log    LOG                   = LogFactory.getLog(TableDataProcessor.class);

    /**
     * TableDataProcessor的构造函数
     */
    private TableDataProcessor(){

    }

    /**
     * 将行数据转化为列数据 由于json中的数据是按照一条条的记录返回的，
     * 而在显示的时候需要按照“名称”、“值”两列显示，所以要做转化。
     * 
     * @param rowData 原始数据
     * @param keyword 关键字,可以为null
     * @return 生成的列数据的对象
     */
    public static ColumnData row2col(ArrayList<LinkedHashMap<String, Object>> rowData, String keyword) {
        ColumnData data = new ColumnData();
        ArrayList<LinkedHashMap<String, Object>> colData = new ArrayList<LinkedHashMap<String, Object>>();
        ArrayList<String> colNames = new ArrayList<String>();
        int rowCount = 0;
        int colCount = 0;
        for (LinkedHashMap<String, Object> row : rowData) {
            if (keyword != null) {
                String keyNow = row.remove(keyword).toString();
                colNames.add(keyNow);
            }
            rowCount++;

            for (Map.Entry<String, Object> element : row.entrySet()) {
                LinkedHashMap<String, Object> colDataItem = new LinkedHashMap<String, Object>();
                colDataItem.put(COLUMN_KEY_NAME, element.getKey());
                colDataItem.put(COLUMN_VALUE_NAME, element.getValue());
                colData.add(colDataItem);
                if (rowCount == 1) {
                    colCount++;
                }
            }
        }
        data.setCount(colCount);
        data.setData(colData);
        data.setNames(colNames);
        return data;
    }

    /**
     * 将行数据转化为多个表格中的列数据 和row2col类似，只不过这里是返回多个表格数据
     * 
     * @param rowData 原始数据
     * @param keyword the keyword
     * @return 生成的列数据的对象
     */
    public static ColumnData multiRow2Col(ArrayList<LinkedHashMap<String, Object>> rowData, String keyword) {
        ColumnData data = new ColumnData();
        ArrayList<ArrayList<LinkedHashMap<String, Object>>> tableData = new ArrayList<ArrayList<LinkedHashMap<String, Object>>>();

        ArrayList<String> colNames = new ArrayList<String>();
        int rowCount = 0;

        for (LinkedHashMap<String, Object> row : rowData) {
            if (keyword != null) {
                String keyNow = row.remove(keyword).toString();
                colNames.add(keyNow);
            }
            rowCount++;

            ArrayList<LinkedHashMap<String, Object>> colData = new ArrayList<LinkedHashMap<String, Object>>();
            for (Map.Entry<String, Object> element : row.entrySet()) {
                LinkedHashMap<String, Object> colDataItem = new LinkedHashMap<String, Object>();
                colDataItem.put(COLUMN_KEY_NAME, element.getKey());
                colDataItem.put(COLUMN_VALUE_NAME, element.getValue());
                colData.add(colDataItem);

            }
            tableData.add(colData);
        }
        data.setCount(rowCount);
        data.setTableData(tableData);
        data.setNames(colNames);
        return data;
    }

    /**
     * 将行数据转化为列数据，这里只是调用了，将第二个参数置为null。
     * 
     * @param rowData 原始数据
     * @return 生成的列数据的对象
     */
    public static ColumnData row2col(ArrayList<LinkedHashMap<String, Object>> rowData) {
        return row2col(rowData, null);
    }
    
    /**
     * 解析调用service后得到JSON数据
     * 
     * @param respData 获取到的json对象
     * @return 返回解析后的数据
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<LinkedHashMap<String, Object>> parseData(Object respData) {
        ArrayList<LinkedHashMap<String, Object>> data = null;
        if (respData instanceof Map) {
            LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>) respData;
            int rv = (Integer) map.get(RESP_JSON_RESULT_KEY);

            if (rv == RESP_SUCCESS_RESULT) {
                Object content = map.get(RESP_JSON_CONTENT_KEY);
                if (content instanceof List) {
                    data = (ArrayList<LinkedHashMap<String, Object>>) content;
                } else if (content instanceof Map) {
                    LinkedHashMap<String, Object> contentEle = (LinkedHashMap<String, Object>) content;
                    data = new ArrayList<LinkedHashMap<String, Object>>();
                    data.add(contentEle);
                }
            }
        }
        return data;
    }

    /**
     * 调用service，返回数据
     * 
     * @param url service的地址
     * @param conn MBeanServerConnection对象
     * @return 调用service后返回的数据
     * @throws Exception
     */
    public static Object getData(String url, MBeanServerConnection conn) throws Exception {
        Object o = null;
        ObjectName name = new ObjectName(DruidStatService.MBEAN_NAME);

        String result = (String) conn.invoke(name, "service", new String[] { url },
                                             new String[] { String.class.getName() });
        o = JSONUtils.parse(result);
        if (LOG.isDebugEnabled()) {
            LOG.debug(o.toString());
        }
        return o;
    }

    /**
     * The Class ColumnData.
     */
    public static class ColumnData {

        /** 关键字集合. */
        private ArrayList<String>                                   names;

        /** 单个表格数据. */
        private ArrayList<LinkedHashMap<String, Object>> data;

        /** 多个表格数据. */
        private ArrayList<ArrayList<LinkedHashMap<String, Object>>> tableData;

        /** 返回的数据总数，如果返回单个表格，则为表格行数；如果返回多个表格数据，则为表格个数. */
        private int                                                 count;

        /**
         * ColumnData构造函数
         */
        public ColumnData(){
            super();
        }

        /**
         * 获取关键字集合
         * 
         * @return the names
         */
        public ArrayList<String> getNames() {
            return names;
        }

        /**
         * 设置关键字集合
         * 
         * @param names the new names
         */
        public void setNames(ArrayList<String> names) {
            this.names = names;
        }

        /**
         * 获取单个表格数据
         * 
         * @return the data
         */
        public ArrayList<LinkedHashMap<String, Object>> getData() {
            return data;
        }

        /**
         * 设置单个表格数据
         * 
         * @param data the data
         */
        public void setData(ArrayList<LinkedHashMap<String, Object>> data) {
            this.data = data;
        }

        /**
         * 返回的数据总数，如果返回单个表格，则为表格行数；如果返回多个表格数据，则为表格个数
         * 
         * @return the count
         */
        public int getCount() {
            return count;
        }

        /**
         * Sets the count.
         * 
         * @param count the new count
         */
        public void setCount(int count) {
            this.count = count;
        }

        /**
         * 返回多个表格数据
         * 
         * @return the table data
         */
        public ArrayList<ArrayList<LinkedHashMap<String, Object>>> getTableData() {
            return tableData;
        }

        /**
         * 设置多个表格数据
         * 
         * @param tableData the table data
         */
        public void setTableData(ArrayList<ArrayList<LinkedHashMap<String, Object>>> tableData) {
            this.tableData = tableData;
        }

    }
}
