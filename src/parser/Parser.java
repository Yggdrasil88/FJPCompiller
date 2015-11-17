package parser;

import java.util.Arrays;
import java.util.List;

public class Parser {
	private List<Token> inputTokens;
	private Token input;
	private Token oldInput;
	private int pivot;
	private Node root;
	private Node node;

	public Node parse(List<Token> tokens) {
		pivot = 0;
		input = null;
		oldInput = input;
		inputTokens = tokens;
		root = new Node();
		node = root;

		program();
		return root;
	}

	public void program() {
		getInput();
		blok();
	}

	public void blok() {
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
			Token jmenoPromenne = getInput();
			node.addChild(jmenoPromenne);
			while(accept(Token.COMMA)) {
				jmenoPromenne = getInput();
				node.addChild(jmenoPromenne);
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
			Token jmenoMetody = getInput();
			node = node.addChild(jmenoMetody);
			expect(Token.LBRAC);

			if(!accept(Token.RBRAC)) {
				Token argument = getInput();
				node.addChild(argument);
				while(accept(Token.COMMA)) {
					argument = getInput();
					node.addChild(argument);
				}
				expect(Token.RBRAC);
			}
			node = node.getParent();
			blok();
			node = node.getParent();
		}
		if (!accept(Token.RETURN)) {
			prikaz();
			expect(Token.RETURN);
		}
		node = node.addChild(oldInput);
		if(!accept(Token.SEMI)) {
			node.addChild(vyraz());
			expect(Token.SEMI);
		}
		node = node.getParent();
	}

	private void prikaz() {
		Token vstup = getInput();
		switch (vstup.getToken()) {
		case Token.CALL:
			/*
			 * Vrchol call, vlevo jmeno fce, vpravo argumenty
			 *      call
			 * fce  5  13  2
			 */
			node = node.addChild(vstup);
			Token jmenoFce = getInput();
			node.addChild(jmenoFce);
			expect(Token.LBRAC);
			if(!accept(Token.RBRAC)) {
				node.addChild(vyraz());
				while(accept(Token.COMMA)) {
					node.addChild(vyraz());
				}
				expect(Token.RBRAC);
			}
			node = node.getParent();
			break;
		case Token.BEGIN:
			/*
			 * Vice prikazu v bloku - prikazy vedle sebe
			 */
			prikaz();
			while(accept(Token.SEMI)) {
				prikaz();
			}
			expect(Token.END);
			break;
		case Token.IF:
			/*
			 * Vrchol if vlevo podminka, uprostred then, vpravo else (pokud existuje)
			 *        if
			 * podm  then  (else)
			 * 
			 */
			node = node.addChild(oldInput);
			node.addChild(podminka());
			expect(Token.THEN);
			prikaz();
			if(accept(Token.ELSE)) {
				prikaz();
			}
			node = node.getParent();
			break;
		case Token.WHILE:
			/*
			 * Vrchol while, vlevo podminka, vpravo prikaz
			 *     while
			 * podm     prikaz
			 */
			node = node.addChild(oldInput);
			node.addChild(podminka());
			expect(Token.DO);
			prikaz();
			node = node.getParent();
			break;
		case Token.DO:
			/*
			 * Vrchol DO, vlevo prikaz, vpravo while
			 *        do
			 * prikaz    podm
			 */
			node = node.addChild(oldInput);
			prikaz();
			expect(Token.WHILE);
			node.addChild(podminka());
			node = node.getParent();
			break;
		case Token.SWITCH:
			/*
			 * Vrchol switch, vlevo vyraz, vpravo hodnoty, pod nimi prikazy
			 *    switch
			 * a    1      2
			 *     prik   prik
			 */
			node = node.addChild(oldInput);
			node.addChild(vyraz());
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
			Token promenna = oldInput;
			Node vrchol = node;
			expect(Token.ASSIGN);
			node = node.addChild(oldInput);
			node.addChild(promenna);
			while(!accept(Token.ASSIGN)) {
				promenna = getInput();
				expect(Token.ASSIGN);
				node = node.addChild(oldInput);
				node.addChild(promenna);
			}
			node.addChild(vyraz());
			node = vrchol;
			break;
		}
	}

	public Node podminka() {
		Node podminka = null;
		if (accept(Token.EXCL)) {
			/*
			 * Vrchol vykricnik, pod nim podminka
			 *  !
			 * pom()
			 */
			podminka = new Node(oldInput);
			expect(Token.LBRAC);
			podminka.addChild(podminka());
			expect(Token.RBRAC);
		} else {
			/*
			 * Vrchol podminka, pod nim vyrazy
			 *   <
			 * a   >
			 *   b   c
			 */
			Node leva = vyraz();
			int array[] = new int[] {Token.EQUAL, Token.DIFF, Token.LT, Token.GT, Token.LET, Token.GET, Token.AND, Token.OR};
			expect(array);
			podminka = new Node(oldInput);
			Node prava = vyraz();
			podminka.addChild(leva);
			podminka.addChild(prava);
		}
		return podminka;
	}

