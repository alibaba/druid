/**
 * (created at 2011-3-14)
 */
package com.alibaba.druid.sql.parser;

import com.alibaba.druid.sql.parser.SQLLexer;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * @author <a href="mailto:shuo.qius@alibaba-inc.com">QIU Shuo</a>
 */
public class SQLLexerTest extends TestCase {
    public void testSkipSeparator() {
        SQLLexer sut = new SQLLexer("/**/ \t\n\r\n -- \n#\n");
        sut.skipSeparator();
        Assert.assertEquals(sut.eofIndex, sut.curIndex);
        Assert.assertEquals(sut.buf[sut.eofIndex], sut.ch);
    }
}
