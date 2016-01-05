package compiler;

import java.util.List;
/**
 * Class for generating syntax tree
 */
public class Parser {
	/**
	 * Input tokens
	 */
	private List<Token> inputTokens;
	/**
	 * Newly loaded token
	 */
	private Token input;
	/**
	 * Last token
	 */
	private Token oldInput;
	/**
	 * Index to token array
	 */
	private int tokenIndex;
	/**
	 * Actually creating syntax tree node
	 */
	private TokenNode node;
	/**
	 * Parse the given token list
	 * @param tokens token list
	 * @return token node (root)
	 * @throws Exception Syntax error
	 */
	public TokenNode parse(List<Token> tokens) throws Exception {
		if (tokens == null || tokens.isEmpty()) ErrorHandler.progNotFound();
		tokenIndex = 0;
		input = null;
		oldInput = input;
		inputTokens = tokens;
		node = new TokenNode();
		program();
		return node;
	}
	/**
	 * Input point of program
	 * @throws Exception Syntax error
	 */
	public void program() throws Exception {
		getInput();
		block();
	}

	/**
	 * Parsing "block" part of program
	 * @throws Exception Syntax error
	 */
	public void block() throws Exception {
		if (accept(Token.CONST)) {
			/* Vrchol const, vetve prirazeni
			 *        const 
			 *   =      =      = 
			 * a   3  b   1  c   =
			 *                 d   7
			 */
			node = node.addChild(oldInput);
			defineConst();
			while(accept(Token.COMMA)) {
				defineConst();
			}
			expect(Token.SEMI);
			node = node.getParent();
		}
		if(accept(Token.VAR)) {
			/* Vrchol var, vetve identifikatory
			 *   var 
			 * a b c d
			 */
			node = node.addChild(oldInput);
			expect(Token.IDENT);
			Token varName = oldInput;
			node.addChild(varName);
			while(accept(Token.COMMA)) {
				expect(Token.IDENT);
				varName = oldInput;
				node.addChild(varName);
			}
			expect(Token.SEMI);
			node = node.getParent();
		}
		while(accept(Token.PROC)) {
			/*
			 * Vrchol proc, vlevo jmeno, pod nim argumenty, vpravo blok
			 *        proc
			 *   fce       blok()
			 * a     b
			 */
			node = node.addChild(oldInput);
			expect(Token.IDENT);
			Token funcName = oldInput;
			node = node.addChild(funcName);
			expect(Token.LBRAC);

			if(!accept(Token.RBRAC)) {
				expect(Token.IDENT);
				Token argument = oldInput;
				node.addChild(argument);
				while(accept(Token.COMMA)) {
					expect(Token.IDENT);
					argument = oldInput;
					node.addChild(argument);
				}
				expect(Token.RBRAC);
			}
			node = node.getParent();
			block();
			node = node.getParent();
		}
		if (!accept(Token.RETURN)) {
			statement();
			expect(Token.RETURN);
		}
		node = node.addChild(oldInput);
		if(!accept(Token.SEMI)) {
			node.addChild(expression());
			expect(Token.SEMI);
		}
		node = node.getParent();
	}

	/**
	 * Parsing "statement" part of program
	 * @throws Exception Syntax error
	 */
	private void statement() throws Exception {
		Token input = getInput();
		switch (input.getToken()) {
		case Token.CALL:
			/*
			 * Vrchol call, pod nim jmeno fce, pod nim argumenty
			 *   call
			 *   fce  
			 * 5  13  2
			 */
			node = node.addChild(input);
			expect(Token.IDENT);
			Token funcName = oldInput;
			TokenNode hlp = node.addChild(funcName);
			expect(Token.LBRAC);
			if(!accept(Token.RBRAC)) {
				hlp.addChild(expression());
				while(accept(Token.COMMA)) {
					hlp.addChild(expression());
				}
				expect(Token.RBRAC);
			}
			node = node.getParent();
			expect(Token.SEMI);
			break;
		case Token.BEGIN:
			/*
			 * Vice prikazu v bloku - prikazy vedle sebe
			 */
			statement();
			while(!accept(Token.END)) {
				statement();
			}
			break;
		case Token.IF:
			/*
			 * Vrchol if vlevo podminka, uprostred then, vpravo else (pokud existuje)
			 *        if
			 * podm    then      (else)
			 *        P1   P2    P3   P4
			 * 
			 */
			node = node.addChild(oldInput);
			node.addChild(condition());
			expect(Token.THEN);
			node = node.addChild(oldInput);
			statement();
			node = node.getParent();
			if(accept(Token.ELSE)) {
				node = node.addChild(oldInput);
				statement();
				node = node.getParent();
			}
			node = node.getParent();
			break;
		case Token.WHILE:
			/*
			 * Vrchol while, vlevo podminka, vpravo prikaz
			 *     while
			 * podm     DO
			 *        P1  P2
			 */
			node = node.addChild(oldInput);
			node.addChild(condition());
			expect(Token.DO);
			node = node.addChild(oldInput);
			statement();
			node = node.getParent();
			node = node.getParent();
			break;
		case Token.DO:
			/*
			 * Vrchol DO, vlevo prikaz, vpravo while
			 *         DO
			 *     DO      podm
			 *   P1  P2
			 */
			node = node.addChild(oldInput);
			node = node.addChild(oldInput);
			statement();
			node = node.getParent();
			expect(Token.WHILE);
			node.addChild(condition());
			node = node.getParent();
			expect(Token.SEMI);
			break;
		case Token.SWITCH:
			/*
			 * Vrchol switch, vlevo vyraz, vpravo hodnoty, pod nimi prikazy
			 *    switch
			 * a    1      2
			 *     prik   prik
			 */
			node = node.addChild(oldInput);
			node.addChild(expression());
			oneCase();
			while(accept(Token.COMMA)) {
				oneCase();
			}
			node = node.getParent();
			break;
		default:
			/*
			 * Vrchol rovnitko, vlevo promenna, vpravo hodnota
			 *    =            =
			 * a     =       d   9
			 *     c   3
			 */
			Token var = oldInput;
			TokenNode root = node;
			expect(Token.ASSIGN);
			node = node.addChild(oldInput);
			node.addChild(var);
			while (tokenIndex + 1 <= inputTokens.size() && inputTokens.get(tokenIndex).getToken() == Token.ASSIGN) {
				expect(Token.IDENT);
				var = oldInput;
				expect(Token.ASSIGN);
				node = node.addChild(oldInput);
				node.addChild(var);
			}
			
			node.addChild(expression());
			node = root;
			expect(Token.SEMI);
			break;
		}
	}

