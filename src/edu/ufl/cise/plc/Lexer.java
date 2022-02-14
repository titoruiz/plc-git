package edu.ufl.cise.plc;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.ufl.cise.plc.IToken.Kind;

public class Lexer implements ILexer {
	// stores tokens in array list
	ArrayList<Token> tokens;
	// current token index in array list
	int index;
	// holds source code in a String
	String source;
	// current position in the input string
	int pos;
	// stores the indices of every new line character
	ArrayList<Integer> newLines;
	// map of all kind keywords
	Map<String, Kind> map = new HashMap<String, Kind>();
	
	// states of the DFA algorithm 
	private enum State {
		START,
		IN_IDENT,
		HAVE_ZERO,
		HAVE_DOT,
		IN_FLOAT,
		IN_NUM,
		HAVE_EQ,
		HAVE_MINUS,
		IN_STRLIT,
		IN_COMM,
	}

	// constructor for the lexer class
	public Lexer(String input) {
		tokens = new ArrayList<Token>();
		index = 0;
		source = input + '0';
		pos = 0;
		findNewLines(input);
		initMap();
		createTokens();
	}
	
	// initializes the map of keywords and colors
	public void initMap() {
		// keywords
		map.put("if", Kind.KW_IF);
		map.put("fi", Kind.KW_FI);
		map.put("else", Kind.KW_ELSE);
		map.put("write", Kind.KW_WRITE);
		map.put("console", Kind.KW_CONSOLE);
		map.put("void", Kind.KW_VOID);
		// types
		map.put("int", Kind.TYPE);
		map.put("float", Kind.TYPE);
		map.put("string", Kind.TYPE);
		map.put("boolean", Kind.TYPE);
		map.put("color", Kind.TYPE);
		map.put("image", Kind.TYPE);
		// booleans
		map.put("true", Kind.BOOLEAN_LIT);
		map.put("false", Kind.BOOLEAN_LIT);
		// color constants
		map.put("BLACK", Kind.COLOR_CONST);
		map.put("BLUE", Kind.COLOR_CONST);
		map.put("CYAN", Kind.COLOR_CONST);
		map.put("DARK_GRAY", Kind.COLOR_CONST);
		map.put("GRAY", Kind.COLOR_CONST);
		map.put("GREEN", Kind.COLOR_CONST);
		map.put("LIGHT_GRAY", Kind.COLOR_CONST);
		map.put("MAGENTA", Kind.COLOR_CONST);
		map.put("ORANGE", Kind.COLOR_CONST);
		map.put("PINK", Kind.COLOR_CONST);
		map.put("RED", Kind.COLOR_CONST);
		map.put("WHITE", Kind.COLOR_CONST);
		map.put("YELLOW", Kind.COLOR_CONST);
		// color ops
		map.put("getRed", Kind.COLOR_OP);
		map.put("getGreen", Kind.COLOR_OP);
		map.put("getBlue", Kind.COLOR_OP);
		// image ops
		map.put("getWidth", Kind.IMAGE_OP);
		map.put("getHeight", Kind.IMAGE_OP);
	}
	
	// finds the indices of every new line character
	private void findNewLines(String input) {
		newLines = new ArrayList<Integer>();
		if (input.length() == 0) {
			return;
		}
		for (int i = 0; i < input.length(); i++) {
			if (input.charAt(i) == '\n') {
				newLines.add(i);
			}
		}
		// if the input had no end line at the end, add it
		if (input.charAt(input.length() - 1) != '\n') {
			newLines.add(input.length());
		}
	}
	
