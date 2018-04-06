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
package com.alibaba.druid.sql.dialect.mysql.visitor.transform;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectQueryBlock;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitorAdapter;
import com.alibaba.druid.util.FnvHash;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenshao on 27/07/2017.
 */
public class OrderByResolve extends OracleASTVisitorAdapter {
    final static long DBMS_RANDOM_VALUE = FnvHash.hashCode64("DBMS_RANDOM.value");

    public boolean visit(SQLSelect x) {
        SQLSelectQueryBlock queryBlock = x.getQueryBlock();
        if (queryBlock == null) {
            return super.visit(x);
        }

        if (x.getOrderBy() != null && queryBlock.isForUpdate() && queryBlock.getOrderBy() == null) {
            queryBlock.setOrderBy(x.getOrderBy());
            x.setOrderBy(null);
        }

        SQLOrderBy orderBy = queryBlock.getOrderBy();
        if (orderBy == null) {
            return super.visit(x);
        }


        if (!queryBlock.selectItemHasAllColumn(false)) {
            List<SQLSelectOrderByItem> notContainsOrderBy = new ArrayList<SQLSelectOrderByItem>();

            for (SQLSelectOrderByItem orderByItem : orderBy.getItems()) {
                SQLExpr orderByExpr = orderByItem.getExpr();

                if (orderByExpr instanceof SQLName) {
                    if (((SQLName) orderByExpr).hashCode64() == DBMS_RANDOM_VALUE) {
                        continue;
                    }

                    long hashCode64 = ((SQLName) orderByExpr).nameHashCode64();
                    SQLSelectItem selectItem = queryBlock.findSelectItem(hashCode64);
                    if (selectItem == null) {
                        queryBlock.addSelectItem(orderByExpr.clone());
                    }
                }
            }

            if (notContainsOrderBy.size() > 0) {
                for (SQLSelectOrderByItem orderByItem : notContainsOrderBy) {
                    queryBlock.addSelectItem(orderByItem.getExpr());
                }

                OracleSelectQueryBlock queryBlock1 = new OracleSelectQueryBlock();
                queryBlock1.setFrom(queryBlock, "x");
                x.setQuery(queryBlock1);
            }
        }



        return super.visit(x);
    }
}
