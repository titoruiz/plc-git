package edu.ufl.cise.plc.test;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import edu.ufl.cise.plc.CompilerComponentFactory;
import edu.ufl.cise.plc.ILexer;
import edu.ufl.cise.plc.IToken;
import edu.ufl.cise.plc.IToken.Kind;
import edu.ufl.cise.plc.LexicalException;


public class LexerTests {

	ILexer getLexer(String input){
		 return CompilerComponentFactory.getLexer(input);
	}
	
	//makes it easy to turn output on and off (and less typing than System.out.println)
	static final boolean VERBOSE = true;
	void show(Object obj) {
		if(VERBOSE) {
			System.out.println(obj);
		}
	}
	
	//check that this token has the expected kind
	void checkToken(IToken t, Kind expectedKind) {
		assertEquals(expectedKind, t.getKind());
	}
		
	//check that the token has the expected kind and position
	void checkToken(IToken t, Kind expectedKind, int expectedLine, int expectedColumn){
		assertEquals(expectedKind, t.getKind());
		assertEquals(new IToken.SourceLocation(expectedLine,expectedColumn), t.getSourceLocation());
	}
	
	//check that this token is an IDENT and has the expected name
	void checkIdent(IToken t, String expectedName){
		assertEquals(Kind.IDENT, t.getKind());
		assertEquals(expectedName, t.getText());
	}
	
	//check that this token is an IDENT, has the expected name, and has the expected position
	void checkIdent(IToken t, String expectedName, int expectedLine, int expectedColumn){
		checkIdent(t,expectedName);
		assertEquals(new IToken.SourceLocation(expectedLine,expectedColumn), t.getSourceLocation());
	}
	
	//check that this token is an INT_LIT with expected int value
	void checkInt(IToken t, int expectedValue) {
		assertEquals(Kind.INT_LIT, t.getKind());
		assertEquals(expectedValue, t.getIntValue());	
	}
	
	//check that this token  is an INT_LIT with expected int value and position
	void checkInt(IToken t, int expectedValue, int expectedLine, int expectedColumn) {
		checkInt(t,expectedValue);
		assertEquals(new IToken.SourceLocation(expectedLine,expectedColumn), t.getSourceLocation());		
	}
	
	//check that this token is the EOF token
	void checkEOF(IToken t) {
		checkToken(t, Kind.EOF);
	}
	

	//The lexer should add an EOF token to the end.
	@Test
	void testEmpty() throws LexicalException {
		String input = "";
		show(input);
		ILexer lexer = getLexer(input);
		checkEOF(lexer.next());
	}
	
	//A couple of single character tokens
	@Test
	void testSingleChar0() throws LexicalException {
		String input = """
				+ 
				- 	 
				""";
		show(input);
		ILexer lexer = getLexer(input);
		checkToken(lexer.next(), Kind.PLUS, 0,0);
		checkToken(lexer.next(), Kind.MINUS, 1,0);
		checkEOF(lexer.next());
	}
	
	//comments should be skipped
	@Test
	void testComment0() throws LexicalException {
		//Note that the quotes around "This is a string" are passed to the lexer.  
		String input = """
				"This is a string"
				#this is a comment
				*
				""";
		show(input);
		ILexer lexer = getLexer(input);
		checkToken(lexer.next(), Kind.STRING_LIT, 0,0);
		checkToken(lexer.next(), Kind.TIMES, 2,0);
		checkEOF(lexer.next());
	}
	
	//Example for testing input with an illegal character 
	@Test
	void testError0() throws LexicalException {
		String input = """
				abc
				@
				""";
		show(input);
		ILexer lexer = getLexer(input);
		//this check should succeed
		checkIdent(lexer.next(), "abc");
		//this is expected to throw an exception since @ is not a legal 
		//character unless it is part of a string or comment
		assertThrows(LexicalException.class, () -> {
			@SuppressWarnings("unused")
			IToken token = lexer.next();
		});
	}
	
