package parser;

import java.util.Arrays;

public class ErrorHandler {
	public static void scannnerError(Exception e) {
		System.err.println("Pøi skenování vznikla chyba.");
		e.printStackTrace();
		System.exit(1);
	}
	
	public static void parserError(int token) {
		System.err.println("Pøi parsování vznikla chyba.");
		System.err.println("Oèekáváno token ID " + token);
		System.exit(1);
	}
	
	public static void parserError(int[] tokens) {
		System.err.println("Pøi parsování vznikla chyba.");
		System.err.println("Oèekáváno token ID " + Arrays.toString(tokens));
		System.exit(1);
	}

	public static void constAssign() {
		System.err.println("Pøiøazujete do konstanty.");
		System.exit(1);
	}
	
	public static void dupliciteVariable(Variable var) {
		System.err.println("Hodnota s nazvem " + var.getName() + " jiz existuje.");
		System.exit(1);
	}
	
	public static void dupliciteMethod(BlockNode child) {
		System.err.println("Metoda s nazvem " + child.getName() + " jiz existuje.");
		System.exit(1);
	}
	
	public static void varNotFound(String var) {
		System.err.println("Promìnná s názvem " + var + " nebyla nalezena.");
		System.exit(1);
	}
	
	public static void procNotFound(String proc) {
		System.err.println("Procedura s názvem " + proc + " nebyla nalezena.");
		System.exit(1);
	}
}
