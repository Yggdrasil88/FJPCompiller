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
		 * 
		 * Kod:
		 * 	Pro kazdou proceduru:
		 * 		vytvoreni mista pro bazi a navratovou hodnotu (4 mista) 
		 * 			v pripade fce. slouceno s vytvarenim mista pro argumenty
		 * 		vytvareni konstant
		 * 		vytvareni mista pro promenne
		 * 		JMP pokud nasleduji procedury, jinak nic
		 * 			JMP preskoci procedury dane fce
		 * 		vykonny kod
		 * 		pokud se nejedna o main, pak vraceni navr. hodnoty
		 * 		return
		 */
		this.tokenNode = root;
		this.blockNode = new BlockNode(null, 0, 0);
		instructions = new ArrayList<>();
		createBlock(0);
		joinInts();
		DSutils.show(blockNode, 500, 80);
		return instructions;
	}

	private void joinInts() {
		for (int i = 0; i < instructions.size() - 1; i++) {
			if (instructions.get(i).split(" ")[0].toLowerCase().equals("int")) {
				if (instructions.get(i + 1).split(" ")[0].toLowerCase().equals("int")) {
					//Sloucime dva za sebou jdouci prikazy int
					int var1 = Integer.parseInt(instructions.get(i).split(" ")[2]);
					int var2 = Integer.parseInt(instructions.get(i + 1).split(" ")[2]);
					instructions.remove(i);
					instructions.remove(i);
					instructions.add(i, PL0_Code._int(var1 + var2));
					i--;
				}
			}
		}
	}

	private void createBlock(int level) {
		int stackIndex = 1;
		int index = 0;
		stackIndex += PROC_RESERVE_SIZE;
		instructions.add(PL0_Code._int(PROC_RESERVE_SIZE));
		if (level > 0) {
			TokenNode procArgs = tokenNode.getChild(index);
			int pocetPromennych = procArgs.childCount();
			for (int i = 0; i < pocetPromennych; i++) {
				Variable v = new Variable(procArgs.getChild(i).getToken().getLexem(), false, stackIndex, level);
				blockNode.addVariable(v);
				stackIndex++;
			}
			instructions.add(PL0_Code._int(pocetPromennych));
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
		//Budeme skakat za def. precedur
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
			createCommand();
			index++;
		}

		if(tokenNode.getParent() != null) {
			int returnValue = 0;
			if (tokenNode.getChild(index).childCount() != 0) {
				//TODO
				//Zpracovat vyraz
			}
			instructions.add(PL0_Code._lit(returnValue));
			instructions.add(PL0_Code._sto(1, 3));
		}
		instructions.add(PL0_Code._ret());
	}

	private void createCommand() {
		instructions.add("prikaz");
		
	}

	private void createProc(TokenNode procNode, int level) {
		BlockNode newProc = new BlockNode(procNode.getChild(0).getToken().getLexem(), instructions.size(), level + 1);
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
