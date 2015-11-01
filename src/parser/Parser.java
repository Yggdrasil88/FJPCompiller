package parser;

public class Parser {
	private String input;

	private String getInput() {
		String oldInput = input;
		input = "input";	//TODO load from input
		return oldInput;
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
		if (accept("!(")) {
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