	//Several identifiers to test positions
	@Test
	public void testIdent0() throws LexicalException {
		String input = """
				abc
				  def
				     ghi

				""";
		show(input);
		ILexer lexer = getLexer(input);
		checkIdent(lexer.next(), "abc", 0,0);
		checkIdent(lexer.next(), "def", 1,2);
		checkIdent(lexer.next(), "ghi", 2,5);
		checkEOF(lexer.next());
	}
	
	@Test
	public void testEquals0() throws LexicalException {
		String input = """
				= == ===
				""";
		show(input);
		ILexer lexer = getLexer(input);
		checkToken(lexer.next(),Kind.ASSIGN,0,0);
		checkToken(lexer.next(),Kind.EQUALS,0,2);
		checkToken(lexer.next(),Kind.EQUALS,0,5);
		checkToken(lexer.next(),Kind.ASSIGN,0,7);
		checkEOF(lexer.next());
	}
	
	@Test
	public void testIdenInt() throws LexicalException {
		String input = """
				a123 456b
				""";
		show(input);
		ILexer lexer = getLexer(input);
		checkIdent(lexer.next(), "a123", 0,0);
		checkInt(lexer.next(), 456, 0,5);
		checkIdent(lexer.next(), "b",0,8);
		checkEOF(lexer.next());
		}
	
	//example showing how to handle number that are too big.
	@Test
	public void testIntTooBig() throws LexicalException {
		String input = """
				42
				99999999999999999999999999999999999999999999999999999999999999999999999
				""";
		ILexer lexer = getLexer(input);
		checkInt(lexer.next(),42);
		Exception e = assertThrows(LexicalException.class, () -> {
			lexer.next();			
		});
	}
	
	// testing (<,<=,<,<-)
	@Test
	void testLess() throws LexicalException {
		String input = """
				<< <=<-<
				""";
		show(input);
		ILexer lexer = getLexer(input);
		checkToken(lexer.next(), Kind.LANGLE, 0,0);
		checkToken(lexer.next(), Kind.LE, 0,3);
		checkToken(lexer.next(), Kind.LARROW, 0,5);
		checkToken(lexer.next(), Kind.LT, 0,7);
		checkEOF(lexer.next());
	}
	
	// testing (>,>=,>)
	@Test
	void testGreater() throws LexicalException {
		String input = """
				>=> >>
				""";
		show(input);
		ILexer lexer = getLexer(input);
		checkToken(lexer.next(), Kind.GE, 0,0);
		checkToken(lexer.next(), Kind.GT, 0,2);
		checkToken(lexer.next(), Kind.RANGLE, 0,4);
		checkEOF(lexer.next());
	}
	
	// testing div and mod
	@Test
	void testDivMod() throws LexicalException {
		String input = """
				/%
				""";
		show(input);
		ILexer lexer = getLexer(input);
		checkToken(lexer.next(), Kind.DIV, 0,0);
		checkToken(lexer.next(), Kind.MOD, 0,1);
		checkEOF(lexer.next());
	}
	
	// testing and and or
	@Test
	void testAndOr() throws LexicalException {
		String input = """
				&|
				""";
		show(input);
		ILexer lexer = getLexer(input);
		checkToken(lexer.next(), Kind.AND, 0,0);
		checkToken(lexer.next(), Kind.OR, 0,1);
		checkEOF(lexer.next());
	}
	
	// testing bang and ne
	@Test
	void testBangNe() throws LexicalException {
		String input = """
				!!=
				""";
		show(input);
		ILexer lexer = getLexer(input);
		checkToken(lexer.next(), Kind.BANG, 0,0);
		checkToken(lexer.next(), Kind.NOT_EQUALS, 0,1);
		checkEOF(lexer.next());
	}

