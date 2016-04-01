package com.alibaba.druid.bvt.sql.teradata.parser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import junit.framework.TestCase;

import com.alibaba.druid.sql.dialect.teradata.parser.TeradataLexer;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.Utils;

public class TeradataLexerTest2 extends TestCase{

	public void test_0() throws Exception {
		InputStream is = null;
		// mainly test SEL keyword
		is = Thread.currentThread().getContextClassLoader().getResourceAsStream("bvt/parser/teradata-1.txt");
		Reader reader = new InputStreamReader(is, "UTF-8");
		String input = Utils.read(reader);
		String sql = input.trim();
		
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
