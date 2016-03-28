package com.alibaba.druid.bvt.sql.teradata.parser;

import junit.framework.TestCase;

import com.alibaba.druid.sql.dialect.teradata.parser.TeradataLexer;
import com.alibaba.druid.sql.parser.Token;

public class TeradataLexerTest3 extends TestCase{
	// test COMMENT
	public void test_0() throws Exception {
		String sql = "/*SUM(CASE WHEN comment_score=2 THEN 1 ELSE 0 END) AS posfb ,"
				+ "SUM(CASE WHEN comment_score=-1 THEN 1 ELSE 0 END) AS negfb ,*/";
		TeradataLexer lexer = new TeradataLexer(sql);

		lexer.scanComment();
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
