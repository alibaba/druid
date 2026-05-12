package com.alibaba.druid.filter.config;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.util.JdbcUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Jonas Yang
 */
public class ConfigFileGenerator {
    protected String filePath;

    @BeforeEach
    public void setUp() {
        PrintWriter out = null;

        try {
            File file = File.createTempFile("MyTest", Long.toString(System.nanoTime()));
            filePath = file.getAbsolutePath();

            out = new PrintWriter(new FileWriter(file));
            out.println(DruidDataSourceFactory.PROP_MAXWAIT + "=1000");
            out.println(DruidDataSourceFactory.PROP_USERNAME + "=test1");
            out.println(DruidDataSourceFactory.PROP_PASSWORD + "=OJfUm6WCHi7EuXqE6aEc+Po2xFrAGBeSNy8O2jWhV2FTG8/5kbRRr2rjNKhptlevm/03Y0048P7h88gdUOXAYg==");
            out.println(DruidDataSourceFactory.PROP_URL + "=jdbc:oracle:thin:@");
        } catch (IOException e) {
            assertNull(e, "Failed to init resource.");
        } finally {
            JdbcUtils.close(out);
        }
    }

    @AfterEach
    public void tearDown() {
        if (this.filePath == null) {
            return;
        }

        File file = new File(this.filePath);
        if (file.exists()) {
            file.delete();
        }
    }
}
