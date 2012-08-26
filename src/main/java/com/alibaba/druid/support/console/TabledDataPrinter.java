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
package com.alibaba.druid.support.console;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TabledDataPrinter {

    private static final int SQL_MAX_LEN = 32;

    private static final String[] sqlFields = new String[] {
        "ID", "DataSource", "SQL", "ExecuteCount", "ErrorCount",
        "TotalTime", "LastTime", "MaxTimespan", "LastError", "EffectedRowCount",
        "FetchRowCount", "MaxTimespanOccurTime", "BatchSizeMax", "BatchSizeTotal", "ConcurrentMax",
        "RunningCount", "Name", "File",
        "LastErrorMessage", "LastErrorClass", "LastErrorStackTrace", "LastErrorTime",
        "DbType", "URL", "InTransactionCount",
        "Histogram", "LastSlowParameters", "ResultSetHoldTime", "ExecuteAndResultSetHoldTime", "FetchRowCountHistogram",
        "EffectedRowCountHistogram", "ExecuteAndResultHoldTimeHistogram", "EffectedRowCountMax", "FetchRowCountMax", "ClobOpenCount" };

    private static final String[] dsFields = new String[] {
        "Identity", "Name", "DbType", "DriverClassName", "URL", "UserName", "FilterClassNames",
        "WaitThreadCount", "NotEmptyWaitCount", "NotEmptyWaitMillis",
        "PoolingCount", "PoolingPeak", "PoolingPeakTime",
        "ActiveCount", "ActivePeak", "ActivePeakTime",
        "InitialSize", "MinIdle", "MaxActive",
        "QueryTimeout", "TransactionQueryTimeout", "LoginTimeout", "ValidConnectionCheckerClassName", "ExceptionSorterClassName",
        "TestOnBorrow", "TestOnReturn", "TestWhileIdle",
        "DefaultAutoCommit", "DefaultReadOnly", "DefaultTransactionIsolation",
        "LogicConnectCount", "LogicCloseCount", "LogicConnectErrorCount",
        "PhysicalConnectCount", "PhysicalCloseCount", "PhysicalConnectErrorCount",
        "ExecuteCount", "ErrorCount", "CommitCount", "RollbackCount",
        "PSCacheAccessCount", "PSCacheHitCount", "PSCacheMissCount",
        "StartTransactionCount", "TransactionHistogram",
        "ConnectionHoldTimeHistogram", "RemoveAbandoned", "ClobOpenCount" };

    public static void printActiveConnStack(List<List<String >> content,Option opt) {
        for (List<String> stack : content) {
            for (String line : stack) {
                System.out.println(line);
            }
            System.out.println("===============================\n");
        }
    }

    public static void printDataSourceData(List<Map<String, Object>> content,Option opt) {
        if (opt.getStyle() == Option.PrintStyle.HORIZONTAL) {
            System.out.println(getFormattedOutput(content, dsFields));
        } else {
            System.out.println(getVerticalFormattedOutput(content, dsFields));
        }
    }

    public static void printSqlData(List<Map<String, Object>> content, Option opt) {
        if (opt.getStyle() == Option.PrintStyle.HORIZONTAL) {
            System.out.println(getFormattedOutput(content, sqlFields));
        } else {
            System.out.println(getVerticalFormattedOutput(content, sqlFields));
        }
    }

    public static String getFormattedOutput(List<Map<String, Object>> content, String[] titleFields) {
        List<String[]> printContents = new ArrayList<String[]>();
        printContents.add(titleFields);
        
        for (Map<String, Object> sqlStat : content) {
            String[] row = new String[titleFields.length];
            for (int i = 0; i < titleFields.length; ++i) {
                Object value = sqlStat.get(titleFields[i]);
                row[i] = handleAndConvert(value,titleFields[i]);
            }
            printContents.add(row);
        }
        String formattedStr = TableFormatter.format(printContents);
        return formattedStr;
    }


    public static String getVerticalFormattedOutput(List<Map<String, Object>> content, String[] titleFields) {
        List<String[]> printContents = new ArrayList<String[]>();

        for (int i = 0; i < titleFields.length; ++i) {
            String[] row = new String[content.size()+1];
            row[0] = titleFields[i];
            for (int j=0; j<content.size(); j++) {
                Map<String,Object> sqlStat = content.get(j);
                Object value = sqlStat.get(titleFields[i]);
                row[j+1] = handleAndConvert(value, titleFields[i]);
            }
            printContents.add(row);
        }
        String formattedStr = TableFormatter.format(printContents);
        return formattedStr;
    }

    public static String handleAndConvert(Object value, String fieldName) {
        if (value == null) value = "";
        if (fieldName.equals("SQL")) {
            String sql = (String)value;
            if (sql.length() > SQL_MAX_LEN) {
                sql = sql.substring(0,SQL_MAX_LEN-3) + "...";
            }
            value = sql;
        }
        return value.toString();
    }

}
