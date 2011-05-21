package com.alibaba.druid.sql.dialect.oracle.parser;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.druid.sql.parser.Keywords;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.Token;

public class OracleLexer extends Lexer {
    public final static Keywords           DEFAULT_ORACLE_KEYWORDS;
    
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
        
        map.put("START", Token.START);
        map.put("CONNECT", Token.CONNECT);
        map.put("PRIOR", Token.PRIOR);
        map.put("WITH", Token.WITH);
        map.put("EXTRACT", Token.EXTRACT);
        map.put("DATE", Token.DATE);
        map.put("TIMESTAMP", Token.TIMESTAMP);
        
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

    public void scanComment() {
        if (ch != '/') {
            throw new IllegalStateException();
        }

        np = bp;
        sp = 0;
        scanChar();
        sp++;

        // /*+  */
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
           
            return;
        }

        if (ch == '/') {
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
                }

                if (ch == '\r') {
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
