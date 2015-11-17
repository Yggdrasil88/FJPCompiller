package parser;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.StructureGraphic.v1.DSTreeNode;

public class Node implements DSTreeNode {
	private Token token;
	private Node parent;
	private List<Node> childs;
	
	public Node() {
		this(null, null);
	}
	
	public Node(Token token) {
		this(token, null);
	}
	
	public Node(Token token, Node parent) {
		this.token = token;
		this.parent = parent;
		childs = new ArrayList<>();
	}
	
	public Node addChild(Token token) {
		Node child = new Node(token, this);
		this.childs.add(child);
		return child;
	}
	
	public Node addChild(Node n) {
		this.childs.add(n);
		n.setParent(this);
		return n;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public Node getParent() {
		return this.parent;
	}
	
	public Token getToken() {
		return this.token;
	}
	
	@Override
	public String toString() {
		return this.token.toString();
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
		if (this.token == null) return Color.RED;
		if (this.token.getToken() == Token.INT) return Color.BLUE;
		if (this.token.getToken() == Token.IDENT) return Color.GREEN;
		return Color.BLACK;
	}

	@Override
	public Object DSgetValue() {
		return this.token;
	}
}
