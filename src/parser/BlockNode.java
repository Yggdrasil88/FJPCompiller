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
	private final int START_ADDR;
	
	public BlockNode(String name, int start) {
		this.NAME = name;
		this.parent = null;
		this.childs = new ArrayList<>();
		this.variables = new ArrayList<>();
		this.START_ADDR = start;
	}
	
	public void addVariable(Variable var) {
		if (variables.contains(var)) ErrorHandler.dupliciteVariable(var);
		else variables.add(var);
	}
	
	public String getName() {
		return this.NAME;
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
		for (int i = 0; i < variables.size(); i++) {
			if (variables.get(i).isConstant()) builder.append("Const ");
			else builder.append("Var ");
			builder.append(variables.get(i).getName() + ": " + variables.get(i).getStackAddr() + "; ");
		}
		return builder.toString();
	}
}
