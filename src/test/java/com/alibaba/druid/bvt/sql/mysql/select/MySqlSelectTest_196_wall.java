package com.alibaba.druid.bvt.sql.mysql.select;

import com.alibaba.druid.sql.MysqlTest;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.ParserException;

import java.util.List;

public class MySqlSelectTest_196_wall extends MysqlTest {

    public void test_0() throws Exception {
        String sql = "update task_info set priority = 1, content = 'x渓螼h\u0013[\u0014\u0006饆蜭�)漉=灔�\u000B\u0011\u0011u�\uE06A圥Z[\u0017殞c怈﹝櫃&c摍0\u007Fj璟\u0004]JWRb峌膮埜\u0014譢\"瓺\\\\塼!.D牡玕\"邥\t� 佥篼緖.W=Rw4�\u001E3\t玭YF螭�\u0004�);\u0011�I\u0017�%\u0017谦漜f*\bm梄恀拒t)/�I�\"uSP蟉�\u0006I3\\\"�\u0016敐\f\\Z殷觀n\\\"餭讖;祵Y換g\uE5B7&柋\u0017�\u0011﹗A欍媞(I=\u0010�x鐹�\u000B!蜿\u0006\u0013�lQw�(33毳圳倀!\uE324=&释烃襲�&b\uE1EF�\u0013mm孺L\u0014嚍_MS7湏痘笫滴;y鶲\b爱骤求秩G-\u001By~殿揀\uE222�\u0003鮝[�Dqo:蟎'~=裼�\u001F垴蟵\\rY�\u0013.\uE3F9R\u000E�\u0012滂燶n鑲�\u0002贛龎=\u0010ミ�\u0007/%�\u001C癳c�\u000E\\Z旊\u001B�\u000B2栣玄岜�#cc吜\uE6C5�\u001B嚮醽nx�\u001B巘肅輕4E鎊\u0018E\u0018跴欱貯i\u0007蘉渚秘\u0007c\u00129狓\u0004F\u001F\f\u0013�觠3\u000B��-XS8�\u0010�\u0019V\u001E諭餸�\u000Fk\\r|\u0014V\u0005躒_A彙?E�*�\u0015琔�\u001Ep共\u000B�\u0006Z\u001F�%楳�\u001D\u001C玾郳\"\uE26B\u0011�\u0006Jｊ\u0016h\u0005�\u001C�\u0002�\u0002�\u0004j貌`.�\u0001�\u0005韁\"�\u0006z佉\u0012�\u001CxQ�\u0007\u0017@呃-斵曼\\0k砛0k\u0003鬨Z\uE5E9\u007F\u0006^�\u000Es\u0005欳B�\f\\0\\0��+僜'�', attribute = 0, create_time = from_unixtime(1531714297) where id = 2726633030";

        Exception error = null;
        try {
            MySqlStatementParser parser = new MySqlStatementParser(sql);
            List<SQLStatement> statementList = parser.parseStatementList();
        } catch (ParserException ex) {
            error = ex;
        }

        assertNotNull(error);
    }
}