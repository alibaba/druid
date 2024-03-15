package com.alibaba.druid.bvt.sql.mysql.issues;

import java.util.List;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author lizongbo
 * @see <a href="https://github.com/alibaba/druid/issues/5774">Issue来源</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/create-user.html">CREATE USER Statement</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/account-names.html">Specifying Account Names</a>
 */
public class Issue5774 {


    @Test
    public void test_createuser_sql() {
        for (String sql : new String[]{
            "create user IF NOT EXISTS \"ptscr-2kaq\"@\"%\" identified by \"asdasdasdasd\";",
            "create user IF NOT EXISTS \"ptscr-2kaq\" identified by \"asdasdasdasd\";",
            "create user \"ptscr-2kaq\"@\"%\" identified by \"asdasdasdasd\";",
            "create user \"ptscr-2kaq\"@\"%\" identified by RANDOM PASSWORD;",
            "CREATE USER 'jeffrey'@'localhost' IDENTIFIED BY 'password';",
            "CREATE USER 'jeffrey'@'localhost'\n"
                + "  IDENTIFIED BY 'password';",
            "CREATE USER 'jeffrey'@localhost IDENTIFIED BY 'password';",
//            "CREATE USER 'jeffrey'@'localhost'\n"
//                + "  IDENTIFIED BY 'new_password' PASSWORD EXPIRE;",
            "CREATE USER 'jeffrey'@'localhost'\n"
                + "  IDENTIFIED WITH mysql_native_password BY 'password';",
//            "CREATE USER 'u1'@'localhost'\n"
//                + "  IDENTIFIED WITH caching_sha2_password\n"
//                + "    BY 'sha2_password'\n"
//                + "  AND IDENTIFIED WITH authentication_ldap_sasl\n"
//                + "    AS 'uid=u1_ldap,ou=People,dc=example,dc=com';",
//            "CREATE USER 'jeffrey'@'localhost' PASSWORD EXPIRE;",
//            "CREATE USER 'jeffrey'@'localhost' PASSWORD EXPIRE DEFAULT;",
//            "CREATE USER 'jeffrey'@'localhost' PASSWORD EXPIRE NEVER;",
        }) {
            DbType dbType = DbType.mysql;
            System.out.println("原始的sql===" + sql);
            SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
            List<SQLStatement> statementList = parser.parseStatementList();
            System.out.println("生成的sql===" + statementList);
            StringBuilder sb = new StringBuilder();
            for (SQLStatement statement : statementList) {
                sb.append(statement.toString()).append(";");
            }
            sb.deleteCharAt(sb.length() - 1);
            parser = SQLParserUtils.createSQLStatementParser(sb.toString(), dbType);
            List<SQLStatement> statementListNew = parser.parseStatementList();
            System.out.println("再生成sql===" + statementListNew);
            assertEquals(statementList.toString(), statementListNew.toString());
        }
    }
}
