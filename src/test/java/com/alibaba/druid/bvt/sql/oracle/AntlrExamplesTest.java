package com.alibaba.druid.bvt.sql.oracle;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.URL;

public class AntlrExamplesTest extends TestCase {
    public void test_for_antlr_examples() throws Exception {
        String path = "bvt/parser/antlr_grammers_v4_plsql/examples/";
        URL resource = Thread.currentThread().getContextClassLoader().getResource(path);
        File dir = new File(resource.getFile());
        for (File file : dir.listFiles()) {
            System.out.println(file);
            String sql = FileUtils.readFileToString(file);
            try {
                SQLUtils.parseStatements(sql, JdbcConstants.ORACLE);
            } catch (ParserException ex) {
                System.out.println(sql);
                throw ex;
            }
        }
    }
}
