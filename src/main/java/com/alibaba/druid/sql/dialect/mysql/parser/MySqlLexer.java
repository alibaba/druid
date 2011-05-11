package com.alibaba.druid.sql.dialect.mysql.parser;

import static com.alibaba.druid.sql.parser.CharTypes.isFirstIdentifierChar;
import static com.alibaba.druid.sql.parser.CharTypes.isIdentifierChar;
import static com.alibaba.druid.sql.parser.LayoutCharacters.EOI;
import static com.alibaba.druid.sql.parser.Token.LITERAL_CHARS;

import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLParseException;
import com.alibaba.druid.sql.parser.Token;

public class MySqlLexer extends Lexer {

	public MySqlLexer(char[] input, int inputLength) {
		super(input, inputLength);
	}

	public MySqlLexer(String input) {
		super(input);
	}

	public void scanIdentifier() {
		final char first = ch;

		if (ch == '`') {
			int hash = first;

			offsetCache = curIndex;
			sizeCache = 1;
			char ch;
			for (;;) {
				ch = sql[++curIndex];

				if (ch == '`') {
					sizeCache++;
					ch = sql[++curIndex];
					break;
				} else if (ch == EOI) {
					throw new SQLParseException("illegal identifier");
				}

				hash = 31 * hash + ch;

				sizeCache++;
				continue;
			}

			this.ch = sql[curIndex];

			stringVal = symbolTable.addSymbol(sql, offsetCache, sizeCache, hash);
			Token tok = keywods.getKeyword(stringVal);
			if (tok != null) {
				token = tok;
			} else {
				token = Token.IDENTIFIER;
			}
		} else {

			final boolean firstFlag = isFirstIdentifierChar(first);
			if (!firstFlag) {
				throw new SQLParseException("illegal identifier");
			}

			int hash = first;

			offsetCache = curIndex;
			sizeCache = 1;
			char ch;
			for (;;) {
				ch = sql[++curIndex];

				if (!isIdentifierChar(ch)) {
					break;
				}

				hash = 31 * hash + ch;

				sizeCache++;
				continue;
			}

			this.ch = sql[curIndex];

			stringVal = symbolTable.addSymbol(sql, offsetCache, sizeCache, hash);
			Token tok = keywods.getKeyword(stringVal);
			if (tok != null) {
				token = tok;
			} else {
				token = Token.IDENTIFIER;
			}
		}
	}

	protected void scanString() {
		offsetCache = curIndex;
		boolean hasSpecial = false;

		for (;;) {
			if (curIndex >= sqlLength) {
				lexError(tokenPos, "unclosed.str.lit");
				return;
			}

			ch = sql[++curIndex];

			if (ch == '\\') {
				scanChar();
				if (!hasSpecial) {
					System.arraycopy(sql, offsetCache + 1, sbuf, 0, sizeCache);
					hasSpecial = true;
				}

				switch (ch) {
				case '\0':
					putChar('\0');
					break;
				case '\'':
					putChar('\'');
					break;
				case '"':
					putChar('"');
					break;
				case 'b':
					putChar('\b');
					break;
				case 'n':
					putChar('\n');
					break;
				case 'r':
					putChar('\r');
					break;
				case 't':
					putChar('\t');
					break;
				case '\\':
					putChar('\\');
					break;
				case 'Z':
					putChar((char) 0x1A); // ctrl + Z
					break;
				default:
					putChar(ch);
					break;
				}
				scanChar();
			}

			if (ch == '\'') {
				scanChar();
				if (ch != '\'') {
					token = LITERAL_CHARS;
					break;
				} else {
					System.arraycopy(sql, offsetCache + 1, sbuf, 0, sizeCache);
					hasSpecial = true;
					putChar('\'');
					continue;
				}
			}

			if (!hasSpecial) {
				sizeCache++;
				continue;
			}

			if (sizeCache == sbuf.length) {
				putChar(ch);
			} else {
				sbuf[sizeCache++] = ch;
			}
		}

		if (!hasSpecial) {
			stringVal = new String(sql, offsetCache + 1, sizeCache);
		} else {
			stringVal = new String(sbuf, 0, sizeCache);
		}
	}
}
