package compiler;

/**
 * Class for generating pl0 code
 */
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
	/**
	 * Load a constant to the top
	 * @param constant constant to load
	 * @return pl0 code
	 */
	public static String _lit(int constant) {
		return "LIT 0 " + constant;
	}
	/**
	 * Executes instruction (+ , - , * , ...)
	 * @param instr instruction to execute
	 * @return pl0 code
	 */
	public static String _opr(int instr) {
		return "OPR 0 " + instr;
	}
	/**
	 * Load the value
	 * @param level level 
	 * @param addr address
	 * @return pl0 code
	 */
	public static String _lod(int level, int addr) {
		return "LOD " + level + " " + addr;
	}
	/**
	 * Stores top value to the given variable
	 * @param level level of variable
 	 * @param var position of variable
	 * @return pl0 code
	 */
	public static String _sto(int level, int var) {
		return "STO " + level + " " + var;
	}
	/**
	 * Calls a function
	 * @param level level of function
	 * @param proc position of function in given level
	 * @return pl0 code
	 */
	public static String _cal(int level, int proc) {
		return "CAL " + level + " " + proc;
	}
	/**
	 * Create empty space
	 * @param var how much space we want
	 * @return pl0 code
	 */
	public static String _int(int var) {
		return "INT 0 " + var;
	}
	/**
	 * Jump to 
	 * @param addr address to jump
	 * @return pl0 code 
	 */
	public static String _jmp(int addr) {
		return "JMP 0 " + addr;
	}
	/**
	 * Conditional jump, 0 jumps to address 1 continues.
	 * @param addr address
	 * @return pl0 code
	 */
	public static String _jpc(int addr) {
		// V debuggeru i na portalu je chyba - spravne ma byt JPC, ale s tim nefunguje debugger
		return "JMC 0 " + addr;
	}
	/**
	 * Return - end program
	 * @return pl0 code
	 */
	public static String _ret() {
		return "RET 0 0";
	}
}
