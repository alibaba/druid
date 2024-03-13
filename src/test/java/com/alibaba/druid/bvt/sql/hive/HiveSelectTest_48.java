package com.alibaba.druid.bvt.sql.hive;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.List;

public class HiveSelectTest_48 extends TestCase {


    public void test_select() throws Exception {
        String sql = "SELECT account_id FROM taobao_office.cloud_yunpan WHERE dt='2021-04-12' AND target='update'";
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.HIVE);
        SQLStatement sqlStatement = stmtList.get(0);
        SQLSelectStatement stmt = (SQLSelectStatement) sqlStatement;
        List<SQLSelectItem> list = ((SQLSelectQueryBlock) stmt.getSelect().getQuery()).getSelectList();

    }
}
