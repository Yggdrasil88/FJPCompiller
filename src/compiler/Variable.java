package compiler;

public class Variable {
	private final String NAME;
	private final boolean CONSTANT;
	private final int STACK_INDEX;
	private final int LEVEL;
	
	public Variable(String name, boolean constant, int stackIndex, int level) {
		this.NAME = name;
		this.CONSTANT = constant;
		this.STACK_INDEX = stackIndex;
		this.LEVEL = level;
	}

	public String getName() {
		return NAME;
	}

	public boolean isConstant() {
		return CONSTANT;
	}
	
	public int getStackIndex() {
		return this.STACK_INDEX;
	}
	
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
