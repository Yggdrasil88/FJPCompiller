package compiler;

import java.util.Arrays;

public class ErrorHandler {
	public static void argCountError(String name) throws Exception {
		throw new Exception("Nesouhlasí poèet argumentù funkce " + name + ".");
	}
	
	public static void parserError(int token) throws Exception {
		throw new Exception("Pøi parsování vznikla chyba. \n Oèekáváno token ID " + token);
	}
	
	public static void parserError(int[] tokens) throws Exception {
		throw new Exception("Pøi parsování vznikla chyba. \n Oèekáváno token ID " + Arrays.toString(tokens));
	}

	public static void constAssign() throws Exception {
		throw new Exception("Pøiøazujete do konstanty.");
	}
	
	public static void dupliciteVariable(Variable var) throws Exception {
		throw new Exception("Hodnota s nazvem " + var.getName() + " jiz existuje.");
	}
	
	public static void dupliciteFunction(ProcNode child) throws Exception {
		throw new Exception("Funkce s nazvem " + child.getName() + " jiz existuje.");
	}
	
	public static void varNotFound(String var) throws Exception {
		throw new Exception("Promìnná s názvem " + var + " nebyla nalezena.");
	}
	
	public static void procNotFound(String proc) throws Exception {
		throw new Exception("Procedura s názvem " + proc + " nebyla nalezena.");
	}
	
	public static void notConst(String name) throws Exception {
		throw new Exception("Oèekáváno oznaèení konstanty, na vstupu " + name + ".");
	}
	
	public static Exception progNotFound() throws Exception {
		throw new Exception("Nenalezen žádný program.");
	}
}
