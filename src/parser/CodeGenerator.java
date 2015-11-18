package parser;

import java.util.ArrayList;
import java.util.List;

import org.StructureGraphic.v1.DSutils;

public class CodeGenerator {
	private final int PROC_RESERVE_SIZE = 4;

	private TokenNode tokenNode;
	private BlockNode blockNode;
	private int stackIndex;
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
		this.blockNode = new BlockNode(null, 0);
		stackIndex = 0;
		instructions = new ArrayList<>();
		createBlock(true);

		DSutils.show(blockNode, 500, 80);
		return instructions;
	}

	private void createBlock(boolean main) {
		int index = 1;
		if (main) {
			index = 0;
			stackIndex += PROC_RESERVE_SIZE;
			instructions.add(PL0_Code._int(PROC_RESERVE_SIZE));
		}

		if (tokenNode.getChild(index).getToken().getToken() == Token.CONST) {
			//Existuji konstanty - ukladame na zacetek
			TokenNode consts = tokenNode.getChild(index);
			for (int i = 0; i < consts.childCount(); i++) {
				//Pro kazdou mnozinu konstant poznamename konstanty do tabulky, vytvarime jen jednu
				stackIndex++;
				int hodnota = createConst(consts.getChild(i));
				instructions.add(PL0_Code._lit(hodnota));
			}
			index++;
		}
		if (tokenNode.getChild(index).getToken().getToken() == Token.VAR) {
			//Existuji promenne - vytvorime pro ne misto
			TokenNode vars = tokenNode.getChild(index);
			int pocetPromennych = vars.childCount();
			for (int i = 0; i < pocetPromennych; i++) {
				stackIndex++;
				Variable v = new Variable(vars.getChild(i).getToken().getLexem(), false, stackIndex);
				blockNode.addVariable(v);
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
			createProc(tokenNode.getChild(index));
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
			//TODO
			//Zpracovani prikazu
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

	private void createProc(TokenNode procNode) {
		BlockNode newProc = new BlockNode(procNode.getChild(0).getToken().getLexem(), instructions.size());
		//Prepnuti kontextu
		blockNode = blockNode.addChild(newProc);
		tokenNode = procNode;

		TokenNode procArgs = tokenNode.getChild(0);
		int pocetPromennych = procArgs.childCount();
		stackIndex += PROC_RESERVE_SIZE;
		for (int i = 0; i < pocetPromennych; i++) {
			stackIndex++;
			Variable v = new Variable(procArgs.getChild(i).getToken().getLexem(), false, stackIndex);
			newProc.addVariable(v);
		}
		instructions.add(PL0_Code._int((pocetPromennych + PROC_RESERVE_SIZE)));

		createBlock(false);

		//Navrat zpet
		tokenNode = procNode.getParent();
		blockNode = blockNode.getParent();
	}

	private int createConst(TokenNode tokenNode) {
		/*
		 * Rekurzivne zjistime hodnotu a poznamename si konstanty do stromu
		 */
		if (tokenNode.childCount() == 0) return Integer.parseInt(tokenNode.getToken().getLexem());
		int hodnota = createConst(tokenNode.getChild(1));
		Variable v = new Variable(tokenNode.getChild(0).getToken().getLexem(), true, stackIndex);
		blockNode.addVariable(v);
		return hodnota;
	}
}
