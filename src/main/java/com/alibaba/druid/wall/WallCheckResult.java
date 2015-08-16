/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.wall;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.sql.ast.SQLStatement;

public class WallCheckResult {

    private final List<SQLStatement>               statementList;
    private final Map<String, WallSqlTableStat>    tableStats;

    private final List<Violation>                  violations;

    private final Map<String, WallSqlFunctionStat> functionStats;

    private final boolean                          syntaxError;

    private final WallSqlStat                      sqlStat;

    private String                                 sql;

    public WallCheckResult(){
        this(null);
    }

    public WallCheckResult(WallSqlStat sqlStat, List<SQLStatement> stmtList){
        if (sqlStat != null) {
            tableStats = sqlStat.getTableStats();
            violations = sqlStat.getViolations();
            functionStats = sqlStat.getFunctionStats();
            statementList = stmtList;
            syntaxError = sqlStat.isSyntaxError();
        } else {
            tableStats = Collections.emptyMap();
            violations = Collections.emptyList();
            functionStats = Collections.emptyMap();
            statementList = stmtList;
            syntaxError = false;
        }
        this.sqlStat = sqlStat;
    }

    public WallCheckResult(WallSqlStat sqlStat){
        this(sqlStat, Collections.<SQLStatement> emptyList());
    }

    public WallCheckResult(WallSqlStat sqlStat, List<Violation> violations, Map<String, WallSqlTableStat> tableStats,
                           Map<String, WallSqlFunctionStat> functionStats, List<SQLStatement> statementList,
                           boolean syntaxError){
        this.sqlStat = sqlStat;
        this.tableStats = tableStats;
        this.violations = violations;
        this.functionStats = functionStats;
        this.statementList = statementList;
        this.syntaxError = syntaxError;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<Violation> getViolations() {
        return violations;
    }

    public List<SQLStatement> getStatementList() {
        return statementList;
    }

    public Map<String, WallSqlTableStat> getTableStats() {
        return tableStats;
    }

    public Map<String, WallSqlFunctionStat> getFunctionStats() {
        return functionStats;
    }

    public boolean isSyntaxError() {
        return syntaxError;
    }

    public WallSqlStat getSqlStat() {
        return sqlStat;
    }
}