	// implements DFA algorithm
	private void createTokens() {
		State state = State.START;
		String curr = "";
		int startPos = 0;
		while (true) {
			char ch = source.charAt(pos);
			switch (state) {
			case START -> {
				startPos = pos;
				switch (ch) {
				case ' ', '\n', '\t', '\r' -> {
					pos++;
				}
				case '(' -> {
					tokens.add(new Token(Kind.LPAREN, "(", startPos, 1, newLines));
					pos++;
				}
				case ')' -> {
					tokens.add(new Token(Kind.RPAREN, ")", startPos, 1, newLines));
					pos++;
				}
				case '[' -> {
					tokens.add(new Token(Kind.LSQUARE, "[", startPos, 1, newLines));
					pos++;
				}
				case ']' -> {
					tokens.add(new Token(Kind.RSQUARE, "]", startPos, 1, newLines));
					pos++;
				}
				case '<' -> {
					char next = source.charAt(pos+1);
					if(next == '<') {
						tokens.add(new Token(Kind.LANGLE, "<<", startPos, 1, newLines));
						pos++;
					}
					else if(next == '=') {
						tokens.add(new Token(Kind.LE, "<=", startPos, 1, newLines));
						pos++;
					}
					else if(next == '-') {
						tokens.add(new Token(Kind.LARROW, "<-", startPos, 1, newLines));
						pos++;
					}
					else {
						tokens.add(new Token(Kind.LT, "<", startPos, 1, newLines));
					}
					pos++;
				}
				case '>' -> {
					char next = source.charAt(pos+1);
					if(next == '>') {
						tokens.add(new Token(Kind.RANGLE, ">>", startPos, 1, newLines));
						pos++;
					}
					else if(next == '=') {
						tokens.add(new Token(Kind.GE, ">=", startPos, 1, newLines));
						pos++;
					}
					else {
						tokens.add(new Token(Kind.GT, ">", startPos, 1, newLines));
					}
					pos++;
				}
				case '+' -> {
					tokens.add(new Token(Kind.PLUS, "+", startPos, 1, newLines));
					pos++;
				}
				case '-' -> {
					char next = source.charAt(pos+1);
					if(next == '>') {
						tokens.add(new Token(Kind.RARROW, "->", startPos, 1, newLines));
						pos++;
					}
					else {
						tokens.add(new Token(Kind.MINUS, "-", startPos, 1, newLines));
					}
					pos++;
				}
				case '"' -> {
					state = State.IN_STRLIT;
					curr += '\"';
					pos++;
				}
				case '#' -> {
					state = State.IN_COMM;
					pos++;
				}
				case '*' -> {
					tokens.add(new Token(Kind.TIMES, "*", startPos, 1, newLines));
					pos++;
				}
				case '/' -> {
					tokens.add(new Token(Kind.DIV, "/", startPos, 1, newLines));
					pos++;
				}
				case '%' -> {
					tokens.add(new Token(Kind.MOD, "%", startPos, 1, newLines));
					pos++;
				}
				case '&' -> {
					tokens.add(new Token(Kind.AND, "&", startPos, 1, newLines));
					pos++;
				}
				case '|' -> {
					tokens.add(new Token(Kind.OR, "%", startPos, 1, newLines));
					pos++;
				}
				case '!' -> {
					char next = source.charAt(pos+1);
					if(next == '=') {
						tokens.add(new Token(Kind.NOT_EQUALS, "!=", startPos, 1, newLines));
						pos++;
					}
					else {
						tokens.add(new Token(Kind.BANG, "!", startPos, 1, newLines));
					}
					pos++;
				}
				case ';' -> {
					tokens.add(new Token(Kind.SEMI, ";", startPos, 1, newLines));
					pos++;
				}
				case ',' -> {
					tokens.add(new Token(Kind.COMMA, ",", startPos, 1, newLines));
					pos++;
				}
				case '^' -> {
					tokens.add(new Token(Kind.RETURN, "^", startPos, 1, newLines));
					pos++;
				}
				case 'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','_','$' -> {
					state = State.IN_IDENT;
					curr += ch;
					pos++;
				}
				case '=' -> {
					state = State.HAVE_EQ;
					curr += ch;
					pos++;
				}
				case '1','2','3','4','5','6','7','8','9' -> {
					state = State.IN_NUM;
					curr += ch;
					pos++;
				}
				case '0' -> {
					// check if we're at the end of the source
					if (pos == (source.length() - 1)) {
						tokens.add(new Token(Kind.EOF, "0", startPos, 1, newLines));
						return;
					} else {
						state = State.HAVE_ZERO;
						curr += ch;
						pos++;
					}
				}
				default -> {
					// an illegal token has been detected
					curr += ch;
					tokens.add(new Token(Kind.ERROR, curr, startPos, 1, newLines));
					pos++;
					curr = "";
				}
				}
			}
			case HAVE_ZERO -> { // check for dots
				switch(ch) {
				case '.' -> {
					state = State.HAVE_DOT;
					curr += ch;
					pos++;
				}
				default -> {
					// a dot was not detected after the zero, so it's only a 0
					// next char is not part of this token, so do not increment pos
					tokens.add(new Token(Kind.INT_LIT, curr, startPos, curr.length(), newLines));
					state = State.START;
					curr = "";
				}
				}
			}
			case IN_FLOAT -> {
				switch(ch) {
				case '0','1','2','3','4','5','6','7','8','9' -> {
					curr += ch;
					pos++;
				}
				default -> {
					// a float token has been detected
					tokens.add(new Token(Kind.FLOAT_LIT, curr, startPos, curr.length(), newLines));
					// next char is not part of this token, so do not increment pos
					state = State.START;
					curr = "";
				}
				}
			}
			case IN_NUM -> {
				switch(ch) {
				case '0','1','2','3','4','5','6','7','8','9' -> {
					curr += ch;
					pos++;
				}
				case '.' -> {
					state = State.HAVE_DOT;
					curr += ch;
					pos++;
				}
				default -> {
					// check if the integer is not above the maximum value
					try {
						int intVal = Integer.parseInt(curr);
						tokens.add(new Token(Kind.INT_LIT, curr, startPos, curr.length(), newLines));
						state = State.START;
						curr = "";
					}
					catch(NumberFormatException e) {
						tokens.add(new Token(Kind.ERROR, curr, startPos, curr.length(), newLines));
						state = State.START;
						curr = "";
					}
				}
				}
			}
			case HAVE_DOT -> {
				switch(ch) {
				case '0','1','2','3','4','5','6','7','8','9' -> {
					state = State.IN_FLOAT;
					curr += ch;
					pos++;
				}
				default -> {
					// there was no digit after the dot, so the token is not a float.
					tokens.add(new Token(Kind.ERROR, curr, startPos, curr.length(), newLines));
					// next char is not part of this token, so do not increment pos
					state = State.START;
					curr = "";
				}
				}
			}
			case HAVE_EQ -> {
				switch(ch) {
				case '=' -> {
					curr += ch;
					pos++;
					tokens.add(new Token(Kind.EQUALS, curr, startPos, curr.length(), newLines));
					state = State.START;
					curr = "";
				}
				default -> {
					// an assignment operator has been detected
					tokens.add(new Token(Kind.ASSIGN, curr, startPos, curr.length(), newLines));
					// next char is not part of this token, so do not increment pos
					state = State.START;
					curr = "";
				}
				}
			}
			case IN_IDENT -> {
				switch(ch) {
				case 'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','_','$','0','1','2','3','4','5','6','7','8','9' -> {
					// if the next character is the EOF
					if ((ch == '0') && (pos == (source.length() - 1))) {
						tokens.add(new Token(Kind.IDENT, curr, startPos, curr.length(), newLines));
						// return to the START state and handle the EOF character
						state = State.START;
						// next char is not part of this token, so do not increment pos
						curr = "";
					}
					else {
						curr += ch;
						pos++;
					}
				}
				default -> {
					
					// if the identifier is in the keywords map
					if(map.containsKey(curr)) {
						// adds token for the keyword
						tokens.add(new Token(map.get(curr), curr, startPos, curr.length(), newLines));
					}
					else {
						// else it is an identifier
						tokens.add(new Token(Kind.IDENT, curr, startPos, curr.length(), newLines));
					}
					// next char is not part of this token, so do not increment pos
					state = State.START;
					curr = "";
				}
				}
			}
			case IN_STRLIT -> {
				switch(ch) {
				case 'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',' ' -> {
					curr += ch;
					pos++;
				}
				case '"' -> {
					curr += ch;
					pos++;
					tokens.add(new Token(Kind.STRING_LIT, curr, startPos, curr.length(), newLines));
					state = State.START;
					curr = "";
				}
				default -> {
					throw new IllegalStateException("string literal bug");
				}
				}
			}
			case IN_COMM -> {
				switch(ch) {
				case '\n' -> {
					pos++;
					state = State.START;
				}
				default -> {
					pos++;
				}
				}
			}
			default -> throw new IllegalStateException("lexer bug");
			}
		}
	}

	// returns the next token in the token array list and increments pos
	public IToken next() throws LexicalException {
		// get the next token in the list
		int curr = index;
		index++;
		// if the next token is an illegal character, throw error
		if (tokens.get(curr).kind == Kind.ERROR) {
			throw new LexicalException("input bug");
		}
		else {
			return tokens.get(curr);
		}
	}

	// returns the next token in the token array list but does not increment pos
	public IToken peek() throws LexicalException {
		return null;
	}

}
