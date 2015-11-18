package parser;

public class Variable {
	private final String NAME;
	private final boolean CONSTANT;
	private final int STACK_ADDR;
	
	public Variable(String name, boolean constant, int stackAddr) {
		this.NAME = name;
		this.CONSTANT = constant;
		this.STACK_ADDR = stackAddr;
	}

	public String getName() {
		return NAME;
	}

	public boolean isConstant() {
		return CONSTANT;
	}
	
	public int getStackAddr() {
		return STACK_ADDR;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Variable) {
			if (((Variable)o).getName().equals(this.NAME)) return true;
		}
		return false;
	}
}
