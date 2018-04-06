package com.alibaba.druid.bvt.sql.oracle.select;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.PagerUtils;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;

import java.util.List;

import static com.alibaba.druid.util.JdbcConstants.ORACLE;

public class OracleSelectTest_limit_join_with_order_by extends OracleTest {

    /**
     * 当sql有order by语句时， 分页结果sql中， join语句丢失on的条件
     */
    public void testLimitJoinWithOrderBy(){

        String sql = "select * from A a " +
                " left join B b on a.id = b.id " +
                " where a.id = 10 order by create_time desc";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, ORACLE);

        SQLSelectStatement select = ((SQLSelectStatement)statementList.get(0));

        PagerUtils.limit(select.getSelect(), ORACLE, 0, 200, true);

        String expected = "select XX.*, ROWNUM as RN from ( select * from A a left join B b on a.id = b.id  where a.id = 10 order by create_time desc ) XX where ROWNUM <= 200";
        assertEquals(expected, SQLUtils.format(select.toString(), ORACLE, new SQLUtils.FormatOption(null)));
    }
}
