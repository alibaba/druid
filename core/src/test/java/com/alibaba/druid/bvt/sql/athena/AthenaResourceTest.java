package com.alibaba.druid.bvt.sql.athena;

import static org.junit.Assert.assertEquals;

import com.alibaba.druid.DbType;
import com.alibaba.druid.bvt.sql.SQLResourceTest;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.util.Utils;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import org.junit.Test;

public class AthenaResourceTest extends SQLResourceTest{

    public AthenaResourceTest() {
        super(DbType.athena);
    }
    
    @Test
    public void athena_parse() throws Exception {
        fileTest(0, 999, i -> "bvt/parser/athena/" + i + ".txt");
    }
}
