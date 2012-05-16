package com.alibaba.druid.sql.dialect.oracle.parser;

import static com.alibaba.druid.sql.parser.CharTypes.isIdentifierChar;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.druid.sql.parser.Keywords;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.NotAllowCommentException;
import com.alibaba.druid.sql.parser.SQLParseException;
import com.alibaba.druid.sql.parser.Token;

import static com.alibaba.druid.sql.parser.LayoutCharacters.EOI;

public class OracleLexer extends Lexer {

    public final static Keywords DEFAULT_ORACLE_KEYWORDS;

    static {
        Map<String, Token> map = new HashMap<String, Token>();
        map.put("EXISTS", Token.EXISTS);
        map.put("THEN", Token.THEN);
        map.put("AS", Token.AS);
        map.put("GROUP", Token.GROUP);
        map.put("BY", Token.BY);
        map.put("HAVING", Token.HAVING);
        map.put("DELETE", Token.DELETE);
        map.put("ORDER", Token.ORDER);
        map.put("INDEX", Token.INDEX);
        map.put("FOR", Token.FOR);
        map.put("SCHEMA", Token.SCHEMA);
        map.put("FOREIGN", Token.FOREIGN);
        map.put("REFERENCE", Token.REFERENCE);
        map.put("REFERENCES", Token.REFERENCES);
        map.put("CHECK", Token.CHECK);
        map.put("PRIMARY", Token.PRIMARY);
        map.put("KEY", Token.KEY);
        map.put("CONSTRAINT", Token.CONSTRAINT);
        map.put("DEFAULT", Token.DEFAULT);
        map.put("VIEW", Token.VIEW);
        map.put("CREATE", Token.CREATE);
        map.put("VALUES", Token.VALUES);
        map.put("ALTER", Token.ALTER);
        map.put("TABLE", Token.TABLE);
        map.put("DROP", Token.DROP);
        map.put("SET", Token.SET);
        map.put("INTO", Token.INTO);
        map.put("UPDATE", Token.UPDATE);
        map.put("NULL", Token.NULL);
        map.put("IS", Token.IS);
        map.put("NOT", Token.NOT);
        map.put("SELECT", Token.SELECT);
        map.put("INSERT", Token.INSERT);
        map.put("FROM", Token.FROM);
        map.put("WHERE", Token.WHERE);
        map.put("AND", Token.AND);
        map.put("OR", Token.OR);
        map.put("XOR", Token.XOR);
        map.put("DISTINCT", Token.DISTINCT);
        map.put("UNIQUE", Token.UNIQUE);
        map.put("ALL", Token.ALL);
        map.put("UNION", Token.UNION);
        map.put("INTERSECT", Token.INTERSECT);
        map.put("MINUS", Token.MINUS);
        map.put("INNER", Token.INNER);
        map.put("LEFT", Token.LEFT);
        map.put("RIGHT", Token.RIGHT);
        map.put("FULL", Token.FULL);
        map.put("ON", Token.ON);
        map.put("OUTER", Token.OUTER);
        map.put("JOIN", Token.JOIN);
        map.put("NEW", Token.NEW);
        map.put("CASE", Token.CASE);
        map.put("WHEN", Token.WHEN);
        map.put("END", Token.END);
        map.put("WHEN", Token.WHEN);
        map.put("ELSE", Token.ELSE);
        map.put("EXISTS", Token.EXISTS);
        map.put("CAST", Token.CAST);
        map.put("IN", Token.IN);
        map.put("ASC", Token.ASC);
        map.put("DESC", Token.DESC);
        map.put("LIKE", Token.LIKE);
        map.put("ESCAPE", Token.ESCAPE);
        map.put("BETWEEN", Token.BETWEEN);
        map.put("INTERVAL", Token.INTERVAL);
        map.put("LOCK", Token.LOCK);
        map.put("SOME", Token.SOME);
        map.put("ANY", Token.ANY);
        map.put("TRUNCATE", Token.TRUNCATE);

        map.put("START", Token.START);
        map.put("CONNECT", Token.CONNECT);
        map.put("PRIOR", Token.PRIOR);
        map.put("WITH", Token.WITH);
        map.put("EXTRACT", Token.EXTRACT);
        map.put("COLUMN", Token.COLUMN);

        map.put("CURSOR", Token.CURSOR);
        
        map.put("TO", Token.TO);
        map.put("MODEL", Token.MODEL);
        map.put("MERGE", Token.MERGE);
        map.put("USING", Token.USING);
        map.put("MATCHED", Token.MATCHED);
        map.put("ERRORS", Token.ERRORS);
        map.put("REJECT", Token.REJECT);
        map.put("UNLIMITED", Token.UNLIMITED);
        map.put("RETURNING", Token.RETURNING);
        map.put("LIMIT", Token.LIMIT);
        map.put("OF", Token.OF);
        map.put("BEGIN", Token.BEGIN);
        map.put("SHARE", Token.SHARE);
        map.put("EXCLUSIVE", Token.EXCLUSIVE);
        map.put("MODE", Token.MODE);
        map.put("WAIT", Token.WAIT);
        map.put("NOWAIT", Token.NOWAIT);
        map.put("SESSION", Token.SESSION);
        map.put("PROCEDURE", Token.PROCEDURE);
        map.put("LOCAL", Token.LOCAL);
        map.put("SYSDATE", Token.SYSDATE);
        map.put("DECLARE", Token.DECLARE);
        map.put("EXCEPTION", Token.EXCEPTION);
        map.put("GRANT", Token.GRANT);
        map.put("COMMENT", Token.COMMENT);
        map.put("LOOP", Token.LOOP);
        map.put("IF", Token.IF);
        map.put("ELSE", Token.ELSE);
        map.put("GOTO", Token.GOTO);
        map.put("COMMIT", Token.COMMIT);
        map.put("ROLLBACK", Token.ROLLBACK);
        map.put("SAVEPOINT", Token.SAVEPOINT);
        map.put("CROSS", Token.CROSS);

        DEFAULT_ORACLE_KEYWORDS = new Keywords(map);
    }

