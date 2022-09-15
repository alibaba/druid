package com.alibaba.druid.bvt.sql.oracle.visitor;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenshao on 16/8/23.
 */
public class OracleParameterizedOutputVisitorTest_02 extends TestCase {
    public void test_for_parameterize() throws Exception {
        String sql = "/*\n" +
                "  The name + type results of these queries will be used by the Code Assistant\n" +
                "  if the \"Describe Context\" option is enabled. After typing 3 or more characters\n" +
                "  the Code Assistant will show a list of matching names.\n" +
                "  Separate multiple queries with semi-colons and use the :schema bind variable\n" +
                "  to restrict names to the currently connected user.\n" +
                "  In case of an error the query results will be omitted. No error message will\n" +
                "  be displayed.\n" +
                "  Place this file in the PL/SQL Developer installation directory for all users,\n" +
                "  or in the \"%APPDATA%\\PLSQL Developer\" directory for a specific user.\n" +
                "*/\n" +
                "SELECT object_name, object_type\n" +
                "FROM sys.all_objects o\n" +
                "WHERE o.owner = :schema\n" +
                "  AND o.object_type IN (?)";

        List<Object> parameters = new ArrayList<Object>();
        parameters.add(3);
        parameters.add(4);
        String rsql = SQLUtils.format(sql, JdbcConstants.ORACLE, parameters);
        assertEquals("/*\n" +
                "  The name + type results of these queries will be used by the Code Assistant\n" +
                "  if the \"Describe Context\" option is enabled. After typing 3 or more characters\n" +
                "  the Code Assistant will show a list of matching names.\n" +
                "  Separate multiple queries with semi-colons and use the :schema bind variable\n" +
                "  to restrict names to the currently connected user.\n" +
                "  In case of an error the query results will be omitted. No error message will\n" +
                "  be displayed.\n" +
                "  Place this file in the PL/SQL Developer installation directory for all users,\n" +
                "  or in the \"%APPDATA%\\PLSQL Developer\" directory for a specific user.\n" +
                "*/\n" +
                "SELECT object_name, object_type\n" +
                "FROM sys.all_objects o\n" +
                "WHERE o.owner = 3\n" +
                "\tAND o.object_type IN (4)", rsql);
    }
}
