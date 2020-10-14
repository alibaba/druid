package com.alibaba.druid.benckmark.sql;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlSelectParser;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.parser.*;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Created by kaiwang.ckw on 15/05/2017.
 */
public class SqlHolder {
    private String text;
    private DbType dialect;

    private boolean parsed;
    public SQLStatement ast;

    private boolean isParam;

    public static SQLSelectListCache selectListCache = new SQLSelectListCache(JdbcConstants.MYSQL);
    static {
        selectListCache.add("select id as id,    gmt_create as gmtCreate,    gmt_modified as gmtModified,    name as name,    owner as owner,    type as type,    statement as statement,    datasource as datasource,    meta as meta,    param_file as paramFile,    sharable as sharable,    data_type as dataType,    status as status,    config as config,    project_id as projectId,    plugins as plugins,    field_compare as fieldCompare,    field_ext as fieldExt,    openx as openx   from");
        selectListCache.add("SELECT id, dispute_id, buyer_id, seller_id, total_fee, refund_fee, max_apply_goods_fee, apply_goods_fee, apply_carriage_fee, refund_goods_fee, refund_carriage_fee, refund_point, refund_coupon, refund_return_point, refund_cash, real_deduct_refund_point, real_refund_return_point, refund_return_commission, gmt_create, gmt_modified, attributes, attributes_cc FROM");
//        selectListCache.add("select `auction_relation`.`id`,`auction_relation`.`item_id`,`auction_relation`.`sku_id`,`auction_relation`.`user_id`,`auction_relation`.`target_id`,`auction_relation`.`extra_id`,`auction_relation`.`type`,`auction_relation`.`target_type`,`auction_relation`.`type_attr`,`auction_relation`.`status`,`auction_relation`.`target_user_id`,`auction_relation`.`options`,`auction_relation`.`features`,`auction_relation`.`version`,`auction_relation`.`sub_type`,`auction_relation`.`gmt_create`,`auction_relation`.`gmt_modified` from");
//        selectListCache.add("SELECT biz_order_id, value_type, key_value, gmt_create, gmt_modified\n" +
//                "\t, attribute_cc, buyer_id\n" +
//                "FROM");
//        selectListCache.add("SELECT biz_order_id, value_type, key_value, gmt_create, gmt_modified\n" +
//                "\t, attribute_cc, buyer_id\n" +
//                "FROM");
//        selectListCache.add("SELECT sub_logistics_order_id, consign_time, attribute_cc, attributes, out_logistics_id\n" +
//                "\t, parent_id, gmt_create, gmt_modified, detail_order_id, is_last\n" +
//                "\t, ship_amount, buyer_id, seller_id, ship_status, step_order_id\n" +
//                "FROM");
        selectListCache.add("SELECT biz_order_id, out_order_id, seller_nick, buyer_nick, seller_id\n" +
                "\t, buyer_id, auction_id, auction_title, auction_price, buy_amount\n" +
                "\t, biz_type, sub_biz_type, fail_reason, pay_status, logistics_status\n" +
                "\t, out_trade_status, snap_path, gmt_create, status\n" +
                "\t, ifnull(buyer_rate_status, 4) AS buyer_rate_status\n" +
                "\t, ifnull(seller_rate_status, 4) AS seller_rate_status, auction_pict_url\n" +
                "\t, seller_memo, buyer_memo, seller_flag, buyer_flag, buyer_message_path\n" +
                "\t, refund_status, attributes, attributes_cc, gmt_modified, ip\n" +
                "\t, end_time, pay_time, is_main, is_detail, point_rate\n" +
                "\t, parent_id, adjust_fee, discount_fee, refund_fee, confirm_paid_fee\n" +
                "\t, cod_status, trade_tag, shop_id, sync_version, options\n" +
                "\t, ignore_sold_quantity, from_group, attribute1, attribute2, attribute3\n" +
                "\t, attribute4, attribute11\n" +
                "FROM");
        selectListCache.add("select `member_cart`.`CART_ID`,`member_cart`.`SKU_ID`,`member_cart`.`ITEM_ID`,`member_cart`.`QUANTITY`,`member_cart`.`USER_ID`,`member_cart`.`SELLER_ID`,`member_cart`.`STATUS`,`member_cart`.`EXT_STATUS`,`member_cart`.`TYPE`,`member_cart`.`SUB_TYPE`,`member_cart`.`GMT_CREATE`,`member_cart`.`GMT_MODIFIED`,`member_cart`.`ATTRIBUTE`,`member_cart`.`ATTRIBUTE_CC`,`member_cart`.`EX2` from");
    }

    public static SqlHolder of(String sql) {
        return new SqlHolder(sql);
    }

    SqlHolder(String text) {
        this(text, DbType.mysql);
    }

    public SqlHolder(String text, DbType dbType, boolean isParam) {
        this(text, dbType);
        this.isParam = isParam;
    }

    SqlHolder(String text, DbType dbType) {
        if (dbType != DbType.mysql) {
            throw new IllegalArgumentException("only mysql is");
        }

        this.text = text;
        this.dialect = dbType;
    }

