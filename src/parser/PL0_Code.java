package parser;

public class PL0_Code {
	public static String _lit(int constant) {
		return "LIT 0 " + constant;
	}
	
	public static String _opr(int instr) {
		return "OPR 0 " + instr;
	}

	public static String _lod(int level, int var) {
		return "LOD " + level + " " + var;
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
