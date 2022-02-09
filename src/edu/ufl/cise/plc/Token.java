package edu.ufl.cise.plc;

import java.util.ArrayList;

public class Token implements IToken {
	public Kind kind;
	public String text;
	public int pos;
	public int length;
	public ArrayList<Integer> newLines;
	
	public Token(Kind _kind, String _text, int _pos, int _length, ArrayList<Integer> _newLines) {
		kind = _kind;
		text = _text;
		pos = _pos;
		length = _length;
		newLines = _newLines;
	}
	
	@Override
	public Kind getKind() {
		// saves the kind of token this represents
		return kind;
	}

	@Override
	public String getText() {
		// return the text of the token
		return text;
	}

	@Override
	public SourceLocation getSourceLocation() {
		// use token position and length to find line and column
		int line = 0;
		int column = 0;
		for (int i = 0; i < newLines.size(); i++) {
			if (pos < newLines.get(i)) {
				line = i;
				if (line == 0) {
					column = pos;
					break;
				} else {
					column = pos - newLines.get(i-1) - 1;
					break;
				}
			}
		}
		SourceLocation loc = new SourceLocation(line, column);
		return loc;
	}

	@Override
	public int getIntValue() {
		// return integer value of string
		return Integer.parseInt(text);
	}

	@Override
	public float getFloatValue() {
		// return float value of string
		return Float.valueOf(text).floatValue();
	}

	@Override
	public boolean getBooleanValue() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getStringValue() {
		// TODO Auto-generated method stub
		return null;
	}

}
