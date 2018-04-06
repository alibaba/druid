package com.alibaba.druid.bvt.filter.wall.mysql;

import com.alibaba.druid.wall.WallCheckResult;
import com.alibaba.druid.wall.WallProvider;
import com.alibaba.druid.wall.spi.MySqlWallProvider;
import junit.framework.TestCase;

public class MySqlWallTest150 extends TestCase {

    public void test_false() throws Exception {
        WallProvider provider = new MySqlWallProvider();

        provider.getConfig().setSelectLimit(100);

        String sql = "select\n" +
                "a.main_card_no as card_no, a.city, a.bussiness_type, 'main' as card_type,\n" +
                "c.company_name as company, a.status_flag,\n" +
                "a.card_balance as balance from oil_card_main a left join oil_company c on c.company_id=a.company_id\n" +
                "\n" +
                "union all\n" +
                "\n" +
                "select\n" +
                "b.card_no,b.city,b.bussiness_type,'secon' as card_type,c.company_name as company,\n" +
                "b.status_flag,b.card_balance as balance\n" +
                "from oil_card_associate b left join oil_card_main a\n" +
                "on a.main_card_no = b.main_card_no\n" +
                "left join oil_company c on c.company_id=a.company_id";

//        assertTrue(
//                provider.checkValid(sql)
//        );

        WallCheckResult result = provider.check(sql);
        assertEquals(0, result.getViolations().size());
        String wsql = result
                .getStatementList().get(0).toString();

        assertEquals("SELECT a.main_card_no AS card_no, a.city, a.bussiness_type, 'main' AS card_type, c.company_name AS company\n" +
                "\t, a.status_flag, a.card_balance AS balance\n" +
                "FROM oil_card_main a\n" +
                "\tLEFT JOIN oil_company c ON c.company_id = a.company_id\n" +
                "UNION ALL\n" +
                "SELECT b.card_no, b.city, b.bussiness_type, 'secon' AS card_type, c.company_name AS company\n" +
                "\t, b.status_flag, b.card_balance AS balance\n" +
                "FROM oil_card_associate b\n" +
                "\tLEFT JOIN oil_card_main a ON a.main_card_no = b.main_card_no\n" +
                "\tLEFT JOIN oil_company c ON c.company_id = a.company_id\n" +
                "LIMIT 100", wsql);
    }
}
