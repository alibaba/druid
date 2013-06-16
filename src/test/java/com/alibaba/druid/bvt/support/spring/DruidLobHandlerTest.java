package com.alibaba.druid.bvt.support.spring;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.alibaba.druid.mock.MockBlob;
import com.alibaba.druid.mock.MockClob;
import com.alibaba.druid.mock.MockResultSet;
import com.alibaba.druid.support.spring.DruidLobHandler;

public class DruidLobHandlerTest extends TestCase {

    public void test_0() throws Exception {
        DruidLobHandler handler = new DruidLobHandler();
        List<Object[]> rows = new ArrayList<Object[]>();
        rows.add(new Object[] { null, new MockBlob(), new MockClob() });
        MockResultSet rs = new MockResultSet(null, rows);
        rs.next();

        handler.getBlobAsBinaryStream(rs, 1);
        handler.getBlobAsBinaryStream(rs, "1");
        handler.getBlobAsBytes(rs, 1);
        handler.getBlobAsBytes(rs, "1");
        
        handler.getBlobAsBinaryStream(rs, 2);
        handler.getBlobAsBinaryStream(rs, "2");
        handler.getBlobAsBytes(rs, 2);
        handler.getBlobAsBytes(rs, "2");
        
        handler.getClobAsAsciiStream(rs, 1);
        handler.getClobAsAsciiStream(rs, "1");
        handler.getClobAsCharacterStream(rs, 1);
        handler.getClobAsCharacterStream(rs, "1");
        handler.getClobAsString(rs, 1);
        handler.getClobAsString(rs, "1");
        
        handler.getClobAsAsciiStream(rs, 3);
        handler.getClobAsAsciiStream(rs, "3");
        handler.getClobAsCharacterStream(rs, 3);
        handler.getClobAsCharacterStream(rs, "3");
        handler.getClobAsString(rs, 3);
        handler.getClobAsString(rs, "3");
        
        handler.getLobCreator();
    }
}
