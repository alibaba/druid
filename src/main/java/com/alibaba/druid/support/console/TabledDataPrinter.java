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
package com.alibaba.druid.support.console;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;

public class TabledDataPrinter {

    private static final int      SQL_MAX_LEN = 32;
    private static final int      MAX_COL     = 4;

    private static final String[] sqlRowTitle = new String[] { "ID", "SQL", "ExecCount", "ExecTime", "ExecMax", "Txn",
            "Error", "Update", "FetchRow", "Running", "Concurrent", "ExecRsHisto" };

    private static final String[] sqlRowField = new String[] { "ID", "SQL", "ExecuteCount", "TotalTime", "MaxTimespan",
            "InTransactionCount", "ErrorCount", "EffectedRowCount", "FetchRowCount", "RunningCount", "ConcurrentMax",
            "ExecuteAndResultHoldTimeHistogram" };

    private static final String[] sqlColField = new String[] { "ID", "DataSource", "SQL", "ExecuteCount", "ErrorCount",
            "TotalTime", "LastTime", "MaxTimespan", "LastError", "EffectedRowCount", "FetchRowCount",
            "MaxTimespanOccurTime", "BatchSizeMax", "BatchSizeTotal", "ConcurrentMax", "RunningCount", "Name", "File",
            "LastErrorMessage", "LastErrorClass", "LastErrorStackTrace", "LastErrorTime", "DbType", "URL",
            "InTransactionCount", "Histogram", "LastSlowParameters", "ResultSetHoldTime",
            "ExecuteAndResultSetHoldTime", "FetchRowCountHistogram", "EffectedRowCountHistogram",
            "ExecuteAndResultHoldTimeHistogram", "EffectedRowCountMax", "FetchRowCountMax", "ClobOpenCount" };

    private static final String[] dsRowTitle  = new String[] { "Identity", "DbType", "PoolingCount", "PoolingPeak",
            "PoolingPeakTime", "ActiveCount", "ActivePeak", "ActivePeakTime", "ExecuteCount", "ErrorCount" };

    private static final String[] dsRowField  = new String[] { "Identity", "DbType", "PoolingCount", "PoolingPeak",
            "PoolingPeakTime", "ActiveCount", "ActivePeak", "ActivePeakTime", "ExecuteCount", "ErrorCount" };

    private static final String[] dsColField  = new String[] { "Identity", "Name", "DbType", "DriverClassName", "URL",
            "UserName", "FilterClassNames", "WaitThreadCount", "NotEmptyWaitCount", "NotEmptyWaitMillis",
            "PoolingCount", "PoolingPeak", "PoolingPeakTime", "ActiveCount", "ActivePeak", "ActivePeakTime",
            "InitialSize", "MinIdle", "MaxActive", "QueryTimeout", "TransactionQueryTimeout", "LoginTimeout",
            "ValidConnectionCheckerClassName", "ExceptionSorterClassName", "TestOnBorrow", "TestOnReturn",
            "TestWhileIdle", "DefaultAutoCommit", "DefaultReadOnly", "DefaultTransactionIsolation",
            "LogicConnectCount", "LogicCloseCount", "LogicConnectErrorCount", "PhysicalConnectCount",
            "PhysicalCloseCount", "PhysicalConnectErrorCount", "ExecuteCount", "ErrorCount", "CommitCount",
            "RollbackCount", "PSCacheAccessCount", "PSCacheHitCount", "PSCacheMissCount", "StartTransactionCount",
            "TransactionHistogram", "ConnectionHoldTimeHistogram", "RemoveAbandoned", "ClobOpenCount" };

    public static void printActiveConnStack(List<List<String>> content, Option opt) {
		PrintStream out = opt.getPrintStream();
        for (List<String> stack : content) {
            for (String line : stack) {
                out.println(line);
            }
            out.println("===============================\n");
        }
    }

