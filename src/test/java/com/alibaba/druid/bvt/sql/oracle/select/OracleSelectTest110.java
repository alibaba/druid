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

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.OracleTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;

import java.util.List;

public class OracleSelectTest110 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "SELECT\n" + "  Home_City,\n" + "  bank_type,\n" + "  differ_flag,\n" + "  adjust_flag,\n"
                     + "  out_user_id,\n" + "  out_msisdn,\n" + "  out_account_id,\n" + "  out_month,\n"
                     + "  to_char(out_action_date, 'yyyymmdd'),\n" + "  to_char(out_action_time, 'yyyymmddhh24miss'),\n"
                     + "  out_payment_amount,\n" + "  out_cnltyp,\n" + "  out_bank_id,\n" + "  out_discount,\n"
                     + "  out_real_payed,\n" + "  out_BusiTransID,\n" + "  out_PayTransID,\n" + "  out_OrderNo,\n"
                     + "  out_ProductNo,\n" + "  out_Order_Payment,\n" + "  out_OrderCnt,\n" + "  out_Commision,\n"
                     + "  out_RebateFee,\n" + "  out_ProdDiscount,\n" + "  out_CreditCardFee,\n" + "  out_ServiceFee,\n"
                     + "  out_ActivityNo,\n" + "  out_ProductShelfNo,\n" + "  OUT_PayOrganID,\n"
                     + "  OUT_CorrelationID,\n" + "  in_user_id,\n" + "  in_msisdn,\n" + "  in_account_id,\n"
                     + "  in_month,\n" + "  to_char(in_action_date, 'yyyymmdd'),\n"
                     + "  to_char(in_action_time, 'yyyymmddhh24miss'),\n" + "  in_payment_amount,\n"
                     + "  in_discount,\n" + "  in_real_payed,\n" + "  in_BusiTransID,\n" + "  in_PayTransID,\n"
                     + "  in_OrderNo,\n" + "  in_ProductNo,\n" + "  in_Order_Payment,\n" + "  in_OrderCnt,\n"
                     + "  in_Commision,\n" + "  in_RebateFee,\n" + "  in_ProdDiscount,\n" + "  in_CreditCardFee,\n"
                     + "  in_ServiceFee,\n" + "  in_ActivityNo,\n" + "  in_ProductShelfNo,\n" + "  IN_PayOrganID,\n"
                     + "  IN_CorrelationID\n"
                     + "INTO :b0, :b1, :b2:b3, :b4:b5, :b6:b7, :b8:b9, :b10:b11, :b12:b13, :b14:b15, :b16:b17, :b18:b19, :b20:b21, :b22:b23, :b24:b25, :b26:b27, :b28:b29, :b30:b31, :b32:b33, :b34:b35, :b36:b37, :b38:b39, :b40:b41, :b42:b43, :b44:b45, :b46:b47, :b48:b49, :b50:b51, :b52:b53, :b54:b55, :b56:b57, :b58:b59, :b60:b61, :b62:b63, :b64:b65, :b66:b67, :b68:b69, :b70:b71, :b72:b73, :b74:b75, :b76:b77, :b78:b79, :b80:b81, :b82:b83, :b84:b85, :b86:b87, :b88:b89, :b90:b91, :b92:b93, :b94:b95, :b96:b97, :b98:b99, :b100:b101, :b102:b103, :b104:b105\n"
                     + "FROM b2b_payment_ReconDetail\n" + "WHERE (Recon_seq_id = :b106 AND transaction_id = :b107)";

        OracleStatementParser parser = new OracleStatementParser(sql, SQLParserFeature.KeepComments);
        List<SQLStatement> statementList = parser.parseStatementList();
        System.out.println(statementList.toString());

        assertEquals(1, statementList.size());

        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.oracle);
        SQLStatement stmt = statementList.get(0);
        stmt.accept(visitor);

        {
            String text = SQLUtils.toOracleString(stmt);

            assertEquals("SELECT Home_City, bank_type, differ_flag, adjust_flag, out_user_id\n"
                         + "\t, out_msisdn, out_account_id, out_month\n" + "\t, to_char(out_action_date, 'yyyymmdd')\n"
                         + "\t, to_char(out_action_time, 'yyyymmddhh24miss'), out_payment_amount\n"
                         + "\t, out_cnltyp, out_bank_id, out_discount, out_real_payed, out_BusiTransID\n"
                         + "\t, out_PayTransID, out_OrderNo, out_ProductNo, out_Order_Payment, out_OrderCnt\n"
                         + "\t, out_Commision, out_RebateFee, out_ProdDiscount, out_CreditCardFee, out_ServiceFee\n"
                         + "\t, out_ActivityNo, out_ProductShelfNo, OUT_PayOrganID, OUT_CorrelationID, in_user_id\n"
                         + "\t, in_msisdn, in_account_id, in_month\n" + "\t, to_char(in_action_date, 'yyyymmdd')\n"
                         + "\t, to_char(in_action_time, 'yyyymmddhh24miss'), in_payment_amount\n"
                         + "\t, in_discount, in_real_payed, in_BusiTransID, in_PayTransID, in_OrderNo\n"
                         + "\t, in_ProductNo, in_Order_Payment, in_OrderCnt, in_Commision, in_RebateFee\n"
                         + "\t, in_ProdDiscount, in_CreditCardFee, in_ServiceFee, in_ActivityNo, in_ProductShelfNo\n"
                         + "\t, IN_PayOrganID, IN_CorrelationID\n"
                         + "INTO (:b0, :b1, :b2:b3, :b4:b5, :b6:b7, :b8:b9, :b10:b11, :b12:b13, :b14:b15, :b16:b17, :b18:b19, :b20:b21, :b22:b23, :b24:b25, :b26:b27, :b28:b29, :b30:b31, :b32:b33, :b34:b35, :b36:b37, :b38:b39, :b40:b41, :b42:b43, :b44:b45, :b46:b47, :b48:b49, :b50:b51, :b52:b53, :b54:b55, :b56:b57, :b58:b59, :b60:b61, :b62:b63, :b64:b65, :b66:b67, :b68:b69, :b70:b71, :b72:b73, :b74:b75, :b76:b77, :b78:b79, :b80:b81, :b82:b83, :b84:b85, :b86:b87, :b88:b89, :b90:b91, :b92:b93, :b94:b95, :b96:b97, :b98:b99, :b100:b101, :b102:b103, :b104:b105)\n"
                         + "FROM b2b_payment_ReconDetail\n" + "WHERE Recon_seq_id = :b106\n"
                         + "\tAND transaction_id = :b107", text);
        }

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());
        assertEquals(56, visitor.getColumns().size());
        assertEquals(2, visitor.getConditions().size());
        assertEquals(0, visitor.getRelationships().size());
        assertEquals(0, visitor.getOrderByColumns().size());

    }
    public void test_1() throws Exception {
        String sql = "select max(Request_Seq) into :b0:b1  from TrustBill where "
                     + "(((Acct_Home_City=:b2 and (Acct_Home_County=:b3 or :b3=0)) and Accounting_Period=:b5) and Trust_Method=:b6)";

        OracleStatementParser parser = new OracleStatementParser(sql, SQLParserFeature.KeepComments);
        List<SQLStatement> statementList = parser.parseStatementList();

        assertEquals(1, statementList.size());


        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.oracle);
        SQLStatement stmt = statementList.get(0);
        System.out.println(stmt);
        stmt.accept(visitor);

        {
            String text = SQLUtils.toOracleString(stmt);

            assertEquals("SELECT max(Request_Seq)\n" + "INTO :b0:b1\n" + "FROM TrustBill\n"
                         + "WHERE Acct_Home_City = :b2\n" + "\tAND (Acct_Home_County = :b3\n" + "\t\tOR :b3 = 0)\n"
                         + "\tAND Accounting_Period = :b5\n" + "\tAND Trust_Method = :b6", text);
        }

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());
        assertEquals(5, visitor.getColumns().size());
        assertEquals(4, visitor.getConditions().size());
        assertEquals(0, visitor.getRelationships().size());
        assertEquals(0, visitor.getOrderByColumns().size());

    }
    public void test_2() throws Exception {
        String sql = "SELECT\n" + "  Debtbill_item_id,\n" + "  Home_City,\n" + "  WriteOff_Status,\n" + "  User_ID,\n"
                     + "  Msisdn,\n" + "  Home_County,\n" + "  Brand_ID,\n" + "  User_Type,\n" + "  DebtBill_ID,\n"
                     + "  TO_CHAR(Gen_Time, 'yyyymmddhh24miss'),\n" + "  Bill_Type,\n"
                     + "  TO_CHAR(Debting_Date, 'yyyymmdd'),\n" + "  Sum_BillItem,\n" + "  Detail_BillItem,\n"
                     + "  TO_CHAR(Lagging_Start_Date, 'yyyymmdd'),\n" + "  Amount_Debt,\n" + "  Amount_Discount,\n"
                     + "  Amount_WriteOff,\n" + "  Lagging_Debt,\n" + "  Lagging_WriteOff,\n" + "  WriteOff_Type,\n"
                     + "  TO_CHAR(WriteOff_Time, 'yyyymmddhh24miss'),\n" + "  Acct_Home_City,\n" + "  ACCOUNT_ID,\n"
                     + "  SUBTOTAL_DETAILBILL_ID,\n" + "  NVL(ACCT_HOME_COUNTY, (-1)),\n"
                     + "  TO_CHAR(NVL(BILLCYCLE_INURE_DATE, TO_DATE(TO_CHAR(GEN_TIME, 'yyyymm'), 'yyyymm')), 'yyyymmdd'),\n"
                     + "  NVL(BILLCYCLE_MONTH, (-1)),\n" + "  NVL(Detailbill_Flag, 0)\n"
                     + "INTO :b0, :b1, :b2, :b3, :b4, :b5, :b6, :b7, :b8, :b9, :b10, :b11, :b12, :b13, :b14, :b15, :b16, :b17, :b18, :b19, :b20:b21, :b22:b23, :b24:b25, :b26:b27, :b28:b29, :b30, :b31, :b32, :b33\n"
                     + "FROM DebtBill_Item\n" + "WHERE ROWID = :b34";

        OracleStatementParser parser = new OracleStatementParser(sql, SQLParserFeature.KeepComments);
        List<SQLStatement> statementList = parser.parseStatementList();

        assertEquals(1, statementList.size());


        SchemaStatVisitor visitor = SQLUtils.createSchemaStatVisitor(DbType.oracle);
        SQLStatement stmt = statementList.get(0);
        System.out.println(stmt);
        stmt.accept(visitor);

        {
            String text = SQLUtils.toOracleString(stmt);

            assertEquals("SELECT Debtbill_item_id, Home_City, WriteOff_Status, User_ID, Msisdn\n"
                         + "\t, Home_County, Brand_ID, User_Type, DebtBill_ID\n"
                         + "\t, TO_CHAR(Gen_Time, 'yyyymmddhh24miss'), Bill_Type\n"
                         + "\t, TO_CHAR(Debting_Date, 'yyyymmdd'), Sum_BillItem\n"
                         + "\t, Detail_BillItem, TO_CHAR(Lagging_Start_Date, 'yyyymmdd'), Amount_Debt\n"
                         + "\t, Amount_Discount, Amount_WriteOff, Lagging_Debt, Lagging_WriteOff, WriteOff_Type\n"
                         + "\t, TO_CHAR(WriteOff_Time, 'yyyymmddhh24miss'), Acct_Home_City\n"
                         + "\t, ACCOUNT_ID, SUBTOTAL_DETAILBILL_ID, NVL(ACCT_HOME_COUNTY, -1)\n"
                         + "\t, TO_CHAR(NVL(BILLCYCLE_INURE_DATE, TO_DATE(TO_CHAR(GEN_TIME, 'yyyymm'), 'yyyymm')), 'yyyymmdd')\n"
                         + "\t, NVL(BILLCYCLE_MONTH, -1)\n" + "\t, NVL(Detailbill_Flag, 0)\n"
                         + "INTO (:b0, :b1, :b2, :b3, :b4, :b5, :b6, :b7, :b8, :b9, :b10, :b11, :b12, :b13, :b14, :b15, :b16, :b17, :b18, :b19, :b20:b21, :b22:b23, :b24:b25, :b26:b27, :b28:b29, :b30, :b31, :b32, :b33)\n"
                         + "FROM DebtBill_Item\n" + "WHERE ROWID = :b34", text);
        }

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(1, visitor.getTables().size());
        assertEquals(30, visitor.getColumns().size());
        assertEquals(1, visitor.getConditions().size());
        assertEquals(0, visitor.getRelationships().size());
        assertEquals(0, visitor.getOrderByColumns().size());

    }
}
