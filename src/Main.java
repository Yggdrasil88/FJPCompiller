import java.util.List;

import org.StructureGraphic.v1.DSutils;

import parser.CodeGenerator;
import parser.Parser;
import parser.Scanner;
import parser.Token;
import parser.TokenNode;


public class Main {

	public static void main(String[] args) {
		String text = "const a=b=c=3, d=9, e=4;\n" +
				"var f,g,h;\n" +
				"procedure fce1(f, g) var i; i = = f+g return i;\n" +
				"procedure fce2() var j; procedure fce(a) procedure fce() var a; return; return;  j = = ? !(!(a <> b)) ?(c+b*-4): a*(-8+(-6)) return a*b+8--9;\n" +
				"procedure fce3() var a; a= =call fce1() return;\n" +
				"begin f=g=h= =6 ; call fce1(f, g, call fce2(f,g)); if a> b then c= = 5; if a<b then c= =4 else c = =-8; switch a+5 case 1:d= =1, case 2:d = = -8*4/7+56/(8+(4*8)); \n" +
				"while a>b do c= = 4; do c= = 4 while a>4 end\n" +
				"return 0;\n";
		/*String text = "var a;\n" +
				"procedure pr1(d)\n" +
				" var b;\n" +
				" procedure pr2()\n" +
				"  begin\n" +
				"   c= =11;\n" +
				"   b= =22;\n" +
				"   a= =33;\n" +
				"   if 1==1 then call pr1()\n" +
				"  end\n" +
				"  return 0;\n"+
				" begin\n" +
				"  a= = 10;\n" +
				"  b= =20;\n" +
				"  call pr2()\n" +
				" end\n" +
				" return;\n"+
				"begin\n" +
				" a= =100;\n" +
				" call pr1()\n" +
				"end\n" +
				"return 0;\n";*/
		/*String text = "begin\n" +
				" \n" +
				"\n" +
				"\n" +
				"\n" +
				"\n" +
				"\n" +
				"end\n" +
				"return\n";*/
		
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
