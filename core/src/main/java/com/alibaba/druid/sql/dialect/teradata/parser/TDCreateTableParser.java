package com.alibaba.druid.sql.dialect.teradata.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLPrimaryKey;
import com.alibaba.druid.sql.dialect.teradata.ast.TDCreateTableStatement;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.FnvHash;

public class TDCreateTableParser extends SQLCreateTableParser {
  /** <p>
   * CREATE [ SET | MULTISET ] [ GLOBAL TEMPORARY | VOLATILE ] TABLE table_specification
   *    [ , table_option [,...] ] Not supported.
   *        { MAP = map_name [COLOCATE USING colocation_name |
   *            [NO] FALLBACK [PROTECTION] |
   *            WITH JOURNAL TABLE = table_specification |
   *            [NO] LOG |
   *            [ NO | DUAL ] [BEFORE] JOURNAL |
   *            [ NO | DUAL | LOCAL | NOT LOCAL ] AFTER JOURNAL |
   *            CHECKSUM = { DEFAULT | ON | OFF } |
   *            FREESPACE = integer [PERCENT] |
   *            mergeblockratio |
   *            datablocksize |
   *            blockcompression |
   *            isolated_loading
   *        }
   *    ( column_partition_definition ) Limited supported
   *        column_name data_type [ column_data_type_attribute [,...] ] |
   *        [ COLUMN | ROW ] ( column_name data_type [column_attributes] [,...] )
   *        [ [NO] AUTO COMPRESS] |
   *        PERIOD FOR period_name ( period_begin_column , period_end_column ) |
   *        normalize_option |
   *        table_constraint
   *        ][,...]
   *    [ index [,...] ] Limited supported
   *        [UNIQUE] PRIMARY INDEX [index_name] ( index_column_name [,...] ) |
   *        NO PRIMARY INDEX |
   *        PRIMARY AMP [INDEX] [index_name] ( index_column_name [,...] ) |
   *        PARTITION BY { partitioning_level | ( partitioning_level [,...] ) } |
   *        UNIQUE INDEX [ index_name ] [ ( index_column_name [,...] ) ] [loading] |
   *        INDEX [index_name] [ALL] ( index_column_name [,...] ) [ordering] [loading]
   *    [ table_preservation ][;]
   *        ON COMMIT { DELETE | PRESERVE } ROWS
   * </p>
   */

    public TDCreateTableParser(SQLExprParser exprParser) {
        super(exprParser);
        dbType = DbType.teradata;
    }

    @Override
    protected void createTableBefore(SQLCreateTableStatement createTable) {
        // Need to support both [ SET | MULTISET ] [ GLOBAL TEMPORARY | VOLATILE ] and [ GLOBAL TEMPORARY | VOLATILE ][ SET | MULTISET ].
        parseTableType(createTable);
        parseTableType(createTable);
    }

    private void parseTableType(SQLCreateTableStatement createTable) {
        if (lexer.nextIf(Token.SET)) {
            createTable.config(SQLCreateTableStatement.Feature.Set);
        } else if (lexer.nextIfIdentifier(FnvHash.Constants.MULTISET)) {
            createTable.config(SQLCreateTableStatement.Feature.MultiSet);
        } else if (lexer.nextIfIdentifier(FnvHash.Constants.GLOBAL)) {
            createTable.config(SQLCreateTableStatement.Feature.Global);
            acceptIdentifier(FnvHash.Constants.TEMPORARY);
            createTable.config(SQLCreateTableStatement.Feature.Temporary);
        } else if (lexer.nextIfIdentifier(FnvHash.Constants.VOLATILE)) {
            createTable.config(SQLCreateTableStatement.Feature.Volatile);
        }
    }

    @Override
    protected void parseCreateTableRest(SQLCreateTableStatement stmt) {
        super.parseCreateTableRest(stmt);
        if (stmt instanceof TDCreateTableStatement) {
            if (lexer.token() == Token.PRIMARY) {
                SQLPrimaryKey sqlPrimaryKey = this.exprParser.parsePrimaryKey();
                ((TDCreateTableStatement) stmt).setPrimaryKey(sqlPrimaryKey);
            }
            if (lexer.nextIf(Token.ON)) {
                acceptIdentifier("COMMIT");
                if (lexer.nextIf(Token.DELETE)) {
                    ((TDCreateTableStatement) stmt)
                            .setOnCommitRows(TDCreateTableStatement.OnCommitType.DELETE);
                } else if (lexer.nextIfIdentifier("PRESERVE")) {
                    ((TDCreateTableStatement) stmt).setOnCommitRows(TDCreateTableStatement.OnCommitType.PRESERVE);
                } else {
                    throw new ParserException("syntax error " + lexer.info());
                }
                acceptIdentifier("ROWS");
            }
        }
    }

    @Override
    protected SQLCreateTableStatement newCreateStatement() {
        return new TDCreateTableStatement(getDbType());
    }
}
