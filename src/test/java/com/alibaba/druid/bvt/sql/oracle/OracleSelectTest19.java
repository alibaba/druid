package com.alibaba.druid.bvt.sql.oracle;

import java.util.List;

import junit.framework.Assert;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

public class OracleSelectTest19 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
        "SELECT ddf.file_name file_name, vdf.status status, ddf.tablespace_name tablespace_name" + //
                ", '', ddf.autoextensible autoextensible" + //
                "   , ddf.increment_by increment_by, ddf.maxbytes max_file_size, vdf.create_bytes " + //
                "FROM sys.dba_data_files ddf, v$datafile vdf /*+ all_rows use_concat */ " + //
                "WHERE (ddf.file_name = vdf.name) " + //
                "UNION ALL " + //
                "SELECT dtf.file_name file_name, vtf.status status, dtf.tablespace_name tablespace_name" + //
                "   , '', dtf.autoextensible autoextensible, dtf.increment_by increment_by" + //
                "   , dtf.maxbytes max_file_size, vtf.create_bytes " + //
                "FROM sys.dba_temp_files dtf, v$tempfile vtf " + //
                "WHERE (dtf.file_id = vtf.file#) "; //

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);
        System.out.println(output(statementList));

        Assert.assertEquals(1, statementList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        statemen.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(1, visitor.getTables().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("\"DUAL\"")));

        Assert.assertEquals(0, visitor.getColumns().size());

        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("pivot_table", "*")));
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("pivot_table", "YEAR")));
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("pivot_table", "order_mode")));
    }
}
