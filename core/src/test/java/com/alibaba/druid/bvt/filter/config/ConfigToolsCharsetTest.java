package com.alibaba.druid.bvt.filter.config;

import com.alibaba.druid.filter.config.ConfigTools;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConfigToolsCharsetTest {
    // A password with multibyte (non-ASCII) characters. encrypt() encodes with UTF-8,
    // so decrypt() must also decode with UTF-8, independent of the JVM default charset.
    @Test
    public void test_decrypt_roundtrips_multibyte_regardless_of_default_charset() throws Exception {
        String password = "密码P@sséü";   // 密码P@sséü
        String cipher = ConfigTools.encrypt(password);
        String plain = ConfigTools.decrypt(cipher);
        assertEquals(password, plain,
                "decrypt must round-trip a UTF-8-encoded password even when the JVM default charset is not UTF-8");
    }
}
