package parser;


public class Parser {
	private String word;	//Pomocny buffer pro aktualne nacitane slovo
	private String input;	//Aktualni vstup - klicove slovo nebo retezec
	
	private static final String KEY_WORDS_STANDALONE[] = new String[]{"call", "return", "begin", "end", "if", "else", "then", "while", "do", 
		"switch", "case", "procedure", "const", "var", "(", ")", "!", ".", ",", ";", "=", "==", "<>", "<", ">", "<=", ">=", "AND", "OR", 
		"+", "-", "*", "/", "?", ":"};
	private static final String KEY_WORDS_MIDDLE[] = new String[]{"(", ")", "!", ".", ",", ";", "==", "=", "<>", "<", ">", "<=", ">=", "AND", "OR", 
		"+", "-", "*", "/", "?", ":"};

	private String getInput() {
		String oldInput = input; //Aktualni vstup, bude vracen
		
		String inputStream = "xxx";	//Vstup. proud s textem
		if (word == null) {
			word = "xxx"; //Pomocny buffer se vstupnim slovem - vyraz oddeleny mezerami ziskany ze vstupniho proudu
		}
		
		if (equals(KEY_WORDS_STANDALONE, word)) {	//Test, jestli je vyraz klicove slovo
			input = word;		//Pokud ano, stane se novym vstupem
			word = null;
		} else {
			String splitter = contains(KEY_WORDS_MIDDLE, word);
			if (splitter != null) { //Test, jestli je cast vyrazu klicove slovo (napr. !(a+b) ), je vracen klicovy vyraz, jinak null
				int index = splitter.indexOf(splitter);
				if (index == 0) {	//Klicove slovo zacina od prvniho znaku
					input = splitter;	//Vstupem je klicovy vyraz
					word = word.substring(splitter.length() - 1, word.length());	//Oddelime a zbytek vyrazu ponechame na pozdeji
				} else {	//Klicove slovo je v textu az dale
					input = word.substring(0, index + 1);	//Text pred klicovym slovem pouzijeme
					word = word.substring(index); //Klicove slovo a vse za nim ponechame na pozdeji
				}
			} else {	//Zadna cast vyrazu neni klicove slovo, nacteme cely vyraz
				input = word;
				word = null;
			}	
		}
		return oldInput;
	}
	
	private boolean equals(String array[], String word) {
		for (int i = 0; i < array.length; i++) {
			if (array[i].equals(word)) return true;
		}
		return false;
	}
	
	private String contains(String array[], String word) {
		/*
		 * Tohle by MELO prochazet slovo od zacatku a pro kazdy znak(znaky) zkouset, jestli se shoduji s necim ze zadaneho pole 
		 * (pocet zkousenych znaku je dan velikosti momentalne kontrolovaneho vyrazu z pole)
		 * Vratit by to melo symbol z pole, ktery je v danem slove nejblize zacatku, je potreba dat pozor na poradi znaku v poli (= vs == => == musi byt prvni)
		 */
		for (int i = 0; i < word.length(); i++) {
			for (int j = 0; j < array.length; j++) {
				if (i + array[j].length() <= word.length()) {
					if (word.substring(i, i + array[j].length()).equals(array[j])) return array[j];
				}
			}
		}
		return null;
	}

	private boolean expect(String string) {
		if(accept(string)) return true;
		System.out.println("Chyba");
		return false;
	}

	private boolean expect(String array[]) {
		if(accept(array)) return true;
		System.out.println("Chyba");
		return false;
	}

	private boolean accept(String string) {
		boolean result = (string == input);
		if (result) getInput();
		return result;
	}

	private boolean accept(String array[]) {
		boolean result = false;
		int i = 0;
		while (i < array.length && !result) {
			result = array[i++].equals(input);
		}
		if (result) getInput();
		return result;
	}

	public void program() {
		getInput();
		blok();
		expect(".");
	}

	public void blok() {
		if (accept("const")) {
			defineConst();
			while(accept(",")) {
				defineConst();
			}
			expect(";");
		}
		if(accept("var")) {
			String jmenoPromenne = getInput();
			while(accept(",")) {
				jmenoPromenne = getInput();
			}
			expect(";");
		}
		while(accept("procedure")) {
			String jmenoMetody = getInput();
			expect("(");

			if(!input.equals(")")) {
				String argument = getInput();
				while(accept(",")) {
					argument = getInput();
				}
			}

			expect(")");
			blok();
		}
		prikaz();
		expect("return");	//Zmena gramatiky "return" [vyraz] => "return" cislo
		int returnValue = Integer.parseInt(getInput());
	}

	private void prikaz() {
		String vstup = getInput();
		switch (vstup) {
		case "call":
			String jmenoFce = getInput();
			expect("(");
			if(isNextNumber()) {
				int argument = Integer.parseInt(getInput());
				while(accept(",")) {
					argument = Integer.parseInt(getInput());
				}
			}
			expect(")");
			break;
		case "begin":
			prikaz();
			while(accept(";")) {
				prikaz();
			}
			expect("end");
			break;
		case "if":
			podminka();
			expect("then");
			prikaz();
			if(accept("else")) {
				prikaz();
			}
			break;
		case "while":
			podminka();
			expect("do");
			prikaz();
			break;
		case "do":
			prikaz();
			expect("while");
			podminka();
			break;
		case "switch":
			vyraz();
			oneCase();
			while(accept(";")) {
				oneCase();
			}
			break;
		default:
			String promenna = input;
			expect("=");
			while(!isNextNumber()) {	//Zmena gramatiky {identifikator "="} vyraz => {identifikator "="} cislo
				promenna = getInput();
				expect("=");
			}
			int cislo = Integer.parseInt(getInput());
			break;
		}
	}

	public void podminka() {
		if (accept("!")) {
			expect("(");
			podminka();
			expect(")");
		} else {
			vyraz();
			String array[] = new String[] {"==", "<>", "<", ">", "<=", ">=", "AND", "OR"};
			expect(array);
			vyraz();
		}
	}

	public void vyraz() {
		String array[] = new String[] {"+", "-"};
		if(accept(array)) {		//Zmena gramatiky ["+" | "-" | e] term => ("+" | "-") term
			term();
			while(accept(array)) {
				term();
			}
		} else {
			podminka();
			expect("?");
			vyraz();
			expect(":");
			vyraz();
		}
	}

	public void term() {
		faktor();
		String array[] = new String[] {"*", "/"};
		while(accept(array)) {
			faktor();
		}
	}

	public void faktor() {
		if (isNextNumber()) {
			int cislo = Integer.parseInt(getInput());
		} else {
			String vstup = getInput();
			switch (vstup) {
			case "call": 
				String jmenoFce = getInput();
				expect("(");
				if(isNextNumber()) {
					int argument = Integer.parseInt(getInput());
					while(accept(",")) {
						argument = Integer.parseInt(getInput());
					}
				}
				expect(")");
				break;
			case "(":
				vyraz();
				expect(")");
			default:
				String promenna = input;
			}
		}
	}

	public void oneCase() {
		expect("case");
		int cislo = Integer.parseInt(getInput());
		expect(":");
		prikaz();
	}

	public void defineConst() {
		String jmenoKonstanty = getInput();
		expect("=");
		while (!isNextNumber()) {
			jmenoKonstanty = input;
			getInput();
			expect("=");
		}
		int cislo = Integer.parseInt(getInput());
	}

	public boolean isNextNumber() {
		try {
			Integer.parseInt(input.substring(0, 1));
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

}
