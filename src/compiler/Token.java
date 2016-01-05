package compiler;

/**
 * Class represents a token.
 */
public class Token {
	/**
	 * Token CALL
	 */
	public static final int CALL = 0;
	/**
	 * Token RETURN
	 */
	public static final int RETURN = 1;
	/**
	 * Token BEGIN
	 */
	public static final int BEGIN = 2;
	/**
	 * Token END
	 */
	public static final int END = 3;
	/**
	 * Token IF
	 */
	public static final int IF = 4;
	/**
	 * Token ELSE
	 */
	public static final int ELSE = 5;
	/**
	 * Token THEN
	 */
	public static final int THEN = 6;
	/**
	 * Token WHILE
	 */
	public static final int WHILE = 7;
	/**
	 * Token DO
	 */
	public static final int DO = 8;
	/**
	 * Token SWITCH
	 */
	public static final int SWITCH = 9;
	/**
	 * Token CASE
	 */
	public static final int CASE = 10;
	/**
	 * Token PROC
	 */
	public static final int PROC = 11;
	/**
	 * Token CONST
	 */
	public static final int CONST = 12;
	/**
	 * Token VAR
	 */
	public static final int VAR = 13;
	/**
	 * Token LBRAC
	 */
	public static final int LBRAC = 14;
	/**
	 * Token RBRAC
	 */
	public static final int RBRAC = 15;
	/**
	 * Token EXCL
	 */
	public static final int EXCL = 16;
	/**
	 * Token DOT
	 */
	public static final int DOT = 17;
	/**
	 * Token COMMA
	 */
	public static final int COMMA = 18;
	/**
	 * Token SEMI
	 */
	public static final int SEMI = 19;
	/**
	 * Token ASSIGN
	 */
	public static final int ASSIGN = 20;
	/**
	 * Token EQUAL
	 */
	public static final int EQUAL = 21;
	/**
	 * Token DIFF
	 */
	public static final int DIFF = 22;
	/**
	 * Token LT
	 */
	public static final int LT = 23;
	/**
	 * Token GT
	 */
	public static final int GT = 24;
	/**
	 * Token LET
	 */
	public static final int LET = 25;
	/**
	 * Token GET
	 */
	public static final int GET = 26;
	/**
	 * Token AND
	 */
	public static final int AND = 27;
	/**
	 * Token OR
	 */
	public static final int OR = 28;
	/**
	 * Token PLUS
	 */
	public static final int PLUS = 29;
	/**
	 * Token MINUS
	 */
	public static final int MINUS = 30;
	/**
	 * Token TIMES
	 */
	public static final int TIMES = 31;
	/**
	 * Token DIVIDE
	 */
	public static final int DIVIDE = 32;
	/**
	 * Token QUEST
	 */
	public static final int QUEST = 33;
	/**
	 * Token COLON
	 */
	public static final int COLON = 34;
	/**
	 * Token IDENT
	 */
	public static final int IDENT = 35;
	/**
	 * Token INT
	 */
	public static final int INT = 36;
	
	/**
	 * Array with all possible tokens
	 */
	public static final String[] TOKEN_STRINGS = new String[] {"call", "return", "begin", "end", "if", "else", "then", "while", "do", 
		"switch", "case", "procedure", "const", "var", "(", ")", "!", ".", ",", ";", "=", "==", "<>", "<", ">", "<=", ">=", "and", "or", 
		"+", "-", "*", "/", "?", ":", "IDENT", "INT"};
	/**
	 * Lexem is original string with unidentified token
	 */
	private String lexem;
	/**
	 * Already identified lexem
	 */
	private final int TOKEN;
	/**
	 * Constructor
	 * @param lexem Original String with unidentified token
	 * @param token Already identified lexem
	 */
	public Token(String lexem, int token) {
		this.lexem = lexem;
		this.TOKEN = token;
	}
	/**
	 * Gets lexem
	 * @return lexem
	 */
	public String getLexem() {
		return lexem;
	}
	/**
	 * Gets token
	 * @return token
	 */
	public int getToken() {
		return TOKEN;
	}
	/**
	 * Parses lexem and identifies
	 * @param lexem lexem to identify
	 * @param tokenStrings array of keywords
	 * @return identified token
	 */
	public static Token createToken(String lexem, String[] tokenStrings) {
		//is number?
		try {
			Integer.parseInt(lexem.substring(0, 1));
			return new Token(lexem, Token.INT);
		} catch (NumberFormatException e) {
		}
		
		//is KEYWORD?
		Token token = null;
		String lex = lexem.toLowerCase();
		for (int i = 0; i < tokenStrings.length; i++) {
			if (tokenStrings[i].equals(lex)) {
				token = new Token(lex, i);
			}
		}
		
		//is IDENT?
		if (token == null) {
			token = new Token(lex, Token.IDENT);
		}
		
		return token;
	}
	/**
	 * Inverts positive int
	 */
	public void minusNumber() {
		if (this.TOKEN == Token.INT) {
			this.lexem = "-" + this.lexem;
		}
	}
}
