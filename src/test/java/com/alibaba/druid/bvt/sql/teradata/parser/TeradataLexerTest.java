package com.alibaba.druid.bvt.sql.teradata.parser;

import junit.framework.TestCase;

import com.alibaba.druid.sql.dialect.teradata.parser.TeradataLexer;
import com.alibaba.druid.sql.parser.Token;

public class TeradataLexerTest extends TestCase{

	public void test_0() throws Exception {
		String sql = "SELECT a.user_id  , "
				+ "SUM(CASE WHEN comment_score=2 THEN 1 ELSE 0 END) AS posfb ,"
				+ "SUM(CASE WHEN comment_score=-1 THEN 1 ELSE 0 END) AS negfb ,"
				+ "SUM(CASE WHEN comment_score=0 THEN 1 ELSE 0 END) AS neutfb "
				+ "FROM dw_feedback_detail a "
				+ "WHERE a.creation_dt >= '2012-11-13' "
				+ "AND a.ID_TYPE = 'S' "
				+ "AND a.TRX_TYPE <> 1 "
				+ "AND a.user_id IN ( SELECT DISTINCT aa.user_id "
					+ "FROM DW_TNS_RCNT_SLR_ACTVTY_SL aa "
					+ "INNER JOIN DW_CATEGORY_GROUPINGS b "
					+ "ON aa.site_id=b.site_id AND aa.LEAF_CATEG_ID=b.LEAF_CATEG_ID "
					+ "WHERE aa.leaf_categ_id NOT IN (100843,100844,100845,100846,100847,100848,100849,100850) "
					+ "AND b.META_CATEG_ID NOT IN (2038,6000,9800,10542,60089) "
					+ "GROUP BY aa.user_id "
					+ "HAVING SUM(LIVE_GMV_USD) + SUM(END_GMV_USD) > 10000 "
					+ "AND SUM(END_GMV_USD) > 50 "
					+ "AND SUM(live_lstg_count) + SUM(SUCC_LSTG_COUNT) > 5 "
					+ "AND SUM(live_lstg_count) + SUM(SUCC_LSTG_COUNT) <> 99999 "
					+ "AND "
					+ "CASE WHEN (SUM(LIVE_GMV_USD)/(SUM(LIVE_LSTG_QTY) + 0.0001)>=SUM(END_GMV_USD)/(SUM(END_QTY_SOLD)+0.0001)) "
					+ "THEN (SUM(LIVE_GMV_USD)/(SUM(LIVE_LSTG_QTY) + 0.0001)) "
					+ "ELSE (SUM(END_GMV_USD)/(SUM(END_QTY_SOLD)+0.0001)) END > 50 "
					+ "AND "
					+ "CASE WHEN (SUM(LIVE_GMV_USD)/(SUM(LIVE_LSTG_QTY) + 0.0001)>=SUM(END_GMV_USD)/(SUM(END_QTY_SOLD)+0.0001)) "
					+ "THEN (SUM(LIVE_GMV_USD)/(SUM(LIVE_LSTG_QTY) + 0.0001)) "
					+ "ELSE (SUM(END_GMV_USD)/(SUM(END_QTY_SOLD)+0.0001)) END < 100000 ) "
				+ "GROUP BY a.user_id ORDER BY a.user_id;";
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
