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
package com.alibaba.druid.bvt.sql;

import java.util.List;

import com.alibaba.druid.DbType;
import junit.framework.TestCase;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;

public class BigOrTest extends TestCase {

    public void testBigOr() throws Exception {
        StringBuilder buf = new StringBuilder();
        buf.append("SELECT * FROM T WHERE FID = ?");
        for (int i = 0; i < 10000; ++i) {
            buf.append(" OR FID = " + i);
        }
        String sql = buf.toString();
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, (DbType) null);
        String text = SQLUtils.toSQLString(stmtList.get(0));
        //System.out.println(text);
    }
}
