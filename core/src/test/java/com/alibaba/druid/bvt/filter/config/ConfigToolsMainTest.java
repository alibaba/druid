package com.alibaba.druid.bvt.filter.config;

import com.alibaba.druid.filter.config.ConfigTools;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class ConfigToolsMainTest {
    @Test
    public void main_withNoArgs_printsUsageToStdErr_notAIOOBE() throws Exception {
        PrintStream origErr = System.err;
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        System.setErr(new PrintStream(err, true, "UTF-8"));
        try {
            Assertions.assertDoesNotThrow(() -> ConfigTools.main(new String[0]),
                    "main() with no args should print usage, not throw ArrayIndexOutOfBoundsException");
        } finally {
            System.setErr(origErr);
        }
        String stderr = new String(err.toByteArray(), StandardCharsets.UTF_8);
        Assertions.assertTrue(stderr.contains("Usage:"),
                "the usage message is a diagnostic and should be written to System.err, not System.out");
    }
}
