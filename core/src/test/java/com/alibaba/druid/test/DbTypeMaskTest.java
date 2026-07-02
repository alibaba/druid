package com.alibaba.druid.test;

import com.alibaba.druid.DbType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DbTypeMaskTest {
    @Test
    public void test_oceanbase_oracle_mask_no_sign_extension() {
        long expectedMask = 1L << 31; // 0x0000000080000000L
        assertEquals(expectedMask, DbType.oceanbase_oracle.mask,
                "oceanbase_oracle mask should be a single clean bit at position 31, not sign-extended");
    }

    @Test
    public void test_of_oceanbase_oracle_plus_polardb_no_corruption() {
        long combined = DbType.of(DbType.oceanbase_oracle, DbType.polardb);
        long expected = (1L << 31) | (1L << 32);
        assertEquals(expected, combined,
                "Combining oceanbase_oracle + polardb should not corrupt high bits due to sign extension");
    }

    @Test
    public void test_all_masks_are_clean_single_bits() {
        for (DbType type : DbType.values()) {
            long mask = type.mask;
            if (mask == 0) {
                continue;
            }
            boolean isPowerOfTwo = (mask & (mask - 1)) == 0;
            assertEquals(true, isPowerOfTwo,
                    "DbType " + type + " mask should be a clean power of two (single bit), but was 0x" + Long.toHexString(mask));
        }
    }
}
