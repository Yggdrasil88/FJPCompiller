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
		expect("DOT");
	}

	public void blok() {
		System.out.println("blok");
		if (accept("CONST")) {
			defineConst();
			while(accept("COMMA")) {
				defineConst();
			}
			expect("SEMI");
		}
		if(accept("VAR")) {
			Token jmenoPromenne = getInput();
			while(accept("COMMA")) {
				jmenoPromenne = getInput();
			}
			expect("SEMI");
		}
		while(accept("PROC")) {
			Token jmenoMetody = getInput();
			expect("LBRAC");

			if(!input.equals("RBRAC")) {
				Token argument = getInput();
				while(accept("COMMA")) {
					argument = getInput();
				}
			}

			expect("RBRAC");
			blok();
		}
		prikaz();
		expect("RETURN");	//Zmena gramatiky "return" [vyraz] => "return" cislo
		Token returnValue = getInput();
	}

	private void prikaz() {
		System.out.println("prikaz");
		Token vstup = getInput();
		switch (vstup.getToken()) {
		case "CALL":
			Token jmenoFce = getInput();
			expect("LBRAC");
			if(isNextNumber()) {
				Token argument = getInput();
				while(accept("COMMA")) {
					argument = getInput();
				}
			}
			expect("RBRAC");
			break;
		case "BEGIN":
			prikaz();
			while(accept("SEMI")) {
				prikaz();
			}
			expect("END");
			break;
		case "IF":
			podminka();
			expect("THEN");
			prikaz();
			if(accept("ELSE")) {
				prikaz();
			}
			break;
		case "WHILE":
			podminka();
			expect("DO");
			prikaz();
			break;
		case "DO":
			prikaz();
			expect("WHILE");
			podminka();
			break;
		case "SWITCH":
			vyraz();
			oneCase();
			while(accept("SEMI")) {
				oneCase();
			}
			break;
		default:
			Token promenna = input;
			expect("ASSIGN");
			while(!isNextNumber()) {	//Zmena gramatiky {identifikator "="} vyraz => {identifikator "="} cislo
				promenna = getInput();
				expect("ASSIGN");
			}
			Token cislo = getInput();
			break;
		}
	}

	public void podminka() {
		System.out.println("podminka");
		if (accept("EXCL")) {
			expect("LBRAC");
			podminka();
			expect("RBRAC");
		} else {
			vyraz();
			String array[] = new String[] {"EQUAL", "DIFF", "LT", "GT", "LET", "GET", "AND", "OR"};
			expect(array);
			vyraz();
		}
	}

	public void vyraz() {
		System.out.println("vyraz");
		String array[] = new String[] {"PLUS", "MINUS"};
		if(accept(array)) {		//Zmena gramatiky ["+" | "-" | e] term => ("+" | "-") term
			term();
			while(accept(array)) {
				term();
			}
		} else {
			podminka();
			expect("QUEST");
			vyraz();
			expect("COLON");
			vyraz();
		}
	}

	public void term() {
		System.out.println("term");
		faktor();
		String array[] = new String[] {"TIMES", "DIVIDE"};
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
			case "CALL": 
				Token jmenoFce = getInput();
				expect("LBRAC");
				if(isNextNumber()) {
					Token argument = getInput();
					while(accept("COMMA")) {
						argument = getInput();
					}
				}
				expect("RBRAC");
				break;
			case "LBRAC":
				vyraz();
				expect("RBRAC");
			default:
				Token promenna = input;
			}
		}
	}

	public void oneCase() {
		expect("CASE");
		Token cislo = getInput();
		expect("COLON");
		prikaz();
	}

	public void defineConst() {
		Token jmenoKonstanty = getInput();
		expect("ASSIGN");
		while (!isNextNumber()) {
			jmenoKonstanty = input;
			getInput();
			expect("ASSIGN");
		}
		Token cislo = getInput();
	}

	public boolean isNextNumber() {
		return input.getToken().equals("INT");
	}
	
	public Token getInput() {
		Token oldInput = input;
		input = inputTokens.get(pivot);
		if (pivot < inputTokens.size() - 1) pivot++;
		
		System.out.println("   " + input.getToken());
		return oldInput;
	}

	private boolean expect(String token) {
		if(accept(token)) return true;
		System.out.println("Chybny vstup: " + token);
		return false;
	}

	private boolean expect(String tokens[]) {
		if(accept(tokens)) return true;
		System.out.println("Chybny vstup: " + Arrays.toString(tokens));
		return false;
	}

	private boolean accept(String token) {
		boolean result = (input.getToken().equals(token));
		if (result) getInput();
		return result;
	}

	private boolean accept(String tokens[]) {
		boolean result = false;
		int i = 0;
		while (i < tokens.length && !result) {
			result = tokens[i++].equals(input);
		}
		if (result) getInput();
		return result;
	}
}
