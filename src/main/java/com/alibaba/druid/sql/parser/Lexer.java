/*
 * Copyright 2011 Alibaba Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.sql.parser;

import static com.alibaba.druid.sql.parser.CharTypes.isFirstIdentifierChar;
import static com.alibaba.druid.sql.parser.CharTypes.isIdentifierChar;
import static com.alibaba.druid.sql.parser.CharTypes.isWhitespace;
import static com.alibaba.druid.sql.parser.LayoutCharacters.EOI;
import static com.alibaba.druid.sql.parser.Token.COLON;
import static com.alibaba.druid.sql.parser.Token.COLONEQ;
import static com.alibaba.druid.sql.parser.Token.COMMA;
import static com.alibaba.druid.sql.parser.Token.EOF;
import static com.alibaba.druid.sql.parser.Token.ERROR;
import static com.alibaba.druid.sql.parser.Token.LBRACE;
import static com.alibaba.druid.sql.parser.Token.LBRACKET;
import static com.alibaba.druid.sql.parser.Token.LITERAL_ALIAS;
import static com.alibaba.druid.sql.parser.Token.LITERAL_CHARS;
import static com.alibaba.druid.sql.parser.Token.LPAREN;
import static com.alibaba.druid.sql.parser.Token.RBRACE;
import static com.alibaba.druid.sql.parser.Token.RBRACKET;
import static com.alibaba.druid.sql.parser.Token.RPAREN;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author wenshao<szujobs@hotmail.com>
 */
public class Lexer {
	protected final char[] sql;
	protected int curIndex;
	protected int sqlLength;
	// QS_TODO what is the purpose?
	protected int eofPos;

	/** The current character. */
	protected char ch;

	/** The token's position, 0-based offset from beginning of text. */
	protected int tokenPos;

	/** A character buffer for literals. */
	protected final static ThreadLocal<char[]> sbufRef = new ThreadLocal<char[]>();
	protected char[] sbuf;

	/** string point as size */
	protected int sizeCache;
	/** string point as offset */
	protected int offsetCache;

	protected SymbolTable symbolTable = new SymbolTable();

	/**
	 * The token, set by nextToken().
	 */
	protected Token token;

	protected Keywords keywods = Keywords.DEFAULT_KEYWORDS;

	protected String stringVal;

	public Lexer(String input) {
		this(input.toCharArray(), input.length());
	}

	public Lexer(char[] input, int inputLength) {
		this.sbuf = sbufRef.get(); // new char[1024];
		if (this.sbuf == null) {
			this.sbuf = new char[1024];
			sbufRef.set(sbuf);
		}

		this.eofPos = inputLength;

		// QS_TODO ?
		if (inputLength == input.length) {
			if (input.length > 0 && isWhitespace(input[input.length - 1])) {
				inputLength--;
			} else {
				char[] newInput = new char[inputLength + 1];
				System.arraycopy(input, 0, newInput, 0, input.length);
				input = newInput;
			}
		}
		this.sql = input;
		this.sqlLength = inputLength;
		this.sql[this.sqlLength] = EOI;
		this.curIndex = -1;

		scanChar();
	}

	protected final void scanChar() {
		ch = sql[++curIndex];
	}

	/**
	 * Report an error at the given position using the provided arguments.
	 */
	protected void lexError(int pos, String key, Object... args) {
		token = ERROR;
	}

	/**
	 * Report an error at the current token position using the provided arguments.
	 */
	private void lexError(String key, Object... args) {
		lexError(tokenPos, key, args);
	}

	/**
	 * Return the current token, set by nextToken().
	 */
	public final Token token() {
		return token;
	}