	/**
	 * Parsing "condition" part of program
	 * @return Newly created token node
	 * @throws Exception Syntax error
	 */
	public TokenNode condition() throws Exception {
		TokenNode condition = null;
		if (accept(Token.EXCL)) {
			/*
			 * Vrchol vykricnik, pod nim podminka
			 *  !
			 * pom()
			 */
			condition = new TokenNode(oldInput);
			expect(Token.LBRAC);
			condition.addChild(condition());
			expect(Token.RBRAC);
		} else {
			/*
			 * Vrchol podminka, pod nim vyrazy
			 *   <
			 * a   >
			 *   b   c
			 */
			TokenNode left = expression();
			int array[] = new int[] {Token.EQUAL, Token.DIFF, Token.LT, Token.GT, Token.LET, Token.GET, Token.AND, Token.OR};
			expect(array);
			condition = new TokenNode(oldInput);
			TokenNode right = expression();
			condition.addChild(left);
			condition.addChild(right);
		}
		return condition;
	}

	/**
	 * Parsing "expression" part of program
	 * @return Newly created token node
	 * @throws Exception Syntax error
	 */
	public TokenNode expression() throws Exception {
		TokenNode expression = null; 
		if(accept(Token.QUEST)) {
			/*
			 * Vrchol otaznik, vlevo true, vpravo false
			 *        ?
			 * a > b  3   5
			 */
			TokenNode cond = condition();
			expect(Token.QUEST);
			expression = new TokenNode(oldInput);
			expression.addChild(cond);
			expression.addChild(expression());
			expect(Token.COLON);
			expression.addChild(expression());
		} 
		else {
			/*
			 * Vrchol znamenko, vlevo term, vpravo term nebo znamenko
			 * term - term1 + term2 - term3 =>
			 *         -
			 * term         +
			 *       term1       -
			 *             term2   term3
			 */
			TokenNode root = null;
			TokenNode left = term();
			int array[] = new int[] {Token.PLUS, Token.MINUS};
			while(accept(array)) {
				if(expression == null) {
					expression = new TokenNode(oldInput);
					root = expression;
				}
				else expression = expression.addChild(oldInput);
				expression.addChild(left);
				left = term();
			}
			if(expression == null) {
				expression = left;
				root = expression;
			}
			else expression.addChild(left);
			expression = root;
		}
		return expression;
	}

	/**
	 * Parsing "term" part of program
	 * @return Newly created token node
	 * @throws Exception Syntax error
	 */
	public TokenNode term() throws Exception {
		/*
		 * Vrchol cislo, nebo znamenko a vlevo cislo a vpravo dalsi znamenka a cisla
		 * a * b / c =>
		 *    *
		 * a     /
		 *     b   c
		 */
		TokenNode root = null;
		TokenNode term = null;
		TokenNode left = factor();
		int array[] = new int[] {Token.TIMES, Token.DIVIDE};
		while(accept(array)) {
			if(term == null) {
				term = new TokenNode(oldInput);
				root = term;
			}
			else term = term.addChild(oldInput);
			term.addChild(left);
			left = factor();
		}
		if(term == null) {
			term = left;
			root = term;
		}
		else term.addChild(left);
		term = root;
		return term;
	}

