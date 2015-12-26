package compiler;

/**
 * Class for handling errors
 */
public class ErrorHandler {
	/**
	 * Error - Wrong number of function arguments
	 * @param name function name
	 * @throws Exception exception
	 */
	public static void argCountError(String name) throws Exception {
		throw new Exception("Chybny pocet argumentu funkce \"" + name + "\".");
	}
	/**
	 * Error - parsing token
	 * @param token token number
	 * @param index token index
	 * @throws Exception exception
	 */
	public static void parserError(int token, int index) throws Exception {
		throw new Exception("Pri parsovani vznikla chyba na tokenu " + index + ". \n" +
				"Ocekavan token \"" + Token.TOKEN_STRINGS[token] + "\".");
	}
	/**
	 * Error - parsing error
	 * @param tokens tokens
	 * @param index token index
	 * @throws Exception exception
	 */
	public static void parserError(int[] tokens, int index) throws Exception {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < tokens.length; i++) {
			sb.append("\n" + Token.TOKEN_STRINGS[tokens[i]] + "\n");
			if (i < tokens.length - 1) sb.append(", ");
		}
		
		throw new Exception("Pri parsovani vznikla chyba na tokenu " + index + ". \n" +
				"Ocekavan jeden z tokenu: " + sb.toString() + ".");
	}
	/**
	 * Error - assigning to constant
	 * @param name name of constant
	 * @throws Exception exception
	 */
	public static void constAssign(String name) throws Exception {
		throw new Exception("Nelze priradit, \"" + name + "\" je konstanta.");
	}
	/**
	 * Error - variable name duplicate
	 * @param name duplicate name
	 * @throws Exception exception
	 */
	public static void dupliciteVariable(String name) throws Exception {
		throw new Exception("Hodnota s nazvem \n" + name + "\" jiz existuje.");
	}
	/**
	 * Error - function name duplicate
	 * @param name function name
	 * @throws Exception exception
	 */
	public static void dupliciteFunction(String name) throws Exception {
		throw new Exception("Funkce s nazvem \"" + name + "\" jiz existuje.");
	}
	/**
	 * Error - variable not found
	 * @param var variables name
	 * @throws Exception exception
	 */
	public static void varNotFound(String var) throws Exception {
		throw new Exception("Promenna s nazvem \"" + var + "\" nebyla nalezena.");
	}
	/**
	 * Error - function not found
	 * @param proc functions name
	 * @throws Exception exception
	 */
	public static void procNotFound(String proc) throws Exception {
		throw new Exception("Procedura s nazvem \"" + proc + "\" nebyla nalezena.");
	}
	/**
	 * Error - Constant expected
	 * @param name name 
	 * @throws Exception exception
	 */
	public static void notConst(String name) throws Exception {
		throw new Exception("Ocekavano oznaceni konstanty, na vstupu \"" + name + "\".");
	}
	/**
	 * Error - Program not found (input empty)
	 * @throws Exception exception
	 */
	public static void progNotFound() throws Exception {
		throw new Exception("Nenalezen zadny program.");
	}
}
