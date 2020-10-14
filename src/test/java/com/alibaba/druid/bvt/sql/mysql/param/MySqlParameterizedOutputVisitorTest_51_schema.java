package com.alibaba.druid.bvt.sql.mysql.param;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenshao on 16/8/23.
 */
public class MySqlParameterizedOutputVisitorTest_51_schema extends TestCase {
    public void test_for_parameterize() throws Exception {
        final DbType dbType = JdbcConstants.MYSQL;
        String sql = "UPDATE `galicdb_0102`.auction_auctions_0201 SET `starts` = ?, `pict_url` = ?, `category` = ?, `minimum_bid` = ?, `reserve_price` = ?, `city` = ?, `prov` = ?, `ends` = ?, `current_bid` = NULL, `quantity` = ?, `zoo` = ?, `secure_trade_ordinary_post_fee` = ?, `secure_trade_fast_post_fee` = ?, `old_quantity` = ?, `options` = ?, `secure_trade_ems_post_fee` = ?, `property` = ?, `last_modified` = ?, `desc_path` = ?, `postage_id` = ?, `shop_categories_id_lists` = ?, `spu_id` = ?, `sync_version` = ?, `auction_status` = ?, `features` = ?, `feature_cc` = ?, `main_color` = ?, `outer_id` = ?, `auction_sub_status` = ?, `commodity_id` = ? WHERE `auction_id` = ?";

        SQLStatementParser parser = SQLParserUtils.createSQLStatementParser(sql, dbType);
        List<SQLStatement> stmtList = parser.parseStatementList();
        SQLStatement statement = stmtList.get(0);

        StringBuilder out = new StringBuilder();
        SQLASTOutputVisitor visitor = SQLUtils.createOutputVisitor(out, JdbcConstants.MYSQL);
        List<Object> parameters = new ArrayList<Object>();
        visitor.setParameterized(true);
        visitor.setParameterizedMergeInList(true);
        visitor.setParameters(parameters);
        /*visitor.setPrettyFormat(false);*/
        statement.accept(visitor);
       /* JSONArray array = new JSONArray();
        for(String table : visitor.getTables()){
            array.add(table.replaceAll("`",""));
        }*/

        String psql = out.toString();

        System.out.println(psql);


        assertEquals("UPDATE galicdb.auction_auctions\n" +
                "SET `starts` = ?, `pict_url` = ?, `category` = ?, `minimum_bid` = ?, `reserve_price` = ?, `city` = ?, `prov` = ?, `ends` = ?, `current_bid` = NULL, `quantity` = ?, `zoo` = ?, `secure_trade_ordinary_post_fee` = ?, `secure_trade_fast_post_fee` = ?, `old_quantity` = ?, `options` = ?, `secure_trade_ems_post_fee` = ?, `property` = ?, `last_modified` = ?, `desc_path` = ?, `postage_id` = ?, `shop_categories_id_lists` = ?, `spu_id` = ?, `sync_version` = ?, `auction_status` = ?, `features` = ?, `feature_cc` = ?, `main_color` = ?, `outer_id` = ?, `auction_sub_status` = ?, `commodity_id` = ?\n" +
                "WHERE `auction_id` = ?", psql);
    }
}
