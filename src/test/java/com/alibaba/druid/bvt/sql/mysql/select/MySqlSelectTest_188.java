package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;

import java.util.List;

public class MySqlSelectTest_188 extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "( ( SELECT user_id FROM (select user_id, havana_id, create_time, reg_days, own, reg_own_group, reg_src_group, reg_channel, src, global_city_name, locale, cid, cid_name, account_certify_type, certified_from, certified_time, certify_type, is_certified, account_type, address, age, sex, birthday, global_province_name, group_phone_area_no, group_phone_zone_no, l_level, crm_label, account_structure, account_frozen, hid_b2b, hid_taobao, is_aliyun_test_user, is_aliyun_user, is_enterprise, is_enterprise_account, is_global_user, is_net_test_user, is_oversea_user, is_valid_reg_user, is_net_user, parent_pk, taobao_account, update_time, user_region, fenxiao_create_time, fenxiao_label, fenxiao_parent_uid, fenxiao_region, crm_customer_priority, crm_customer_rank, order_cnt, cloud_order_cnt, cloud_market_order_cnt, count_ecs_paid_order, count_rds_paid_order, count_oss_paid_order, count_cdn_paid_order, count_slb_paid_order, count_nas_paid_order, count_mongodb_paid_order, count_eip_paid_order, count_redis_paid_order, count_communicate_paid_order, is_paid_retain, count_1_all_visit, count_7_all_visit, count_15_all_visit, count_30_all_visit, is_retain_d,count_1_yq_visit,count_7_yq_visit,count_15_yq_visit,count_30_yq_visit,count_1_promotion_visit,count_7_promotion_visit,count_15_promotion_visit,count_30_promotion_visit,count_1_market_visit,count_7_market_visit,count_15_market_visit,count_30_market_visit,count_1_search_visit,count_7_search_visit,count_15_search_visit,count_30_search_visit,count_1_help_visit,count_7_help_visit,count_15_help_visit,count_30_help_visit,count_7_domain_visit,count_15_domain_visit,count_30_domain_visit,count_1_beian_visit,count_7_beian_visit,count_15_beian_visit,count_30_beian_visit,count_1_wanwang_visit,count_7_wanwang_visit,count_15_wanwang_visit,count_30_wanwang_visit,count_1_domain_visit,crm_biz_category,cloud_pay_greater_zero_order_cnt, retain_cnt_d, retain_cnt_ecs_d, retain_cnt_rds_d, retain_cnt_oss_d, retain_cnt_cdn_d, retain_cnt_slb_d, retain_cnt_nas_d, retain_cnt_mangodb_d, retain_cnt_eip_d, retain_cnt_redis_d, retain_cnt_communication_d, retain_cnt_domain_d, retain_cnt_email_d, retain_cnt_dns_d from user_info_offline where user_id = 1953401122571952) WHERE create_time>'2018-01-01 00:00:00' EXCEPT SELECT user_id FROM (select user_id, create_time, reg_channel, src, certified_from, certified_time, account_certify_type, is_certified, crm_label, order_cnt, cloud_order_cnt, cloud_market_order_cnt, count_ecs_paid_order, count_rds_paid_order, count_oss_paid_order, count_cdn_paid_order, count_slb_paid_order, count_nas_paid_order, count_mongodb_paid_order, count_eip_paid_order, count_redis_paid_order, count_communicate_paid_order from user_info_online where user_id = 1953401122571952) ) UNION SELECT coalesce(a.user_id, b.user_id) as user_id FROM (select user_id, create_time, reg_channel, src, certified_from, certified_time, account_certify_type, is_certified, crm_label, order_cnt, cloud_order_cnt, cloud_market_order_cnt, count_ecs_paid_order, count_rds_paid_order, count_oss_paid_order, count_cdn_paid_order, count_slb_paid_order, count_nas_paid_order, count_mongodb_paid_order, count_eip_paid_order, count_redis_paid_order, count_communicate_paid_order from user_info_online where user_id = 1953401122571952) a LEFT JOIN (select user_id, havana_id, create_time, reg_days, own, reg_own_group, reg_src_group, reg_channel, src, global_city_name, locale, cid, cid_name, account_certify_type, certified_from, certified_time, certify_type, is_certified, account_type, address, age, sex, birthday, global_province_name, group_phone_area_no, group_phone_zone_no, l_level, crm_label,account_structure, account_frozen, hid_b2b, hid_taobao, is_aliyun_test_user, is_aliyun_user, is_enterprise, is_enterprise_account, is_global_user, is_net_test_user, is_oversea_user, is_valid_reg_user, is_net_user, parent_pk, taobao_account, update_time, user_region, fenxiao_create_time, fenxiao_label, fenxiao_parent_uid, fenxiao_region, crm_customer_priority, crm_customer_rank,order_cnt, cloud_order_cnt, cloud_market_order_cnt, count_ecs_paid_order, count_rds_paid_order, count_oss_paid_order, count_cdn_paid_order, count_slb_paid_order, count_nas_paid_order, count_mongodb_paid_order, count_eip_paid_order, count_redis_paid_order, count_communicate_paid_order, is_paid_retain, count_1_all_visit, count_7_all_visit, count_15_all_visit, count_30_all_visit, is_retain_d,count_1_yq_visit,count_7_yq_visit,count_15_yq_visit,count_30_yq_visit,count_1_promotion_visit,count_7_promotion_visit,count_15_promotion_visit,count_30_promotion_visit,count_1_market_visit,count_7_market_visit,count_15_market_visit,count_30_market_visit,count_1_search_visit,count_7_search_visit,count_15_search_visit,count_30_search_visit,count_1_help_visit,count_7_help_visit,count_15_help_visit,count_30_help_visit,count_7_domain_visit,count_15_domain_visit,count_30_domain_visit,count_1_beian_visit,count_7_beian_visit,count_15_beian_visit,count_30_beian_visit,count_1_wanwang_visit,count_7_wanwang_visit,count_15_wanwang_visit,count_30_wanwang_visit,count_1_domain_visit,crm_biz_category,cloud_pay_greater_zero_order_cnt, retain_cnt_d, retain_cnt_ecs_d, retain_cnt_rds_d, retain_cnt_oss_d, retain_cnt_cdn_d, retain_cnt_slb_d, retain_cnt_nas_d, retain_cnt_mangodb_d, retain_cnt_eip_d, retain_cnt_redis_d, retain_cnt_communication_d, retain_cnt_domain_d, retain_cnt_email_d, retain_cnt_dns_d from user_info_offline where user_id = 1953401122571952) b ON a.user_id = b.user_id WHERE ( (a.create_time>'2018-01-01 00:00:00') or (a.create_time is null and b.create_time>'2018-01-01 00:00:00') ) ) INTERSECT ( SELECT coalesce(a.user_id, b.user_id) as user_id FROM (select user_id, create_time, reg_channel, src, certified_from, certified_time, account_certify_type, is_certified, crm_label, order_cnt, cloud_order_cnt, cloud_market_order_cnt, count_ecs_paid_order, count_rds_paid_order, count_oss_paid_order, count_cdn_paid_order, count_slb_paid_order, count_nas_paid_order, count_mongodb_paid_order, count_eip_paid_order, count_redis_paid_order, count_communicate_paid_order from user_info_online where user_id = 1953401122571952) a FULL JOIN (select user_id, havana_id, create_time, reg_days, own, reg_own_group, reg_src_group, reg_channel, src, global_city_name, locale, cid, cid_name, account_certify_type, certified_from, certified_time, certify_type, is_certified, account_type, address, age, sex, birthday, global_province_name, group_phone_area_no, group_phone_zone_no, l_level, crm_label,account_structure, account_frozen, hid_b2b, hid_taobao, is_aliyun_test_user, is_aliyun_user, is_enterprise, is_enterprise_account, is_global_user, is_net_test_user, is_oversea_user, is_valid_reg_user, is_net_user, parent_pk, taobao_account, update_time, user_region, fenxiao_create_time, fenxiao_label, fenxiao_parent_uid, fenxiao_region, crm_customer_priority, crm_customer_rank,order_cnt, cloud_order_cnt, cloud_market_order_cnt, count_ecs_paid_order, count_rds_paid_order, count_oss_paid_order, count_cdn_paid_order, count_slb_paid_order, count_nas_paid_order, count_mongodb_paid_order, count_eip_paid_order, count_redis_paid_order, count_communicate_paid_order, is_paid_retain, count_1_all_visit, count_7_all_visit, count_15_all_visit, count_30_all_visit, is_retain_d,count_1_yq_visit,count_7_yq_visit,count_15_yq_visit,count_30_yq_visit,count_1_promotion_visit,count_7_promotion_visit,count_15_promotion_visit,count_30_promotion_visit,count_1_market_visit,count_7_market_visit,count_15_market_visit,count_30_market_visit,count_1_search_visit,count_7_search_visit,count_15_search_visit,count_30_search_visit,count_1_help_visit,count_7_help_visit,count_15_help_visit,count_30_help_visit,count_7_domain_visit,count_15_domain_visit,count_30_domain_visit,count_1_beian_visit,count_7_beian_visit,count_15_beian_visit,count_30_beian_visit,count_1_wanwang_visit,count_7_wanwang_visit,count_15_wanwang_visit,count_30_wanwang_visit,count_1_domain_visit,crm_biz_category,cloud_pay_greater_zero_order_cnt, retain_cnt_d, retain_cnt_ecs_d, retain_cnt_rds_d, retain_cnt_oss_d, retain_cnt_cdn_d, retain_cnt_slb_d, retain_cnt_nas_d, retain_cnt_mangodb_d, retain_cnt_eip_d, retain_cnt_redis_d, retain_cnt_communication_d, retain_cnt_domain_d, retain_cnt_email_d, retain_cnt_dns_d from user_info_offline where user_id = 1953401122571952) b ON a.user_id = b.user_id WHERE ( (a.account_certify_type='personal') or (a.account_certify_type is null and b.account_certify_type='personal') ) )";

