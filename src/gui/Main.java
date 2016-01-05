package gui;

import compiler.PL0_Compiler;

/**
 * Main class for the app, creates UI.
 */
public class Main {
	/**
	 * Main method
	 * @param args arguments
	 */
	public static void main(String[] args) {
		if (args.length == 2) PL0_Compiler.fromFile(args[0], args[1]);
		else new MyFrame();
	}

}
