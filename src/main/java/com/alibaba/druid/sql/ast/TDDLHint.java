package com.alibaba.druid.sql.ast;

import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlExprParser;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.FnvHash;

import java.util.ArrayList;
import java.util.List;

public class TDDLHint extends SQLCommentHint {
    private List<Function> functions = new ArrayList<Function>();
    private String json;
    private Type type = Type.Unknown;

    public List<Function> getFunctions() {
        return functions;
    }

    public TDDLHint(String text) {
        super(text);

        MySqlExprParser hintParser = new MySqlExprParser(text, SQLParserFeature.TDDLHint);
        Lexer lexer = hintParser.getLexer();
        if (lexer.token() == Token.PLUS || lexer.token() == Token.BANG) {
            lexer.nextToken();
        }

        if (!lexer.identifierEquals(FnvHash.Constants.TDDL)) {
            // error tddl hint
            return;
        }


        lexer.nextToken();

        switch (lexer.token()) {
            case COLON:
                lexer.nextToken();
                break;
            case LPAREN:
                int rp = text.lastIndexOf(')');
                if (rp != -1) {
                    json = text.substring(lexer.pos(), rp);
                    type = Type.JSON;
                }
                return;
            default:
                return;
        }

        for(;;) {
            if (lexer.token() == Token.AND) {
                lexer.nextToken();
            }

            String name = lexer.stringVal();
            long hash = lexer.hash_lower();

            if (lexer.identifierEquals(FnvHash.Constants.NODE)) {
                lexer.nextToken();
                if (lexer.token() == Token.IN) {
                    lexer.nextToken();
                    name = "NODE_IN";
                } else if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                    name = "NODE_IN";
                    SQLExpr value = hintParser.primary();
                    Function function = new Function(name);
                    Argument argument = new Argument(null, value);
                    function.getArguments().add(argument);
                    functions.add(function);

                    if (lexer.token() == Token.EOF) {
                        break;
                    }

                    continue;
                }
            } else if (hash == FnvHash.Constants.SCAN || hash == FnvHash.Constants.DEFER) {
                lexer.nextToken();

                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                    SQLExpr value = hintParser.primary();
                    Function function = new Function(name);
                    Argument argument = new Argument(null, value);
                    function.getArguments().add(argument);
                    functions.add(function);

                    if (lexer.token() == Token.EOF) {
                        break;
                    }

                    continue;
                } else if (lexer.token() == Token.EOF){
                    Function function = new Function(name);
                    functions.add(function);
                    break;
                }
            } else if (hash == FnvHash.Constants.SQL_DELAY_CUTOFF
                    || hash == FnvHash.Constants.SOCKET_TIMEOUT
                    || hash == FnvHash.Constants.UNDO_LOG_LIMIT
                    || hash == FnvHash.Constants.FORBID_EXECUTE_DML_ALL) {
                lexer.nextToken();

                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                    SQLExpr value = hintParser.primary();
                    Function function = new Function(name);
                    Argument argument = new Argument(null, value);
                    function.getArguments().add(argument);
                    functions.add(function);

                    if (lexer.token() == Token.EOF) {
                        break;
                    }

                    continue;
                }
            } else {
                lexer.nextToken();
            }

            // /!TDDL:table_name.partition_key=value [and table_name1.partition_key=value1]*/
            // =>
            // /!TDDL:partitions('table_name.partition_key=value','table_name1.partition_key=value1')*/
            if (lexer.token() == Token.DOT) {
                lexer.nextToken();
                if (lexer.identifierEquals("partition_key")) {
                    String table = name;
                    lexer.nextToken();
                    hintParser.accept(Token.EQ);
                    SQLExpr value = hintParser.primary();

                    Function function = new Function("PARTITIONS");
                    functions.add(function);

                    function.getArguments().add(new Argument(new SQLPropertyExpr(name, "partition_key"), value));

                    while (lexer.token() == Token.AND) {
                        lexer.nextToken();
                        SQLExpr key = hintParser.primary();
                        hintParser.accept(Token.EQ);
                        value = hintParser.primary();

                        function.getArguments().add(new Argument(key, value));
                    }

                    if (lexer.token() == Token.EOF) {
                        break;
                    }
                } else {
                    return; // skip
                }
            } else if (lexer.token() == Token.EQ) {
                lexer.nextToken();
                // For other KV.
                SQLExpr value = hintParser.primary();
                Function function = new Function(name);
                Argument argument = new Argument(null, value);
                function.getArguments().add(argument);
                functions.add(function);

                if (lexer.token() == Token.EOF) {
                    break;
                }

                continue;
            }


            Function function = new Function(name);

            functions.add(function);

            if (hash == FnvHash.Constants.MASTER) {
                if (lexer.token() == Token.EOF) {
                    break;
                } else if (lexer.token() == Token.BAR) {
                    lexer.nextToken();
                    continue;
                }
            }

            if (hash == FnvHash.Constants.SLAVE) {
                if (lexer.token() == Token.EOF) {
                    break;
                } else if (lexer.token() == Token.AND) {
                    lexer.nextToken();
                    continue;
                }
            }

            if (lexer.token() == Token.AND) {
                continue;
            }

            hintParser.accept(Token.LPAREN);

            if (lexer.token() != Token.RPAREN) {
                for (; ; ) {
                    Lexer.SavePoint mark = lexer.mark();

                    SQLExpr value = null;

                    String keyVal = lexer.stringVal();
                    long keyHash = lexer.hash_lower();

                    lexer.nextToken();

                    SQLIdentifierExpr key = new SQLIdentifierExpr(keyVal, keyHash);

                    if (lexer.token() == Token.EQ) {
                        hintParser.accept(Token.EQ);

                        if (lexer.token() == Token.LITERAL_ALIAS) {
                            String stringVal = lexer.stringVal();
                            stringVal = stringVal.substring(1, stringVal.length() - 1);
                            value = new SQLCharExpr(stringVal);
                            value = hintParser.exprRest(value);

                            lexer.nextToken();
                        } else {
                            value = hintParser.expr();
                        }
                    }

                    //为了处理 add_ms(t.pk, true) 的情况；
                    if (value == null) {
                        lexer.reset(mark);
                        key = null;
                        value = hintParser.expr();
                    }

                    Argument argument = new Argument(key, value);
                    function.getArguments().add(argument);

                    if (lexer.token() == Token.COMMA) {
                        lexer.nextToken();
                        continue;
                    }

                    if (lexer.token() == Token.RPAREN) {
                        lexer.nextToken();
                        break;
                    }
                }
            } else {
                lexer.nextToken();
            }

            if (lexer.token() == Token.AND) {
                continue;
            }

            if (hash == FnvHash.Constants.MASTER && lexer.token() == Token.BAR) {
                lexer.nextToken();
            }

            if (lexer.token() == Token.EOF) {
                break;
            }
        }

        if (functions.size() > 0) {
            type = Type.Function;
        }
    }

    public String getJson() {
        return json;
    }

    public Type getType() {
        return type;
    }

    public static class Function {
        private final String         name;
        private final List<Argument> arguments = new ArrayList<Argument>();

        public Function(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public List<Argument> getArguments() {
            return arguments;
        }
    }

    public static class Argument {
        private final SQLExpr  name;
        private final SQLExpr value;

        public Argument(SQLExpr name, SQLExpr value) {
            this.name = name;
            this.value = value;
        }

        public SQLExpr getName() {
            return name;
        }

        public SQLExpr getValue() {
            return value;
        }
    }

    public static enum Type {
        Function,
        JSON,
        Unknown
    }
}
