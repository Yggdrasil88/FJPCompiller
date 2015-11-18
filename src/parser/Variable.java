package parser;

public class Variable {
	private final String NAME;
	private final boolean CONSTANT;
	private final int STACK_ADDR;
	private final int LEVEL;
	
	public Variable(String name, boolean constant, int stackAddr, int level) {
		this.NAME = name;
		this.CONSTANT = constant;
		this.STACK_ADDR = stackAddr;
		this.LEVEL = level;
	}

	public String getName() {
		return NAME;
	}

	public boolean isConstant() {
		return CONSTANT;
	}
	
	public int getStackAddr() {
		return this.STACK_ADDR;
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
