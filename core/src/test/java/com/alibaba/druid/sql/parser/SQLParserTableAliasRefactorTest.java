package com.alibaba.druid.sql.parser;

import org.junit.Assert;
import org.junit.Test;

public class SQLParserTableAliasRefactorTest {
    @Test
    public void testTableAliasJoinKeywordBranchKeepsJoinParsing() {
        ExposedSQLParser parser = new ExposedSQLParser("left join t");
        Assert.assertNull(parser.readTableAlias(false));
        Assert.assertEquals(Token.LEFT, parser.getLexer().token());
    }

    @Test
    public void testTableAliasDistributeByReturnsNull() {
        ExposedSQLParser parser = new ExposedSQLParser("distribute by c1");
        Assert.assertNull(parser.readTableAlias(false));
        Assert.assertEquals(Token.IDENTIFIER, parser.getLexer().token());
    }

    @Test
    public void testTableAliasDistributeWithoutByReturnsAlias() {
        ExposedSQLParser parser = new ExposedSQLParser("distribute x");
        Assert.assertEquals("distribute", parser.readTableAlias(false));
        Assert.assertEquals(Token.IDENTIFIER, parser.getLexer().token());
    }

    @Test
    public void testTableAliasPartitionFeatureGating() {
        ExposedSQLParser parser = new ExposedSQLParser("partition p");
        parser.configDialectFeature(DialectFeature.ParserFeature.TableAliasPartition, true);
        Assert.assertEquals("partition", parser.readTableAlias(false));
    }

    @Test
    public void testTableAliasNaturalJoinReturnsNull() {
        ExposedSQLParser parser = new ExposedSQLParser("natural join t");
        Assert.assertNull(parser.readTableAlias(false));
        Assert.assertEquals(Token.IDENTIFIER, parser.getLexer().token());
    }

    @Test
    public void testTableAliasCrossJoinReturnsNull() {
        ExposedSQLParser parser = new ExposedSQLParser("cross join t");
        Assert.assertNull(parser.readTableAlias(false));
        Assert.assertEquals(Token.IDENTIFIER, parser.getLexer().token());
    }

    private static class ExposedSQLParser extends SQLParser {
        ExposedSQLParser(String sql) {
            super(sql);
        }

        String readTableAlias(boolean must) {
            return tableAlias(must);
        }

        void configDialectFeature(DialectFeature.ParserFeature feature, boolean state) {
            this.lexer.dialectFeature.configFeature(feature, state);
        }
    }
}
