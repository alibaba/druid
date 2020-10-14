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
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

import java.util.List;

public class OracleCreateProcedureTest4 extends OracleTest {

    public void test_0() throws Exception {
        String sql = "PROCEDURE abc_apply(in_json  IN VARCHAR2,\n" +
                "                                      out_ret  OUT NUMBER,\n" +
                "                                      out_desc OUT VARCHAR2) AS\n" +
                "  /*\n" +
                "      TRANSNAME       :???????????\n" +
                "      AUTHOR          :AARON\n" +
                "      VERSION         :V1.1\n" +
                "      DATE            :2016-01-15\n" +
                "  */\n" +
                "  --?????\n" +
                "  lv_module VARCHAR2(20) := '?????';\n" +
                "  lv_opertype VARCHAR2(20) := '?????????';\n" +
                "  lv_tradecode VARCHAR2(20) := 'ABC_APPLY';\n" +
                "  dbnum NUMBER;\n" +
                "  crudate DATE;\n" +
                "  db_code NUMBER;\n" +
                "  db_file var2varray;\n" +
                "  db_date abc_finance_apply%ROWTYPE;\n" +
                "  db_date2 fact_brand_provider%ROWTYPE;\n" +
                "  in_loginname VARCHAR2(20);\n" +
                "  in_json_str json;\n" +
                "  in_store VARCHAR2(2000);\n" +
                "BEGIN\n" +
                "  crudate := SYSDATE;\n" +
                "  db_code := seq_abc.NEXTVAL;\n" +
                "  --??????JSON?,???????????\n" +
                "  in_json_str := json(in_json);\n" +
                "  -- ?????\n" +
                "  db_date.gyscode := intime_json.get_string(in_json_str, 'gyscode');\n" +
                "  db_date.amount := intime_json.get_string(in_json_str, 'amount');\n" +
                "  db_date.bankcode := intime_json.get_string(in_json_str, 'bankcode');\n" +
                "  db_date.productcode := intime_json.get_string(in_json_str, 'productcode');\n" +
                "  db_date.memo := intime_json.get_string(in_json_str, 'memo');\n" +
                "  db_date.status := '0';\n" +
                "  db_date.createdate := crudate;\n" +
                "  db_date.seqno := db_code;\n" +
                "  --???????\n" +
                "  db_date2.gyscode := intime_json.get_string(in_json_str, 'gyscode');\n" +
                "  db_date2.gysname := intime_json.get_string(in_json_str, 'gysname');\n" +
                "  db_date2.yyzz := intime_json.get_string(in_json_str, 'yyzz'); --????\n" +
                "  db_date2.gysqydm := intime_json.get_string(in_json_str, 'zzjg'); --????\n" +
                "  db_date2.frdb := intime_json.get_string(in_json_str, 'frdb'); --????\n" +
                "  db_date2.frdbtel := intime_json.get_string(in_json_str, 'frdblxfs'); --??????\n" +
                "  db_date2.people := intime_json.get_string(in_json_str, 'people'); --???\n" +
                "  db_date2.apply_tel := intime_json.get_string(in_json_str, 'lxrphone'); --?????\n" +
                "  in_store := intime_json.get_string(in_json_str, 'store'); --????\n" +
                "  SELECT COUNT(1)\n" +
                "    INTO dbnum\n" +
                "    FROM fact_brand_provider\n" +
                "   WHERE gyscode = db_date.gyscode;\n" +
                "  IF dbnum > 0 THEN\n" +
                "    --??\n" +
                "    UPDATE fact_brand_provider\n" +
                "       SET gyscode   = db_date2.gyscode,\n" +
                "           gysname   = db_date2.gysname,\n" +
                "           yyzz      = db_date2.yyzz,\n" +
                "           gysqydm   = db_date2.gysqydm,\n" +
                "           frdb      = db_date2.frdb,\n" +
                "           frdbtel   = db_date2.frdbtel,\n" +
                "           people    = db_date2.people,\n" +
                "           apply_tel = db_date2.apply_tel\n" +
                "     WHERE gyscode = db_date.gyscode;\n" +
                "  ELSE\n" +
                "    out_ret := -1;\n" +
                "    out_desc := '?????';\n" +
                "  END IF;\n" +
                "  --????\n" +
                "  SELECT COUNT(1)\n" +
                "    INTO dbnum\n" +
                "    FROM abc_finance_apply\n" +
                "   WHERE gyscode = db_date.gyscode;\n" +
                "  IF dbnum > 0 THEN\n" +
                "    db_date.flag := '1';\n" +
                "    db_date.mflag := '1';\n" +
                "  ELSE\n" +
                "    db_date.flag := '0';\n" +
                "    db_date.mflag := '0';\n" +
                "  END IF;\n" +
                "  INSERT INTO abc_finance_apply VALUES db_date;\n" +
                "  --??????\n" +
                "  IF in_store IS NOT NULL THEN\n" +
                "    db_file := intime_common.strsplit(in_store, ',');\n" +
                "    FOR i IN 1 .. db_file.COUNT\n" +
                "    LOOP\n" +
                "      INSERT INTO abc_fv_info\n" +
                "        (seqno,\n" +
                "         gyscode,\n" +
                "         gys,\n" +
                "         gysname,\n" +
                "         storeno,\n" +
                "         NAME,\n" +
                "         rq)\n" +
                "        SELECT seq_abc_fav.NEXTVAL,\n" +
                "               a.cable,\n" +
                "               a.code,\n" +
                "               a.NAME,\n" +
                "               a.storeno,\n" +
                "               b.NAME,\n" +
                "               SYSDATE\n" +
                "          FROM gys_store_total a, area b\n" +
                "         WHERE a.storeno = b.id\n" +
                "           AND a.cable = db_date.gyscode\n" +
                "           AND a.storeno = db_file(i);\n" +
                "    END LOOP;\n" +
                "  ELSE\n" +
                "    out_ret := -1;\n" +
                "    out_desc := '?????';\n" +
                "  END IF;\n" +
                "  --??????\n" +
                "  out_ret := 1;\n" +
                "  out_desc := '????';\n" +
                "  intime_common.sys_log(in_loginname,\n" +
                "                        '0',\n" +
                "                        lv_module,\n" +
                "                        lv_opertype,\n" +
                "                        crudate,\n" +
                "                        out_ret,\n" +
                "                        out_desc,\n" +
                "                        lv_tradecode,\n" +
                "                        '???:' || in_loginname);\n" +
                "  COMMIT;\n" +
                "EXCEPTION\n" +
                "  WHEN OTHERS THEN\n" +
                "    --????\n" +
                "    --??????\n" +
                "    out_ret := -1;\n" +
                "    out_desc := '????' || SQLERRM;\n" +
                "    dbms_output.put_line('sqlerrm : ' || SQLERRM);\n" +
                "    intime_common.sys_log(in_loginname,\n" +
                "                          '0',\n" +
                "                          lv_module,\n" +
                "                          lv_opertype,\n" +
                "                          crudate,\n" +
                "                          out_ret,\n" +
                "                          out_desc,\n" +
                "                          lv_tradecode,\n" +
                "                          '???:' || in_loginname);\n" +
                "    ROLLBACK;\n" +
                "    RETURN;\n" +
                "END;"; //

        OracleStatementParser parser = new OracleStatementParser(sql);
        List<SQLStatement> statementList = parser.parseStatementList();
        print(statementList);

        assertEquals(1, statementList.size());

        OracleSchemaStatVisitor visitor = new OracleSchemaStatVisitor();
        for (SQLStatement statement : statementList) {
            statement.accept(visitor);
        }

        System.out.println("Tables : " + visitor.getTables());
        System.out.println("fields : " + visitor.getColumns());
        System.out.println("coditions : " + visitor.getConditions());
        System.out.println("relationships : " + visitor.getRelationships());
        System.out.println("orderBy : " + visitor.getOrderByColumns());

        assertEquals(5, visitor.getTables().size());

        assertTrue(visitor.getTables().containsKey(new TableStat.Name("fact_brand_provider")));

        assertEquals(24, visitor.getColumns().size());
        assertEquals(6, visitor.getConditions().size());
        assertEquals(4, visitor.getRelationships().size());

        assertTrue(visitor.containsColumn("fact_brand_provider", "gyscode"));
        assertTrue(visitor.containsColumn("fact_brand_provider", "gysname"));
    }
}
