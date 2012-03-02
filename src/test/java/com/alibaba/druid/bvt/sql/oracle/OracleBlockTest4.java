package com.alibaba.druid.bvt.sql.oracle;

import java.util.List;

import junit.framework.Assert;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

public class OracleBlockTest4 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "declare "
                     + " cursor cspid(vspid dba_hist_snapshot.snap_id%type) is "
                     + "  select end_interval_time , startup_time " //
                     + "  from dba_hist_snapshot "
                     + //
                     "  where snap_id = vspid and instance_number = :inst_num"
                     + "  and dbid            = :dbid;    "
                     + "  bsnapt  dba_hist_snapshot.end_interval_time%type;   bstart  dba_hist_snapshot.startup_time%type;   esnapt  dba_hist_snapshot.end_interval_time%type;   estart  dba_hist_snapshot.startup_time%type;  begin    -- Check Begin Snapshot id is valid, get corresponding instance startup time   open cspid(:bid);   fetch cspid into bsnapt, bstart;   if cspid%notfound then     raise_application_error(-20200,       'Begin Snapshot Id '||:bid||' does not exist for this database/instance');   end if;   close cspid;    -- Check End Snapshot id is valid and get corresponding instance startup time   open cspid(:eid);   fetch cspid into esnapt, estart;   if cspid%notfound then     raise_application_error(-20200,       'End Snapshot Id '||:eid||' does not exist for this database/instance');   end if;   if esnapt <= bsnapt then     raise_application_error(-20200,       'End Snapshot Id '||:eid||' must be greater than Begin Snapshot Id '||:bid);   end if;   close cspid;    -- Check startup time is same for begin and end snapshot ids   if ( bstart != estart) then     raise_application_error(-20200,       'The instance was shutdown between snapshots '||:bid||' and '||:eid);   end if;  end;";

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

        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("departments")));
        Assert.assertTrue(visitor.getTables().containsKey(new TableStat.Name("employees")));

        Assert.assertEquals(7, visitor.getColumns().size());

        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("departments", "department_id")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "department_id")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "employee_id")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "commission_pct")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("employees", "department_id")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("UNKNOWN", "job_id")));
        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("UNKNOWN", "location_id")));
    }
}
