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
package com.alibaba.druid.sql.dialect.hive.parser;

import java.util.Arrays;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLOrderingSpecification;
import com.alibaba.druid.sql.ast.expr.SQLArrayExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.dialect.hive.ast.HiveClusterBy;
import com.alibaba.druid.sql.dialect.hive.ast.HiveClusterByItem;
import com.alibaba.druid.sql.dialect.hive.ast.HiveDistributeBy;
import com.alibaba.druid.sql.dialect.hive.ast.HiveDistributeByItem;
import com.alibaba.druid.sql.dialect.hive.ast.HiveSelectSortByItem;
import com.alibaba.druid.sql.dialect.hive.ast.HiveSortBy;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.FnvHash;

public class HiveExprParser extends SQLExprParser {
    private final static String[] AGGREGATE_FUNCTIONS;
    private final static long[] AGGREGATE_FUNCTIONS_CODES;

    static {
        String[] strings = { "AVG", "COUNT", "MAX", "MIN", "STDDEV", "SUM", "ROW_NUMBER",
                "ROWNUMBER" };

        AGGREGATE_FUNCTIONS_CODES = FnvHash.fnv1a_64_lower(strings, true);
        AGGREGATE_FUNCTIONS = new String[AGGREGATE_FUNCTIONS_CODES.length];
        for (String str : strings) {
            long hash = FnvHash.fnv1a_64_lower(str);
            int index = Arrays.binarySearch(AGGREGATE_FUNCTIONS_CODES, hash);
            AGGREGATE_FUNCTIONS[index] = str;
        }
    }

    public HiveExprParser(String sql){
        this(new HiveLexer(sql));
        this.lexer.nextToken();
    }

    public HiveExprParser(String sql, SQLParserFeature... features){
        this(new HiveLexer(sql, features));
        this.lexer.nextToken();
    }

    public HiveExprParser(Lexer lexer){
        super(lexer);
        this.aggregateFunctions = AGGREGATE_FUNCTIONS;
        this.aggregateFunctionHashCodes = AGGREGATE_FUNCTIONS_CODES;
    }

    public SQLExpr primaryRest(SQLExpr expr) {
        if (lexer.token() == Token.COLONCOLON) {
            lexer.nextToken();
            String propertyName = lexer.stringVal();
            lexer.nextToken();
            expr = new SQLPropertyExpr(expr, propertyName);
        } else if (lexer.token() == Token.LBRACKET) {
            SQLArrayExpr array = new SQLArrayExpr();
            array.setExpr(expr);
            lexer.nextToken();
            this.exprList(array.getValues(), array);
            accept(Token.RBRACKET);
            expr = array;
        }
        return super.primaryRest(expr);
    }
    
	public HiveSortBy parseHiveSortBy() {
		if (lexer.token() == Token.SORT) {
			HiveSortBy hiveSortBy = new HiveSortBy();
			lexer.nextToken();
			accept(Token.BY);
			sortBy(hiveSortBy.getItems(), hiveSortBy);
			return hiveSortBy;
		}
		return null;
	}

	public void sortBy(List<HiveSelectSortByItem> items, SQLObject parent) {
		HiveSelectSortByItem item = parseSelectSortByItem();
		item.setParent(parent);
		items.add(item);
		while (lexer.token() == Token.COMMA) {
			lexer.nextToken();
			item = parseSelectSortByItem();
			item.setParent(parent);
			items.add(item);
		}
	}

	public HiveSelectSortByItem parseSelectSortByItem() {
		HiveSelectSortByItem item = new HiveSelectSortByItem();
		item.setExpr(expr());
		if (lexer.token() == Token.ASC) {
			lexer.nextToken();
			item.setType(SQLOrderingSpecification.ASC);
		} else if (lexer.token() == Token.DESC) {
			lexer.nextToken();
			item.setType(SQLOrderingSpecification.DESC);
		}
		return item;
	}
	
	public HiveDistributeBy parseHiveDistributeBy() {
		if (lexer.token() == Token.DISTRIBUTE) {
			HiveDistributeBy hiveDistributeBy = new HiveDistributeBy();
			lexer.nextToken();
			accept(Token.BY);
			distributeBy(hiveDistributeBy.getItems(), hiveDistributeBy);
			return hiveDistributeBy;
		}
		return null;
	}
	
	public void distributeBy(List<HiveDistributeByItem> items, SQLObject parent) {
		HiveDistributeByItem item = parseHiveDistributeByItem();
		item.setParent(parent);
		items.add(item);
		while (lexer.token() == Token.COMMA) {
			lexer.nextToken();
			item = parseHiveDistributeByItem();
			item.setParent(parent);
			items.add(item);
		}
	}
	
	public HiveDistributeByItem parseHiveDistributeByItem() {
		HiveDistributeByItem item = new HiveDistributeByItem();
		item.setExpr(expr());
		return item;
	}
	
	public HiveClusterBy parseHiveClusterBy() {
		if (lexer.token() == Token.CLUSTER) {
			HiveClusterBy hiveClusterBy = new HiveClusterBy();
			lexer.nextToken();
			accept(Token.BY);
			clusterBy(hiveClusterBy.getItems(), hiveClusterBy);
			return hiveClusterBy;
		}
		return null;
	}
	
	public void clusterBy(List<HiveClusterByItem> items, SQLObject parent) {
		HiveClusterByItem item = parseHiveClusterByItem();
		item.setParent(parent);
		items.add(item);
		while (lexer.token() == Token.COMMA) {
			lexer.nextToken();
			item = parseHiveClusterByItem();
			item.setParent(parent);
			items.add(item);
		}
	}
	
	public HiveClusterByItem parseHiveClusterByItem() {
		HiveClusterByItem item = new HiveClusterByItem();
		item.setExpr(expr());
		return item;
	}
}
