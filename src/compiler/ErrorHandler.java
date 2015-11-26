package compiler;

import java.util.Arrays;

public class ErrorHandler {
	public static void argCountError(String name) throws Exception {
		throw new Exception("Nesouhlas� po�et argument� funkce " + name + ".");
	}
	
	public static void parserError(int token) throws Exception {
		throw new Exception("P�i parsov�n� vznikla chyba. \n O�ek�v�no token ID " + token);
	}
	
	public static void parserError(int[] tokens) throws Exception {
		throw new Exception("P�i parsov�n� vznikla chyba. \n O�ek�v�no token ID " + Arrays.toString(tokens));
	}

	public static void constAssign() throws Exception {
		throw new Exception("P�i�azujete do konstanty.");
	}
	
	public static void dupliciteVariable(Variable var) throws Exception {
		throw new Exception("Hodnota s nazvem " + var.getName() + " jiz existuje.");
	}
	
	public static void dupliciteFunction(ProcNode child) throws Exception {
		throw new Exception("Funkce s nazvem " + child.getName() + " jiz existuje.");
	}
	
	public static void varNotFound(String var) throws Exception {
		throw new Exception("Prom�nn� s n�zvem " + var + " nebyla nalezena.");
	}
	
	public static void procNotFound(String proc) throws Exception {
		throw new Exception("Procedura s n�zvem " + proc + " nebyla nalezena.");
	}
	
	public static void notConst(String name) throws Exception {
		throw new Exception("O�ek�v�no ozna�en� konstanty, na vstupu " + name + ".");
	}
	
	public static Exception progNotFound() throws Exception {
		throw new Exception("Nenalezen ��dn� program.");
	}
}
