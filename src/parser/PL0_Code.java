package parser;

public class PL0_Code {
	public static final int UNAR_MINUS = 1;
	public static final int PLUS = 2;
	public static final int MINUS = 3;
	public static final int TIMES = 4;
	public static final int DIVIDE = 5;
	public static final int MODULO = 6;
	public static final int ODD = 7;
	public static final int EQUAL = 8;
	public static final int DIFF = 9;
	public static final int LT = 10;
	public static final int GET = 11;
	public static final int GT = 12;
	public static final int LET = 13;
	
	public static String _lit(int constant) {
		return "LIT 0 " + constant;
	}
	
	public static String _opr(int instr) {
		return "OPR 0 " + instr;
	}

	public static String _lod(int level, int addr) {
		return "LOD " + level + " " + addr;
	}
	
	public static String _sto(int level, int var) {
		return "STO " + level + " " + var;
	}
	
	public static String _cal(int level, int proc) {
		return "CAL " + level + " " + proc;
	}
	
	public static String _int(int var) {
		return "INT 0 " + var;
	}
	
	public static String _jmp(int addr) {
		return "JMP 0 " + addr;
	}
	
	public static String _jmc(int addr) {
		return "JMC 0 " + addr;
	}
	
	public static String _ret() {
		return "RET 0 0";
	}
}
