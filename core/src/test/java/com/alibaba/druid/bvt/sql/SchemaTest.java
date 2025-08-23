package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.repository.SchemaRepository;
import static org.junit.Assert.*;
import org.junit.Test;

public class SchemaTest {
    @Test
    public void test_schema() {
        SchemaRepository repository = new SchemaRepository(DbType.gaussdb);
        repository.acceptDDL("create table test.test(a int) partition by list (dt) (partition p1 values(20251111))");
        String sql = "alter table test.test truncate partition for (to_date('${day}')-1);";
        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, DbType.gaussdb);
        SQLStatement sqlStatement = parser.parseStatement();
        repository.accept(sqlStatement);
        assertNotNull(((SQLAlterTableStatement) sqlStatement).getTableSource().getSchemaObject());
    }
}
