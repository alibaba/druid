package com.alibaba.druid.bvt.sql.oracle.issues;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Oracle ALTER TABLE ... ADD CONSTRAINT ... USING INDEX referencing an existing (possibly
 * schema-qualified) index name.
 *
 * @see <a href="https://github.com/alibaba/druid/issues/6458">Issue #6458</a>
 */
public class Issue6458 {
    @Test
    public void test_using_index_qualified_name() {
        String sql = "ALTER TABLE \"SC\".\"TB1\"\n"
                + "\tADD CONSTRAINT \"PK_XXX\" PRIMARY KEY (\"ID\")\n"
                + "\t\tUSING INDEX \"SC\".\"PK_XXX\"\n"
                + "\t\tENABLE";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.oracle);
        assertEquals(1, stmts.size());
        String out = SQLUtils.toSQLString(stmts.get(0), DbType.oracle);
        assertTrue(out.contains("USING INDEX \"SC\".\"PK_XXX\""), out);
        assertTrue(out.contains("ENABLE"), out);
    }

    @Test
    public void test_using_index_unquoted_name() {
        String sql = "ALTER TABLE t ADD CONSTRAINT pk PRIMARY KEY (id) USING INDEX my_index ENABLE";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, DbType.oracle);
        assertEquals(1, stmts.size());
        String out = SQLUtils.toSQLString(stmts.get(0), DbType.oracle);
        assertTrue(out.contains("USING INDEX my_index"), out);
        assertTrue(out.contains("ENABLE"), out);
    }
}