	public final void nextToken() {
		sizeCache = 0;

		for (;;) {
			tokenPos = curIndex;

			if (isWhitespace(ch)) {
				scanChar();
				continue;
			}// QS_TODO skip comment

			// QS_TODO id may start from digit
			if (isFirstIdentifierChar(ch)) {
				if (ch == 'N') {
					if (sql[curIndex + 1] == '\'') {
						++curIndex;
						ch = '\'';
						scanString();
						token = Token.LITERAL_NCHARS;
						return;
					}
				}

				scanIdentifier();
				return;
			}

			switch (ch) {
			case '0':
				if (sql[curIndex + 1] == 'x') {
					scanChar();
					scanChar();
					scanHexaDecimal();
				} else {
					scanNumber();
				}
				return;
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				scanNumber();
				return;
			case ',':
				scanChar();
				token = COMMA;
				return;
			case '(':
				scanChar();
				token = LPAREN;
				return;
			case ')':
				scanChar();
				token = RPAREN;
				return;
			case '[':
				scanChar();
				token = LBRACKET;
				return;
			case ']':
				scanChar();
				token = RBRACKET;
				return;
			case '{':
				scanChar();
				token = LBRACE;
				return;
			case '}':
				scanChar();
				token = RBRACE;
				return;
			case ':':
				scanChar();
				if (ch == '=') {
					scanChar();
					token = COLONEQ;
				} else {
					token = COLON;
				}
				return;
			case '.':
				scanChar();
				token = Token.DOT;
				return;
			case '\'':
				scanString();
				return;
			case '\"':
				scanAlias();
				return;
			case '*':
				scanChar();
				token = Token.STAR;
				return;
			case '?':
				scanChar();
				token = Token.QUES;
				return;
			case ';':
				scanChar();
				token = Token.SEMI;
				return;
			case '`':
				throw new SQLParseException("TODO"); // TODO
			case '@':
				scanVariable();
				token = Token.USR_VAR;
				return;
			default:
				if (Character.isLetter(ch)) {
					scanIdentifier();
					return;
				}

				if (isOperator(ch)) {
					scanOperator();
					return;
				}

				// QS_TODO ?
				if (curIndex == sqlLength || ch == EOI && curIndex + 1 == sqlLength) { // JLS
					token = EOF;
					tokenPos = curIndex = eofPos;
				} else {
					lexError("illegal.char", String.valueOf((int) ch));
					scanChar();
				}

				return;
			}
		}

	}

