package parser;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.StructureGraphic.v1.DSTreeNode;

public class BlockNode implements DSTreeNode {
	private final String NAME;
	private BlockNode parent;
	private List<BlockNode> childs;
	private List<Variable> variables;
	private final TokenNode TOKEN_NODE;
	private final int START_ADDR;
	private final int LEVEL;

	public BlockNode(String name, int start, int level, TokenNode tokenNode) {
		this.NAME = name;
		this.parent = null;
		this.childs = new ArrayList<>();
		this.variables = new ArrayList<>();
		this.START_ADDR = start;
		this.LEVEL = level;
		this.TOKEN_NODE = tokenNode;
	}

	public Variable getVar(String name) {
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

	public BlockNode getProc(String name) {
		//Je procedura potomkem?
		for (int i = 0; i < childs.size(); i++) {
			if (childs.get(i).getName().equals(name)) {
				return childs.get(i);
			}
		}
		//neni - zkusime postoupit o uroven vys
		if (this.getParent() == null) ErrorHandler.procNotFound(name);
		return this.getParent().getProc(NAME);
	}

	public int getArgStartIndex() {
		return variables.get(variables.size() - 1).getStackAddr() + 1;
	}

	public TokenNode getTokenNode() {
		return this.TOKEN_NODE;
	}

	public void addVariable(Variable var) {
		if (variables.contains(var)) ErrorHandler.dupliciteVariable(var);
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

	public BlockNode addChild(BlockNode child) {
		if (childs.contains(child)) ErrorHandler.dupliciteMethod(child);
		this.childs.add(child);
		child.setParent(this);
		return child;
	}

	public BlockNode getParent() {
		return this.parent;
	}

	public void setParent(BlockNode parent) {
		this.parent = parent;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof BlockNode) {
			if (((BlockNode)o).getName().equals(this.NAME)) return true;
		}
		return false;
	}

	/*
	 * Metody pro vizualizaci
	 * prilinkovat knihovnu z https://code.google.com/p/structure-graphic/
	 */

	@Override
	public DSTreeNode[] DSgetChildren() {
		DSTreeNode[] kids = childs.toArray(new DSTreeNode[childs.size()]);
		return kids;
	}

	@Override
	public Color DSgetColor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object DSgetValue() {
		StringBuilder builder = new StringBuilder();
		if (NAME != null) builder.append(NAME + ", Level: " + LEVEL + ", Start: " + START_ADDR + "\n");
		for (int i = 0; i < variables.size(); i++) {
			if (variables.get(i).isConstant()) builder.append("Const ");
			else builder.append("Var ");
			builder.append(variables.get(i).getName() + ": " + variables.get(i).getStackAddr() + ",Level: " + variables.get(i).getLevel() + "; ");
		}
		return builder.toString();
	}
}
