package compiler;

import java.util.ArrayList;
import java.util.List;

/**
 * Class represents node for tokens.
 */
public class TokenNode {
	/**
	 * Token
	 */
	private Token token;
	/**
	 * Parent of this node
	 */
	private TokenNode parent;
	/**
	 * Childs of this node
	 */
	private List<TokenNode> childs;
	/**
	 * Basic constructor
	 */
	public TokenNode() {
		this(null, null);
	}
	/**
	 * Constructor
	 * @param token token to set for the node
	 */
	public TokenNode(Token token) {
		this(token, null);
	}
	/**
	 * Constructor
	 * @param token token to set for the node
	 * @param parent parent of this node
	 */
	public TokenNode(Token token, TokenNode parent) {
		this.token = token;
		this.parent = parent;
		childs = new ArrayList<>();
	}
	/**
	 * Adds child to the current node
	 * @param token childs token
	 * @return created child node
	 */
	public TokenNode addChild(Token token) {
		TokenNode child = new TokenNode(token, this);
		this.childs.add(child);
		return child;
	}
	/**
	 * Adds child to the current node
	 * @param child child to add 
	 * @return added child node
	 */
	public TokenNode addChild(TokenNode child) {
		this.childs.add(child);
		child.setParent(this);
		return child;
	}
	/**
	 * Gets child on the given index
	 * @param index index to get
	 * @return child node
	 */
	public TokenNode getChild(int index) {
		return this.childs.get(index);
	}
	/**
	 * Gets number of childs
	 * @return number of childs
	 */
	public int childCount() {
		return this.childs.size();
	}
	/**
	 * Sets parent of this node
	 * @param parent parent to set
	 */
	public void setParent(TokenNode parent) {
		this.parent = parent;
	}
	/**
	 * Gets parent of this node
	 * @return Parent node
	 */
	public TokenNode getParent() {
		return this.parent;
	}
	/**
	 * Gets token of this node
	 * @return nodes token
	 */
	public Token getToken() {
		return this.token;
	}
}
