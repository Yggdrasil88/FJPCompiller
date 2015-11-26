package compiler;

import java.util.ArrayList;
import java.util.List;

public class ProcNode {
	private final String NAME;
	private ProcNode parent;
	private List<ProcNode> childs;
	private List<Variable> variables;
	private final int START_ADDR;
	private final int LEVEL;
	//Pocet vstupnich arg
	private int argCount;
	//Index do zasobniku, kam budeme ukladat arg. pro volane fce.
	private int argStartIndex;

	public ProcNode(String name, int start, int level) {
		this.NAME = name;
		this.parent = null;
		this.childs = new ArrayList<>();
		this.variables = new ArrayList<>();
		this.START_ADDR = start;
		this.LEVEL = level;
	}

	public Variable getVar(String name) throws Exception {
		//Je promenna v teto urovni?
		for (int i = 0; i < variables.size(); i++) {
			if (variables.get(i).getName().equals(name)) {
				return variables.get(i);
			}
		}
		//neni, postupujeme vys
		if(this.getParent() == null) ErrorHandler.varNotFound(name);
		return this.getParent().getVar(name);
	}

	public ProcNode getProc(String name) throws Exception {
		//Je procedura potomkem?
		for (int i = 0; i < childs.size(); i++) {
			if (childs.get(i).getName().equals(name)) {
				return childs.get(i);
			}
		}
		//neni - zkusime postoupit o uroven vys
		if (this.getParent() == null) ErrorHandler.procNotFound(name);
		return this.getParent().getProc(name);
	}

	public int getArgCount() {
		return argCount;
	}

	public void setArgCount(int argCount) {
		this.argCount = argCount;
	}
	
	public int getArgStartIndex() {
		return argStartIndex;
	}

	public void setArgStartIndex(int argStartIndex) {
		this.argStartIndex = argStartIndex;
	}

	public void addVariable(Variable var) throws Exception {
		if (variables.contains(var)) ErrorHandler.dupliciteVariable(var.getName());
		else variables.add(var);
	}

	public String getName() {
		return this.NAME;
	}

	public int getStartAddr() {
		return this.START_ADDR;
	}

	public int getLevel() {
		return this.LEVEL;
	}

	public ProcNode addChild(ProcNode child) throws Exception {
		if (childs.contains(child)) ErrorHandler.dupliciteFunction(child.getName());
		this.childs.add(child);
		child.setParent(this);
		return child;
	}

	public ProcNode getParent() {
		return this.parent;
	}

	public void setParent(ProcNode parent) {
		this.parent = parent;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ProcNode) {
			if (((ProcNode)o).getName().equals(this.NAME)) return true;
		}
		return false;
	}
}
