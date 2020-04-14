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
package com.alibaba.druid.sql.builder;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.JdbcUtils;

/**
 * Created by wenshao on 09/07/2017.
 */
public class FunctionBuilder {
    private final String dbType;

    public FunctionBuilder(String dbType) {
        this.dbType = dbType;
    }

    // for character function
    public SQLMethodInvokeExpr length(SQLExpr expr) {
        return new SQLMethodInvokeExpr("length", null, expr);
    }

    public SQLMethodInvokeExpr lower(SQLExpr expr) {
        return new SQLMethodInvokeExpr("lower", null, expr);
    }

    public SQLMethodInvokeExpr upper(SQLExpr expr) {
        return new SQLMethodInvokeExpr("upper", null, expr);
    }

    public SQLMethodInvokeExpr substr(SQLExpr expr) {
        return new SQLMethodInvokeExpr("substr", null, expr);
    }

    public SQLMethodInvokeExpr ltrim(SQLExpr expr) {
        return new SQLMethodInvokeExpr("ltrim", null, expr);
    }

    public SQLMethodInvokeExpr rtrim(SQLExpr expr) {
        return new SQLMethodInvokeExpr("rtrim", null, expr);
    }

    public SQLMethodInvokeExpr trim(SQLExpr expr) {
        return new SQLMethodInvokeExpr("trim", null, expr);
    }

    public SQLMethodInvokeExpr ifnull(SQLExpr expr1, SQLExpr expr2) {
        if (JdbcConstants.ALIYUN_ADS.equals(dbType)
                || JdbcConstants.PRESTO.equals(dbType)
                || JdbcConstants.ODPS.equals(dbType)) {
            return new SQLMethodInvokeExpr("coalesce", null, expr1, expr2);
        }

        if (JdbcUtils.isOracleDbType(dbType)) {
            return new SQLMethodInvokeExpr("nvl", null, expr1, expr2);
        }

        if (JdbcConstants.SQL_SERVER.equals(dbType)) {
            return new SQLMethodInvokeExpr("isnull", null, expr1, expr2);
        }

        return new SQLMethodInvokeExpr("ifnull", null, expr1, expr2);
    }
}
