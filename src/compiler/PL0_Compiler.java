package compiler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;

/**
 * Class for compiling input to PL/0 Code
 */
public class PL0_Compiler {
	/**
	 * Compile given input to PL/0
	 * @param text Input code
	 * @return PL/0 code
	 * @throws Exception exception
	 */
	public static String compile(String text) throws Exception {
		List<Token> tokens = new Scanner().analyse(text);
		TokenNode node = new Parser().parse(tokens);
		List<String> instructions = new CodeGenerator().generate(node);
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < instructions.size(); i++) {
			builder.append(i + " " + instructions.get(i) + "\n");
		}
		return builder.toString();
	}
	
	/**
	 * Read input from file, write to output file
	 * @param input Input file
	 * @param output Output file
	 */
	public static void fromFile(String input, String output) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(input)));
			StringBuilder text = new StringBuilder();
			String hlp = br.readLine();
			while(hlp != null) {
				text.append(hlp);
				hlp = br.readLine();
			}
			br.close();
			String result = compile(text.toString());
			
			BufferedWriter wr = new BufferedWriter(new FileWriter(new File(output)));
			wr.write(result);
			wr.close();
			System.out.println("OK");
			
		} catch (FileNotFoundException e) {
			System.err.println("Input file doesn't exist.");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
