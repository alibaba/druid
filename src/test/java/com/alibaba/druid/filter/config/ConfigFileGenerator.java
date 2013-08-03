package com.alibaba.druid.filter.config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.junit.Assert;

import org.junit.After;
import org.junit.Before;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.util.JdbcUtils;

/**
 * @author Jonas Yang
 */
public class ConfigFileGenerator {

    protected String filePath;

    @Before
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
            Assert.assertNull("Failed to init resource.", e);
        } finally {
            JdbcUtils.close(out);
        }
    }

    @After
    public void tearDown() {
        if (this.filePath == null) {
            return;
        }

        File file = new File(this.filePath);
        if(file.exists()) {
            file.delete();
        }
    }

}
