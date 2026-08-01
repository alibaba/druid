package com.alibaba.druid.util;

import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regression for issue #4280: {@link MySqlUtils}' charset decoders ({@code utf8}, {@code utf32},
 * {@code gbk}, {@code big5}) called {@link HexBin#decode(String)} and used the result without a
 * null check. Odd-length hex strings make {@code HexBin.decode} return {@code null}, so the
 * subsequent {@code new String(bytes, ...)} (and {@code bytes.length} in {@code utf32}) threw
 * {@link NullPointerException}. {@code utf16} already handled this and serves as the template.
 */
public class MySqlUtilsHexDecodeTest {
    @Test
    public void utf8_oddLengthHex_returnsNull() {
        assertNull(MySqlUtils.utf8("ABC"));
    }

    @Test
    public void utf32_oddLengthHex_returnsNull() {
        assertNull(MySqlUtils.utf32("ABC"));
    }

    @Test
    public void gbk_oddLengthHex_returnsNull() {
        assertNull(MySqlUtils.gbk("ABC"));
    }

    @Test
    public void big5_oddLengthHex_returnsNull() {
        assertNull(MySqlUtils.big5("ABC"));
    }

    @Test
    public void evenLengthHex_stillDecodes() {
        // Regression guard: a valid even-length utf8 hex string must still decode.
        // D0B0D0B2D0B2 = "авв" (already covered by MySqlSelectTest_mtr at the SQL level).
        assertEquals("авв", MySqlUtils.utf8("D0B0D0B2D0B2"));
    }

    @Test
    public void parseSelectWithOddLengthHexUtf8_doesNotThrow() {
        // Reproduces the exact NPE from the issue: odd-length hex in a _utf8 X'...' literal
        // previously aborted parsing with NPE inside MySqlUtils.utf8. After the fix parsing
        // completes and the literal degrades to a null-backed char expr instead of throwing.
        MySqlStatementParser parser = new MySqlStatementParser("select _utf8 X'ABC'");
        // The key assertion is that no NullPointerException is thrown during parsing.
        assertNotNull(parser.parseStatement());
    }
}
