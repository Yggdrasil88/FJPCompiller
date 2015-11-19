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
		 * 		dalsich x mist vyhrazeno pro vstupni argumenty (pri volani z fce)
		 * 		dalsich x mist nastaveno na hodnoty konstant 
		 * 			konstanty ukladane v ramci vicenasobneho prirazeni se ulozi jen jednou 
		 * 				(v tabulce budou ale vicekrat)
		 *		dalsich x mist vyhrazeno pro promenne
		 *		dalsich x mist vyhrazeno pro argumenty predavane fci (x == pocet argumentu fce s nejvice arg.)
		 * 
		 * Kod:
		 * 	Pro kazdou proceduru:
		 * 		vytvoreni mista pro bazi a navratovou hodnotu (4 mista) 
		 * 		v pripade fce. nastaveni argumentu z nadrazene fce
		 * 		vytvareni konstant
		 * 		vytvareni mista pro promenne
		 * 		JMP pokud nasleduji procedury, jinak nic
		 * 			JMP preskoci procedury dane fce
		 * 		vykonny kod
		 * 		pokud se nejedna o main, pak ulozeni navr hodnoty do nadrazene fce.
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
		int stackIndex = 0;
		int index = 0;
		stackIndex += PROC_RESERVE_SIZE;
		instructions.add(PL0_Code._int(PROC_RESERVE_SIZE));
		if (level > 0) {
			//Nastavime argumenty
			TokenNode procArgs = tokenNode.getChild(index);
			int pocetPromennych = procArgs.childCount();
			instructions.add(PL0_Code._int(pocetPromennych));
			int argIndex = blockNode.getParent().getArgStart();
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
		blockNode.setArgStart(stackIndex);
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
			prikaz(tokenNode.getChild(index));
			index++;
		}

		if(tokenNode.getParent() != null) {
			if (tokenNode.getChild(index).childCount() != 0) {
				vyraz(tokenNode.getChild(index).getChild(0));
			}
			instructions.add(PL0_Code._sto(1, 3));
		}
		instructions.add(PL0_Code._ret());
	}

	private void prikaz(TokenNode tokenNode) {
		//Zjistime typ uzlu a zavolame potrebnou metodu
		switch(tokenNode.getToken().getToken()) {
		case Token.CALL:
			call(tokenNode.getChild(0));
			break;
		case Token.IF:
			//Na vrcholu zasobniku bude vysledek
			podminka(tokenNode.getChild(0));
			int jpcIndex = instructions.size();
			instructions.add(PL0_Code._jpc(-1));
			prikaz(tokenNode.getChild(1));
			int jmpIndex = instructions.size();
			instructions.add(PL0_Code._jmp(-1));
			//Zmenime skok na spravnou hodnotu
			instructions.set(jpcIndex, PL0_Code._jpc(instructions.size()));
			if (tokenNode.childCount() == 3) {
				prikaz(tokenNode.getChild(2));
			}
			instructions.set(jmpIndex, PL0_Code._jmp(instructions.size()));
			break;
		case Token.WHILE:
			int whileAddr = instructions.size();
			podminka(tokenNode.getChild(0));
			jpcIndex = instructions.size();
			instructions.add(PL0_Code._jpc(-1));
			prikaz(tokenNode.getChild(1));
			instructions.add(PL0_Code._jmp(whileAddr));
			instructions.set(jpcIndex, PL0_Code._jpc(instructions.size()));
			break;
		case Token.DO:
			int doAddr = instructions.size();
			prikaz(tokenNode.getChild(0));
			podminka(tokenNode.getChild(1));
			//negace podminky
			instructions.add(PL0_Code._lit(0));
			instructions.add(PL0_Code._opr(PL0_Code.EQUAL));
			instructions.add(PL0_Code._jpc(doAddr));
			break;
		case Token.SWITCH:
			vyraz(tokenNode.getChild(0));
			for (int i = 1; i < tokenNode.childCount(); i++) {
				//TODO jpc
				prikaz(tokenNode.getChild(i));
			}
			break;
		case Token.ASSIGN:
			assign(tokenNode, 0);
		}
	}

	private void assign(TokenNode tokenNode, int lev) {
		if (tokenNode.getChild(1).getToken().getToken() != Token.ASSIGN) {
			vyraz(tokenNode.getChild(1));
		}
		else {
			assign(tokenNode.getChild(1), lev + 1);
		}
		//Promenna do ktere prirazujeme
		Variable var = blockNode.getVar(tokenNode.getChild(0).getToken().getLexem());
		if (var.isConstant()) ErrorHandler.constAssign();
		//ulozit vrchol zasobniku na nalezene misto
		instructions.add(PL0_Code._sto(blockNode.getLevel() - var.getLevel(), var.getStackAddr()));
		//A hned ji vratime zpet na zasobnik (Pokud uz nejsme na vrcholu)
		if (lev > 0) instructions.add(PL0_Code._lod(blockNode.getLevel() - var.getLevel(), var.getStackAddr()));
	}

	private void podminka(TokenNode tokenNode) {
		//0 false, 1 true
		switch (tokenNode.getToken().getToken()) {
		case Token.EQUAL:
			vyraz(tokenNode.getChild(0));
			vyraz(tokenNode.getChild(1));
			instructions.add(PL0_Code._opr(PL0_Code.EQUAL));
			break;
		case Token.DIFF:
			vyraz(tokenNode.getChild(0));
			vyraz(tokenNode.getChild(1));
			instructions.add(PL0_Code._opr(PL0_Code.DIFF));
			break;
		case Token.LT:
			vyraz(tokenNode.getChild(0));
			vyraz(tokenNode.getChild(1));
			instructions.add(PL0_Code._opr(PL0_Code.LT));
			break;
		case Token.GT:
			vyraz(tokenNode.getChild(0));
			vyraz(tokenNode.getChild(1));
			instructions.add(PL0_Code._opr(PL0_Code.GT));
			break;
		case Token.LET:
			vyraz(tokenNode.getChild(0));
			vyraz(tokenNode.getChild(1));
			instructions.add(PL0_Code._opr(PL0_Code.LET));
			break;
		case Token.GET:
			vyraz(tokenNode.getChild(0));
			vyraz(tokenNode.getChild(1));
			instructions.add(PL0_Code._opr(PL0_Code.GET));
			break;
		case Token.AND:
			vyraz(tokenNode.getChild(0));
			instructions.add(PL0_Code._lit(0));
			instructions.add(PL0_Code._opr(PL0_Code.DIFF));
			vyraz(tokenNode.getChild(1));
			instructions.add(PL0_Code._lit(0));
			instructions.add(PL0_Code._opr(PL0_Code.DIFF));
			instructions.add(PL0_Code._opr(PL0_Code.PLUS));
			instructions.add(PL0_Code._lit(2));
			instructions.add(PL0_Code._opr(PL0_Code.GET));
			break;
		case Token.OR:
			vyraz(tokenNode.getChild(0));
			instructions.add(PL0_Code._lit(0));
			instructions.add(PL0_Code._opr(PL0_Code.DIFF));
			vyraz(tokenNode.getChild(1));
			instructions.add(PL0_Code._lit(0));
			instructions.add(PL0_Code._opr(PL0_Code.DIFF));
			instructions.add(PL0_Code._opr(PL0_Code.PLUS));
			instructions.add(PL0_Code._lit(1));
			instructions.add(PL0_Code._opr(PL0_Code.GET));
			break;
		case Token.EXCL: 
			podminka(tokenNode.getChild(0));
			instructions.add(PL0_Code._lit(0));
			instructions.add(PL0_Code._opr(PL0_Code.EQUAL));
			break;
		}
	}

	private void vyraz(TokenNode tokenNode) {
		if (tokenNode.getToken().getToken() == Token.QUEST) {
			//Na vrcholu zasobniku bude vysledek
			podminka(tokenNode.getChild(0));
			int jpcIndex = instructions.size();
			instructions.add(PL0_Code._jpc(-1));
			vyraz(tokenNode.getChild(1));
			int jmpIndex = instructions.size();
			instructions.add(PL0_Code._jmp(-1));
			//Zmenime skok na spravnou hodnotu
			instructions.set(jpcIndex, PL0_Code._jpc(instructions.size()));
			vyraz(tokenNode.getChild(2));
			//A druhy skok take
			instructions.set(jmpIndex, PL0_Code._jmp(instructions.size()));
		} else {
			term(tokenNode);
		}
	}

	private void term(TokenNode tokenNode) {
		//+ - * /
		//Na vrcholu zasobniku bude vysledek
		switch (tokenNode.getToken().getToken()) {
		case Token.PLUS:
			faktor(tokenNode.getChild(0));
			faktor(tokenNode.getChild(1));
			instructions.add(PL0_Code._opr(PL0_Code.PLUS));
			break;
		case Token.MINUS:
			faktor(tokenNode.getChild(0));
			faktor(tokenNode.getChild(1));
			instructions.add(PL0_Code._opr(PL0_Code.MINUS));
			break;
		case Token.TIMES:
			faktor(tokenNode.getChild(0));
			faktor(tokenNode.getChild(1));
			instructions.add(PL0_Code._opr(PL0_Code.TIMES));
			break;
		case Token.DIVIDE:
			faktor(tokenNode.getChild(0));
			faktor(tokenNode.getChild(1));
			instructions.add(PL0_Code._opr(PL0_Code.DIVIDE));
			break;
		default:
			faktor(tokenNode);
		}
	}

	private void faktor(TokenNode tokenNode) {
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
			call(tokenNode.getChild(0));
			break;
		default: 
			term(tokenNode);
		}
	}

	private void call(TokenNode tokenNode) {
		String name = tokenNode.getToken().getLexem();
		int pocetArg = tokenNode.childCount();
		BlockNode procNode = blockNode.getProc(name);
		int level = blockNode.getLevel() - procNode.getLevel() + 1;
		int index = procNode.getParent().getArgStart();
		for(int i = 0; i < pocetArg; i++) {
			faktor(tokenNode.getChild(i));
			//Hodnota je na vrcholu zasobniku, ulozime na misto pro promenne
			instructions.add(PL0_Code._sto(level, index + i));
		}

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
