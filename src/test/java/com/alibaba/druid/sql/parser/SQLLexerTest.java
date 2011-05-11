/*
 * Copyright 2011 Alibaba Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
