package compiler;

import java.util.ArrayList;
import java.util.List;

public class TokenNode {
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
}
