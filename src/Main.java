import java.util.List;

import org.StructureGraphic.v1.DSutils;

import parser.CodeGenerator;
import parser.Parser;
import parser.Scanner;
import parser.Token;
import parser.TokenNode;


public class Main {

	public static void main(String[] args) {
		String text = "\n" +
				"var a; \n" +
				"procedure proc2(a, b, c) return a+b+c;\n" +
				"procedure proc1(a, b) return a * call proc2(b, b+5, b+10);\n" +
				"begin a= =call proc1(2, 3); end \n" +
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
