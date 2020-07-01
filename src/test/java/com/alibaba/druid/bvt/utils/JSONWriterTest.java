package com.alibaba.druid.bvt.utils;

import java.io.PrintWriter;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.support.json.JSONWriter;

public class JSONWriterTest extends TestCase {

    public void test_intArray() throws Exception {
        JSONWriter writer = new JSONWriter();
        writer.writeObject(new int[] { 1, 2, 3 });
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
}
