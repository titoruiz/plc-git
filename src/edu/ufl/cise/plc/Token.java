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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SourceLocation getSourceLocation() {
		int[] arr = new int[newLines.size()];
		for (int i = 0; i < newLines.size(); i++) {
			arr[i] = newLines.get(i);
		}
		
		// use token position and length to find line and column
		int line;
		int column;
		SourceLocation loc = new SourceLocation(line, column);
		return null;
	}

	@Override
	public int getIntValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getFloatValue() {
		// TODO Auto-generated method stub
		return 0;
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
