import java.util.List;

import org.StructureGraphic.v1.DSutils;

import parser.CodeGenerator;
import parser.Parser;
import parser.Scanner;
import parser.Token;
import parser.TokenNode;


public class Main {

	public static void main(String[] args) {
		String text = "const a=5;\n" +
				"var b; \n" +
				"begin switch a case 1:b= =10, case 5: b= =5, case 10:b= = 1; end \n" +
				"return;";
		
		Scanner sc = new Scanner();
		List<Token> tokens = sc.analyse(text);
		
		Parser pa = new Parser();
		TokenNode n = pa.parse(tokens);
		DSutils.show(n, 100, 80);
		
		CodeGenerator gen = new CodeGenerator();
		List<String> instructions = gen.generate(n);
		
		for (int i = 0; i < instructions.size(); i++) {
			System.out.println(i + " " + instructions.get(i));
		}
	}

}