	// testing semi and comma
	@Test
	void testSemiComma() throws LexicalException {
		String input = """
				;,
				""";
		show(input);
		ILexer lexer = getLexer(input);
		checkToken(lexer.next(), Kind.SEMI, 0,0);
		checkToken(lexer.next(), Kind.COMMA, 0,1);
		checkEOF(lexer.next());
	}
	
	// testing minus and right arrow
	@Test
	void testMinusArrow() throws LexicalException {
		String input = """
				->-
				""";
		show(input);
		ILexer lexer = getLexer(input);
		checkToken(lexer.next(), Kind.RARROW, 0,0);
		checkToken(lexer.next(), Kind.MINUS, 0,2);
		checkEOF(lexer.next());
	}
	
	// testing return
	@Test
	void testReturn() throws LexicalException {
		String input = """
				^
				""";
		show(input);
		ILexer lexer = getLexer(input);
		checkToken(lexer.next(), Kind.RETURN, 0,0);
		checkEOF(lexer.next());
	}
	
	// test for boolean Kinds
	@Test
	public void testBooleanLit() throws LexicalException {
		String input = """
				true false
				""";
		ILexer lexer = getLexer(input);
		checkToken(lexer.next(), Kind.BOOLEAN_LIT, 0,0);
		checkToken(lexer.next(), Kind.BOOLEAN_LIT, 0,5);
		checkEOF(lexer.next());
	}
	
	// test for color const Kind
	@Test
	public void testColor() throws LexicalException {
		String input = """
				BLACK BLUE1
				""";
		ILexer lexer = getLexer(input);
		checkToken(lexer.next(), Kind.COLOR_CONST, 0,0);
		checkIdent(lexer.next(), "BLUE1", 0,6);
		checkEOF(lexer.next());
	}
	
	// test for keyword Kinds
	@Test
	public void testKeyword() throws LexicalException {
		String input = """
				if
				fi
				else
				write
				console
				void
				""";
		ILexer lexer = getLexer(input);
		checkToken(lexer.next(), Kind.KW_IF, 0,0);
		checkToken(lexer.next(), Kind.KW_FI, 1,0);
		checkToken(lexer.next(), Kind.KW_ELSE, 2,0);
		checkToken(lexer.next(), Kind.KW_WRITE, 3,0);
		checkToken(lexer.next(), Kind.KW_CONSOLE, 4,0);
		checkToken(lexer.next(), Kind.KW_VOID, 5,0);
		checkEOF(lexer.next());
	}
	
	// test for type Kind
	@Test
	public void testType() throws LexicalException {
		String input = """
				int float
				string boolean
				color image
				""";
		ILexer lexer = getLexer(input);
		checkToken(lexer.next(), Kind.TYPE, 0,0);
		checkToken(lexer.next(), Kind.TYPE, 0,4);
		checkToken(lexer.next(), Kind.TYPE, 1,0);
		checkToken(lexer.next(), Kind.TYPE, 1,7);
		checkToken(lexer.next(), Kind.TYPE, 2,0);
		checkToken(lexer.next(), Kind.TYPE, 2,6);
		checkEOF(lexer.next());
	}
	
	// test for color ops Kind
	@Test
	public void testColorOp() throws LexicalException {
		String input = """
				getRed getGreen
				getBlue
				""";
		ILexer lexer = getLexer(input);
		checkToken(lexer.next(), Kind.COLOR_OP, 0,0);
		checkToken(lexer.next(), Kind.COLOR_OP, 0,7);
		checkToken(lexer.next(), Kind.COLOR_OP, 1,0);
		checkEOF(lexer.next());
	}
	
	// test for image ops Kind
	@Test
	public void testImageOp() throws LexicalException {
		String input = """
				getWidth getHeight
				""";
		ILexer lexer = getLexer(input);
		checkToken(lexer.next(), Kind.IMAGE_OP, 0,0);
		checkToken(lexer.next(), Kind.IMAGE_OP, 0,9);
		checkEOF(lexer.next());
	}

}
