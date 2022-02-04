package edu.ufl.cise.plc;
import java.util.ArrayList;

import edu.ufl.cise.plc.IToken.Kind;

public class Lexer implements ILexer {
	ArrayList<Token> tokens;
	// index of tokens list
	int index;
	String source;
	int pos;
	ArrayList<Integer> newLines;
	private enum State {
		START,
		IN_IDENT,
		HAVE_ZERO,
		HAVE_DOT,
		IN_FLOAT,
		IN_NUM,
		HAVE_EQ,
		HAVE_MINUS,
	}

	public Lexer(String input) {
		// hold source code in a String
		tokens = new ArrayList<Token>();
		index = 0;
		source = input + '0';
		pos = 0;
		findNewLines(input);
		createTokens();
	}
	
	// create list of all new line instances
	private void findNewLines(String input) {
		newLines = new ArrayList<Integer>();
		for (int i = 0; i < input.length(); i++) {
			if (input.charAt(i) == '\n') {
				newLines.add(i);
			}
		}
	}
	
	private void createTokens() {
		State state = State.START;
		while (true) {
			char ch = source.charAt(pos);
			switch (state) {
			case START -> {
				int startPos = pos;
				switch (ch) {
				case ' ', '\n', '\t', '\r' -> {
					pos++;
				}
				case '+' -> {
					tokens.add(new Token(Kind.PLUS, "+", startPos, 1, newLines));
					pos++;
				}
				case '-' -> {
					tokens.add(new Token(Kind.MINUS, "-", startPos, 1, newLines));
					pos++;
				}
				case '0' -> {
					// end of the input
					tokens.add(new Token(Kind.EOF, "0", startPos, 1, newLines));
					return;
				}
				}
			}
			default -> throw new IllegalStateException("lexer bug");
			}
		}
		// if EOF has been reached
		//if (pos == source.length()) {
		//	tokens.add(new Token(Kind.EOF, "", 0, 0));
		//}
	}

	@Override
	public IToken next() throws LexicalException {
		// TODO Auto-generated method stub
		return tokens.get(index);
	}

	@Override
	public IToken peek() throws LexicalException {
		// TODO Auto-generated method stub
		return null;
	}

}
