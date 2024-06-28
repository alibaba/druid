package com.alibaba.druid.bvt.utils;

import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.support.json.JSONWriter;

public class JSONWriterTest extends TestCase {
    public void test_intArray() throws Exception {
        JSONWriter writer = new JSONWriter();
        writer.writeObject(new int[]{1, 2, 3});
        Assert.assertEquals("[1,2,3]", writer.toString());
    }

    public void test_throwable() throws Exception {
        JSONWriter writer = new JSONWriter();
        writer.writeObject(new Throwable() {
            public void printStackTrace(PrintWriter s) {
            }
        });
        Assert.assertEquals("{\"Class\":\"com.alibaba.druid.bvt.utils.JSONWriterTest$1\",\"Message\":null,\"StackTrace\":\"\"}",
                writer.toString());
    }

    public void test_localDate() {
        JSONWriter writer = new JSONWriter();
        LocalDate localDate = LocalDate.of(2023, 12, 21);
        writer.writeObject(localDate);
        Assert.assertEquals("\"2023-12-21\"", writer.toString());
    }

    public void test_localTime() {
        JSONWriter writer = new JSONWriter();
        LocalTime localTime = LocalTime.of(12, 0,1);
        writer.writeObject(localTime);
        Assert.assertEquals("\"12:00:01\"", writer.toString());
    }

    public void test_localDateTime() {
        JSONWriter writer = new JSONWriter();
        LocalDate localDate = LocalDate.of(2023, 12, 21);
        LocalTime localTime = LocalTime.of(12, 0,1);
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        writer.writeObject(localDateTime);
        Assert.assertEquals("\"2023-12-21 12:00:01\"", writer.toString());
    }

}
