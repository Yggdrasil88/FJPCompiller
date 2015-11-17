package parser;

public class Variable {
	private final String NAME;
	private final boolean CONSTANT;
	private int value;
	
	public Variable(String name, boolean constant, int value) {
		this.NAME = name;
		this.CONSTANT = constant;
		this.value = value;
	}
	
	public Variable(String name, boolean constant) {
		this(name, constant, 0);
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		if(!isConstant()) this.value = value;
		else {
			ErrorHandler.constAssign();
		}
	}

	public String getName() {
		return NAME;
	}

	public boolean isConstant() {
		return CONSTANT;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Variable) {
			if (((Variable)o).getName().equals(this.NAME)) return true;
		}
		return false;
	}
}
