package com.alibaba.druid.bvt.filter.config;

import com.alibaba.druid.filter.config.ConfigTools;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConfigToolsMainTest {
    @Test
    public void run_withNoArgs_printsUsageToStderr_andReturnsFailureStatus() throws Exception {
        PrintStream stderr = System.err;
        ByteArrayOutputStream buf = new ByteArrayOutputStream();

        int status;
        try {
            System.setErr(new PrintStream(buf, true, "UTF-8"));
            status = ConfigTools.run(new String[0]);
        } finally {
            System.setErr(stderr);
        }

        assertEquals(1, status, "run() with no args should report a usage error, not success");
        assertTrue(buf.toString("UTF-8").contains("Usage: ConfigTools <password>"),
                "run() with no args should print the usage message to stderr");
    }

    @Test
    public void run_withPassword_returnsSuccessStatus() throws Exception {
        PrintStream stdout = System.out;
        ByteArrayOutputStream buf = new ByteArrayOutputStream();

        int status;
        try {
            System.setOut(new PrintStream(buf, true, "UTF-8"));
            status = ConfigTools.run(new String[]{"druid"});
        } finally {
            System.setOut(stdout);
        }

        assertEquals(0, status);
        assertTrue(buf.toString("UTF-8").contains("password:"));
    }
}
