package com.alibaba.druid.bvt.sql.mysql.param;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.util.JdbcConstants;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by yunning on 16/6/3.
 */
public class ParseUtil {

    private final static String DML_REGEX = "^(\\s)*(SELECT|INSERT|UPDATE|DELETE)";
    private final static Pattern DML_PATTERN = Pattern.compile(DML_REGEX, Pattern.CASE_INSENSITIVE); //忽略大小写
    private final static String IP_REGEX = "^(?:(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?))$";
    private final static Pattern IP_PATTERN = Pattern.compile(IP_REGEX, Pattern.CASE_INSENSITIVE);
    private static Logger logger = Logger.getLogger(ParseUtil.class);

    public static boolean isDmlSQL(String querySql) {
        return DML_PATTERN.matcher(querySql).find();
    }

    public static boolean isDmlSQL(SQLStatement statement /*String querySql*/) {
        //  return DML_1_PATTERN.matcher(querySql).find() || DML_2_PATTERN.matcher(querySql).find();
        return statement instanceof SQLSelectStatement ||
                statement instanceof SQLInsertStatement ||
                statement instanceof SQLReplaceStatement ||
                statement instanceof SQLUpdateStatement ||
                statement instanceof SQLDeleteStatement;
    }

    public static void main(String[] args) {
        //     String sql = "alter table sql_perf add index `idx_instance_8` (`host`,`port`,`hashcode`,`item`,`time`,`value`);";
      /*  String sql = "CREATE INDEX PersonIndex\n" +
                "ON Person (LastName) ";*/
        //   String sql = "CREATE TABLE t (id int );";
        // String sql = "ALTER TABLE `app_api_dup_control`\n\tDROP INDEX `idx_url_uuid`,\n\tADD UNIQUE KEY `uk_url_uuid` (uuid, url)";
      /*  String sql = "ALTER TABLE `push_seed_0000`\n" +
                "\tADD KEY `idx_betstatus_gmtcreate` (bet_status, gmt_create),\n" +
                "\tADD KEY `idx_winstatus_gmtcreate` (win_status, gmt_create)";*/
        //  System.out.println(getIdxInfo(sql, "AA", null));

        //   System.out.println(DateTimeUtils.toYyyyMMddhhmmss(new Date()));
        /*String ip = "xx";
        System.out.println(IP_PATTERN.matcher(ip).find());*/
       /* String sql = "\nalter table `dms_sign_info_0222` modify column `station_id` bigint comment '分拣流水中记录的分拨中心ID'";

        List<IdxFlagInfo> list = getIdxInfo(sql,"XX",null);
        System.out.println(list);*/
        // String sql = "select * from task where status = 1 and valid = 1 and type <> 100 and type <> 99 order by priority desc,gmt_create limit 0,500";
        //  System.out.println(parseSQL(sql));

        /*String sql = "SELECT IFNULL(SUM(CASE WHEN `tms_waybill_detail`.`dissendout_status` = ? AND DATE_ADD(`tms_waybill_detail`.`rdc_accept_time`, INTERVAL ? HOUR) < `tms_waybill_detail`.`dissendout_time` OR `tms_waybill_detail`.`dissendout_status` = ? AND DATE_ADD(`tms_waybill_detail`.`rdc_accept_time`, INTERVAL ? HOUR) < NOW() THEN ? ELSE ? END), ?) AS `count` FROM tms_waybill_detail `tms_waybill_detail` WHERE `tms_waybill_detail`.`rdc_accept_time` >= ? AND `tms_waybill_detail`.`rdc_accept_time` < ? AND `tms_waybill_detail`.`districenter_code` = ? AND `tms_waybill_detail`.`schedule_code` = ?";

        String table = "[\"tms_waybill_detail_0014\"]";
        String params = "[1,24,0,24,1,0,0,\"2017-01-15 00:00:00\",\"2017-01-15 17:32:03.558\",686,\"10102\"]";

        System.out.println("----- : "+restore(sql,table,params));*/

        String sql = "/* 0bba613214845441110397435e/0.4.6.25// */select `f`.`id`,`f`.`biz_id`,`f`.`user_id`,`f`.`file_name`,`f`.`parent_id`,`f`.`length`,`f`.`type`,`f`.`stream_key`,`f`.`biz_status`,`f`.`mark`,`f`.`content_modified`,`f`.`status`,`f`.`gmt_create`,`f`.`gmt_modified`,`f`.`md5`,`f`.`extra_str1`,`f`.`extra_str2`,`f`.`extra_str3`,`f`.`extra_num1`,`f`.`extra_num2`,`f`.`extra_num3`,`f`.`safe`,`f`.`open_status`,`f`.`inner_mark`,`f`.`sys_extra`,`f`.`feature`,`f`.`domain_option`,`f`.`version`,`f`.`reference_type`,`f`.`dentry_type`,`f`.`space_id`,`f`.`extension`,`f`.`creator_id`,`f`.`modifier_id`,`f`.`store_type`,`f`.`link_mark`,`f`.`content_type` from  ( select `vfs_dentry_2664`.`id` from `vfs_dentry_2664` FORCE INDEX (idx_gmt) where ((`vfs_dentry_2664`.`extra_str1` = '97d45a25df387b4460e5b4151daeb452') AND (`vfs_dentry_2664`.`biz_id` = 62) AND (`vfs_dentry_2664`.`status` = 0) AND (`vfs_dentry_2664`.`user_id` = '11168360') AND (`vfs_dentry_2664`.`dentry_type` = 1)) limit 0,50 )  `t`  join `vfs_dentry_2664` `f` on `t`.`id` = `f`.`id` where ((`t`.`id` = `f`.`id`) AND (`f`.`user_id` = 11168360))";
        // SQLStatement sqlStatement = getStatement(sql);

        String sqlTempalte = parseSQL(sql);
        JSONArray array = new JSONArray();
        array.add("VFS_DENTRY_001");
        System.out.println(restore(sqlTempalte,array.toJSONString(),new JSONArray().toJSONString()));
    }

