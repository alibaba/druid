package com.alibaba.druid.sql.hive.demo;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.hive.parser.HiveStatementParser;
import com.alibaba.druid.sql.dialect.hive.visitor.HiveOutputVisitor;
import junit.framework.TestCase;

import java.util.List;

/**
 * @program: druid
 * @package: com.alibaba.druid.sql.hive.demo
 * @description: storedBy支持测试
 * @author: DingLuPan
 * @create: 2021/12/22 10:27
 **/
public class Issue4622 extends TestCase {
    public void test_for_issue() throws Exception {
        String sql = "CREATE TABLE ads_coshr_waybill_vehicle_full_outer_fee_detail_cm_es(\n" +
                "\tkey string COMMENT '主键ID'\n" +
                "   ,trade_code string  COMMENT '交易编码' \n" +
                "   ,waybill_number string  COMMENT  '运单号' \n" +
                "   ,fee_type string  COMMENT   '费用项编码' \n" +
                "   ,fee_type_name  string  COMMENT  '费用项名称' \n" +
                "   ,fee decimal(18,2) COMMENT '费用项金额'\n" +
                "   ,shiping_month string COMMENT '寄件')\n" +
                "COMMENT '成本分摊接口表'\n" +
                "ROW FORMAT SERDE \n" +
                " 'org.elasticsearch.hadoop.hive.EsSerDe' \n" +
                "WITH SERDEPROPERTIES ( \n" +
                "'serialization.format'='1')\n" +
                "STORED BY \n" +
                " 'org.elasticsearch.hadoop.hive.EsStorageHandler' \n" +
                "TBLPROPERTIES (\n" +
                " 'bucketing_version'='2', \n" +
                " 'es.http.timeout'='10m', \n" +
                " 'es.index.auto.create'='true', \n" +
                " 'es.index.read.missing.as.empty'='true', \n" +
                " 'es.mapping.id'='key', \n" +
                " 'es.mapping.names'='key,trade_code,waybill_number,fee_type,fee_type_name,fee', \n" +
                " 'es.nodes'='192.168.196.23:9200,192.168.196.24:9200,192.168.196.25:9200,192.168.196.26:9200,192.168.196.27:9200,192.168.196.28:9200,192.168.196.29:9200,192.168.196.30:9200,192.168.196.31:9200,192.168.196.32:9200', \n" +
                " 'es.query'='?q=*', \n" +
                " 'es.read.metadata'='true', \n" +
                " 'es.resource'='ads_coshr_waybill_vehicle_full_outer_fee_detail_cm_es_{shiping_month}/doc');";
        HiveStatementParser parser = new HiveStatementParser(sql);
        List<SQLStatement> stmtList = parser.parseStatementList();
        StringBuilder out = new StringBuilder();
        HiveOutputVisitor visitor = new HiveOutputVisitor(out);
        for (SQLStatement stmt : stmtList) {
            stmt.accept(visitor);
            out.append(";");
        }

        System.out.println(out.toString());


    }
}
