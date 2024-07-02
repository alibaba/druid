package com.alibaba.druid.bvt.sql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.IntFunction;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SQLResourceTest {
    protected final static String DELIMITER_LONG = "------------------------------------------------------------------------------------------------------------------------";
    protected final static String DELIMITER_SHORT = "--------------------";

    protected final DbType dbType;

    public SQLResourceTest(DbType dbType) {
        this.dbType = dbType;
    }

    protected File dir(String path) {
        URL resource = SQLResourceTest.class.getResource(SQLResourceTest.class.getSimpleName() + ".class");
        String classPath = resource.toString();
        if (classPath.startsWith("file:")) {
            classPath = classPath.substring("file:".length());
        }

        String filePath = SQLResourceTest.class.getName().replace('.', '/') + ".class";
        assertTrue(classPath.endsWith(filePath));
        String root = classPath.substring(0, classPath.length() - filePath.length() - 1);

        return new File(new File(root), path);
    }

    public void fileParse(String path) throws Exception {
        File dir = dir(path);
        File[] files = dir.listFiles();
        Arrays.sort(files, Comparator.comparing(File::getName));

        for (File file : files) {
            System.out.println(DELIMITER_LONG);
            System.out.println("## BEGIN parse sql, file " + file);
            System.out.println(DELIMITER_LONG);

            String sql = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            System.out.println(sql);
            System.out.println(DELIMITER_SHORT);

            SQLStatementParser stmtParser = SQLParserUtils.createSQLStatementParser(sql, dbType);

            List<SQLStatement> stmtList;
            try {
                stmtList = stmtParser.parseStatementList();
            } catch (ParserException e) {
                throw new ParserException("parse error, file " + file, e);
            }

            String output = SQLUtils.toSQLString(stmtList, dbType);
            System.out.println(output);

            SQLStatementParser stmtParser2 = SQLParserUtils.createSQLStatementParser(output, dbType);
            List<SQLStatement> stmtList2 = stmtParser2.parseStatementList();
            String output2 = SQLUtils.toSQLString(stmtList2, dbType);

            assertEquals(output, output2);
        }
    }

    public void fileTest(int start, int end, IntFunction<String> pathF) throws Exception {
        ClassLoader tcl = Thread.currentThread().getContextClassLoader();
        for (int fileIndex = start; fileIndex < end; fileIndex++) {
            String path = pathF.apply(fileIndex);
            URL resource = tcl.getResource(path);
            if (resource == null) {
                break;
            }

            File file = new File(resource.getFile());
            if (!file.exists()) {
                break;
            }

            fileTest(file);
        }
    }

    protected void fileTest(File file) throws IOException {
        System.out.println(DELIMITER_LONG);
        System.out.println();
        System.out.println("## BEGIN parse sql, file " + file);
        System.out.println(DELIMITER_LONG);
        System.out.println();

        String string = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        String[] tests = string.split(DELIMITER_LONG);
        for (int i = 0; i < tests.length; i++) {
            String test = tests[i].trim();
            String[] parts = test.split(DELIMITER_SHORT);
            assertEquals(2, parts.length);

            String sql = parts[0].trim();
            String expected = parts[1].trim().replaceAll("\r\n","\n");

            System.out.println();
            System.out.println(sql);
            System.out.println();
            System.out.println(DELIMITER_SHORT +  " [" + (i + 1) + "/" + tests.length + "] " + dbType);
            System.out.println();

            String result = SQLUtils.format(sql, dbType);
            assertEquals(expected, result);

            System.out.println(result);
            System.out.println();
            System.out.println(DELIMITER_LONG);
        }
    }
}
