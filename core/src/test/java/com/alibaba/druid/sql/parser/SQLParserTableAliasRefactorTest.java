package com.alibaba.druid.sql.parser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SQLParserTableAliasRefactorTest {
    @Test
    public void testTableAliasJoinKeywordBranchKeepsJoinParsing() {
        ExposedSQLParser parser = new ExposedSQLParser("left join t");
        assertNull(parser.readTableAlias(false));
        assertEquals(Token.LEFT, parser.getLexer().token());
    }

    @Test
    public void testTableAliasDistributeByReturnsNull() {
        ExposedSQLParser parser = new ExposedSQLParser("distribute by c1");
        assertNull(parser.readTableAlias(false));
        assertEquals(Token.IDENTIFIER, parser.getLexer().token());
    }

    @Test
    public void testTableAliasDistributeWithoutByReturnsAlias() {
        ExposedSQLParser parser = new ExposedSQLParser("distribute x");
        assertEquals("distribute", parser.readTableAlias(false));
        assertEquals(Token.IDENTIFIER, parser.getLexer().token());
    }

    @Test
    public void testTableAliasPartitionFeatureGating() {
        ExposedSQLParser parser = new ExposedSQLParser("partition p");
        parser.configDialectFeature(DialectFeature.ParserFeature.TableAliasPartition, true);
        assertEquals("partition", parser.readTableAlias(false));
    }

    @Test
    public void testTableAliasNaturalJoinReturnsNull() {
        ExposedSQLParser parser = new ExposedSQLParser("natural join t");
        assertNull(parser.readTableAlias(false));
        assertEquals(Token.IDENTIFIER, parser.getLexer().token());
    }

    @Test
    public void testTableAliasCrossJoinReturnsNull() {
        ExposedSQLParser parser = new ExposedSQLParser("cross join t");
        assertNull(parser.readTableAlias(false));
        assertEquals(Token.IDENTIFIER, parser.getLexer().token());
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
