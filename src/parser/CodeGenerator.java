package parser;

public class CodeGenerator {
	private TokenNode tokenNode;
	private BlockNode blockNode;
	
	public BlockNode generate(TokenNode root) {
		this.tokenNode = root;
		this.blockNode = new BlockNode(null);
		createBlockTable(0);
		
		return blockNode;
	}

	private void createBlockTable(int index) {
		if (tokenNode.getChild(index).getToken().getToken() == Token.CONST) {
			TokenNode consts = tokenNode.getChild(index);
			for (int i = 0; i < consts.childCount(); i++) {
				createConstTable(consts.getChild(i));
			}
			index++;
		}
		if (tokenNode.getChild(index).getToken().getToken() == Token.VAR) {
			TokenNode vars = tokenNode.getChild(index);
			for (int i = 0; i < vars.childCount(); i++) {
				Variable v = new Variable(vars.getChild(i).getToken().getLexem(), false);
				blockNode.addVariable(v);
			}
			index++;
		}
		while (tokenNode.getChild(index).getToken().getToken() == Token.PROC) {
			createProcTable(tokenNode.getChild(index));
			index++;
		}
	}
	
	private void createProcTable(TokenNode tokenNode) {
		BlockNode block = new BlockNode(tokenNode.getChild(0).getToken().getLexem());
		TokenNode args = tokenNode.getChild(0);
		for (int i = 0; i < args.childCount(); i++) {
			Variable v = new Variable(args.getChild(i).getToken().getLexem(), false);
			block.addVariable(v);
		}
		blockNode = blockNode.addChild(block);
		this.tokenNode = tokenNode;
		createBlockTable(1);
		this.tokenNode = this.tokenNode.getParent();
		blockNode = blockNode.getParent();
	}

	private int createConstTable(TokenNode tokenNode) {
		if (tokenNode.childCount() == 0) return Integer.parseInt(tokenNode.getToken().getLexem());
		Variable v = new Variable(tokenNode.getChild(0).getToken().getLexem(), true, createConstTable(tokenNode.getChild(1)));
		blockNode.addVariable(v);
		return v.getValue();
	}
}
