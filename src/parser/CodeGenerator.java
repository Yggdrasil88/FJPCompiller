package parser;

import java.util.ArrayList;
import java.util.List;

import org.StructureGraphic.v1.DSutils;

public class CodeGenerator {
	//Baze + navr. hodnota
	private final int PROC_RESERVE_SIZE = 4;

	private TokenNode tokenNode;
	private BlockNode blockNode;
	private List<String> instructions;

	public List<String> generate(TokenNode root) {
		/*
		 * Zasobnik:
		 * 	Pro kazdou proceduru:
		 * 		prvni tri mista pro bazi - vytvorime a nevsimame si jich
		 * 		dalsi 1 misto vyhrazeno pro navratovou hodnotu volane fce.
		 * 		dalsich x mist vyhrazeno pro argumenty
		 * 		dalsich x mist nastaveno na hodnoty konstant 
		 * 			konstanty ukladane v ramci vicenasobneho prirazeni se ulozi jen jednou 
		 * 				(v tabulce budou ale vicekrat)
		 *		dalsich x mist vyhrazeno pro promenne
		 *		dalsich x mist vyhrazeno pro argumenty fce(x == pocet argumentu fce s nejvice arg.)
		 * 
		 * Kod:
		 * 	Pro kazdou proceduru:
		 * 		vytvoreni mista pro bazi a navratovou hodnotu (4 mista) 
		 * 		v pripade fce. nastaveni argumentu
		 * 		vytvareni konstant
		 * 		vytvareni mista pro promenne
		 * 		JMP pokud nasleduji procedury, jinak nic
		 * 			JMP preskoci procedury dane fce
		 * 		vykonny kod
		 * 		pokud se nejedna o main, pak vraceni navr. hodnoty
		 * 		return
		 */
		this.tokenNode = root;
		this.blockNode = new BlockNode(null, 0, 0, this.tokenNode);
		instructions = new ArrayList<>();
		createBlock(0);
		DSutils.show(blockNode, 500, 80);
		return instructions;
	}

	private void createBlock(int level) {
		int stackIndex = 1;
		int index = 0;
		stackIndex += PROC_RESERVE_SIZE;
		instructions.add(PL0_Code._int(PROC_RESERVE_SIZE));
		if (level > 0) {
			//Nastavime argumenty
			TokenNode procArgs = tokenNode.getChild(index);
			int pocetPromennych = procArgs.childCount();
			int argIndex = blockNode.getParent().getArgStartIndex();
			for (int i = 0; i < pocetPromennych; i++) {
				Variable v = new Variable(procArgs.getChild(i).getToken().getLexem(), false, stackIndex, level);
				blockNode.addVariable(v);
				instructions.add(PL0_Code._lod(1, argIndex));
				instructions.add(PL0_Code._sto(0, stackIndex));
				stackIndex++;
				argIndex++;
			}
			index++;
		}

		if (tokenNode.getChild(index).getToken().getToken() == Token.CONST) {
			//Existuji konstanty - ukladame na zacetek
			TokenNode consts = tokenNode.getChild(index);
			for (int i = 0; i < consts.childCount(); i++) {
				//Pro kazdou mnozinu konstant poznamename konstanty do tabulky, vytvarime jen jednu
				int hodnota = createConst(consts.getChild(i), stackIndex, level);
				instructions.add(PL0_Code._lit(hodnota));
				stackIndex++;
			}
			index++;
		}
		if (tokenNode.getChild(index).getToken().getToken() == Token.VAR) {
			//Existuji promenne - vytvorime pro ne misto
			TokenNode vars = tokenNode.getChild(index);
			int pocetPromennych = vars.childCount();
			for (int i = 0; i < pocetPromennych; i++) {
				Variable v = new Variable(vars.getChild(i).getToken().getLexem(), false, stackIndex, level);
				blockNode.addVariable(v);
				stackIndex++;
			}
			instructions.add(PL0_Code._int(pocetPromennych));
			index++;
		}

		//Misto pro argumenty
		int maxArgumentu = 0;
		int pomIndex = index;
		while (tokenNode.getChild(pomIndex).getToken().getToken() == Token.PROC) {
			int pocetArg = tokenNode.getChild(pomIndex).getChild(0).childCount();
			if (maxArgumentu < pocetArg) maxArgumentu = pocetArg;
			pomIndex++;
		}
		if (maxArgumentu > 0) {
			instructions.add(PL0_Code._int(maxArgumentu));
		}

		//Budeme skakat za def. procedur
		int insJmpAddr = instructions.size();
		instructions.add(PL0_Code._jmp(-1));
		boolean procDef = false;

		while (tokenNode.getChild(index).getToken().getToken() == Token.PROC) {
			//Prochazime definice procedur
			procDef = true;
			createProc(tokenNode.getChild(index), level);
			index++;
		}

		if (!procDef) {
			//Nedefinovna zadna procedura, zrusime instrukci
			instructions.remove(insJmpAddr);
		} else {
			//Nastavime korektni hodnotu JMP
			instructions.set(insJmpAddr, PL0_Code._jmp(instructions.size()));
		}

		while (tokenNode.getChild(index).getToken().getToken() != Token.RETURN) {
			prikaz(blockNode, tokenNode.getChild(index));
			index++;
		}

		if(tokenNode.getParent() != null) {
			int returnValue = 0;
			if (tokenNode.getChild(index).childCount() != 0) {
				vyraz(blockNode, tokenNode);
			}
			instructions.add(PL0_Code._lit(returnValue));
			instructions.add(PL0_Code._sto(1, 3));
		}
		instructions.add(PL0_Code._ret());
	}

