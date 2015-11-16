package parser;

public class Token {
	public static final String TOKEN_STRING[] = new String[]{"call", "return", "begin", "end",
		"if", "else", "then", "while", "do", 
		"switch", "case", "procedure", "const", "var", "(", ")", "!", ".", ",", ";",
		"=", "==", "<>", "<", ">", "<=", ">=", "AND", "OR", 
		"+", "-", "*", "/", "?", ":"};
	public static final String TOKEN_MARK[] = new String[]{"CALL", "RETURN", "BEGIN", "END",
		"IF", "ELSE", "THEN", "WHILE", "DO", 
		"SWITCH", "CASE", "PROC", "CONST", "VAR", "LBRAC", "RBRAC", "EXCL", "DOT", "COMMA", "SEMI",
		"ASSIGN", "EQUAL", "DIFF", "LT", "GT", "LET", "GET", "AND", "OR", 
		"PLUS", "MINUS", "TIMES", "DIVIDE", "QUEST", "COLON",
		"IDENT", "INT"};
	
	private final String LEXEM;
	private final String TOKEN;
	private final int TOKEN_ID;

	public Token(String lexem, int tokenId) {
		this.LEXEM = lexem;
		this.TOKEN_ID = tokenId;
		this.TOKEN = TOKEN_MARK[tokenId];
	}

	public String getLexem() {
		return LEXEM;
	}
	
	public String getToken() {
		return TOKEN;
	}
	
	public int getTokenId() {
		return TOKEN_ID;
	}

	public static Token createToken(String lexem) {
		try {	//is number?
			Integer.parseInt(lexem.substring(0, 1));
			return new Token(lexem, TOKEN_STRING.length + 1);
		} catch (NumberFormatException e) {
		}
		
		Token token = null;
		String lex = lexem.toLowerCase();	//is KEYWORD?
		for (int i = 0; i < TOKEN_STRING.length; i++) {
			if (TOKEN_STRING[i].equals(lex)) {
				token = new Token(lex, i);
			}
		}
		
		if (token == null) {	//is IDENT?
			token = new Token(lex, TOKEN_STRING.length);
		}
		
		return token;
	}
}
