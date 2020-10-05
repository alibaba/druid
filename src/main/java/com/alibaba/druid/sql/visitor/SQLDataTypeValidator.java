package com.alibaba.druid.sql.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.FastsqlException;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.SQLCaseExpr;
import com.alibaba.druid.sql.ast.statement.SQLCharacterDataType;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.util.FnvHash;
import com.alibaba.druid.util.HiveUtils;
import com.alibaba.druid.util.MySqlUtils;
import com.alibaba.druid.util.OdpsUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SQLDataTypeValidator extends SQLASTVisitorAdapter {
    private long[] supportTypeHashCodes;

    public SQLDataTypeValidator(String[] supportTypes) {
        this.supportTypeHashCodes = FnvHash.fnv1a_64_lower(supportTypes, true);
    }

    public SQLDataTypeValidator(Set<String> typeSet) {
        this.supportTypeHashCodes = new long[typeSet.size()];

        int i = 0;
        for (String type : typeSet) {
            this.supportTypeHashCodes[i++] = FnvHash.fnv1a_64_lower(type);
        }
        Arrays.sort(supportTypeHashCodes);
    }

    public boolean visit(SQLDataType x) {
        validate(x);
        return true;
    }

    public boolean visit(SQLCharacterDataType x) {
        validate(x);
        return true;
    }

    public boolean visit(SQLArrayDataType x) {
        validate(x);
        return true;
    }

    public boolean visit(SQLMapDataType x) {
        validate(x);
        return true;
    }

    public boolean visit(SQLStructDataType x) {
        validate(x);
        return true;
    }

    public void validate(SQLDataType x) {
        long hash = x.nameHashCode64();
        if (Arrays.binarySearch(supportTypeHashCodes, hash) < 0) {
            String msg = "illegal dataType : " + x.getName();

            final SQLObject parent = x.getParent();
            if (parent instanceof SQLColumnDefinition) {
                SQLColumnDefinition column = (SQLColumnDefinition) parent;
                if (column.getName() != null) {
                    msg += ", column " + column.getName();
                }
            }
            throw new FastsqlException(msg);
        }
    }

    private static String[] odpsTypes = null;
    private static String[] hiveTypes = null;
    private static String[] mysqlTypes = null;

    public static SQLDataTypeValidator of(DbType dbType) {
        Set<String> typeSet = null;
        String[] types = null;
        switch (dbType) {
            case odps: {
                types = odpsTypes;
                if (types == null) {
                    typeSet = new HashSet<String>();
                    OdpsUtils.loadDataTypes(typeSet);
                }
                break;
            }
            case hive: {
                types = hiveTypes;
                if (types == null) {
                    typeSet = new HashSet<String>();
                    HiveUtils.loadDataTypes(typeSet);
                }
                break;
            }
            case mysql: {
                types = mysqlTypes;
                if (types == null) {
                    typeSet = new HashSet<String>();
                    MySqlUtils.loadDataTypes(typeSet);
                }
                break;
            }
            default:
                break;
        }

        if (types == null && typeSet != null) {
            types = typeSet.toArray(new String[typeSet.size()]);
        }

        if (types == null) {
            throw new FastsqlException("dataType " + dbType + " not support.");
        }

        return new SQLDataTypeValidator(types);
    }

    public static void check(SQLStatement stmt) {
        SQLDataTypeValidator v = of(stmt.getDbType());
        stmt.accept(v);
    }

    public static void check(List<SQLStatement> stmtList) {
        if (stmtList.size() == 0) {
            return;
        }

        DbType dbType = stmtList.get(0).getDbType();
        SQLDataTypeValidator v = of(dbType);

        check(stmtList, dbType);
    }

    public static void check(List<SQLStatement> stmtList, DbType dbType) {
        if (stmtList.size() == 0) {
            return;
        }

        SQLDataTypeValidator v = of(dbType);

        for (SQLStatement stmt : stmtList) {
            stmt.accept(v);
        }
    }
}
