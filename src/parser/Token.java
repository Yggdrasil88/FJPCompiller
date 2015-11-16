package parser;

public class Token {
	public static final int CALL = 0;
	public static final int RETURN = 1;
	public static final int BEGIN = 2;
	public static final int END = 3;
	public static final int IF = 4;
	public static final int ELSE = 5;
	public static final int THEN = 6;
	public static final int WHILE = 7;
	public static final int DO = 8;
	public static final int SWITCH = 9;
	public static final int CASE = 10;
	public static final int PROC = 11;
	public static final int CONST = 12;
	public static final int VAR = 13;
	public static final int LBRAC = 14;
	public static final int RBRAC = 15;
	public static final int EXCL = 16;
	public static final int DOT = 17;
	public static final int COMMA = 18;
	public static final int SEMI = 19;
	public static final int ASSIGN = 20;
	public static final int EQUAL = 21;
	public static final int DIFF = 22;
	public static final int LT = 23;
	public static final int GT = 24;
	public static final int LET = 25;
	public static final int GET = 26;
	public static final int AND = 27;
	public static final int OR = 28;
	public static final int PLUS = 29;
	public static final int MINUS = 30;
	public static final int TIMES = 31;
	public static final int DIVIDE = 32;
	public static final int QUEST = 33;
	public static final int COLON = 34;
	public static final int IDENT = 35;
	public static final int INT = 36;
	
	private final String LEXEM;
	private final int TOKEN;

	public Token(String lexem, int token) {
		this.LEXEM = lexem;
		this.TOKEN = token;
	}

	public String getLexem() {
		return LEXEM;
	}
	
	public int getToken() {
		return TOKEN;
	}

	public static Token createToken(String lexem, String[] tokenStrings) {
		try {	//is number?
			if(lexem.substring(0, 1).equals("-")) 
				new Token(lexem, Token.INT); //negativni cislo
			Integer.parseInt(lexem.substring(0, 1));
			return new Token(lexem, Token.INT);
		} catch (NumberFormatException e) {
		}
		
		Token token = null;
		String lex = lexem.toLowerCase();	//is KEYWORD?
		for (int i = 0; i < tokenStrings.length; i++) {
			if (tokenStrings[i].equals(lex)) {
				token = new Token(lex, i);
			}
		}
		
		if (token == null) {	//is IDENT?
			token = new Token(lex, Token.IDENT);
		}
		
		return token;
	}
}
