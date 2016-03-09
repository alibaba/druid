package com.alibaba.druid.bvt.sql.schemaStat;

import org.junit.Assert;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.odps.parser.OdpsStatementParser;
import com.alibaba.druid.sql.dialect.odps.visitor.OdpsSchemaStatVisitor;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;

import junit.framework.TestCase;

public class SchemaStatTest extends TestCase {
    public void test_schemaStat() throws Exception {
        String sql = "select "
                + " create_time_dd          as  来电日期"
                + " from alisec_app.adl_tb_wing_rubbish_laidian_new_reason_realname_fdt  "
                + " order by   来电日期  desc  limit  30;";
        
        SQLStatementParser parser = new OdpsStatementParser(sql);
        SQLStatement stmt = parser.parseStatementList().get(0);
        
        SchemaStatVisitor statVisitor = new OdpsSchemaStatVisitor();
        stmt.accept(statVisitor);
        
        System.out.println(statVisitor.getColumns());
        
        Assert.assertEquals(1, statVisitor.getColumns().size());
    }
}
