package com.alibaba.druid.bvt.sql.oracle;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.util.JdbcConstants;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.net.URL;

public class AntlrScriptExamplesTest {
    @Test
    public void test_for_antlr_examples() throws Exception {
        String path = "bvt/parser/antlr_grammers_v4_plsql/examples-sql-script/";
        URL resource = Thread.currentThread().getContextClassLoader().getResource(path);
        File dir = new File(resource.getFile());
        for (File file : dir.listFiles()) {
            System.out.println(file);
            String sql = FileUtils.readFileToString(file);
            SQLUtils.parseStatements(sql, JdbcConstants.ORACLE);
        }
    }
}
