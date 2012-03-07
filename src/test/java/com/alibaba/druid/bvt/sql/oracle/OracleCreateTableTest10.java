package com.alibaba.druid.bvt.sql.oracle;

import java.util.List;

import junit.framework.Assert;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

public class OracleCreateTableTest10 extends OracleTest {

    public void test_0() throws Exception {
        String sql = //
        "create global temporary table sys.ora_temp_1_ds_16247 on commit preserve rows cache noparallel " + //
                "as " + //
                "select /*+ no_parallel(t) no_parallel_index(t) dbms_stats cursor_sharing_exact use_weak_name_resl dynamic_sampling(0) no_monitoring */\"OBJ#\",\"INTCOL#\",\"SAVTIME\",\"FLAGS\",\"NULL_CNT\",\"MINIMUM\",\"MAXIMUM\",\"DISTCNT\",\"DENSITY\",\"LOWVAL\",\"HIVAL\",\"AVGCLN\",\"SAMPLE_DISTCNT\",\"SAMPLE_SIZE\",\"TIMESTAMP#\",\"SPARE1\",\"SPARE2\",\"SPARE3\",\"SPARE4\",\"SPARE5\",\"SPARE6\",SYS_EXTRACT_UTC(\"SAVTIME\") SYS_DS_ALIAS_22 from \"SYS\".\"WRI$_OPTSTAT_HISTHEAD_HISTORY\" sample (  5.8764601401) t  where 1 = 2";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement statemen = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        statemen.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(2, visitor.getTables().size());

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("sys.ora_temp_1_ds_16247")));
        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("SYS.WRI$_OPTSTAT_HISTHEAD_HISTORY")));

        Assert.assertEquals(21, visitor.getColumns().size());

        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("SYS.WRI$_OPTSTAT_HISTHEAD_HISTORY", "OBJ#")));
        // Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("pivot_table", "order_mode")));
    }
}
