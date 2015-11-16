package parser;

import java.util.Arrays;
import java.util.List;

public class Parser {
	private List<Token> inputTokens;
	private Token input;
	private int pivot;
	private Node root;

	public Node parse(List<Token> tokens) {
		pivot = 0;
		input = null;
		inputTokens = tokens;
		program();
		return root;
	}

	public void program() {
		System.out.println("program");
		/*
		 * TODO
		 * generovani stromu
		 */
		
		getInput();
		blok();
		expect(Token.DOT);
	}

	public void blok() {
		System.out.println("blok");
		if (accept(Token.CONST)) {
			defineConst();
			while(accept(Token.COMMA)) {
				defineConst();
			}
			expect(Token.SEMI);
		}
		if(accept(Token.VAR)) {
			Token jmenoPromenne = getInput();
			while(accept(Token.COMMA)) {
				jmenoPromenne = getInput();
			}
			expect(Token.SEMI);
		}
		while(accept(Token.PROC)) {
			Token jmenoMetody = getInput();
			expect(Token.LBRAC);

			if(!input.equals(Token.RBRAC)) {
				Token argument = getInput();
				while(accept(Token.COMMA)) {
					argument = getInput();
				}
			}

			expect(Token.RBRAC);
			blok();
		}
		prikaz();
		expect(Token.RETURN);	//Zmena gramatiky "return" [vyraz] => "return" cislo
		Token returnValue = getInput();
	}

	private void prikaz() {
		System.out.println("prikaz");
		Token vstup = getInput();
		switch (vstup.getToken()) {
		case Token.CALL:
			Token jmenoFce = getInput();
			expect(Token.LBRAC);
			if(isNextNumber()) {
				Token argument = getInput();
				while(accept(Token.COMMA)) {
					argument = getInput();
				}
			}
			expect(Token.RBRAC);
			break;
		case Token.BEGIN:
			prikaz();
			while(accept(Token.SEMI)) {
				prikaz();
			}
			expect(Token.END);
			break;
		case Token.IF:
			podminka();
			expect(Token.THEN);
			prikaz();
			if(accept(Token.ELSE)) {
				prikaz();
			}
			break;
		case Token.WHILE:
			podminka();
			expect(Token.DO);
			prikaz();
			break;
		case Token.DO:
			prikaz();
			expect(Token.WHILE);
			podminka();
			break;
		case Token.SWITCH:
			vyraz();
			oneCase();
			while(accept(Token.SEMI)) {
				oneCase();
			}
			break;
		default:
			Token promenna = input;
			expect(Token.ASSIGN);
			while(!isNextNumber()) {	//Zmena gramatiky {identifikator "="} vyraz => {identifikator "="} cislo
				promenna = getInput();
				expect(Token.ASSIGN);
			}
			Token cislo = getInput();
			break;
		}
	}

	public void podminka() {
		System.out.println("podminka");
		if (accept(Token.EXCL)) {
			expect(Token.LBRAC);
			podminka();
			expect(Token.RBRAC);
		} else {
			vyraz();
			int array[] = new int[] {Token.EQUAL, Token.DIFF, Token.LT, Token.GT, Token.LET, Token.GET, Token.AND, Token.OR};
			expect(array);
			vyraz();
		}
	}

	public void vyraz() {
		System.out.println("vyraz");
		int array[] = new int[] {Token.PLUS, Token.MINUS};
		if(accept(array)) {		//Zmena gramatiky ["+" | "-" | e] term => ("+" | "-") term
			term();
			while(accept(array)) {
				term();
			}
		} else {
			podminka();
			expect(Token.QUEST);
			vyraz();
			expect(Token.COLON);
			vyraz();
		}
	}

	public void term() {
		System.out.println("term");
		faktor();
		int array[] = new int[] {Token.TIMES, Token.DIVIDE};
		while(accept(array)) {
			faktor();
		}
	}

	public void faktor() {
		System.out.println("faktor");
		if (isNextNumber()) {
			Token cislo = getInput();
		} else {
			Token vstup = getInput();
			switch (vstup.getToken()) {
			case Token.CALL: 
				Token jmenoFce = getInput();
				expect(Token.LBRAC);
				if(isNextNumber()) {
					Token argument = getInput();
					while(accept(Token.COMMA)) {
						argument = getInput();
					}
				}
				expect(Token.RBRAC);
				break;
			case Token.LBRAC:
				vyraz();
				expect(Token.RBRAC);
			default:
				Token promenna = input;
			}
		}
	}

	public void oneCase() {
		expect(Token.CASE);
		Token cislo = getInput();
		expect(Token.COLON);
		prikaz();
	}

	public void defineConst() {
		Token jmenoKonstanty = getInput();
		expect(Token.ASSIGN);
		while (!isNextNumber()) {
			jmenoKonstanty = input;
			getInput();
			expect(Token.ASSIGN);
		}
		Token cislo = getInput();
	}

	public boolean isNextNumber() {
		return (input.getToken() == Token.INT);
	}
	
	public Token getInput() {
		Token oldInput = input;
		input = inputTokens.get(pivot);
		if (pivot < inputTokens.size() - 1) pivot++;
		
		System.out.println("   " + input.getToken());
		return oldInput;
	}

	private boolean expect(int token) {
		if(accept(token)) return true;
		System.out.println("Chybny vstup: " + token);
		return false;
	}

	private boolean expect(int tokens[]) {
		if(accept(tokens)) return true;
		System.out.println("Chybny vstup: " + Arrays.toString(tokens));
		return false;
	}

	private boolean accept(int token) {
		boolean result = (input.getToken() == token);
		if (result) getInput();
		return result;
	}

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
