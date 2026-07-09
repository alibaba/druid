package com.alibaba.druid.bvt.filter.config;

import com.alibaba.druid.filter.config.ConfigTools;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConfigToolsMainTest {
    @Test
    public void main_withNoArgs_printsUsage_notAIOOBE() {
        Assertions.assertDoesNotThrow(() -> ConfigTools.main(new String[0]),
                "main() with no args should print usage, not throw ArrayIndexOutOfBoundsException");
    }
}
