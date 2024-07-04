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
package com.alibaba.druid.bvt.sql.clickhouse;

import com.alibaba.druid.DbType;
import com.alibaba.druid.bvt.sql.SQLResourceTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.util.Utils;
import org.junit.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class CKResourceTest extends SQLResourceTest {
    public CKResourceTest() {
        super(DbType.clickhouse);
    }

    @Test
    public void clickhouse_parse() throws Exception {
        fileTest(0, 999, i -> "bvt/parser/clickhouse/" + i + ".txt");
    }

}
