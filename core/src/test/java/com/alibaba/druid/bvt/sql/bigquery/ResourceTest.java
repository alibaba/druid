package com.alibaba.druid.bvt.sql.bigquery;

import com.alibaba.druid.sql.SQLUtils;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

public class ResourceTest {
    @Test
    public void bigquery_parse() throws Exception {
        parse("bigquery");
    }

    public void parse(String dbType) throws Exception {
        String testDelimiter = "------------------------------------------------------------------------------------------------------------------------";
        String partDelimiter = "--------------------";

        ClassLoader tcl = Thread.currentThread().getContextClassLoader();
        for (int fileIndex = 0; fileIndex < 999; fileIndex++) {
            String path = "bvt/parser/" + dbType + "/" + fileIndex + ".txt";
            URL resource = tcl.getResource(path);
            if (resource == null) {
                break;
            }

            File file = new File(resource.getFile());
            if (!file.exists()) {
                break;
            }

            System.out.println(testDelimiter);
            System.out.println();
            System.out.println("## BEGIN parse sql, file " + path);
            System.out.println(testDelimiter);
            System.out.println();

            String string = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            String[] tests = string.split(testDelimiter);
            for (int i = 0; i < tests.length; i++) {
                String test = tests[i].trim();
                String[] parts = test.split(partDelimiter);
                assertEquals(2, parts.length);

                String sql = parts[0].trim();
                String expected = parts[1].trim();

                System.out.println();
                System.out.println(sql);
                System.out.println();
                System.out.println(partDelimiter +  " [" + (i + 1) + "/" + tests.length + "] " + dbType);
                System.out.println();

                String result = SQLUtils.format(sql, dbType);
                assertEquals(expected, result);

                System.out.println(result);
                System.out.println();
                System.out.println(testDelimiter);
            }
        }
    }
}
