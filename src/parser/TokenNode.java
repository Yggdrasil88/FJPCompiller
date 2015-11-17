package parser;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.StructureGraphic.v1.DSTreeNode;

public class TokenNode implements DSTreeNode {
	private Token token;
	private TokenNode parent;
	private List<TokenNode> childs;
	
	public TokenNode() {
		this(null, null);
	}
	
	public TokenNode(Token token) {
		this(token, null);
	}
	
	public TokenNode(Token token, TokenNode parent) {
		this.token = token;
		this.parent = parent;
		childs = new ArrayList<>();
	}
	
	public TokenNode addChild(Token token) {
		TokenNode child = new TokenNode(token, this);
		this.childs.add(child);
		return child;
	}
	
	public TokenNode addChild(TokenNode n) {
		this.childs.add(n);
		n.setParent(this);
		return n;
	}
	
	public TokenNode getChild(int index) {
		return this.childs.get(index);
	}
	
	public int childCount() {
		return this.childs.size();
	}

	public void setParent(TokenNode parent) {
		this.parent = parent;
	}

	public TokenNode getParent() {
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
