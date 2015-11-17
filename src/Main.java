import java.util.List;

import org.StructureGraphic.v1.DSutils;

import parser.Node;
import parser.Parser;
import parser.Scanner;
import parser.Token;


public class Main {

	public static void main(String[] args) {
		String text = "const a=b=c=3, d=9, e=4;\n" +
				"var f,g,h;\n" +
				"procedure fce1(f, g) var i; i = = f+g return i;\n" +
				"procedure fce2() var j; procedure fce() procedure fce() return; return;  j = = ? !(!(a <> b)) ?(c+b*-4): a*(-8+(-6)) return a*b+8--9;\n" +
				"begin f=g=h= =6 ; call fce1(f, g, call fce2(f,g)); if a> b then c= = 5; if a<b then c= =4 else c = =-8; switch a+5 case 1:d= =1, case 2:d = = -8*4/7+56/(8+(4*8)); \n" +
				"while a>b do c= = 4; do c= = 4 while a>4 end\n" +
				//"a = = 4 end\n" +
				"return 0;\n";
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
		DSutils.show(n, 100, 30);
	}

}