    public OracleLexer(char[] input, int inputLength, boolean skipComment){
        super(input, inputLength, skipComment);
        super.keywods = DEFAULT_ORACLE_KEYWORDS;
    }

    public OracleLexer(String input){
        super(input);
        super.keywods = DEFAULT_ORACLE_KEYWORDS;
    }

    public void scanVariable() {
        if (ch == '@') {
            scanChar();
            token = Token.MONKEYS_AT;
            return;
        }

        if (ch != ':' && ch != '#') {
            throw new SQLParseException("illegal variable");
        }

        int hash = ch;

        np = bp;
        sp = 1;
        char ch;

        boolean quoteFlag = false;
        if (buf[bp + 1] == '"') {
            hash = 31 * hash + '"';
            bp++;
            sp++;
            quoteFlag = true;
        }
        for (;;) {
            ch = buf[++bp];

            if (!isIdentifierChar(ch)) {
                break;
            }

            hash = 31 * hash + ch;

            sp++;
            continue;
        }
        if (quoteFlag) {
            if (ch != '"') {
                throw new SQLParseException("syntax error");
            }
            hash = 31 * hash + '"';
            ++bp;
            sp++;
        }

        this.ch = buf[bp];

        stringVal = symbolTable.addSymbol(buf, np, sp, hash);
        Token tok = keywods.getKeyword(stringVal);
        if (tok != null) {
            token = tok;
        } else {
            token = Token.VARIANT;
        }
    }

    public void scanComment() {
        if (ch != '/' && ch != '-') {
            throw new IllegalStateException();
        }

        np = bp;
        sp = 0;
        scanChar();

        // /*+ */
        if (ch == '*') {
            scanChar();
            sp++;

            while (ch == ' ') {
                scanChar();
                sp++;
            }

            boolean isHint = false;
            int startHintSp = sp + 1;
            if (ch == '+') {
                isHint = true;
                scanChar();
                sp++;
            }

            for (;;) {
                if (ch == '*' && buf[bp + 1] == '/') {
                    sp += 2;
                    scanChar();
                    scanChar();
                    break;
                }

                scanChar();
                sp++;
            }

            if (isHint) {
                stringVal = new String(buf, np + startHintSp, (sp - startHintSp) - 2).trim();
                token = Token.HINT;
            } else {
                stringVal = new String(buf, np, sp);
                token = Token.MULTI_LINE_COMMENT;
            }
            
            if (token != Token.HINT && !isAllowComment()) {
                throw new NotAllowCommentException();
            }

            return;
        }
        
        if (!isAllowComment()) {
            throw new NotAllowCommentException();
        }

        if (ch == '/' || ch == '-') {
            scanChar();
            sp++;

            for (;;) {
                if (ch == '\r') {
                    if (buf[bp + 1] == '\n') {
                        sp += 2;
                        scanChar();
                        break;
                    }
                    sp++;
                    break;
                } else if (ch == EOI) {
                    break;
                }

                if (ch == '\n') {
                    scanChar();
                    sp++;
                    break;
                }

                scanChar();
                sp++;
            }

            stringVal = new String(buf, np + 1, sp);
            token = Token.LINE_COMMENT;
            return;
        }
    }

    public void scanNumber() {
        np = bp;

        if (ch == '-') {
            sp++;
            ch = buf[++bp];
        }

        for (;;) {
            if (ch >= '0' && ch <= '9') {
                sp++;
            } else {
                break;
            }
            ch = buf[++bp];
        }

        boolean isDouble = false;

        if (ch == '.') {
            if (buf[bp + 1] == '.') {
                token = Token.LITERAL_INT;
                return;
            }
            sp++;
            ch = buf[++bp];
            isDouble = true;

            for (;;) {
                if (ch >= '0' && ch <= '9') {
                    sp++;
                } else {
                    break;
                }
                ch = buf[++bp];
            }
        }

        if (ch == 'e' || ch == 'E') {
            sp++;
            ch = buf[++bp];

            if (ch == '+' || ch == '-') {
                sp++;
                ch = buf[++bp];
            }

            for (;;) {
                if (ch >= '0' && ch <= '9') {
                    sp++;
                } else {
                    break;
                }
                ch = buf[++bp];
            }

            isDouble = true;
        }

        if (ch == 'f' || ch == 'F') {
            token = Token.BINARY_FLOAT;
            scanChar();
            return;
        }

        if (ch == 'd' || ch == 'D') {
            token = Token.BINARY_DOUBLE;
            scanChar();
            return;
        }

        if (isDouble) {
            token = Token.LITERAL_FLOAT;
        } else {
            token = Token.LITERAL_INT;
        }
    }
    
}
