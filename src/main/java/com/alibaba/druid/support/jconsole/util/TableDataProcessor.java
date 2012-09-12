/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 表格数据处理类
 * 
 * @author yunnysunny<yunnysunny@gmail.com>
 */
public final class TableDataProcessor {

    /** 名称列的列名. */
    private static final String COLUMN_KEY_NAME   = "名称";

    /** 内容列的列名. */
    private static final String COLUMN_VALUE_NAME = "值";

    /**
     * TableDataProcessor的构造函数
     */
    private TableDataProcessor(){

    }

    /**
     * 将行数据转化为列数据 由于json中的数据是按照一条条的记录返回的，
     * 而在显示的时候需要按照“名称”、“值”两列显示，所以要做转化。
     * 
     * @param rowDatas 原始数据
     * @param keyword 关键字,可以为null
     * @return 生成的列数据的对象
     */
    public static ColumnData row2col(ArrayList<LinkedHashMap<String, Object>> rowDatas, String keyword) {
        ColumnData datas = new ColumnData();
        ArrayList<LinkedHashMap<String, Object>> coldatas = new ArrayList<LinkedHashMap<String, Object>>();
        ArrayList<String> colNames = new ArrayList<String>();
        int rowCount = 0;
        int colCount = 0;
        for (LinkedHashMap<String, Object> row : rowDatas) {
            if (keyword != null) {
                String keyNow = row.remove(keyword).toString();
                colNames.add(keyNow);
            }
            rowCount++;

            for (Map.Entry<String, Object> element : row.entrySet()) {
                LinkedHashMap<String, Object> colData = new LinkedHashMap<String, Object>();
                colData.put(COLUMN_KEY_NAME, element.getKey());
                colData.put(COLUMN_VALUE_NAME, element.getValue());
                coldatas.add(colData);
                if (rowCount == 1) {
                    colCount++;
                }
            }
        }
        datas.setCount(colCount);
        datas.setDatas(coldatas);
        datas.setNames(colNames);
        return datas;
    }

    /**
     * 将行数据转化为多个表格中的列数据 和{@link #row2col(ArrayList<LinkedHashMap<String,Object>>, String)}类似，
     * 只不过这里是返回多个表格数据
     * 
     * @param rowDatas 原始数据
     * @param keyword the keyword
     * @return 生成的列数据的对象
     */
    public static ColumnData mutilRow2col(ArrayList<LinkedHashMap<String, Object>> rowDatas, String keyword) {
        ColumnData datas = new ColumnData();
        ArrayList<ArrayList<LinkedHashMap<String, Object>>> tableDatas = new ArrayList<ArrayList<LinkedHashMap<String, Object>>>();

        ArrayList<String> colNames = new ArrayList<String>();
        int rowCount = 0;

        for (LinkedHashMap<String, Object> row : rowDatas) {
            if (keyword != null) {
                String keyNow = row.remove(keyword).toString();
                colNames.add(keyNow);
            }
            rowCount++;

            ArrayList<LinkedHashMap<String, Object>> coldatas = new ArrayList<LinkedHashMap<String, Object>>();
            for (Map.Entry<String, Object> element : row.entrySet()) {
                LinkedHashMap<String, Object> colData = new LinkedHashMap<String, Object>();
                colData.put(COLUMN_KEY_NAME, element.getKey());
                colData.put(COLUMN_VALUE_NAME, element.getValue());
                coldatas.add(colData);

            }
            tableDatas.add(coldatas);
        }
        datas.setCount(rowCount);
        datas.setTableDatas(tableDatas);
        datas.setNames(colNames);
        return datas;
    }

    /**
     * 将行数据转化为列数据，这里只是调用了{@link #row2col(ArrayList<LinkedHashMap<String,Object>>, String)}，
     * 将第二个参数置为null。
     * 
     * @param rowDatas 原始数据
     * @return 生成的列数据的对象
     */
    public static ColumnData row2col(ArrayList<LinkedHashMap<String, Object>> rowDatas) {
        return row2col(rowDatas, null);
    }

    /**
     * The Class ColumnData.
     */
    public static class ColumnData {

        /** 关键字集合. */
        private ArrayList<String>                                   names;

        /** 单个表格数据. */
        private ArrayList<LinkedHashMap<String, Object>>            datas;

        /** 多个表格数据. */
        private ArrayList<ArrayList<LinkedHashMap<String, Object>>> tableDatas;

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
         * @return the datas
         */
        public ArrayList<LinkedHashMap<String, Object>> getDatas() {
            return datas;
        }

        /**
         * 设置单个表格数据
         * 
         * @param datas the datas
         */
        public void setDatas(ArrayList<LinkedHashMap<String, Object>> datas) {
            this.datas = datas;
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
         * @return the table datas
         */
        public ArrayList<ArrayList<LinkedHashMap<String, Object>>> getTableDatas() {
            return tableDatas;
        }

        /**
         * 设置多个表格数据
         * 
         * @param tableDatas the table datas
         */
        public void setTableDatas(ArrayList<ArrayList<LinkedHashMap<String, Object>>> tableDatas) {
            this.tableDatas = tableDatas;
        }

    }
}
