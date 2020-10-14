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
package com.alibaba.druid.bvt.sql.oracle.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;


public class OracleSelectTest123 extends MysqlTest {
    public void test_0() throws Exception {
        String sql = "SELECT ECC_CPR.cms_con_dev_sites_change_obj(b.hc_con_id, b.po_header_id, b.po_header_code, b.differ_header_id, b.Id, b.Customize_Sites_Id, b.Customize_Site_Des, b.site_attr, b.Device_Bom_Id, b.DEVICE_QTY, b.item_code, b.Device_Bom_Name, b.Device_Bom_EngName, b.Dev_Bom_Type_Attribute, b.Dev_Bom_Type_Desc, b.Dev_Bom_Type_EngDesc, b.change_type, b.cms_site_status, b.last_update_date, b.Memo, para.parameter_value, b.customer_po, b.batch_delivery, FIVE_Address_NAME, CON_CONTRACT_PO_ID, b.DIFF_DEVICE_QTY)\n" +
                "FROM (\n" +
                "\tSELECT poheader.hc_con_id, poheader.id AS po_header_id, poheader.po_header_code, chg.differ_header_id, poItem.Id\n" +
                "\t\t, addr.customize_sites_id AS Customize_Sites_Id\n" +
                "\t\t, cu.site_address || cu.second_level || cu.third_level || cu.fourth_level || cu.five_level AS Customize_Site_Des\n" +
                "\t\t, site.seq AS site_seq, addr.seq AS addr_seq, site.site_attribute AS site_attr, addr.site_address_id, poItem.Device_Bom_Id\n" +
                "\t\t, poItem.Config_Amount, nvl(bom.scbom_pto_no, bom.item_code) AS item_code, bom_desc.description AS Device_Bom_Name\n" +
                "\t\t, bom.PTO_NAME AS Device_Bom_EngName, poItem.Dev_Bom_Type_Attribute, prop.chinese_name AS Dev_Bom_Type_Desc, prop.english_name AS Dev_Bom_Type_EngDesc, chg.change_type\n" +
                "\t\t, poItem.cms_site_status\n" +
                "\t\t, to_date(to_char(poItem.last_update_date, 'YYYY-MM-DD HH24:MI:SS'), 'YYYY-MM-DD HH24:MI:SS') AS last_update_date\n" +
                "\t\t, site.remark AS Memo\n" +
                "\t\t, CASE \n" +
                "\t\t\tWHEN po.PONUMS IS NOT NULL THEN po.PONUMS\n" +
                "\t\t\tWHEN poheader.id = poheader.hc_con_id THEN h1.user_con_number\n" +
                "\t\t\tELSE poheader.user_con_number\n" +
                "\t\tEND AS customer_po\n" +
                "\t\t, decode(addr.batch_delivery, ?, NULL, addr.batch_delivery) AS batch_delivery\n" +
                "\t\t, addr.SITE_ADDRESS || addr.SECOND_LEVEL || addr.THIRD_LEVEL || addr.FOURTH_LEVEL || addr.FIVE_LEVEL AS FIVE_Address_NAME\n" +
                "\t\t, addr.CON_CONTRACT_PO_ID, chg.DEVICE_QTY, chg.DIFF_DEVICE_QTY\n" +
                "\tFROM ecc_cpr.ecc_cpr_hc_po_heahers poheader, ecc_cpr.ecc_cpr_hc_po_config poItem, ecc_cpr.ecc_cpr_hc_con_heahers h1, (\n" +
                "\t\tSELECT DISTINCT add_chg.contract_header_id, add_chg.differ_header_id, add_chg.site_address_id, add_chg.change_type, add_chg.DEVICE_QTY\n" +
                "\t\t\t, add_chg.DIFF_DEVICE_QTY\n" +
                "\t\tFROM ecc_cpr.diff_address_change add_chg, ecc_cpr.cpr_config_differ_header diff\n" +
                "\t\tWHERE add_chg.enabled_flag = ? -- and add_chg.ref_flag=0\n" +
                "\t\t\tAND add_chg.change_type > ?\n" +
                "\t\t\tAND diff.review_status = ?\n" +
                "\t\t\tAND add_chg.differ_header_id = ?\n" +
                "\t\t\tAND diff.differ_header_id = ?\n" +
                "\t) chg\n" +
                "\tLEFT JOIN ECC_CPR.CON_PC_ADDRESS_PO po ON po.ENABLED_FLAG = ?\n" +
                "\tAND po.SITE_ADDRESS_ID = chg.site_address_id\n" +
                "\tAND po.CONTRACT_HEADER_ID = ? , ecc_cpr.con_sites site, ecc_cpr.con_sites_address addr, ecc_cpr.con_customize_sites cu, ecc_cpr.system_bom bom, ecc_cpr.system_bom_desc bom_desc, ecc_fnd.ecc_fnd_iecc_property prop\n" +
                "\tWHERE poHeader.Enabled_Flag = ?\n" +
                "\t\tAND bom_desc.language = ?\n" +
                "\t\tAND bom_desc.system_bom_id = poItem.Device_Bom_Id\n" +
                "\t\tAND prop.enable_flag = ?\n" +
                "\t\tAND prop.property_type = ?\n" +
                "\t\tAND poItem.Dev_Bom_Type_Attribute = prop.property_id\n" +
                "\t\tAND poItem.Cms_Sync_Flag = ?\n" +
                "\t\tAND bom.system_bom_id = poItem.Device_Bom_Id\n" +
                "\t\tAND poItem.Con_Site_Id = site.site_id\n" +
                "\t\tAND addr.customize_sites_id = cu.customize_sites_id\n" +
                "\t\tAND addr.customize_sites_id != ?\n" +
                "\t\tAND poItem.Con_Address_Id = addr.site_address_id\n" +
                "\t\tAND poItem.Con_Address_Id = chg.site_address_id\n" +
                "\t\tAND poheader.hc_con_id = h1.hc_con_id\n" +
                "\t\tAND poItem.Po_Header_Id = poheader.id\n" +
                "\t\tAND poItem.Po_Header_Id = ?\n" +
                "\t\tAND cu.contract_header_id = ?\n" +
                "\t\tAND chg.contract_header_id = ?\n" +
                "\t\tAND poItem.Contract_Header_Id = ?\n" +
                ") b\n" +
                "\tLEFT JOIN (\n" +
                "\t\t/**---------------20150527 update begin-------------------------------\n" +
                "\t\t\t\t  select params.contract_header_id,\n" +
                "                           params.site_address_id,\n" +
                "                           ECC_CPR.GET_CON_ADDRESS_PARAMS_VALUE(\n" +
                "                                 params.contract_header_id,params.contract_config_id)  parameter_value\n" +
                "                    from\n" +
                "                    (\n" +
                "                           SELECT distinct con_param.contract_header_id,\n" +
                "                                  address.site_address_id,\n" +
                "                                  address.contract_config_id\n" +
                "                           FROM  ecc_cpr.con_sites_address address,\n" +
                "                                 ecc_cpr.temp_parameters   param,\n" +
                "                                 ecc_cpr.con_parameters    con_param\n" +
                "                           WHERE address.enabled_flag = 'Y'\n" +
                "                                 AND con_param.contract_config_id = address.contract_config_id\n" +
                "                                 AND con_param.enabled_flag = 'Y'\n" +
                "                                 AND param.temp_parameters_id = con_param.temp_parameters_id\n" +
                "                                 AND param.is_transfer = 'Y'\n" +
                "                                 AND con_param.contract_header_id=2058912\n" +
                "                     ) params\n" +
                "\t\t\t\t\t---------------20150527 update end------------------------**/\n" +
                "\t\tSELECT DISTINCT s.contract_header_id, address.site_address_id\n" +
                "\t\t\t, substr(to_char(WM_CONCAT(DISTINCT con_param.parameter_value)), ?, ?) AS parameter_value\n" +
                "\t\tFROM ecc_cpr.con_sites s, ecc_cpr.con_sites_address address, ecc_cpr.temp_parameters param, ecc_cpr.con_parameters con_param\n" +
                "\t\tWHERE param.temp_parameters_id = con_param.temp_parameters_id\n" +
                "\t\t\tAND con_param.contract_config_id = address.contract_config_id\n" +
                "\t\t\tAND address.site_id = s.site_id\n" +
                "\t\t\tAND param.is_transfer = ?\n" +
                "\t\t\tAND con_param.enabled_flag = ?\n" +
                "\t\t\tAND s.contract_header_id = ?\n" +
                "\t\t\tAND con_param.contract_header_id = ?\n" +
                "\t\t\tAND s.contract_header_id <> (\n" +
                "\t\t\t\tSELECT p.property_id\n" +
                "\t\t\t\tFROM ecc_fnd.ecc_fnd_iecc_property p\n" +
                "\t\t\t\tWHERE PROPERTY_TYPE = ?\n" +
                "\t\t\t)\n" +
                "\t\t\tAND con_param.contract_header_id <> (\n" +
                "\t\t\t\tSELECT p.property_id\n" +
                "\t\t\t\tFROM ecc_fnd.ecc_fnd_iecc_property p\n" +
                "\t\t\t\tWHERE PROPERTY_TYPE = ?\n" +
                "\t\t\t)\n" +
                "\t\tGROUP BY s.contract_header_id, address.site_address_id\n" +
                "\t) para ON para.site_address_id = b.site_address_id \n" +
                "ORDER BY b.Device_Bom_Id, b.Dev_Bom_Type_Attribute, b.site_seq, b.addr_seq";

        List<SQLStatement> statementList = SQLUtils.parseStatements(sql, JdbcConstants.ORACLE);

        assertEquals(1, statementList.size());

        SQLSelectStatement stmt = (SQLSelectStatement) statementList.get(0);

        assertEquals("SELECT ECC_CPR.cms_con_dev_sites_change_obj(b.hc_con_id, b.po_header_id, b.po_header_code, b.differ_header_id, b.Id, b.Customize_Sites_Id, b.Customize_Site_Des, b.site_attr, b.Device_Bom_Id, b.DEVICE_QTY, b.item_code, b.Device_Bom_Name, b.Device_Bom_EngName, b.Dev_Bom_Type_Attribute, b.Dev_Bom_Type_Desc, b.Dev_Bom_Type_EngDesc, b.change_type, b.cms_site_status, b.last_update_date, b.Memo, para.parameter_value, b.customer_po, b.batch_delivery, FIVE_Address_NAME, CON_CONTRACT_PO_ID, b.DIFF_DEVICE_QTY)\n" +
                "FROM (\n" +
                "\tSELECT poheader.hc_con_id, poheader.id AS po_header_id, poheader.po_header_code, chg.differ_header_id, poItem.Id\n" +
                "\t\t, addr.customize_sites_id AS Customize_Sites_Id\n" +
                "\t\t, cu.site_address || cu.second_level || cu.third_level || cu.fourth_level || cu.five_level AS Customize_Site_Des\n" +
                "\t\t, site.seq AS site_seq, addr.seq AS addr_seq, site.site_attribute AS site_attr, addr.site_address_id, poItem.Device_Bom_Id\n" +
                "\t\t, poItem.Config_Amount, nvl(bom.scbom_pto_no, bom.item_code) AS item_code, bom_desc.description AS Device_Bom_Name\n" +
                "\t\t, bom.PTO_NAME AS Device_Bom_EngName, poItem.Dev_Bom_Type_Attribute, prop.chinese_name AS Dev_Bom_Type_Desc, prop.english_name AS Dev_Bom_Type_EngDesc, chg.change_type\n" +
                "\t\t, poItem.cms_site_status\n" +
                "\t\t, to_date(to_char(poItem.last_update_date, 'YYYY-MM-DD HH24:MI:SS'), 'YYYY-MM-DD HH24:MI:SS') AS last_update_date\n" +
                "\t\t, site.remark AS Memo\n" +
                "\t\t, CASE \n" +
                "\t\t\tWHEN po.PONUMS IS NOT NULL THEN po.PONUMS\n" +
                "\t\t\tWHEN poheader.id = poheader.hc_con_id THEN h1.user_con_number\n" +
                "\t\t\tELSE poheader.user_con_number\n" +
                "\t\tEND AS customer_po\n" +
                "\t\t, decode(addr.batch_delivery, ?, NULL, addr.batch_delivery) AS batch_delivery\n" +
                "\t\t, addr.SITE_ADDRESS || addr.SECOND_LEVEL || addr.THIRD_LEVEL || addr.FOURTH_LEVEL || addr.FIVE_LEVEL AS FIVE_Address_NAME\n" +
                "\t\t, addr.CON_CONTRACT_PO_ID, chg.DEVICE_QTY, chg.DIFF_DEVICE_QTY\n" +
                "\tFROM ecc_cpr.ecc_cpr_hc_po_heahers poheader, ecc_cpr.ecc_cpr_hc_po_config poItem, ecc_cpr.ecc_cpr_hc_con_heahers h1, (\n" +
                "\t\tSELECT DISTINCT add_chg.contract_header_id, add_chg.differ_header_id, add_chg.site_address_id, add_chg.change_type, add_chg.DEVICE_QTY\n" +
                "\t\t\t, add_chg.DIFF_DEVICE_QTY\n" +
                "\t\tFROM ecc_cpr.diff_address_change add_chg, ecc_cpr.cpr_config_differ_header diff\n" +
                "\t\tWHERE add_chg.enabled_flag = ? -- and add_chg.ref_flag=0\n" +
                "\t\t\tAND add_chg.change_type > ?\n" +
                "\t\t\tAND diff.review_status = ?\n" +
                "\t\t\tAND add_chg.differ_header_id = ?\n" +
                "\t\t\tAND diff.differ_header_id = ?\n" +
                "\t) chg\n" +
                "\tLEFT JOIN ECC_CPR.CON_PC_ADDRESS_PO po ON po.ENABLED_FLAG = ?\n" +
                "\t\tAND po.SITE_ADDRESS_ID = chg.site_address_id\n" +
                "\t\tAND po.CONTRACT_HEADER_ID = ? , ecc_cpr.con_sites site, ecc_cpr.con_sites_address addr, ecc_cpr.con_customize_sites cu, ecc_cpr.system_bom bom, ecc_cpr.system_bom_desc bom_desc, ecc_fnd.ecc_fnd_iecc_property prop\n" +
                "\tWHERE poHeader.Enabled_Flag = ?\n" +
                "\t\tAND bom_desc.language = ?\n" +
                "\t\tAND bom_desc.system_bom_id = poItem.Device_Bom_Id\n" +
                "\t\tAND prop.enable_flag = ?\n" +
                "\t\tAND prop.property_type = ?\n" +
                "\t\tAND poItem.Dev_Bom_Type_Attribute = prop.property_id\n" +
                "\t\tAND poItem.Cms_Sync_Flag = ?\n" +
                "\t\tAND bom.system_bom_id = poItem.Device_Bom_Id\n" +
                "\t\tAND poItem.Con_Site_Id = site.site_id\n" +
                "\t\tAND addr.customize_sites_id = cu.customize_sites_id\n" +
                "\t\tAND addr.customize_sites_id != ?\n" +
                "\t\tAND poItem.Con_Address_Id = addr.site_address_id\n" +
                "\t\tAND poItem.Con_Address_Id = chg.site_address_id\n" +
                "\t\tAND poheader.hc_con_id = h1.hc_con_id\n" +
                "\t\tAND poItem.Po_Header_Id = poheader.id\n" +
                "\t\tAND poItem.Po_Header_Id = ?\n" +
                "\t\tAND cu.contract_header_id = ?\n" +
                "\t\tAND chg.contract_header_id = ?\n" +
                "\t\tAND poItem.Contract_Header_Id = ?\n" +
                ") b\n" +
                "\tLEFT JOIN (\n" +
                "\t\t/**---------------20150527 update begin-------------------------------\n" +
                "\t\t\t\t  select params.contract_header_id,\n" +
                "                           params.site_address_id,\n" +
                "                           ECC_CPR.GET_CON_ADDRESS_PARAMS_VALUE(\n" +
                "                                 params.contract_header_id,params.contract_config_id)  parameter_value\n" +
                "                    from\n" +
                "                    (\n" +
                "                           SELECT distinct con_param.contract_header_id,\n" +
                "                                  address.site_address_id,\n" +
                "                                  address.contract_config_id\n" +
                "                           FROM  ecc_cpr.con_sites_address address,\n" +
                "                                 ecc_cpr.temp_parameters   param,\n" +
                "                                 ecc_cpr.con_parameters    con_param\n" +
                "                           WHERE address.enabled_flag = 'Y'\n" +
                "                                 AND con_param.contract_config_id = address.contract_config_id\n" +
                "                                 AND con_param.enabled_flag = 'Y'\n" +
                "                                 AND param.temp_parameters_id = con_param.temp_parameters_id\n" +
                "                                 AND param.is_transfer = 'Y'\n" +
                "                                 AND con_param.contract_header_id=2058912\n" +
                "                     ) params\n" +
                "\t\t\t\t\t---------------20150527 update end------------------------**/\n" +
                "\t\tSELECT DISTINCT s.contract_header_id, address.site_address_id\n" +
                "\t\t\t, substr(to_char(WM_CONCAT(DISTINCT con_param.parameter_value)), ?, ?) AS parameter_value\n" +
                "\t\tFROM ecc_cpr.con_sites s, ecc_cpr.con_sites_address address, ecc_cpr.temp_parameters param, ecc_cpr.con_parameters con_param\n" +
                "\t\tWHERE param.temp_parameters_id = con_param.temp_parameters_id\n" +
                "\t\t\tAND con_param.contract_config_id = address.contract_config_id\n" +
                "\t\t\tAND address.site_id = s.site_id\n" +
                "\t\t\tAND param.is_transfer = ?\n" +
                "\t\t\tAND con_param.enabled_flag = ?\n" +
                "\t\t\tAND s.contract_header_id = ?\n" +
                "\t\t\tAND con_param.contract_header_id = ?\n" +
                "\t\t\tAND s.contract_header_id <> (\n" +
                "\t\t\t\tSELECT p.property_id\n" +
                "\t\t\t\tFROM ecc_fnd.ecc_fnd_iecc_property p\n" +
                "\t\t\t\tWHERE PROPERTY_TYPE = ?\n" +
                "\t\t\t)\n" +
                "\t\t\tAND con_param.contract_header_id <> (\n" +
                "\t\t\t\tSELECT p.property_id\n" +
                "\t\t\t\tFROM ecc_fnd.ecc_fnd_iecc_property p\n" +
                "\t\t\t\tWHERE PROPERTY_TYPE = ?\n" +
                "\t\t\t)\n" +
                "\t\tGROUP BY s.contract_header_id, address.site_address_id\n" +
                "\t) para ON para.site_address_id = b.site_address_id \n" +
                "ORDER BY b.Device_Bom_Id, b.Dev_Bom_Type_Attribute, b.site_seq, b.addr_seq", stmt.toString());
    }

}