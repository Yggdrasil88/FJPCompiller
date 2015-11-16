import java.util.List;

import parser.Node;
import parser.Parser;
import parser.Scanner;
import parser.Token;


public class Main {

	public static void main(String[] args) {
		String text = "const a=3;\n" +
				"var a;\n" +
				"a = 6\n" +
				"return 0.\n";
		Scanner sc = new Scanner();
		List<Token> tokens = sc.analyse(text);
		
		for (int i = 0; i < tokens.size(); i++) {
			System.out.print(tokens.get(i).getLexem() + " ");
		}
		System.out.println();
		for (int i = 0; i < tokens.size(); i++) {
			System.out.print(tokens.get(i).getToken() + " ");
		}
		System.out.println();
		System.out.println();
		
		Parser pa = new Parser();
		Node n = pa.parse(tokens);
	}

}
