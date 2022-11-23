package com.alibaba.druid.bvt.sql.odps.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class Issue4933 extends TestCase {
    public void testInsert() {
        String sql = "with \n" +
                "  a as (select * from src where key is not  null),\n" +
                "  b as (select  * from src2 where value>0),\n" +
                "  c as (select * from src3 where value>0),\n" +
                "  d as (select a.key,b.value from a join b on a.key=b.key  ),\n" +
                "  e as (select a.key,c.value from a left outer join c on a.key=c.key and c.key is not null )\n" +
                "  insert overwrite table x select * from y;";
        List<String> tables = new ArrayList<>();
        SQLUtils.acceptTableSource(
                sql,
                DbType.odps,
                e -> tables.add(((SQLExprTableSource)e).getTableName()),
                e -> e instanceof SQLExprTableSource
        );
        assertTrue(tables.contains("src"));
    }
}