    public String format() {
        try {
            return SQLUtils.format(text, dialect);
        } catch (ParserException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public void ensureParsed() {
        if (parsed) {
            return;
        }

//        if (text.equals("select @@session.tx_read_only")) {
//            SQLSelect select = new SQLSelect();
//            MySqlSelectQueryBlock queryBlock = new MySqlSelectQueryBlock();
//            queryBlock.addSelectItem(new SQLPropertyExpr(new SQLVariantRefExpr("@@session"), "tx_read_only"));
//            select.setQuery(queryBlock);
//
//            ast = new SQLSelectStatement(select);
//            parsed = true;
//            return;
//        }

        // ast = SQLUtils.parseStatements(text, dialect).get(0);
        SQLParserFeature[] features;

        if (isParam) {
            features = new SQLParserFeature[]{SQLParserFeature.EnableSQLBinaryOpExprGroup, SQLParserFeature.OptimizedForParameterized};
        } else {
            features = new SQLParserFeature[]{SQLParserFeature.EnableSQLBinaryOpExprGroup
                    , SQLParserFeature.OptimizedForParameterized
                    , SQLParserFeature.UseInsertColumnsCache
            };
        }

        try {
            MySqlStatementParser parser = new MySqlStatementParser(text, features);
            parser.setSelectListCache(selectListCache);
            ast = parser.parseStatement();
        } catch (ParserException e) {
            throw new UnsupportedOperationException(e);
        }
        parsed = true;
    }

    // returns rewritten sql, or original string object if not rewritten
    public String select() {
        ensureParsed();

        SQLStatement stmt = ast;
        if (stmt instanceof SQLSelectStatement) {
            boolean rewritten = StatementConverter.rewriteSelect((SQLSelectStatement) stmt);
            if (rewritten) {
                return SQLUtils.toMySqlString(stmt);
            } else {
                return text;
            }
        } else {
            SQLSelectStatement selectStatement = StatementConverter.rewrite(stmt);
            if (stmt == selectStatement) {
                return text;
            } else {
                return SQLUtils.toMySqlString(selectStatement);
            }
        }
    }

    public String parameterize() {
        return parameterize(null, null);
    }

    public String parameterize(Set<String> physicalNames) {
        ensureParsed();
        return Templates.parameterize(ast, physicalNames, null);
    }

    public String parameterize(Set<String> physicalNames, List<Object> params) {
        ensureParsed();

//        if (text.equals("select @@session.tx_read_only")) {
//            return text;
//        }

        return Templates.parameterize(ast, physicalNames, params);
    }


    public String getParams() {
        ensureParsed();
        StringBuilder out = new StringBuilder();
        SQLASTOutputVisitor visitor = SQLUtils.createOutputVisitor(out, JdbcConstants.MYSQL);
        List<Object> parameters = new ArrayList<Object>();
        visitor.setParameterized(true);
        visitor.setParameterizedMergeInList(true);
        visitor.setParameters(parameters);
        ast.accept(visitor);
        String params = JSONArray.toJSONString(parameters, SerializerFeature.WriteClassName);
        params = StringUtils.replace(params, "\"", "\\\"");
        return params;
    }

    public String getSqlItems(String db) {
        Map<String, LinkedHashSet<String>> itemMap = new HashMap<String, LinkedHashSet<String>>();
        try {
            SchemaStatVisitor schemaStatVisitor = new MySqlSchemaStatVisitor();
            ast.accept(schemaStatVisitor);
            List<TableStat.Condition> conditionList = schemaStatVisitor.getConditions();
            for (TableStat.Condition condition : conditionList) {
                String tableName = condition.getColumn().getTable();
                LinkedHashSet<String> condLinkedSet = itemMap.get(tableName);
                if (condLinkedSet == null) {
                    condLinkedSet = new LinkedHashSet<String>();
                    itemMap.put(tableName, condLinkedSet);
                }
                TableStat.Column column = condition.getColumn();
                String upperClnName = column.toString().toUpperCase();
                String finalClnName = filterChar(db, upperClnName);
                condLinkedSet.add(finalClnName);
            }
        } catch (Exception ex) {
        }
        if (itemMap.isEmpty())
            return null;
        StringBuilder resSb = new StringBuilder();
        for (Map.Entry<String, LinkedHashSet<String>> entry : itemMap.entrySet()) {
            resSb.append(StringUtils.join(entry.getValue(), ",")).append(",");
        }
        return resSb.substring(0, resSb.length() - 1);
    }

    public static String filterChar(String db, String name) {
        String resName;
        if (StringUtils.isNotBlank(name)) {
            String[] names = name.split("\\.");
            StringBuilder nameSb = new StringBuilder();
            boolean isFirst = true;
            int size = names.length;
            int k = 0;
            for (String n : names) {
                String tempN = n;
                if (n.startsWith("`") && n.endsWith("`")) {
                    tempN = n.substring(1, n.length() - 1);
                }
                if (k == size - 1) {
                    nameSb.append(tempN).append(".");
                } else if (isFirst) {
                    if (!n.startsWith(db)) {
                        nameSb.append(convert(tempN)).append(".");
                    }
                    isFirst = false;
                } else {
                    nameSb.append(convert(tempN)).append(".");
                }
                k++;
            }
            resName = nameSb.substring(0, nameSb.length() - 1);
        } else {
            resName = name;
        }
        return db + "." + resName;
    }

    public static String convert(String tableName) {
        if (StringUtils.isBlank(tableName)) {
            return tableName;
        }
        int len = tableName.length();
        int k = -1;
        int min = Math.min(4, len);
        for (int i = 1; i <= min; i++) {
            String ch = String.valueOf(tableName.charAt(len - i));
            boolean isNum = StringUtils.isNumeric(ch);
            if (isNum) {
                k = i;
            } else {
                break;
            }
        }

        if (k != -1) {
            tableName = tableName.substring(0, len - k);
            if (tableName.endsWith("_")) {
                tableName = tableName.substring(0, tableName.length() - 1);
            }
        }
        int idx = tableName.lastIndexOf("_");
        if (idx == -1 || (tableName.length() - 1 == idx)) {
            return tableName;
        }
        String num = tableName.substring(idx + 1);
        boolean isNum = StringUtils.isNumeric(num);
        if (isNum) {
            return tableName.substring(0, idx);
        }
        return tableName;
    }
}