	private void prikaz(BlockNode blockNode, TokenNode tokenNode) {
		//Zjistime typ uzlu a zavolame potrebnou metodu
		switch(tokenNode.getToken().getToken()) {
		case Token.CALL:
			call(blockNode, tokenNode.getChild(0));
			break;
		case Token.IF:
			//TODO jmc
			podminka(blockNode, tokenNode.getChild(0));
			prikaz(blockNode, tokenNode.getChild(1));
			if (tokenNode.childCount() == 3) {
				prikaz(blockNode, tokenNode.getChild(2));
			}
			break;
		case Token.WHILE:
			//TODO jmc, jmp
			podminka(blockNode, tokenNode.getChild(0));
			prikaz(blockNode, tokenNode.getChild(1));
			break;
		case Token.DO:
			//TODO jmc
			prikaz(blockNode, tokenNode.getChild(1));
			podminka(blockNode, tokenNode.getChild(0));
			break;
		case Token.SWITCH:
			vyraz(blockNode, tokenNode.getChild(0));
			for (int i = 1; i < tokenNode.childCount(); i++) {
				//TODO jmc
				prikaz(blockNode, tokenNode.getChild(i));
			}
			break;
		case Token.ASSIGN:
			//TODO
			//ident = vyraz
		}
	}

	private void podminka(BlockNode blockNode, TokenNode tokenNode) {
		//0 false, 1 true
		switch (tokenNode.getToken().getToken()) {
		case Token.EQUAL:
			vyraz(blockNode, tokenNode.getChild(0));
			vyraz(blockNode, tokenNode.getChild(1));
			instructions.add(PL0_Code._opr(PL0_Code.EQUAL));
			break;
		case Token.DIFF:
			vyraz(blockNode, tokenNode.getChild(0));
			vyraz(blockNode, tokenNode.getChild(1));
			instructions.add(PL0_Code._opr(PL0_Code.DIFF));
			break;
		case Token.LT:
			vyraz(blockNode, tokenNode.getChild(0));
			vyraz(blockNode, tokenNode.getChild(1));
			instructions.add(PL0_Code._opr(PL0_Code.LT));
			break;
		case Token.GT:
			vyraz(blockNode, tokenNode.getChild(0));
			vyraz(blockNode, tokenNode.getChild(1));
			instructions.add(PL0_Code._opr(PL0_Code.GT));
			break;
		case Token.LET:
			vyraz(blockNode, tokenNode.getChild(0));
			vyraz(blockNode, tokenNode.getChild(1));
			instructions.add(PL0_Code._opr(PL0_Code.LET));
			break;
		case Token.GET:
			vyraz(blockNode, tokenNode.getChild(0));
			vyraz(blockNode, tokenNode.getChild(1));
			instructions.add(PL0_Code._opr(PL0_Code.GET));
			break;
		case Token.AND:
			//TODO
			break;
		case Token.OR:
			//TODO 
			break;
		case Token.EXCL: 
			podminka(blockNode, tokenNode.getChild(0));
			instructions.add(PL0_Code._lit(0));
			instructions.add(PL0_Code._opr(PL0_Code.EQUAL));
			break;
		}
	}

