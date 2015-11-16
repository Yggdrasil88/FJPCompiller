package parser;

import java.util.ArrayList;
import java.util.List;

public class Node {
	private Token token;
	private Node parent;
	private List<Node> childs;
	
	public Node(Token token, Node parent) {
		this.token = token;
		this.parent = parent;
		childs = new ArrayList<>();
	}
	
	public void addChild(Token token) {
		this.childs.add(new Node(token, this));
	}

	public Node getParent() {
		return this.parent;
	}
	
	public Token getToken() {
		return this.token;
	}
}