	public Node vyraz() {
		Node vyraz = null; 
		if(accept(Token.QUEST)) {
			/*
			 * Vrchol otaznik, vlevo true, vpravo false
			 *        ?
			 * a > b  3   5
			 */
			Node podm = podminka();
			expect(Token.QUEST);
			vyraz = new Node(oldInput);
			vyraz.addChild(podm);
			vyraz.addChild(vyraz());
			expect(Token.COLON);
			vyraz.addChild(vyraz());
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
			Node vrchol = null;
			Node leva = term();
			int array[] = new int[] {Token.PLUS, Token.MINUS};
			while(accept(array)) {
				if(vyraz == null) {
					vyraz = new Node(oldInput);
					vrchol = vyraz;
				}
				else vyraz = vyraz.addChild(oldInput);
				vyraz.addChild(leva);
				leva = term();
			}
			if(vyraz == null) {
				vyraz = leva;
				vrchol = vyraz;
			}
			else vyraz.addChild(leva);
			vyraz = vrchol;
		}
		return vyraz;
	}

	public Node term() {
		/*
		 * Vrchol cislo, nebo znamenko a vlevo cislo a vpravo dalsi znamenka a cisla
		 * a * b / c =>
		 *    *
		 * a     /
		 *     b   c
		 */
		Node vrchol = null;
		Node term = null;
		Node leva = faktor();
		int array[] = new int[] {Token.TIMES, Token.DIVIDE};
		while(accept(array)) {
			if(term == null) {
				term = new Node(oldInput);
				vrchol = term;
			}
			else term = term.addChild(oldInput);
			term.addChild(leva);
			leva = faktor();
		}
		if(term == null) {
			term = leva;
			vrchol = term;
		}
		else term.addChild(leva);
		term = vrchol;
		return term;
	}

	public Node faktor() {
		Node faktor = null;
		if (input.getToken() == Token.MINUS) {
			/*
			 * Minus cislo
			 */
			getInput();
			Token cislo = getInput();
			cislo.minusNumber();
			faktor = new Node(cislo);
		}
		else if (isNextNumber()) {
			/*
			 * Jen cislo
			 */
			Token cislo = getInput();
			faktor = new Node(cislo);
		} else {
			Token vstup = getInput();
			switch (vstup.getToken()) {
			case Token.CALL: 
				/*
				 * Vrchol Call, vlevo jmeno fce, vpravo parametry
				 *     call
				 * fce   1   3
				 */
				faktor = new Node(vstup);
				Token jmenoFce = getInput();
				faktor.addChild(jmenoFce);
				expect(Token.LBRAC);
				if(!accept(Token.RBRAC)) {
					faktor.addChild(vyraz());
					while(accept(Token.COMMA)) {
						faktor.addChild(vyraz());
					}
					expect(Token.RBRAC);
				}
				break;
			case Token.LBRAC:
				/*
				 * Vracime vyraz
				 */
				faktor = vyraz();
				expect(Token.RBRAC);
				break;
			default:
				/*
				 * Vracime jmeno promenne
				 */
				faktor = new Node(vstup);
			}
		}
		return faktor;
	}

	public void oneCase() {
		/*
		 * Vrchol cislo, pod nim prikazy
		 *   5
		 * prikaz
		 */
		expect(Token.CASE);
		Token cislo = getInput();
		node = node.addChild(cislo);
		expect(Token.COLON);
		prikaz();
		node = node.getParent();
	}

	public void defineConst() {
		/*
		 * Vrchol prirazeni, vlevo ident, vpravo ident nebo hodnota
		 * a = c = 5 =>
		 *    =
		 * a     =
		 *     c   5
		 */
		Node konstanta = node;
		Token jmenoKonstanty = getInput();
		expect(Token.ASSIGN);
		node = node.addChild(oldInput);
		node.addChild(jmenoKonstanty);
		while (!isNextNumber()) {
			jmenoKonstanty = input;
			getInput();
			expect(Token.ASSIGN);
			node = node.addChild(oldInput);
			node.addChild(jmenoKonstanty);
		}
		Token cislo = getInput();
		node.addChild(cislo);
		node = konstanta;
	}

	public boolean isNextNumber() {
		return (input.getToken() == Token.INT);
	}

	public Token getInput() {
		oldInput = input;
		input = inputTokens.get(pivot);
		if (pivot < inputTokens.size() - 1) pivot++;
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
