package com.alibaba.druid.bvt.sql.mysql.transform;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateViewStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.transform.FromSubqueryResolver;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

/**
 * Created by wenshao on 16/07/2017.
 */
public class Trans_CreateView_2 extends TestCase {
    public void test_createView() throws Exception {
        String sql = "CREATE OR REPLACE VIEW \"AUTH\".\"HR_DEPTEMP_VIEW\" (\n" +
                "\t\"MY_PK\", \n" +
                "\t\"FK_EMP\", \n" +
                "\t\"FK_DEPT\", \n" +
                "\t\"FK_DUTY\", \n" +
                "\t\"DUTYLEVEL\", \n" +
                "\t\"LEADER\", \n" +
                "\t\"DEPT\", \n" +
                "\t\"STORENO\", \n" +
                "\t\"AREA\", \n" +
                "\t\"TYPE\"\n" +
                ")\n" +
                "AS\n" +
                "WITH struct AS (\n" +
                "\t\tSELECT d.code AS dept, s.code AS storeno, a.code AS area, 'S' AS type\n" +
                "\t\tFROM (\n" +
                "\t\t\tSELECT code, name, sjcode\n" +
                "\t\t\tFROM hr_structure\n" +
                "\t\t\tWHERE type = 'A'\n" +
                "\t\t) a, (\n" +
                "\t\t\tSELECT code, name, sjcode\n" +
                "\t\t\tFROM hr_structure\n" +
                "\t\t\tWHERE type = 'S'\n" +
                "\t\t) s, (\n" +
                "\t\t\tSELECT code, name, sjcode\n" +
                "\t\t\tFROM hr_structure\n" +
                "\t\t\tWHERE type = 'D'\n" +
                "\t\t) d\n" +
                "\t\tWHERE d.sjcode = s.code\n" +
                "\t\t\tAND s.sjcode = a.code\n" +
                "\t\tUNION\n" +
                "\t\tSELECT d.code AS dept, NULL AS storeno, a.code AS area, 'A' AS type\n" +
                "\t\tFROM (\n" +
                "\t\t\tSELECT code, name, sjcode\n" +
                "\t\t\tFROM hr_structure\n" +
                "\t\t\tWHERE type = 'A'\n" +
                "\t\t) a, (\n" +
                "\t\t\tSELECT code, name, sjcode\n" +
                "\t\t\tFROM hr_structure\n" +
                "\t\t\tWHERE type = 'D'\n" +
                "\t\t) d\n" +
                "\t\tWHERE d.sjcode = a.code\n" +
                "\t\tUNION\n" +
                "\t\tSELECT d.code AS dept, NULL AS storeno, g.code AS area, 'G' AS type\n" +
                "\t\tFROM (\n" +
                "\t\t\tSELECT code, name, sjcode\n" +
                "\t\t\tFROM hr_structure\n" +
                "\t\t\tWHERE type = 'G'\n" +
                "\t\t) g, (\n" +
                "\t\t\tSELECT code, name, sjcode\n" +
                "\t\t\tFROM hr_structure\n" +
                "\t\t\tWHERE type = 'D'\n" +
                "\t\t) d\n" +
                "\t\tWHERE d.sjcode = g.code\n" +
                "\t)\n" +
                "SELECT \"MY_PK\", \"FK_EMP\", \"FK_DEPT\", \"FK_DUTY\", \"DUTYLEVEL\"\n" +
                "\t, \"LEADER\", \"DEPT\", \"STORENO\", \"AREA\", \"TYPE\"\n" +
                "FROM hr_deptemp t1, struct t2\n" +
                "WHERE t1.FK_DEPT = t2.dept";

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.ORACLE);
        assertEquals(1, stmtList.size());

        SQLStatement stmt = stmtList.get(0);
        List<SQLStatement> targetList = FromSubqueryResolver.resolve((SQLCreateViewStatement) stmt);
        String targetSql = SQLUtils.toSQLString(targetList, JdbcConstants.ORACLE);
        assertEquals(9, targetList.size());
        System.out.println(targetSql);
    }
}
