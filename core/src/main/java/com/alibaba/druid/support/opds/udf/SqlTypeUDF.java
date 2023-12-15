package com.alibaba.druid.support.opds.udf;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.hive.ast.HiveInsertStatement;
import com.alibaba.druid.sql.dialect.hive.ast.HiveMultiInsertStatement;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsDeclareVariableStatement;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsQueryAliasStatement;
import com.alibaba.druid.sql.parser.*;
import com.aliyun.odps.udf.UDF;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class SqlTypeUDF extends UDF {
    public String evaluate(String sql) {
        return evaluate(sql, null, false);
    }

    public String evaluate(String sql, String dbTypeName) {
        return evaluate(sql, dbTypeName, false);
    }

    static SQLType getSqlType(SQLAlterTableStatement stmt) {
        List<SQLAlterTableItem> items = stmt.getItems();
        SQLAlterTableItem item = null;
        if (items.size() == 1) {
            item = items.get(0);
        } else if (items.size() > 1) {
            item = items.get(0);

            for (int i = 1; i < items.size(); i++) {
                SQLAlterTableItem t = items.get(i);
                if (t != null && t.getClass() != item.getClass()) {
                    item = null;
                    break;
                }
            }
        }

        SQLType sqlType = SQLType.ALTER_TABLE;
        if (item instanceof SQLAlterTableAddPartition) {
            sqlType = SQLType.ALTER_TABLE_ADD_PARTITION;
        } else if (item instanceof SQLAlterTableDropPartition) {
            sqlType = SQLType.ALTER_TABLE_DROP_PARTITION;
        } else if (item instanceof SQLAlterTableMergePartition) {
            sqlType = SQLType.ALTER_TABLE_MERGE_PARTITION;
        } else if (item instanceof SQLAlterTableRenamePartition) {
            sqlType = SQLType.ALTER_TABLE_RENAME_PARTITION;
        } else if (item instanceof SQLAlterTableSetLifecycle) {
            sqlType = SQLType.ALTER_TABLE_SET_LIFECYCLE;
        } else if (item instanceof SQLAlterTableEnableLifecycle) {
            sqlType = SQLType.ALTER_TABLE_ENABLE_LIFECYCLE;
        } else if (item instanceof SQLAlterTableDisableLifecycle) {
            sqlType = SQLType.ALTER_TABLE_DISABLE_LIFECYCLE;
        } else if (item instanceof SQLAlterTableRename) {
            sqlType = SQLType.ALTER_TABLE_RENAME;
        } else if (item instanceof SQLAlterTableAddColumn) {
            sqlType = SQLType.ALTER_TABLE_ADD_COLUMN;
        } else if (item instanceof SQLAlterTableAlterColumn) {
            sqlType = SQLType.ALTER_TABLE_ALTER_COLUMN;
        } else if (item instanceof SQLAlterTableSetOption) {
            sqlType = SQLType.ALTER_TABLE_SET_TBLPROPERTIES;
        } else if (item instanceof SQLAlterTableSetComment) {
            sqlType = SQLType.ALTER_TABLE_SET_COMMENT;
        } else if (item instanceof SQLAlterTableRenameColumn) {
            sqlType = SQLType.ALTER_TABLE_RENAME_COLUMN;
        } else if (item instanceof SQLAlterTableTouch) {
            sqlType = SQLType.ALTER_TABLE_TOUCH;
        } else if (item instanceof SQLAlterTableChangeOwner) {
            sqlType = SQLType.ALTER_TABLE_CHANGE_OWNER;
        }
        return sqlType;
    }

    static SQLType getStmtSqlType(SQLStatement stmt, SQLType sqlType) {
        if (stmt instanceof SQLCreateTableStatement) {
            if (((SQLCreateTableStatement) stmt).getSelect() != null) {
                sqlType = SQLType.CREATE_TABLE_AS_SELECT;
            } else {
                sqlType = SQLType.CREATE_TABLE;
            }
        } else if (stmt instanceof HiveInsertStatement) {
            HiveInsertStatement hiveInsert = (HiveInsertStatement) stmt;
            sqlType = hiveInsert.isOverwrite()
                    ? SQLType.INSERT_OVERWRITE
                    : SQLType.INSERT_INTO;
            if (hiveInsert.getQuery() != null) {
                sqlType = hiveInsert.isOverwrite()
                        ? SQLType.INSERT_OVERWRITE_SELECT
                        : SQLType.INSERT_INTO_SELECT;
            } else if (hiveInsert.getValuesList().size() > 0) {
                sqlType = hiveInsert.isOverwrite()
                        ? SQLType.INSERT_OVERWRITE_VALUES
                        : SQLType.INSERT_INTO_VALUES;
            }
        } else if (stmt instanceof SQLUpdateStatement) {
            sqlType = SQLType.UPDATE;
        } else if (stmt instanceof SQLDeleteStatement) {
            sqlType = SQLType.DELETE;
        } else if (stmt instanceof SQLSelectStatement) {
            sqlType = SQLType.SELECT;
        } else if (stmt instanceof HiveMultiInsertStatement) {
            sqlType = SQLType.INSERT_MULTI;
        } else if (stmt instanceof SQLDropTableStatement) {
            sqlType = SQLType.DROP_TABLE;
        } else if (stmt instanceof SQLDropViewStatement) {
            sqlType = SQLType.DROP_VIEW;
        } else if (stmt instanceof SQLPurgeTableStatement) {
            sqlType = SQLType.PURGE;
        } else if (stmt instanceof SQLShowStatement) {
            if (stmt instanceof SQLShowCatalogsStatement) {
                sqlType = SQLType.SHOW_CATALOGS;
            } else if (stmt instanceof SQLShowCreateTableStatement) {
                sqlType = SQLType.SHOW_CREATE_TABLE;
            } else if (stmt instanceof SQLShowGrantsStatement) {
                sqlType = SQLType.SHOW_GRANTS;
            } else if (stmt instanceof SQLShowRecylebinStatement) {
                sqlType = SQLType.SHOW_RECYCLEBIN;
            } else if (stmt instanceof SQLShowStatisticStmt) {
                sqlType = SQLType.SHOW_STATISTIC;
            } else if (stmt instanceof SQLShowStatisticListStmt) {
                sqlType = SQLType.SHOW_STATISTIC_LIST;
            } else if (stmt instanceof SQLShowTablesStatement) {
                sqlType = SQLType.SHOW_TABLES;
            } else if (stmt instanceof SQLShowUsersStatement) {
                sqlType = SQLType.SHOW_USERS;
            } else if (stmt instanceof SQLShowPartitionsStmt) {
                sqlType = SQLType.SHOW_PARTITIONS;
            } else if (sqlType == null) {
                sqlType = SQLType.SHOW;
            }
        } else if (stmt instanceof OdpsQueryAliasStatement
                || stmt instanceof OdpsDeclareVariableStatement
        ) {
            sqlType = SQLType.SCRIPT;
        } else if (stmt instanceof SQLAlterTableStatement) {
            sqlType = getSqlType((SQLAlterTableStatement) stmt);
        }

        return sqlType;
    }

    public String evaluate(String sql, String dbTypeName, boolean throwError) {
        if (sql == null || sql.isEmpty()) {
            return null;
        }

        DbType dbType = dbTypeName == null ? null : DbType.valueOf(dbTypeName);

        SQLType sqlType = null;
        try {
            List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);

            if (stmtList.isEmpty()) {
                return SQLType.EMPTY.name();
            }

            int setCnt = 0, notSetCnt = 0;
            SQLStatement notSetStmt = null;
            for (SQLStatement stmt : stmtList) {
                if (stmt instanceof SQLSetStatement) {
                    setCnt++;
                } else {
                    notSetStmt = stmt;
                    notSetCnt++;
                }
            }

            if (setCnt == stmtList.size()) {
                Lexer lexer = SQLParserUtils.createLexer(sql, dbType);
                sqlType = lexer.scanSQLTypeV2();
                if (sqlType != null) {
                    return sqlType.name();
                }
                return SQLType.SET.name();
            }

            if (notSetCnt == 1) {
                Lexer lexer = SQLParserUtils.createLexer(sql, dbType);
                sqlType = lexer.scanSQLTypeV2();
                sqlType = SqlTypeUDF.getStmtSqlType(notSetStmt, sqlType);
            } else {
                for (SQLStatement stmt : stmtList) {
                    if (stmt instanceof OdpsQueryAliasStatement
                            || stmt instanceof OdpsDeclareVariableStatement) {
                        return SQLType.SCRIPT.name();
                    }
                }

                Set<String> typeNameSet = new TreeSet<>();
                for (SQLStatement stmt : stmtList) {
                    if (stmt instanceof SQLSetStatement) {
                        continue;
                    }

                    SQLType type = SqlTypeUDF.getStmtSqlType(stmt, null);
                    if (type != null) {
                        typeNameSet.add(type.name());
                    }
                }

                if (typeNameSet.size() == 1) {
                    return typeNameSet.stream().findFirst().get();
                }

                if (typeNameSet.size() > 0) {
                    StringBuilder buf = new StringBuilder();
                    for (String s : typeNameSet) {
                        if (buf.length() != 0) {
                            buf.append(',');
                        }
                        buf.append(s);
                    }
                    return "MULTI:" + buf.toString();
                }
                sqlType = SQLType.MULTI;
            }
        } catch (ParserException ex) {
            sql = sql.trim();
            int semiIndex = sql.indexOf(';');
            if (semiIndex == sql.length() - 1 || semiIndex == -1 && sql.indexOf('\n') == -1) {
                String singleLineSqlType = getSqlTypeForSingleLineSql(sql, dbType);
                if (singleLineSqlType != null) {
                    return singleLineSqlType;
                }
            } else {
                if (sql.lastIndexOf('\n', semiIndex) == -1) {
                    String firstSql = sql.substring(0, semiIndex).trim().toLowerCase();
                    if (firstSql.startsWith("set ")) {
                        String restSql = sql.substring(semiIndex + 1);
                        return evaluate(restSql, dbTypeName, throwError);
                    }
                }
            }

            try {
                Lexer lexer = SQLParserUtils.createLexer(sql, dbType);

                int semiCnt = 0;
                for_:
                for (Token token = null; ; ) {
                    lexer.nextToken();

                    if (token == Token.VARIANT && lexer.token() == Token.COLONEQ) {
                        return SQLType.SCRIPT.name();
                    }

                    token = lexer.token();
                    switch (token) {
                        case EOF:
                        case ERROR:
                            break for_;
                        case SEMI:
                            semiCnt++;
                            break;
                        default:
                            break;
                    }
                }
            } catch (ParserException ignored) {
            }

            sqlType = SQLType.ERROR;

        } catch (Throwable ex) {
            if (throwError) {
                throw new IllegalArgumentException("error sql : \n" + sql, ex);
            }

            sqlType = SQLType.ERROR;
        }

        if (sqlType != null) {
            return sqlType.name();
        }

        return SQLType.UNKNOWN.name();
    }

    private String getSqlTypeForSingleLineSql(String sql, DbType dbType) {
        try {
            SQLType sqlType;
            Lexer lexer = SQLParserUtils.createLexer(sql, dbType);
            sqlType = lexer.scanSQLTypeV2();
            if (sqlType == null) {
                return null;
            }

            if (sqlType == SQLType.WITH) {
                lexer = SQLParserUtils.createLexer(sql, dbType);

                int updateCnt = 0, insertCnt = 0, deleteCnt = 0;
                for_:
                for (Token token = null; ; ) {
                    Token last = token;
                    lexer.nextToken();
                    token = lexer.token();

                    if (last == Token.INSERT) {
                        if (token == Token.OVERWRITE) {
                            return SQLType.INSERT_OVERWRITE_SELECT.name();
                        } else if (token == Token.INTO) {
                            return SQLType.INSERT_INTO_SELECT.name();
                        }
                    }
                    switch (token) {
                        case EOF:
                        case ERROR:
                            break for_;
                        case INSERT:
                            insertCnt++;
                            break;
                        case DELETE:
                            deleteCnt++;
                            break;
                        case UPDATE:
                            updateCnt++;
                            break;
                        default:
                            break;
                    }
                }
                if (updateCnt == 0 && insertCnt == 0 && deleteCnt == 0) {
                    return SQLType.SELECT.name();
                }
            }
            return sqlType.name();
        } catch (ParserException ignored) {
        }
        return null;
    }
}
