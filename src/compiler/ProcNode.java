package compiler;

import java.util.ArrayList;
import java.util.List;
/**
 * Function node (procedure)
 */
public class ProcNode {
	/**
	 * Name of function
	 */
	private final String NAME;
	/**
	 * Parent
	 */
	private ProcNode parent;
	/**
	 * Childs
	 */
	private List<ProcNode> childs;
	/**
	 * Functions variables
	 */
	private List<Variable> variables;
	/**
	 * Starting address of function
	 */
	private final int START_ADDR;
	/**
	 * Level
	 */
	private final int LEVEL;
	/**
	 * Number of input arguments
	 */
	private int argCount;
	/**
	 * Index to stack, where we store args for called functions
	 */
	private int argStartIndex;
	/**
	 * Constructor
	 * @param name name of function
	 * @param start starting address
	 * @param level level
	 */
	public ProcNode(String name, int start, int level) {
		this.NAME = name;
		this.parent = null;
		this.childs = new ArrayList<>();
		this.variables = new ArrayList<>();
		this.START_ADDR = start;
		this.LEVEL = level;
	}
	/**
	 * Gets variable, must be in current level (functions block) or above
	 * @param name name of variable
	 * @return variable
	 * @throws Exception exception
	 */
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
	/**
	 * Gets function, must be in current level or above
	 * @param name name of function
	 * @return function
	 * @throws Exception exception
	 */
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
	/**
	 * Gets argument count
	 * @return argument count
	 */
	public int getArgCount() {
		return argCount;
	}
	/**
	 * Sets argument count
	 * @param argCount argument count
	 */
	public void setArgCount(int argCount) {
		this.argCount = argCount;
	}
	/**
	 * Gets argument start index
	 * @return argument start index
	 */
	public int getArgStartIndex() {
		return argStartIndex;
	}
	/**
	 * Sets argument start index
	 * @param argStartIndex argument start index
	 */
	public void setArgStartIndex(int argStartIndex) {
		this.argStartIndex = argStartIndex;
	}
	/**
	 * Adds variable to current node
	 * @param var variable to add
	 * @throws Exception exception
	 */
	public void addVariable(Variable var) throws Exception {
		if (variables.contains(var)) ErrorHandler.dupliciteVariable(var.getName());
		else variables.add(var);
	}
	/**
	 * Gets name of function
	 * @return name of function
	 */
	public String getName() {
		return this.NAME;
	}
	/**
	 * Gets starting address of function
	 * @return address of function
	 */
	public int getStartAddr() {
		return this.START_ADDR;
	}
	/**
	 * Gets level of function
	 * @return level of function
	 */
	public int getLevel() {
		return this.LEVEL;
	}
	/**
	 * Adds new child (function)
	 * @param child child node to add
	 * @return added child node
	 * @throws Exception exception
	 */
	public ProcNode addChild(ProcNode child) throws Exception {
		if (childs.contains(child)) ErrorHandler.dupliciteFunction(child.getName());
		this.childs.add(child);
		child.setParent(this);
		return child;
	}
	/**
	 * Gets parent 
	 * @return parent node
	 */
	public ProcNode getParent() {
		return this.parent;
	}
	/**
	 * Sets node's parent
	 * @param parent node's parent
	 */
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