    public static boolean isIp(String host){
        return IP_PATTERN.matcher(host).find();
    }



    private static String filterChar(String name) {
        if (StringUtils.isNotBlank(name)) {
            String[] names = name.split("\\.");
            StringBuilder nameSb = new StringBuilder();
            for (String n : names) {
                if (n.startsWith("`") && n.endsWith("`")) {
                    nameSb.append(n.substring(1, n.length() - 1)).append(".");
                }
            }
            return nameSb.substring(0, nameSb.length() - 1);
        }
        return name;
    }

    public static String parseSQL(String sql) {
        try {
            final DbType dbType = JdbcConstants.MYSQL;
            return ParameterizedOutputVisitorUtils.parameterize(sql, dbType).toUpperCase();
        } catch (Exception ex) {
            logger.error("parser sql error : " + sql);
            ex.printStackTrace();
        }
        return null;
    }

    public static String computeMD5Hex(String sqlTemplate) {
        return DigestUtils.md5Hex(sqlTemplate).toUpperCase();
    }

    public static String restore(String sql, String table, String params/*JSONArray paramsArray, JSONArray destArray*/) {
        JSONArray destArray = null;

        if (table != null) {
            destArray = JSON.parseArray(table.replaceAll("''", "'"));
        }

        JSONArray paramsArray = JSON.parseArray(params.replaceAll("''", "'"));
        DbType dbType = JdbcConstants.MYSQL;
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);

        SQLStatement stmt = stmtList.get(0);

        StringBuilder out = new StringBuilder();
        SQLASTOutputVisitor visitor = SQLUtils.createOutputVisitor(out, dbType);
        List<Object> paramsList = new ArrayList(paramsArray);
        visitor.setParameters(paramsList);

        JSONArray srcArray = getSrcArray(sql);
/*
        SchemaStatVisitor schemaStatVisitor = new MySqlSchemaStatVisitor();
        stmt.accept(schemaStatVisitor);
        JSONArray srcArray = new JSONArray();
        for (Map.Entry<TableStat.Name, TableStat> entry : schemaStatVisitor.getTables().entrySet()) {
            srcArray.add(entry.getKey().getName());
        }*/

        if (destArray != null) {
            for (int i = 0; i < srcArray.size(); i++) {
                visitor.addTableMapping(srcArray.getString(i), destArray.getString(i));
            }
        }

        stmt.accept(visitor);

        return out.toString();
    }

    private static JSONArray getSrcArray(String sql) {
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SQLStatement stmt = stmtList.get(0);
        StringBuilder out = new StringBuilder();
        SQLASTOutputVisitor visitor = SQLUtils.createOutputVisitor(out, JdbcConstants.MYSQL);
        List<Object> parameters = new ArrayList<Object>();
        visitor.setParameterized(true);
        visitor.setParameterizedMergeInList(true);
        visitor.setParameters(parameters);
        visitor.setExportTables(true);
        stmt.accept(visitor);
        String srcStr = JSONArray.toJSONString(visitor.getTables());
        return  JSONArray.parseArray(srcStr);
    }

    public static SQLStatement getStatement(String sql) {
        try {
            List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
            return stmtList.get(0);
        } catch (Exception ex) {
            logger.error("get statement error", ex);
        }
        return null;
    }

    public static String getSqlHash(String sql) {
        SQLStatement statement = getStatement(sql);
        if (null == statement) return null;
        return getSqlHash(statement);
    }


    public static String getSqlHash(SQLStatement statement) {
        try {

            StringBuilder out = new StringBuilder();
            List<Object> parameters = new ArrayList<Object>();
            SQLASTOutputVisitor visitor = SQLUtils.createOutputVisitor(out, JdbcConstants.MYSQL);
            visitor.setParameterized(true);
            visitor.setParameterizedMergeInList(true);
            visitor.setParameters(parameters);
            // visitor.setExportTables(true);
            visitor.setPrettyFormat(false);
            statement.accept(visitor);

            String sqlTemplate = out.toString();
            return DigestUtils.md5Hex(sqlTemplate);
        } catch (Exception ex) {
            logger.error("parseSql error ", ex);
        }
        return null;
    }
}
