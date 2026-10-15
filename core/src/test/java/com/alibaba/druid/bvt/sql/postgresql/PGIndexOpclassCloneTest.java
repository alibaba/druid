/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.bvt.sql.postgresql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLCreateIndexStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PGIndexOpclassCloneTest {
    @Test
    public void testClonePreservesOperatorClass() {
        SQLCreateIndexStatement original = parseIndex();
        SQLCreateIndexStatement copy = original.clone();

        assertEquals(SQLUtils.toSQLString(original, DbType.postgresql),
                SQLUtils.toSQLString(copy, DbType.postgresql));
        SQLSelectOrderByItem originalItem = original.getItems().get(0);
        SQLSelectOrderByItem copiedItem = copy.getItems().get(0);
        assertNotNull(copiedItem.getOpclass());
        assertNotSame(originalItem.getOpclass(), copiedItem.getOpclass());
        assertSame(copiedItem, copiedItem.getOpclass().getParent());

        copiedItem.setOpclass(new SQLIdentifierExpr("replacement_ops"));
        assertEquals("\"pg_catalog\".\"text_ops\"", originalItem.getOpclass().toString());
    }

    @Test
    public void testParsedOperatorClassHasParent() {
        SQLSelectOrderByItem item = parseIndex().getItems().get(0);
        assertSame(item, item.getOpclass().getParent());
    }

    @Test
    public void testSetOperatorClassParentAndClear() {
        SQLSelectOrderByItem item = new SQLSelectOrderByItem();
        SQLExpr opclass = new SQLIdentifierExpr("text_ops");
        item.setOpclass(opclass);
        assertSame(item, opclass.getParent());

        item.setOpclass(null);
        assertNull(item.getOpclass());
        assertNull(item.clone().getOpclass());
    }

    private SQLCreateIndexStatement parseIndex() {
        return (SQLCreateIndexStatement) SQLUtils.parseSingleStatement(
                "CREATE INDEX idx ON test USING btree "
                        + "(\"name\" COLLATE \"pg_catalog\".\"default\" "
                        + "\"pg_catalog\".\"text_ops\" ASC NULLS LAST)",
                DbType.postgresql);
    }
}
