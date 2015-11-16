package parser;

import java.util.ArrayList;
import java.util.List;

public class Node {
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
}
