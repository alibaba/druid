package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.repository.SchemaRepository;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * TODO 功能描述
 *
 * @author lijun.cailj 2018/3/8
 */
public class MysqlResolveTest {
    @Test
    public void test_1() {
        SchemaRepository repository = new SchemaRepository(DbType.mysql);

        repository.console("create table t_emp(emp_id bigint, name varchar(20));");
        repository.console("create table t_org(org_id bigint, name varchar(20));");

        String sql = "delete from t_emp where name = '12'";

        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, DbType.mysql);
        assertEquals(1, stmtList.size());

        SQLDeleteStatement stmt = (SQLDeleteStatement) stmtList.get(0);

        repository.resolve(stmt);
    }
}
