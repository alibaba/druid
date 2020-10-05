package com.alibaba.druid.sql.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.util.FnvHash;

import java.util.*;

public class SQLSubQueryGroupVisitor extends SQLASTVisitorAdapter {
    private final DbType dbType;

    protected Map<Long, List<SQLSubqueryTableSource>> tableSourceMap = new LinkedHashMap<Long, List<SQLSubqueryTableSource>>();

    public SQLSubQueryGroupVisitor(DbType dbType) {
        this.dbType = dbType;
    }

    public boolean visit(SQLSubqueryTableSource x) {
        String sql = SQLUtils.toSQLString(x.getSelect(), dbType);
        long hashCode64 = FnvHash.fnv1a_64(sql);
        List<SQLSubqueryTableSource> list = tableSourceMap.get(hashCode64);
        if (list == null) {
            list = new ArrayList<SQLSubqueryTableSource>();
            tableSourceMap.put(hashCode64, list);
        }
        list.add(x);

        return true;
    }

    public Collection<List<SQLSubqueryTableSource>> getGroupedSubqueryTableSources() {
        return tableSourceMap.values();
    }

}
