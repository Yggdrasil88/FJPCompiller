package compiler;
/**
 * Class represents variable
 */
public class Variable {
	/**
	 * Name of the variable
	 */
	private final String NAME;
	/**
	 * Defines if variable is constant
	 */
	private final boolean CONSTANT;
	/**
	 * Position of variable
	 */
	private final int STACK_INDEX;
	/**
	 * Level of the variable
	 */
	private final int LEVEL;
	/**
	 * Constructor
	 * @param name Name of the variable
	 * @param constant Defines if variable is constant
	 * @param stackIndex Position of variable
	 * @param level Level of the variable
	 */
	public Variable(String name, boolean constant, int stackIndex, int level) {
		this.NAME = name;
		this.CONSTANT = constant;
		this.STACK_INDEX = stackIndex;
		this.LEVEL = level;
	}
	/**
	 * Gets name of the variable
	 * @return name
	 */
	public String getName() {
		return NAME;
	}
	/**
	 * True if variable is constant
	 * @return True if variable is constant
	 */
	public boolean isConstant() {
		return CONSTANT;
	}
	/**
	 * Gets stack index
	 * @return stack index
	 */
	public int getStackIndex() {
		return this.STACK_INDEX;
	}
	/**
	 * Level of variable
	 * @return
	 */
	public int getLevel() {
		return this.LEVEL;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Variable) {
			if (((Variable)o).getName().equals(this.NAME)) return true;
		}
		return false;
	}
}
