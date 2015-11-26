package compiler;


public class ErrorHandler {
	public static void argCountError(String name) throws Exception {
		throw new Exception("Chybny pocet argumentu funkce \"" + name + "\".");
	}
	
	public static void parserError(int token, int index) throws Exception {
		throw new Exception("Pri parsovani vznikla chyba na tokenu " + index + ". \n" +
				"Ocekavan token \"" + Token.TOKEN_STRINGS[token] + "\".");
	}
	
	public static void parserError(int[] tokens, int index) throws Exception {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < tokens.length; i++) {
			sb.append("\n" + Token.TOKEN_STRINGS[tokens[i]] + "\n");
			if (i < tokens.length - 1) sb.append(", ");
		}
		
		throw new Exception("Pri parsovani vznikla chyba na tokenu " + index + ". \n" +
				"Ocekavan jeden z tokenu: " + sb.toString() + ".");
	}

	public static void constAssign(String name) throws Exception {
		throw new Exception("Nelze priradit, \"" + name + "\" je konstanta.");
	}
	
	public static void dupliciteVariable(String name) throws Exception {
		throw new Exception("Hodnota s nazvem \n" + name + "\" jiz existuje.");
	}
	
	public static void dupliciteFunction(String name) throws Exception {
		throw new Exception("Funkce s nazvem \"" + name + "\" jiz existuje.");
	}
	
	public static void varNotFound(String var) throws Exception {
		throw new Exception("Promenna s nazvem \"" + var + "\" nebyla nalezena.");
	}
	
	public static void procNotFound(String proc) throws Exception {
		throw new Exception("Procedura s nazvem \"" + proc + "\" nebyla nalezena.");
	}
	
	public static void notConst(String name) throws Exception {
		throw new Exception("Ocekavano oznaceni konstanty, na vstupu \"" + name + "\".");
	}
	
	public static void progNotFound() throws Exception {
		throw new Exception("Nenalezen zadny program.");
	}
}