	/**
	 * Parsing "factor" part of program
	 * @return Newly created token node
	 * @throws Exception Syntax error
	 */
	public TokenNode factor() throws Exception {
		TokenNode factor = null;
		if (input.getToken() == Token.MINUS) {
			/*
			 * Minus cislo
			 */
			expect(Token.MINUS);
			expect(Token.INT);
			Token number = oldInput;
			number.minusNumber();
			factor = new TokenNode(number);
		}
		else if (isNextNumber()) {
			/*
			 * Jen cislo
			 */
			expect(Token.INT);
			Token number = oldInput;
			factor = new TokenNode(number);
		} else {
			Token input = getInput();
			switch (input.getToken()) {
			case Token.CALL: 
				/*
				 * Vrchol Call, pod nim jmeno fce, pod nim argumenty
				 *  call
				 *  fce
				 * 1   3
				 */
				factor = new TokenNode(input);
				expect(Token.IDENT);
				Token funcName = oldInput;
				TokenNode hlp = factor.addChild(funcName);
				expect(Token.LBRAC);
				if(!accept(Token.RBRAC)) {
					hlp.addChild(expression());
					while(accept(Token.COMMA)) {
						hlp.addChild(expression());
					}
					expect(Token.RBRAC);
				}
				break;
			case Token.LBRAC:
				/*
				 * Vracime vyraz
				 */
				factor = expression();
				expect(Token.RBRAC);
				break;
			default:
				/*
				 * Vracime jmeno promenne
				 */
				factor = new TokenNode(input);
			}
		}
		return factor;
	}

	/**
	 * Parsing one case of switch statement
	 * @throws Exception Syntax error
	 */
	public void oneCase() throws Exception {
		/*
		 * Vrchol cislo, pod nim prikazy
		 *   5
		 * prikaz
		 */
		expect(Token.CASE);
		if (accept(Token.MINUS)) {
			/*
			 * Minus cislo
			 */
			expect(Token.INT);
			Token number = oldInput;
			number.minusNumber();
			node = node.addChild(number);
		}
		else {
			expect(Token.INT);
			Token number = oldInput;
			node = node.addChild(number);
		}
		expect(Token.COLON);
		statement();
		node = node.getParent();
	}

	/**
	 * Parsing constant statement
	 * @throws Exception Syntax error
	 */
	public void defineConst() throws Exception {
		/*
		 * Vrchol prirazeni, vlevo ident, vpravo ident nebo hodnota
		 * a = c = 5 =>
		 *    =
		 * a     =
		 *     c   5
		 */
		TokenNode constant = node;
		expect(Token.IDENT);
		Token constName = oldInput;
		if (constName.getToken() != Token.IDENT) ErrorHandler.notConst(constName.getLexem());
		expect(Token.ASSIGN);
		node = node.addChild(oldInput);
		node.addChild(constName);
		while (!isNextNumber() && input.getToken() != Token.MINUS) {
			expect(Token.IDENT);
			constName = oldInput;
			expect(Token.ASSIGN);
			node = node.addChild(oldInput);
			node.addChild(constName);
		}

		if (accept(Token.MINUS)) {
			/*
			 * Minus cislo
			 */
			expect(Token.INT);
			Token number = oldInput;
			number.minusNumber();
			node.addChild(number);
		}
		else {
			expect(Token.INT);
			Token number = oldInput;
			node.addChild(number);
		}
		node = constant;
	}

	/**
	 * Check if new token is number
	 * @return True if new token is number
	 */
	public boolean isNextNumber() {
		return (input.getToken() == Token.INT);
	}

	/**
	 * Read new token from token list
	 * @return Old input
	 */
	public Token getInput() {
		oldInput = input;
		input = inputTokens.get(tokenIndex);
		if (tokenIndex < inputTokens.size() - 1) tokenIndex++;
		return oldInput;
	}
	
	/**
	 * Check if actual token equals expected token, read new token
	 * @param token Expected token
	 * @return True if equal
	 * @throws Exception Exception if token not equal
	 */
	private boolean expect(int token) throws Exception {
		if(accept(token)) return true;
		ErrorHandler.parserError(token, tokenIndex);
		return false;
	}

	/**
	 * Check if actual token equals one of expected tokens, read new token
	 * @param tokens Expected token
	 * @return True if equal
	 * @throws Exception Exception if token not equal
	 */
	private boolean expect(int tokens[]) throws Exception {
		if(accept(tokens)) return true;
		ErrorHandler.parserError(tokens, tokenIndex);
		return false;
	}

	/**
	 * Check if actual token equals tested token, if so, read new token
	 * @param token Tested token
	 * @return True if equal
	 */
	private boolean accept(int token) {
		boolean result = (input.getToken() == token);
		if (result) getInput();
		return result;
	}
	/**
	 * Check if actual token equals one of tested tokens, if so, read new token
	 * @param tokens Tested tokens
	 * @return True if equal
	 */
	private boolean accept(int tokens[]) {
		boolean result = false;
		int i = 0;
		while (i < tokens.length && !result) {
			result = (tokens[i++] == input.getToken());
		}
		if (result) getInput();
		return result;
	}
}
