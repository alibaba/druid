package com.alibaba.druid.bvt.sql.schemaStat;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;
import org.junit.Assert;

import java.util.Set;

public class SchemaStatTest12 extends TestCase {

    public void test_schemaStat() throws Exception {
        String sql = "select work_no , nick_name , name , ifnull(sum(investment_percentage ) / 100 , 0 ) as invest_percent " +
                "from ( " +
                "   select t1.id , t1.work_no , t2.nick_name , t2.name , t2.super_work_no , t2.super_nick_name , t2.super_name " +
                "       , t3.project_name , t1.investment_percentage , t4.dept_name " +
                "   from t_pm_aur t2 " +
                "       left outer join ( " +
                "           select * from t_pm_rs_acl " +
                "           where is_deleted = 0 and prj_biz_id is not null and ivt_pt > 0 and iwk = '2016W17' " +
                "       ) t1 on t2.work_no = t1.work_no " +
                "       left outer join ( " +
                "           select * from t_pm_prj_i " +
                "           where( status not in( 'DEPRECATED' , 'FINISHED' ) or status is null ) and( task_type not in( 'WEEKLY_REPORT' ) or task_type is null ) and is_deleted = 0 " +
                "       ) t3 on t1.prj_biz_id = t3.biz_id " +
                "       left outer join t_pm_dpt t4 on t2.dept_no = t4.biz_id " +
                "   where t4.dept_name like 'MY-XDSYB-JSB%' and t2.emp_type in( 'R' , 'V' ) and t2.is_deleted = 'N' and t2.work_status = 'A' " +
                ") t5 group by work_no , nick_name , name having invest_percent < 1.0 ";

        String dbType = JdbcConstants.ORACLE;
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        SQLStatement stmt = parser.parseStatementList().get(0);

        System.out.println(stmt);

        SchemaStatVisitor statVisitor = SQLUtils.createSchemaStatVisitor(dbType);
        stmt.accept(statVisitor);

        Set<TableStat.Relationship> relationships = statVisitor.getRelationships();
        for (TableStat.Relationship relationship : relationships) {
            System.out.println(relationship); // table1.id = table2.id
        }

        System.out.println("columns : " + statVisitor.getColumns());
        System.out.println("group by : " + statVisitor.getGroupByColumns());
        System.out.println("relationships : " + statVisitor.getRelationships());
        System.out.println("conditions : " + statVisitor.getConditions());
        System.out.println("functionns : " + statVisitor.getFunctions());
        assertEquals(3, relationships.size());

        Assert.assertEquals(21, statVisitor.getColumns().size());
        Assert.assertEquals(19, statVisitor.getConditions().size());
        assertEquals(1, statVisitor.getFunctions().size());
    }
}
