package com.alibaba.druid.bvt.sql.mysql;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.spi.MySqlWallProvider;
import junit.framework.TestCase;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.URL;
import java.util.List;

public class AntlrMySqlTest extends TestCase {
    public void test_for_antlr_examples() throws Exception {
        SchemaStatVisitor schemaStatVisitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.MYSQL);
        WallConfig config = new WallConfig();
        config.setConditionDoubleConstAllow(true);
        config.setConditionAndAlwayTrueAllow(true);
        config.setSelectIntoOutfileAllow(true);
        config.setSelectWhereAlwayTrueCheck(false); //FIXME 此处是否要禁用审核h
        config.setSelectUnionCheck(false); //FIXME 此处是否要禁用审核
        config.setCommentAllow(true);
        config.setHintAllow(true);
        MySqlWallProvider provider = new MySqlWallProvider(config);

        String path = "bvt/parser/antlr_grammers_v4_mysql/examples/";
        URL resource = Thread.currentThread().getContextClassLoader().getResource(path);
        File dir = new File(resource.getFile());
        for (File file : dir.listFiles()) {
            System.out.println(file);
            String sql = FileUtils.readFileToString(file);

            List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
            for (SQLStatement stmt : stmtList) {
                String stmtSql = stmt.toString();

                stmt.accept(schemaStatVisitor);
                assertTrue(stmtSql, provider.checkValid(stmtSql));
            }
            
            // test different style newline.
            if (sql.indexOf("\r\n") == -1) {
                sql = sql.replace("\n", "\r\n");
            } else {
                sql = sql.replace("\r\n", "\n");
            }

            stmtList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
            for (SQLStatement stmt : stmtList) {
                String stmtSql = stmt.toString();

                stmt.accept(schemaStatVisitor);
                assertTrue(provider.checkValid(stmtSql));
            }

        }
    }
}