	private final void scanOperator() {
		switch (ch) {
		case '+':
			scanChar();
			token = Token.PLUS;
			break;
		case '-':
			scanChar();
			token = Token.SUB;
			break;
		case '*':
			scanChar();
			token = Token.STAR;
			break;
		case '/':
			scanChar();
			token = Token.SLASH;
			break;
		case '&':
			scanChar();
			if (ch == '&') {
				scanChar();
				token = Token.AMPAMP;
			} else {
				token = Token.AMP;
			}
			break;
		case '|':
			scanChar();
			if (ch == '|') {
				scanChar();
				token = Token.BARBAR;
			} else {
				token = Token.BAR;
			}
			break;
		case '^':
			scanChar();
			token = Token.CARET;
			break;
		case '%':
			scanChar();
			token = Token.PERCENT;
			break;
		case '=':
			scanChar();
			if (ch == '=') {
				scanChar();
				token = Token.EQEQ;
			} else {
				token = Token.EQ;
			}
			break;
		case '>':
			scanChar();
			if (ch == '=') {
				scanChar();
				token = Token.GTEQ;
			} else if (ch == '>') {
				scanChar();
				token = Token.GTGT;
			} else {
				token = Token.GT;
			}
			break;
		case '<':
			scanChar();
			if (ch == '=') {
				scanChar();
				if (ch == '>') {
					token = Token.LTEQGT;
					scanChar();
				} else {
					token = Token.LTEQ;
				}
			} else if (ch == '>') {
				scanChar();
				token = Token.LTGT;
			} else if (ch == '<') {
				scanChar();
				token = Token.LTLT;
			} else {
				token = Token.LT;
			}
			break;
		case '!':
			scanChar();
			if (ch == '=') {
				scanChar();
				token = Token.BANGEQ;
			} else if (ch == '>') {
				scanChar();
				token = Token.BANGGT;
			} else if (ch == '<') {
				scanChar();
				token = Token.BANGLT;
			} else {
				token = Token.BANG;
			}
			break;
		case '?':
			scanChar();
			token = Token.QUES;
			break;
		case '~':
			scanChar();
			token = Token.TILDE;
			break;
		default:
			throw new SQLParseException("TODO");
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

	private final void scanAlias() {
		for (;;) {
			if (curIndex >= sqlLength) {
				lexError(tokenPos, "unclosed.str.lit");
				return;
			}

			ch = sql[++curIndex];

			if (ch == '\"') {
				scanChar();
				token = LITERAL_ALIAS;
				return;
			}

			if (sizeCache == sbuf.length) {
				putChar(ch);
			} else {
				sbuf[sizeCache++] = ch;
			}
		}
	}

	public void scanVariable() {
		final char first = ch;

		if (ch != '@' && ch != ':') {
			throw new SQLParseException("illegal variable");
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

	public void scanIdentifier() {
		final char first = ch;

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

	public void scanNumber() {
		offsetCache = curIndex;

		if (ch == '-') {
			sizeCache++;
			ch = sql[++curIndex];
		}

		for (;;) {
			if (ch >= '0' && ch <= '9') {
				sizeCache++;
			} else {
				break;
			}
			ch = sql[++curIndex];
		}

		boolean isDouble = false;

		if (ch == '.') {
			sizeCache++;
			ch = sql[++curIndex];
			isDouble = true;

			for (;;) {
				if (ch >= '0' && ch <= '9') {
					sizeCache++;
				} else {
					break;
				}
				ch = sql[++curIndex];
			}
		}

		if (ch == 'e' || ch == 'E') {
			sizeCache++;
			ch = sql[++curIndex];

			if (ch == '+' || ch == '-') {
				sizeCache++;
				ch = sql[++curIndex];
			}

			for (;;) {
				if (ch >= '0' && ch <= '9') {
					sizeCache++;
				} else {
					break;
				}
				ch = sql[++curIndex];
			}

			isDouble = true;
		}

		if (isDouble) {
			token = Token.LITERAL_NUM_MIX_DIGIT;
		} else {
			token = Token.LITERAL_NUM_PURE_DIGIT;
		}
	}

	public void scanHexaDecimal() {
		offsetCache = curIndex;

		if (ch == '-') {
			sizeCache++;
			ch = sql[++curIndex];
		}

		for (;;) {
			if (CharTypes.isHex(ch)) {
				sizeCache++;
			} else {
				break;
			}
			ch = sql[++curIndex];
		}

		token = Token.LITERAL_HEX;
	}

	public String hexString() throws NumberFormatException {
		return new String(sql, offsetCache, sizeCache);
	}

	public final boolean isDigit(char ch) {
		return ch >= '0' && ch <= '9';
	}

	/**
	 * Append a character to sbuf.
	 */
	protected final void putChar(char ch) {
		if (sizeCache == sbuf.length) {
			char[] newsbuf = new char[sbuf.length * 2];
			System.arraycopy(sbuf, 0, newsbuf, 0, sbuf.length);
			sbuf = newsbuf;
		}
		sbuf[sizeCache++] = ch;
	}

	/**
	 * Return the current token's position: a 0-based offset from beginning of the raw input stream (before unicode translation)
	 */
	public final int pos() {
		return tokenPos;
	}

	/**
	 * The value of a literal token, recorded as a string. For integers, leading 0x and 'l' suffixes are suppressed.
	 */
	public final String stringVal() {
		return stringVal;
	}

	private boolean isOperator(char ch) {
		switch (ch) {
		case '!':
		case '%':
		case '&':
		case '*':
		case '+':
		case '-':
		case '<':
		case '=':
		case '>':
		case '^':
		case '|':
		case '~':
		case '/':
		case ';':
			return true;
		default:
			return false;
		}
	}

	private static final long MULTMIN_RADIX_TEN = Long.MIN_VALUE / 10;
	private static final long N_MULTMAX_RADIX_TEN = -Long.MAX_VALUE / 10;

	private final static int[] digits = new int[(int) '9' + 1];

	static {
		for (int i = '0'; i <= '9'; ++i) {
			digits[i] = i - '0';
		}
	}

	// QS_TODO negative number is invisible for lexer
	public Number integerValue() throws NumberFormatException {
		long result = 0;
		boolean negative = false;
		int i = offsetCache, max = offsetCache + sizeCache;
		long limit;
		long multmin;
		int digit;

		if (sql[offsetCache] == '-') {
			negative = true;
			limit = Long.MIN_VALUE;
			i++;
		} else {
			limit = -Long.MAX_VALUE;
		}
		multmin = negative ? MULTMIN_RADIX_TEN : N_MULTMAX_RADIX_TEN;
		if (i < max) {
			digit = digits[sql[i++]];
			result = -digit;
		}
		while (i < max) {
			// Accumulating negatively avoids surprises near MAX_VALUE
			digit = digits[sql[i++]];
			if (result < multmin) {
				return new BigInteger(numberString());
			}
			result *= 10;
			if (result < limit + digit) {
				return new BigInteger(numberString());
			}
			result -= digit;
		}

		if (negative) {
			if (i > offsetCache + 1) {
				if (result >= Integer.MIN_VALUE) {
					return (int) result;
				}
				return result;
			} else { /* Only got "-" */
				throw new NumberFormatException(numberString());
			}
		} else {
			result = -result;
			if (result <= Integer.MAX_VALUE) {
				return (int) result;
			}
			return result;
		}
	}

	public final String numberString() {
		return new String(sql, offsetCache, sizeCache);
	}

	public BigDecimal decimalValue() {
		return new BigDecimal(sql, offsetCache, sizeCache);
	}
}