    public static void printDataSourceData(List<Map<String, Object>> content, Option opt) {
        while (true) {
            _printDataSourceData(content, opt);
            if (opt.getInterval() == -1) {
                break;
            }
            try {
                Thread.sleep(opt.getInterval() * 1000);
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public static void _printDataSourceData(List<Map<String, Object>> content, Option opt) {
		PrintStream out = opt.getPrintStream();
        if (opt.getId() != -1) {
            List<Map<String, Object>> matchedContent = new ArrayList<Map<String, Object>>();
            for (Map<String, Object> dsStat : content) {
                Integer idStr = (Integer) dsStat.get("Identity");
                if (idStr == opt.getId()) {
                    matchedContent.add(dsStat);
                    break;
                }
            }
            content = matchedContent;
        }
        if (opt.isDetailPrint()) {
            out.println(getVerticalFormattedOutput(content, dsColField));
        } else {
            out.println(getFormattedOutput(content, dsRowTitle, dsRowField));
        }
    }

    public static void printSqlData(List<Map<String, Object>> content, Option opt) {
        while (true) {
            _printSqlData(content, opt);
            if (opt.getInterval() == -1) {
                break;
            }
            try {
                Thread.sleep(opt.getInterval() * 1000);
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public static void _printSqlData(List<Map<String, Object>> content, Option opt) {

		PrintStream out = opt.getPrintStream();
        if (opt.getId() != -1) {
            List<Map<String, Object>> matchedContent = new ArrayList<Map<String, Object>>();
            for (Map<String, Object> sqlStat : content) {
                Integer idStr = (Integer) sqlStat.get("ID");
                if (idStr == opt.getId()) {
                    matchedContent.add(sqlStat);
                    if (opt.isDetailPrint()) {
                        DbType dbType = DbType.of((String) sqlStat.get("DbType"));
                        String sql = (String) sqlStat.get("SQL");
                        out.println("Formatted SQL:");
                        out.println(SQLUtils.format(sql, dbType));
                        out.println();
                    }
                    break;
                }
            }
            content = matchedContent;
        }
        if (opt.isDetailPrint()) {
            out.println(getVerticalFormattedOutput(content, sqlColField));
        } else {
            out.println(getFormattedOutput(content, sqlRowTitle, sqlRowField));
        }
    }

    public static String getFormattedOutput(List<Map<String, Object>> content, String[] title, String[] rowField) {

        List<String[]> printContents = new ArrayList<String[]>();
        printContents.add(title);

        for (Map<String, Object> sqlStat : content) {
            String[] row = new String[rowField.length];
            for (int i = 0; i < rowField.length; ++i) {
                Object value = sqlStat.get(rowField[i]);
                row[i] = handleAndConvert(value, rowField[i]);
            }
            printContents.add(row);
        }
        return TableFormatter.format(printContents);
    }

    public static String getVerticalFormattedOutput(List<Map<String, Object>> content, String[] titleFields) {
        List<String[]> printContents = new ArrayList<String[]>();

        int maxCol = content.size() > MAX_COL ? MAX_COL : content.size();

        for (String titleField : titleFields) {
            String[] row = new String[maxCol + 1];
            row[0] = titleField;
            for (int j = 0; j < maxCol; j++) {
                Map<String, Object> sqlStat = content.get(j);
                Object value = sqlStat.get(titleField);
                row[j + 1] = handleAndConvert(value, titleField);
            }
            printContents.add(row);
        }
        return TableFormatter.format(printContents);
    }

    public static String handleAndConvert(Object value, String fieldName) {
        if (value == null) {
            value = "";
        }
        if (fieldName.equals("SQL")) {
            String sql = (String) value;
            sql = sql.replace("\n", " ");
            sql = sql.replace("\t", " ");
            if (sql.length() > SQL_MAX_LEN) {
                sql = sql.substring(0, SQL_MAX_LEN - 3) + "...";
            }
            value = sql;
        }
        return value.toString();
    }

}