//        System.out.println(sql);

        MySqlStatementParser parser = new MySqlStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLSelectStatement stmt = (SQLSelectStatement)statementList.get(0);

        assertEquals(1, statementList.size());

        assertEquals("((SELECT user_id\n" +
                "FROM (\n" +
                "\tSELECT user_id, havana_id, create_time, reg_days, own\n" +
                "\t\t, reg_own_group, reg_src_group, reg_channel, src, global_city_name\n" +
                "\t\t, locale, cid, cid_name, account_certify_type, certified_from\n" +
                "\t\t, certified_time, certify_type, is_certified, account_type, address\n" +
                "\t\t, age, sex, birthday, global_province_name, group_phone_area_no\n" +
                "\t\t, group_phone_zone_no, l_level, crm_label, account_structure, account_frozen\n" +
                "\t\t, hid_b2b, hid_taobao, is_aliyun_test_user, is_aliyun_user, is_enterprise\n" +
                "\t\t, is_enterprise_account, is_global_user, is_net_test_user, is_oversea_user, is_valid_reg_user\n" +
                "\t\t, is_net_user, parent_pk, taobao_account, update_time, user_region\n" +
                "\t\t, fenxiao_create_time, fenxiao_label, fenxiao_parent_uid, fenxiao_region, crm_customer_priority\n" +
                "\t\t, crm_customer_rank, order_cnt, cloud_order_cnt, cloud_market_order_cnt, count_ecs_paid_order\n" +
                "\t\t, count_rds_paid_order, count_oss_paid_order, count_cdn_paid_order, count_slb_paid_order, count_nas_paid_order\n" +
                "\t\t, count_mongodb_paid_order, count_eip_paid_order, count_redis_paid_order, count_communicate_paid_order, is_paid_retain\n" +
                "\t\t, count_1_all_visit, count_7_all_visit, count_15_all_visit, count_30_all_visit, is_retain_d\n" +
                "\t\t, count_1_yq_visit, count_7_yq_visit, count_15_yq_visit, count_30_yq_visit, count_1_promotion_visit\n" +
                "\t\t, count_7_promotion_visit, count_15_promotion_visit, count_30_promotion_visit, count_1_market_visit, count_7_market_visit\n" +
                "\t\t, count_15_market_visit, count_30_market_visit, count_1_search_visit, count_7_search_visit, count_15_search_visit\n" +
                "\t\t, count_30_search_visit, count_1_help_visit, count_7_help_visit, count_15_help_visit, count_30_help_visit\n" +
                "\t\t, count_7_domain_visit, count_15_domain_visit, count_30_domain_visit, count_1_beian_visit, count_7_beian_visit\n" +
                "\t\t, count_15_beian_visit, count_30_beian_visit, count_1_wanwang_visit, count_7_wanwang_visit, count_15_wanwang_visit\n" +
                "\t\t, count_30_wanwang_visit, count_1_domain_visit, crm_biz_category, cloud_pay_greater_zero_order_cnt, retain_cnt_d\n" +
                "\t\t, retain_cnt_ecs_d, retain_cnt_rds_d, retain_cnt_oss_d, retain_cnt_cdn_d, retain_cnt_slb_d\n" +
                "\t\t, retain_cnt_nas_d, retain_cnt_mangodb_d, retain_cnt_eip_d, retain_cnt_redis_d, retain_cnt_communication_d\n" +
                "\t\t, retain_cnt_domain_d, retain_cnt_email_d, retain_cnt_dns_d\n" +
                "\tFROM user_info_offline\n" +
                "\tWHERE user_id = 1953401122571952\n" +
                ")\n" +
                "WHERE create_time > '2018-01-01 00:00:00'\n" +
                "EXCEPT\n" +
                "SELECT user_id\n" +
                "FROM (\n" +
                "\tSELECT user_id, create_time, reg_channel, src, certified_from\n" +
                "\t\t, certified_time, account_certify_type, is_certified, crm_label, order_cnt\n" +
                "\t\t, cloud_order_cnt, cloud_market_order_cnt, count_ecs_paid_order, count_rds_paid_order, count_oss_paid_order\n" +
                "\t\t, count_cdn_paid_order, count_slb_paid_order, count_nas_paid_order, count_mongodb_paid_order, count_eip_paid_order\n" +
                "\t\t, count_redis_paid_order, count_communicate_paid_order\n" +
                "\tFROM user_info_online\n" +
                "\tWHERE user_id = 1953401122571952\n" +
                "))\n" +
                "UNION\n" +
                "SELECT coalesce(a.user_id, b.user_id) AS user_id\n" +
                "FROM (\n" +
                "\tSELECT user_id, create_time, reg_channel, src, certified_from\n" +
                "\t\t, certified_time, account_certify_type, is_certified, crm_label, order_cnt\n" +
                "\t\t, cloud_order_cnt, cloud_market_order_cnt, count_ecs_paid_order, count_rds_paid_order, count_oss_paid_order\n" +
                "\t\t, count_cdn_paid_order, count_slb_paid_order, count_nas_paid_order, count_mongodb_paid_order, count_eip_paid_order\n" +
                "\t\t, count_redis_paid_order, count_communicate_paid_order\n" +
                "\tFROM user_info_online\n" +
                "\tWHERE user_id = 1953401122571952\n" +
                ") a\n" +
                "\tLEFT JOIN (\n" +
                "\t\tSELECT user_id, havana_id, create_time, reg_days, own\n" +
                "\t\t\t, reg_own_group, reg_src_group, reg_channel, src, global_city_name\n" +
                "\t\t\t, locale, cid, cid_name, account_certify_type, certified_from\n" +
                "\t\t\t, certified_time, certify_type, is_certified, account_type, address\n" +
                "\t\t\t, age, sex, birthday, global_province_name, group_phone_area_no\n" +
                "\t\t\t, group_phone_zone_no, l_level, crm_label, account_structure, account_frozen\n" +
                "\t\t\t, hid_b2b, hid_taobao, is_aliyun_test_user, is_aliyun_user, is_enterprise\n" +
                "\t\t\t, is_enterprise_account, is_global_user, is_net_test_user, is_oversea_user, is_valid_reg_user\n" +
                "\t\t\t, is_net_user, parent_pk, taobao_account, update_time, user_region\n" +
                "\t\t\t, fenxiao_create_time, fenxiao_label, fenxiao_parent_uid, fenxiao_region, crm_customer_priority\n" +
                "\t\t\t, crm_customer_rank, order_cnt, cloud_order_cnt, cloud_market_order_cnt, count_ecs_paid_order\n" +
                "\t\t\t, count_rds_paid_order, count_oss_paid_order, count_cdn_paid_order, count_slb_paid_order, count_nas_paid_order\n" +
                "\t\t\t, count_mongodb_paid_order, count_eip_paid_order, count_redis_paid_order, count_communicate_paid_order, is_paid_retain\n" +
                "\t\t\t, count_1_all_visit, count_7_all_visit, count_15_all_visit, count_30_all_visit, is_retain_d\n" +
                "\t\t\t, count_1_yq_visit, count_7_yq_visit, count_15_yq_visit, count_30_yq_visit, count_1_promotion_visit\n" +
                "\t\t\t, count_7_promotion_visit, count_15_promotion_visit, count_30_promotion_visit, count_1_market_visit, count_7_market_visit\n" +
                "\t\t\t, count_15_market_visit, count_30_market_visit, count_1_search_visit, count_7_search_visit, count_15_search_visit\n" +
                "\t\t\t, count_30_search_visit, count_1_help_visit, count_7_help_visit, count_15_help_visit, count_30_help_visit\n" +
                "\t\t\t, count_7_domain_visit, count_15_domain_visit, count_30_domain_visit, count_1_beian_visit, count_7_beian_visit\n" +
                "\t\t\t, count_15_beian_visit, count_30_beian_visit, count_1_wanwang_visit, count_7_wanwang_visit, count_15_wanwang_visit\n" +
                "\t\t\t, count_30_wanwang_visit, count_1_domain_visit, crm_biz_category, cloud_pay_greater_zero_order_cnt, retain_cnt_d\n" +
                "\t\t\t, retain_cnt_ecs_d, retain_cnt_rds_d, retain_cnt_oss_d, retain_cnt_cdn_d, retain_cnt_slb_d\n" +
                "\t\t\t, retain_cnt_nas_d, retain_cnt_mangodb_d, retain_cnt_eip_d, retain_cnt_redis_d, retain_cnt_communication_d\n" +
                "\t\t\t, retain_cnt_domain_d, retain_cnt_email_d, retain_cnt_dns_d\n" +
                "\t\tFROM user_info_offline\n" +
                "\t\tWHERE user_id = 1953401122571952\n" +
                "\t) b\n" +
                "\tON a.user_id = b.user_id\n" +
                "WHERE a.create_time > '2018-01-01 00:00:00'\n" +
                "\tOR (a.create_time IS NULL\n" +
                "\t\tAND b.create_time > '2018-01-01 00:00:00'))\n" +
                "INTERSECT\n" +
                "(SELECT coalesce(a.user_id, b.user_id) AS user_id\n" +
                "FROM (\n" +
                "\tSELECT user_id, create_time, reg_channel, src, certified_from\n" +
                "\t\t, certified_time, account_certify_type, is_certified, crm_label, order_cnt\n" +
                "\t\t, cloud_order_cnt, cloud_market_order_cnt, count_ecs_paid_order, count_rds_paid_order, count_oss_paid_order\n" +
                "\t\t, count_cdn_paid_order, count_slb_paid_order, count_nas_paid_order, count_mongodb_paid_order, count_eip_paid_order\n" +
                "\t\t, count_redis_paid_order, count_communicate_paid_order\n" +
                "\tFROM user_info_online\n" +
                "\tWHERE user_id = 1953401122571952\n" +
                ") a\n" +
                "\tFULL JOIN (\n" +
                "\t\tSELECT user_id, havana_id, create_time, reg_days, own\n" +
                "\t\t\t, reg_own_group, reg_src_group, reg_channel, src, global_city_name\n" +
                "\t\t\t, locale, cid, cid_name, account_certify_type, certified_from\n" +
                "\t\t\t, certified_time, certify_type, is_certified, account_type, address\n" +
                "\t\t\t, age, sex, birthday, global_province_name, group_phone_area_no\n" +
                "\t\t\t, group_phone_zone_no, l_level, crm_label, account_structure, account_frozen\n" +
                "\t\t\t, hid_b2b, hid_taobao, is_aliyun_test_user, is_aliyun_user, is_enterprise\n" +
                "\t\t\t, is_enterprise_account, is_global_user, is_net_test_user, is_oversea_user, is_valid_reg_user\n" +
                "\t\t\t, is_net_user, parent_pk, taobao_account, update_time, user_region\n" +
                "\t\t\t, fenxiao_create_time, fenxiao_label, fenxiao_parent_uid, fenxiao_region, crm_customer_priority\n" +
                "\t\t\t, crm_customer_rank, order_cnt, cloud_order_cnt, cloud_market_order_cnt, count_ecs_paid_order\n" +
                "\t\t\t, count_rds_paid_order, count_oss_paid_order, count_cdn_paid_order, count_slb_paid_order, count_nas_paid_order\n" +
                "\t\t\t, count_mongodb_paid_order, count_eip_paid_order, count_redis_paid_order, count_communicate_paid_order, is_paid_retain\n" +
                "\t\t\t, count_1_all_visit, count_7_all_visit, count_15_all_visit, count_30_all_visit, is_retain_d\n" +
                "\t\t\t, count_1_yq_visit, count_7_yq_visit, count_15_yq_visit, count_30_yq_visit, count_1_promotion_visit\n" +
                "\t\t\t, count_7_promotion_visit, count_15_promotion_visit, count_30_promotion_visit, count_1_market_visit, count_7_market_visit\n" +
                "\t\t\t, count_15_market_visit, count_30_market_visit, count_1_search_visit, count_7_search_visit, count_15_search_visit\n" +
                "\t\t\t, count_30_search_visit, count_1_help_visit, count_7_help_visit, count_15_help_visit, count_30_help_visit\n" +
                "\t\t\t, count_7_domain_visit, count_15_domain_visit, count_30_domain_visit, count_1_beian_visit, count_7_beian_visit\n" +
                "\t\t\t, count_15_beian_visit, count_30_beian_visit, count_1_wanwang_visit, count_7_wanwang_visit, count_15_wanwang_visit\n" +
                "\t\t\t, count_30_wanwang_visit, count_1_domain_visit, crm_biz_category, cloud_pay_greater_zero_order_cnt, retain_cnt_d\n" +
                "\t\t\t, retain_cnt_ecs_d, retain_cnt_rds_d, retain_cnt_oss_d, retain_cnt_cdn_d, retain_cnt_slb_d\n" +
                "\t\t\t, retain_cnt_nas_d, retain_cnt_mangodb_d, retain_cnt_eip_d, retain_cnt_redis_d, retain_cnt_communication_d\n" +
                "\t\t\t, retain_cnt_domain_d, retain_cnt_email_d, retain_cnt_dns_d\n" +
                "\t\tFROM user_info_offline\n" +
                "\t\tWHERE user_id = 1953401122571952\n" +
                "\t) b\n" +
                "\tON a.user_id = b.user_id\n" +
                "WHERE a.account_certify_type = 'personal'\n" +
                "\tOR (a.account_certify_type IS NULL\n" +
                "\t\tAND b.account_certify_type = 'personal'))", stmt.toString());
    }
}