package com.alibaba.druid.filter.index;

import com.alibaba.druid.filter.FilterEventAdapter;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.StringUtils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @date 2018-03-22
 * @author willenfoo
 * 由于开发人员水平参差不齐，即使订了开发规范很多人也不遵守
 * SQL是影响系统最重要的因素，所以拦截掉垃圾SQL语句
 *
 * 拦截SQL类型的场景
 * 1.查询left jion 超过3张表
 * 2.在字段上使用函数
 * 3.where条件为空
 * 4.where条件使用了 !=
 * 5.where条件使用了 not 关键字
 * 6.where条件没有索引索引，最左原则
 */
public class IllegalSQLIndexFilter extends FilterEventAdapter {

    private static Map<String, List<IndexInfo>> indexInfoMap = new ConcurrentHashMap<String, List<IndexInfo>>();

    private String dbType;

    @Override
    protected void statementExecuteBefore(StatementProxy statement, String sql) {
        MySqlStatementParser sqlStatementParser = new MySqlStatementParser(sql);
        String tokenName = sqlStatementParser.getLexer().token().name();

        if (Token.SELECT.name().equals(tokenName) || Token.UPDATE.name().equals(tokenName)) {
            //解析select查询
            List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
            SQLStatement stmt = stmtList.get(0);
            MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
            stmt.accept(visitor);

            Map<TableStat.Name, TableStat> tableMap = visitor.getTables();
            if (tableMap.size() > 3) {
                throw new RuntimeException("SQL不允许超过3张表操作");
            }
            //不允许使用函数
            List<SQLMethodInvokeExpr> sqlMethodInvokeExprs = visitor.getFunctions();
            if (sqlMethodInvokeExprs != null && !sqlMethodInvokeExprs.isEmpty()) {
                throw new RuntimeException("请不要在where条件中使用【函数】");
            }
            //TODO 不允许使用子查询

            //查询用到的字段
            List<TableStat.Condition> conditions = visitor.getConditions();
            if (conditions == null || conditions.isEmpty()) {
                throw new RuntimeException("请带上where条件");
            } else {
                for (TableStat.Condition condition: conditions) {
                    //不允许使用不等于
                    if (StringUtils.equals("!=", condition.getOperator())) {
                        throw new RuntimeException("请不要在where条件中使用【!=】");
                    }
                    //不允许使用NOT关键字
                    else if (condition.getOperator().indexOf("NOT") >= 0) {
                        throw new RuntimeException("请不要在where条件中使用【not】");
                    }
                }

                List<String> tableIndexValid = new ArrayList<String>();
                //索引最左原则，得到查询条件中的第一个字段
                for (TableStat.Condition condition: conditions) {
                    String tableInfo = condition.getColumn().getTable();
                    //如果表已经做了验证，不在做验证了
                    if (tableIndexValid.contains(tableInfo)) {
                        continue;
                    }
                    //是否使用索引
                    boolean useIndexFlag = false;
                    //表存在的索引
                    String dbName = null;
                    String tableName = null;
                    String[] tableArray = tableInfo.split("\\.");;
                    if (tableArray.length == 1) {
                        tableName = tableArray[0];
                    } else {
                        dbName = tableArray[0];
                        tableName = tableArray[1];
                    }
                    List<IndexInfo> indexInfos = getIndexInfos(tableInfo, dbName, tableName, statement);
                    //查询条件中的第一个字段
                    String findFirstColumnName = condition.getColumn().getName();
                    for (IndexInfo indexInfo: indexInfos) {
                        if (StringUtils.equals(findFirstColumnName, indexInfo.getColumnName())) {
                            useIndexFlag = true;
                            break;
                        }
                    }
                    if (!useIndexFlag) {
                        throw new RuntimeException("SQL未使用到索引");
                    }
                    tableIndexValid.add(tableInfo);
                }
            }
        }
        super.statementExecuteBefore(statement, sql);
    }

    public List<IndexInfo> getIndexInfos(String key, String dbName, String tableName, StatementProxy statement) {
        List<IndexInfo> indexInfos = indexInfoMap.get(key);
        if (indexInfos == null || indexInfos.isEmpty()) {
            Connection conn = null;
            ResultSet rs = null;
            try {
                conn = statement.getConnectionProxy();
                DatabaseMetaData metadata = conn.getMetaData();
                rs = metadata.getIndexInfo(dbName, dbName, tableName, false, true);
                indexInfos = new ArrayList<IndexInfo>();
                while (rs.next()) {
                    //索引中的列序列号等于1，才有效
                    if (StringUtils.equals(rs.getString(8), "1")) {
                        IndexInfo indexInfo = new IndexInfo();
                        indexInfo.setDbName(rs.getString(1));
                        indexInfo.setTableName(rs.getString(3));
                        indexInfo.setColumnName(rs.getString(9));
                        indexInfos.add(indexInfo);
                    }
                }
                indexInfoMap.put(key, indexInfos);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return indexInfos;
    }

    public static Map<String, List<IndexInfo>> getIndexInfoMap() {
        return indexInfoMap;
    }

    public static void setIndexInfoMap(Map<String, List<IndexInfo>> indexInfoMap) {
        IllegalSQLIndexFilter.indexInfoMap = indexInfoMap;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public static class IndexInfo {

        private String dbName;

        private String tableName;

        private String columnName;

        public String getDbName() {
            return dbName;
        }

        public void setDbName(String dbName) {
            this.dbName = dbName;
        }

        public String getTableName() {
            return tableName;
        }

        public void setTableName(String tableName) {
            this.tableName = tableName;
        }

        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }
    }

}