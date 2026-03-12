package com.alibaba.druid.bvt.utils;

import com.alibaba.druid.support.json.JSONWriter;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

public class JSONWriterTest
 {
    @Test
    public void test_intArray()
            throws Exception {
        JSONWriter writer = new JSONWriter();
        writer.writeObject(new int[] {1, 2, 3});
        assertEquals("[1,2,3]", writer.toString());
    }

    @Test
    public void test_throwable()
            throws Exception {
        JSONWriter writer = new JSONWriter();
        writer.writeObject(new Throwable() {
            public void printStackTrace(PrintWriter s) {
            }
        });
        assertEquals("{\"Class\":\"com.alibaba.druid.bvt.utils.JSONWriterTest$1\",\"Message\":null,\"StackTrace\":\"\"}",
                writer.toString());
    }

    @Test
    public void test_localDate() {
        JSONWriter writer = new JSONWriter();
        LocalDate localDate = LocalDate.of(2023, 12, 21);
        writer.writeObject(localDate);
        assertEquals("\"2023-12-21\"", writer.toString());
    }

    @Test
    public void test_localTime() {
        JSONWriter writer = new JSONWriter();
        LocalTime localTime = LocalTime.of(12, 0, 1);
        writer.writeObject(localTime);
        assertEquals("\"12:00:01\"", writer.toString());
    }

    @Test
    public void test_localDateTime() {
        JSONWriter writer = new JSONWriter();
        LocalDate localDate = LocalDate.of(2023, 12, 21);
        LocalTime localTime = LocalTime.of(12, 0, 1);
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        writer.writeObject(localDateTime);
        assertEquals("\"2023-12-21 12:00:01\"", writer.toString());
    }
}