	private void vyraz(BlockNode blockNode, TokenNode tokenNode) {
		if (tokenNode.getToken().getToken() == Token.QUEST) {
			//Na vrcholu zasobniku bude vysledek
			//TODO jmc
			podminka(blockNode, tokenNode.getChild(0));
			vyraz(blockNode, tokenNode.getChild(1));
			vyraz(blockNode, tokenNode.getChild(2));
		} else {
			term(blockNode, tokenNode);
		}
	}

	private void term(BlockNode blockNode, TokenNode tokenNode) {
		//+ - * /
		//Na vrcholu zasobniku bude vysledek
		switch (tokenNode.getToken().getToken()) {
		case Token.PLUS:
			faktor(blockNode, tokenNode.getChild(0));
			faktor(blockNode, tokenNode.getChild(1));
			instructions.add(PL0_Code._opr(PL0_Code.PLUS));
			break;
		case Token.MINUS:
			faktor(blockNode, tokenNode.getChild(0));
			faktor(blockNode, tokenNode.getChild(1));
			instructions.add(PL0_Code._opr(PL0_Code.MINUS));
			break;
		case Token.TIMES:
			faktor(blockNode, tokenNode.getChild(0));
			faktor(blockNode, tokenNode.getChild(1));
			instructions.add(PL0_Code._opr(PL0_Code.TIMES));
			break;
		case Token.DIVIDE:
			faktor(blockNode, tokenNode.getChild(0));
			faktor(blockNode, tokenNode.getChild(1));
			instructions.add(PL0_Code._opr(PL0_Code.DIVIDE));
			break;
		}
	}

	private void faktor(BlockNode blockNode, TokenNode tokenNode) {
		//Na vrcholu zasobniku bude vysledek
		switch (tokenNode.getToken().getToken()) {
		case Token.IDENT:
			//Faktor je promenna = najdeme jeji vyskyt v tabulce a ulozime ji na vrchol zasobniku
			Variable var = blockNode.getVar(tokenNode.getToken().getLexem());
			int level = blockNode.getLevel() - var.getLevel();
			instructions.add(PL0_Code._lod(level, var.getStackAddr()));
			break;
		case Token.INT:
			//Faktor je hodnota - pushneme ji
			instructions.add(PL0_Code._lit(Integer.parseInt(tokenNode.getToken().getLexem())));
			break;
		case Token.CALL:
			//Faktor je call - nastavime argumenty, zavolame a vratime navr. hodnotu
			call(blockNode, tokenNode.getChild(0));
			break;
		default: 
			term(blockNode, tokenNode);
		}
	}

	private void call(BlockNode blockNode, TokenNode tokenNode) {
		String name = tokenNode.getToken().getLexem();
		int pocetArg = tokenNode.childCount();
		for(int i = 0; i < pocetArg; i++) {
			faktor(blockNode, tokenNode.getChild(i));
			//Hodnota je na vrcholu zasobniku
			//TODO - ulozit ji na misto pro argumenty
		}

		BlockNode procNode = blockNode.getProc(name);
		int level = blockNode.getLevel() - procNode.getLevel() + 1;
		instructions.add(PL0_Code._cal(level, procNode.getStartAddr()));
		instructions.add(PL0_Code._lod(level, 3));	//Zapiseme na vrchol zasobniku navratovou hodnotu
	}

	private void createProc(TokenNode procNode, int level) {
		BlockNode newProc = new BlockNode(procNode.getChild(0).getToken().getLexem(), 
				instructions.size(), level + 1, procNode);
		//Prepnuti kontextu
		blockNode = blockNode.addChild(newProc);
		tokenNode = procNode;

		createBlock(level + 1);

		//Navrat zpet
		tokenNode = tokenNode.getParent();
		blockNode = blockNode.getParent();
	}

	private int createConst(TokenNode tokenNode, int stackIndex, int level) {
		/*
		 * Rekurzivne zjistime hodnotu a poznamename si konstanty do stromu
		 */
		if (tokenNode.childCount() == 0) return Integer.parseInt(tokenNode.getToken().getLexem());
		int hodnota = createConst(tokenNode.getChild(1), stackIndex, level);
		Variable v = new Variable(tokenNode.getChild(0).getToken().getLexem(), true, stackIndex, level);
		blockNode.addVariable(v);
		return hodnota;
	}
}
