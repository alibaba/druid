package com.alibaba.druid.sql.dialect.oracle.parser;

import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.Token;

public class OracleLexer extends Lexer {

    public OracleLexer(char[] input, int inputLength, boolean skipComment){
        super(input, inputLength, skipComment);
    }

    public OracleLexer(String input){
        super(input);
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
}
