/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.bvt.sql.oracle.create;

import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.Assert;

import java.util.List;

public class OracleCreateViewTest1 extends OracleTest {

    public void test_types() throws Exception {
        String sql = //
        "   \n" +
                "    CREATE OR REPLACE FORCE VIEW \"XBO\".\"VW_SCY\" (\"ID\", \"GMT_CREATE\", \"GMT_MODIFIED\", \"COMPANY_NAME\", \"BIZ_TYPE\", \"BIZ_TYPE_2\", \"SERVICE_TYPE\", \"SERVICE_LEVEL\", \"BUSINESS_ROLE\", \"STATUS\", \"RECOMMENDED\", \"COUNTRY\", \"PROVINCE\", \"CITY\", \"ADDRESS\", \"ZIP\", \"LOGO_FILE\", \"EMAIL\", \"BRIEF_PROFILE\", \"DOMAIN_ID\", \"IS_PASS_AV\", \"KEYWORDS\", \"PROVIDE_PRODUCTS\", \"PURCHASE_PRODUCTS\", \"BRAND_NAME\", \"PROMOTION_VALUE\", \"OWNER_MEMBER_ID\", \"OWNER_SEQ\", \"EMPLOYEES_COUNT\", \"ANNUAL_REVENUE\", \"HOMEPAGE_URL\", \"REG_ADDRESS\", \"TRADE_REGION\", \"TRADE_REGION_USER\", \"REG_CAPITAL\", \"OWNERSHIP_TYPE\", \"ESTABLISHED_YEAR\", \"PRINCIPAL\", \"ANNUAL_PURCHASE\", \"CERTIFICATION\", \"CERTIFICATION_2\", \"CONTACT_MANUFACTURING\", \"YEARS_OEM\", \"CREATE_TYPE\", \"VIDEO_PATH\", \"ABOUTUS_IMAGE_PATH\", \"ABOUTUS_IMAGE_TITLE\", \"CHINESE_NAME\", \"C_RID\", \"V_RID\") AS \n" +
                "  select /*+ use_hash(v c) ordered */\n" +
                " c.id,\n" +
                " c.gmt_create,\n" +
                " c.gmt_modified,\n" +
                " c.company_name,\n" +
                " c.biz_type,\n" +
                " c.biz_type_2,\n" +
                " v.service_type,\n" +
                " v.service_level,\n" +
                " v.business_role,\n" +
                " c.status,\n" +
                " c.recommended,\n" +
                " c.country,\n" +
                " c.province,\n" +
                " c.city,\n" +
                " c.address,\n" +
                " c.zip,\n" +
                " c.logo_file,\n" +
                " c.email,\n" +
                " c.brief_profile,\n" +
                " c.domain_id,\n" +
                " c.is_pass_av,\n" +
                " c.keywords,\n" +
                " c.provide_products,\n" +
                " c.purchase_products,\n" +
                " c.brand_name,\n" +
                " c.promotion_value,\n" +
                " c.owner_member_id,\n" +
                " c.owner_seq,\n" +
                " c.employees_count,\n" +
                " c.annual_revenue,\n" +
                " c.homepage_url,\n" +
                " c.reg_address,\n" +
                " c.trade_region,\n" +
                " c.trade_region_user,\n" +
                " c.reg_capital,\n" +
                " c.ownership_type,\n" +
                " c.established_year,\n" +
                " c.principal,\n" +
                " c.annual_purchase,\n" +
                " c.certification,\n" +
                " c.certification_2,\n" +
                " c.contact_manufacturing,\n" +
                " c.years_oem,\n" +
                " v.stage                 create_type,\n" +
                " c.video_path,\n" +
                " c.aboutus_image_path,\n" +
                " c.aboutus_image_title,\n" +
                " c.chinese_name,\n" +
                " c.rowid                 c_rid,\n" +
                " v.rowid                 v_rid\n" +
                "  from tb_001 v, tb_002 c\n" +
                " where (v.service_type in ('gs', 'cgs', 'hkgs', 'twgs') or\n" +
                "       (v.service_type = 'cnfm' and v.stage = 'new_order'))\n" +
                "   and v.id = c.id     ";

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        SQLStatement stmt = statementList.get(0);
        print(statementList);

        Assert.assertEquals(1, statementList.size());

        Assert.assertEquals("CREATE OR REPLACE VIEW \"XBO\".\"VW_SCY\" (\n" +
                        "\t\"ID\", \n" +
                        "\t\"GMT_CREATE\", \n" +
                        "\t\"GMT_MODIFIED\", \n" +
                        "\t\"COMPANY_NAME\", \n" +
                        "\t\"BIZ_TYPE\", \n" +
                        "\t\"BIZ_TYPE_2\", \n" +
                        "\t\"SERVICE_TYPE\", \n" +
                        "\t\"SERVICE_LEVEL\", \n" +
                        "\t\"BUSINESS_ROLE\", \n" +
                        "\t\"STATUS\", \n" +
                        "\t\"RECOMMENDED\", \n" +
                        "\t\"COUNTRY\", \n" +
                        "\t\"PROVINCE\", \n" +
                        "\t\"CITY\", \n" +
                        "\t\"ADDRESS\", \n" +
                        "\t\"ZIP\", \n" +
                        "\t\"LOGO_FILE\", \n" +
                        "\t\"EMAIL\", \n" +
                        "\t\"BRIEF_PROFILE\", \n" +
                        "\t\"DOMAIN_ID\", \n" +
                        "\t\"IS_PASS_AV\", \n" +
                        "\t\"KEYWORDS\", \n" +
                        "\t\"PROVIDE_PRODUCTS\", \n" +
                        "\t\"PURCHASE_PRODUCTS\", \n" +
                        "\t\"BRAND_NAME\", \n" +
                        "\t\"PROMOTION_VALUE\", \n" +
                        "\t\"OWNER_MEMBER_ID\", \n" +
                        "\t\"OWNER_SEQ\", \n" +
                        "\t\"EMPLOYEES_COUNT\", \n" +
                        "\t\"ANNUAL_REVENUE\", \n" +
                        "\t\"HOMEPAGE_URL\", \n" +
                        "\t\"REG_ADDRESS\", \n" +
                        "\t\"TRADE_REGION\", \n" +
                        "\t\"TRADE_REGION_USER\", \n" +
                        "\t\"REG_CAPITAL\", \n" +
                        "\t\"OWNERSHIP_TYPE\", \n" +
                        "\t\"ESTABLISHED_YEAR\", \n" +
                        "\t\"PRINCIPAL\", \n" +
                        "\t\"ANNUAL_PURCHASE\", \n" +
                        "\t\"CERTIFICATION\", \n" +
                        "\t\"CERTIFICATION_2\", \n" +
                        "\t\"CONTACT_MANUFACTURING\", \n" +
                        "\t\"YEARS_OEM\", \n" +
                        "\t\"CREATE_TYPE\", \n" +
                        "\t\"VIDEO_PATH\", \n" +
                        "\t\"ABOUTUS_IMAGE_PATH\", \n" +
                        "\t\"ABOUTUS_IMAGE_TITLE\", \n" +
                        "\t\"CHINESE_NAME\", \n" +
                        "\t\"C_RID\", \n" +
                        "\t\"V_RID\"\n" +
                        ")\n" +
                        "AS\n" +
                        "SELECT /*+ use_hash(v c) ordered */ c.id, c.gmt_create, c.gmt_modified, c.company_name, c.biz_type\n" +
                        "\t, c.biz_type_2, v.service_type, v.service_level, v.business_role, c.status\n" +
                        "\t, c.recommended, c.country, c.province, c.city, c.address\n" +
                        "\t, c.zip, c.logo_file, c.email, c.brief_profile, c.domain_id\n" +
                        "\t, c.is_pass_av, c.keywords, c.provide_products, c.purchase_products, c.brand_name\n" +
                        "\t, c.promotion_value, c.owner_member_id, c.owner_seq, c.employees_count, c.annual_revenue\n" +
                        "\t, c.homepage_url, c.reg_address, c.trade_region, c.trade_region_user, c.reg_capital\n" +
                        "\t, c.ownership_type, c.established_year, c.principal, c.annual_purchase, c.certification\n" +
                        "\t, c.certification_2, c.contact_manufacturing, c.years_oem, v.stage AS create_type, c.video_path\n" +
                        "\t, c.aboutus_image_path, c.aboutus_image_title, c.chinese_name, c.rowid AS c_rid, v.rowid AS v_rid\n" +
                        "FROM tb_001 v, tb_002 c\n" +
                        "WHERE (v.service_type IN ('gs', 'cgs', 'hkgs', 'twgs')\n" +
                        "\t\tOR (v.service_type = 'cnfm'\n" +
                        "\t\t\tAND v.stage = 'new_order'))\n" +
                        "\tAND v.id = c.id",//
                            SQLUtils.toSQLString(stmt, JdbcConstants.ORACLE));

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        stmt.accept(visitor);

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        Assert.assertEquals(2, visitor.getTables().size());

        Assert.assertEquals(51, visitor.getColumns().size());

        Assert.assertTrue(visitor.getColumns().contains(new TableStat.Column("tb_001", "service_type")));
    }
}
