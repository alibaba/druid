package com.alibaba.druid.bvt.sql.teradata.parser;

import com.alibaba.druid.sql.dialect.teradata.parser.TeradataLexer;
import com.alibaba.druid.sql.parser.Token;

import junit.framework.TestCase;

public class TeradataLexerTest1 extends TestCase{

	public void test_0() throws Exception {
		String sql = "LOCKING TABLE TEST1 FOR ACCESS;"
				+ "CREATE MULTISET TABLE TEST("
				+ "ITEM_ID DECIMAL(18,0) NOT NULL,"
				+ "AUCT_START_DT DATE NOT NULL,"
				+ "AUCT_END_DT DATE NOT NULL,"
				+ "PRIMARY INDEX(ITEM_ID)"
				+ "PARTITION BY RANGE_N(AUCT_END_DT BETWEEN "
				+ "DATE '2010-01-01' AND DATE '2012-12-31'));";
		TeradataLexer lexer = new TeradataLexer(sql);
		
		for(;;) {
			lexer.nextToken();
			Token  tok = lexer.token();
			switch (tok) {
            case IDENTIFIER:
                System.out.println(tok.name() + "\t\t" + lexer.stringVal());
                break;
            case HINT:
                System.out.println(tok.name() + "\t\t\t\t" + lexer.stringVal());
                break;
            case LITERAL_INT:
            	System.out.println(tok.name() + "\t\t" + lexer.numberString());
            	break;
            case LITERAL_CHARS:
            	System.out.println(tok.name() + "\t\t" + lexer.stringVal());
                break;
            case LITERAL_FLOAT:
            	System.out.println(tok.name() + "\t\t" + lexer.numberString());
                break;
            default:
                System.out.println(tok.name() + "\t\t\t" + tok.name);
                break;
                }

	        if (tok == Token.EOF) {
	            break;
	        }
		}
	}
}
