package com.alibaba.druid.bvt.sql.mysql.resolve;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.repository.SchemaRepository;
import com.alibaba.druid.sql.repository.SchemaResolveVisitor;
import junit.framework.TestCase;

public class Resolve_AllColumn_Test_1 extends TestCase {
    public void test_resolve() throws Exception {
        SchemaRepository repository = new SchemaRepository(DbType.mysql);

        repository.acceptDDL("create table t_emp(emp_id bigint, name varchar(20));");


        SQLStatement stmt = SQLUtils.parseSingleMysqlStatement("select 1 as tag, * from t_emp");
        repository.resolve(stmt, SchemaResolveVisitor.Option.ResolveAllColumn);

        assertEquals("SELECT 1 AS tag, emp_id, name\n" +
                "FROM t_emp", stmt.toString());


    }
}
