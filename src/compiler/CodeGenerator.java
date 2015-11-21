package compiler;

import java.util.ArrayList;
import java.util.List;

public class CodeGenerator {
	//Baze + navr. hodnota
	private final int PROC_RESERVE_SIZE = 4;

	private TokenNode tokenNode;
	private ProcNode procNode;
	private List<String> instructions;

	public List<String> generate(TokenNode root) throws Exception {
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
		this.procNode = new ProcNode(null, 0, 0);
		instructions = new ArrayList<>();
		createBlock(0);

		return instructions;
	}

	private void createBlock(int level) throws Exception {
		int stackIndex = 0;
		int index = 0;
		stackIndex += PROC_RESERVE_SIZE;
		instructions.add(PL0_Code._int(PROC_RESERVE_SIZE));
		//jsme v procedure?
		if (level > 0) {
			//Vytvorime misto pro argumenty
			TokenNode procArgs = tokenNode.getChild(index);
			int pocetPromennych = procArgs.childCount();
			instructions.add(PL0_Code._int(pocetPromennych));
			int argIndex = procNode.getParent().getArgStartIndex();
			//A nastavime jim spravnou hodnotu, poznamename do tabulky
			for (int i = 0; i < pocetPromennych; i++) {
				Variable v = new Variable(procArgs.getChild(i).getToken().getLexem(), false, stackIndex, level);
				procNode.addVariable(v);
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
				//Ukladame zaznam do tabulky
				Variable v = new Variable(vars.getChild(i).getToken().getLexem(), false, stackIndex, level);
				procNode.addVariable(v);
				stackIndex++;
			}
			instructions.add(PL0_Code._int(pocetPromennych));
			index++;
		}

		//Misto pro argumenty volanych fci - odtud si je budou brat
		int maxArgumentu = 0;
		int pomIndex = index;
		//Projdeme vsechny fce a najdeme kolik je maximalni pocet argumentu
		while (tokenNode.getChild(pomIndex).getToken().getToken() == Token.PROC) {
			int pocetArg = tokenNode.getChild(pomIndex).getChild(0).childCount();
			if (maxArgumentu < pocetArg) maxArgumentu = pocetArg;
			pomIndex++;
		}
		procNode.setArgStartIndex(stackIndex);
		if (maxArgumentu > 0) {

			//Tak velke misto pak vyhradime
			instructions.add(PL0_Code._int(maxArgumentu));
		}

		//Vytvorime skok za kod fci
		int insJmpAddr = instructions.size();
		instructions.add(PL0_Code._jmp(-1));
		boolean procDef = false;
		while (tokenNode.getChild(index).getToken().getToken() == Token.PROC) {
			//Prochazime fce, pro kazdou z nich generujeme jeji kod
			procDef = true;
			createProc(tokenNode.getChild(index), level);
			index++;
		}

		if (!procDef) {
			//Nedefinovna zadna fce, zrusime instrukci skoku - je zbytecna
			instructions.remove(insJmpAddr);
		} else {
			//Jsou definovany fce - nastavime hodnotu skoku na spravnou hodnotu - za kod fci
			instructions.set(insJmpAddr, PL0_Code._jmp(instructions.size()));
		}

		//Zpracovavame prikazy dokud nenarazime na return
		while (tokenNode.getChild(index).getToken().getToken() != Token.RETURN) {
			prikaz(tokenNode.getChild(index));
			index++;
		}

		//return, pokud se jedna o fci (ne o main) vratime navr hodnotu (u mainu neni kam)
		if(tokenNode.getParent() != null) {
			//Zjistime co vratit, jinak vracime nulu
			if (tokenNode.getChild(index).childCount() != 0) {
				vyraz(tokenNode.getChild(index).getChild(0));
			} else {
				instructions.add(PL0_Code._lit(0));
			}
			instructions.add(PL0_Code._sto(1, 3));
		}
		//return za misto volani
		instructions.add(PL0_Code._ret());
	}

	private void prikaz(TokenNode tokenNode) throws Exception {
		//Zjistime typ uzlu a zavolame potrebnou metodu
		switch(tokenNode.getToken().getToken()) {
		case Token.CALL:
			//volame fci
			call(tokenNode.getChild(0));
			break;
		case Token.IF:
			//zkontrolujeme podminku
			podminka(tokenNode.getChild(0));
			//provedeme skok pri false na else vetev
			int jpcIndex = instructions.size();
			instructions.add(PL0_Code._jpc(-1));
			//true - zpracujeme prikaz
			prikaz(tokenNode.getChild(1));
			//provedeme skok za else vetev
			int jmpIndex = instructions.size();
			instructions.add(PL0_Code._jmp(-1));
			instructions.set(jpcIndex, PL0_Code._jpc(instructions.size()));
			//existuje i else - provedeme prikaz
			if (tokenNode.childCount() == 3) {
				prikaz(tokenNode.getChild(2));
			}
			instructions.set(jmpIndex, PL0_Code._jmp(instructions.size()));
			break;
		case Token.WHILE:
			//zapamatujeme si kde zacina
			int whileAddr = instructions.size();
			//zpracovani podminky
			podminka(tokenNode.getChild(0));
			//skok pri false za while
			jpcIndex = instructions.size();
			instructions.add(PL0_Code._jpc(-1));
			//Provadime prikaz
			prikaz(tokenNode.getChild(1));
			//Navrat zpet na podminku
			instructions.add(PL0_Code._jmp(whileAddr));
			instructions.set(jpcIndex, PL0_Code._jpc(instructions.size()));
			break;
		case Token.DO:
			//Zapamatujeme si zacatek
			int doAddr = instructions.size();
			//Provedeme prikaz
			prikaz(tokenNode.getChild(0));
			//Zkontrolujeme znegovanou podminku
			podminka(tokenNode.getChild(1));
			instructions.add(PL0_Code._lit(0));
			instructions.add(PL0_Code._opr(PL0_Code.EQUAL));
			//Pri true skocime zpet na do
			instructions.add(PL0_Code._jpc(doAddr));
			break;
		case Token.SWITCH:
			//Vytvorime pole - potrebujeme znat adresu vsech case
			List<Integer> jmpInstructions = new ArrayList<>();
			//Pro kazdy case
			for (int i = 1; i < tokenNode.childCount(); i++) {
				//Zpracuji vyraz (ano, pro kazdy case znovu), na zasobniku se objevi nejaka hodnota
				vyraz(tokenNode.getChild(0));
				//Porovnani s konstantou casu
				instructions.add(PL0_Code._lit(Integer.parseInt(tokenNode.getChild(i).getToken().getLexem())));
				instructions.add(PL0_Code._opr(PL0_Code.EQUAL));

				//Pri false skaceme na dalsi case
				jpcIndex = instructions.size();
				instructions.add(PL0_Code._jpc(-1));
				//jinak provedeme prikaz
				prikaz(tokenNode.getChild(i).getChild(0));
				//po prikazu skocime na konec switche
				jmpInstructions.add(instructions.size());
				instructions.add(PL0_Code._jmp(-1));
				instructions.set(jpcIndex, PL0_Code._jpc(instructions.size()));
			}
			//Opravime jmp vsech case na konec switche
			for (int i = 0; i < jmpInstructions.size(); i++) {
				instructions.set(jmpInstructions.get(i), PL0_Code._jmp(instructions.size()));
			}
			break;
		case Token.ASSIGN:
			//prirazeni
			assign(tokenNode, 0);
		}
	}

	private void assign(TokenNode tokenNode, int lev) throws Exception {
		//Je pravy potomek vyraz?
		if (tokenNode.getChild(1).getToken().getToken() != Token.ASSIGN) {
			//Pokud ano, vyhodnotime
			vyraz(tokenNode.getChild(1));
		}
		else {
			//Jinak se do nej zanorime
			assign(tokenNode.getChild(1), lev + 1);
		}
		//Vlevo promenna do ktere prirazujeme - najdeme ji v tabulce
		Variable var = procNode.getVar(tokenNode.getChild(0).getToken().getLexem());
		//Test na konstantu - vyhodili bychom chybu
		if (var.isConstant()) ErrorHandler.constAssign();
		//ulozime vrchol zasobniku do promenne (na vrcholu je vysledek praveho potomka = to co prirazujeme)
		instructions.add(PL0_Code._sto(procNode.getLevel() - var.getLevel(), var.getStackIndex()));
		//Pokud nejsme na vrcholu, vratime hodnotu zpet na zasobnik - bude potreba o uroven vyse
		if (lev > 0) instructions.add(PL0_Code._lod(procNode.getLevel() - var.getLevel(), var.getStackIndex()));
	}

	private void podminka(TokenNode tokenNode) throws Exception {
		//Na vrcholu zasobniku zbude: 0 false, 1 true; jpc (jmc) skace pri false
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
			//Zpracujeme vyraz a upravime na 0 pri nule, jinak na 1
			vyraz(tokenNode.getChild(0));
			instructions.add(PL0_Code._lit(0));
			instructions.add(PL0_Code._opr(PL0_Code.DIFF));
			//Zpracujeme vyraz a upravime na 0 pri nule, jinak na 1
			vyraz(tokenNode.getChild(1));
			instructions.add(PL0_Code._lit(0));
			instructions.add(PL0_Code._opr(PL0_Code.DIFF));
			//Na true musi byt oba 1 => soucet >= 2
			instructions.add(PL0_Code._opr(PL0_Code.PLUS));
			instructions.add(PL0_Code._lit(2));
			instructions.add(PL0_Code._opr(PL0_Code.GET));
			break;
		case Token.OR:
			//Zpracujeme vyraz a upravime na 0 pri nule, jinak na 1
			vyraz(tokenNode.getChild(0));
			instructions.add(PL0_Code._lit(0));
			instructions.add(PL0_Code._opr(PL0_Code.DIFF));
			//Zpracujeme vyraz a upravime na 0 pri nule, jinak na 1
			vyraz(tokenNode.getChild(1));
			instructions.add(PL0_Code._lit(0));
			instructions.add(PL0_Code._opr(PL0_Code.DIFF));
			//Na true musi byt alespon jeden 1 => soucet >= 1
			instructions.add(PL0_Code._opr(PL0_Code.PLUS));
			instructions.add(PL0_Code._lit(1));
			instructions.add(PL0_Code._opr(PL0_Code.GET));
			break;
		case Token.EXCL: 
			//Provedeme podminku a znegujeme
			podminka(tokenNode.getChild(0));
			instructions.add(PL0_Code._lit(0));
			instructions.add(PL0_Code._opr(PL0_Code.EQUAL));
			break;
		}
	}

	private void vyraz(TokenNode tokenNode) throws Exception {
		if (tokenNode.getToken().getToken() == Token.QUEST) {
			//Podminene prirazeni - zkusime podminku, na zasobniku bude vysledek
			podminka(tokenNode.getChild(0));
			//Pripravime skok za 1. moznost pri false
			int jpcIndex = instructions.size();
			instructions.add(PL0_Code._jpc(-1));
			//Provadime 1. moznost
			vyraz(tokenNode.getChild(1));
			//Pridame skok na konec prirazeni
			int jmpIndex = instructions.size();
			instructions.add(PL0_Code._jmp(-1));
			//Zacina druhy vyraz - upravime skok na nej
			instructions.set(jpcIndex, PL0_Code._jpc(instructions.size()));
			vyraz(tokenNode.getChild(2));
			//konci 2. vyraz, upravime skok za nej
			instructions.set(jmpIndex, PL0_Code._jmp(instructions.size()));
		} else {
			//Zpracovavame term - ny vrcholu zasobniku zbude vysledek
			term(tokenNode);
		}
	}

	private void term(TokenNode tokenNode) throws Exception {
		//Na vrcholu zasobniku bude vysledek
		switch (tokenNode.getToken().getToken()) {
		case Token.PLUS:
			term(tokenNode.getChild(0));
			term(tokenNode.getChild(1));
			instructions.add(PL0_Code._opr(PL0_Code.PLUS));
			break;
		case Token.MINUS:
			term(tokenNode.getChild(0));
			term(tokenNode.getChild(1));
			instructions.add(PL0_Code._opr(PL0_Code.MINUS));
			break;
		case Token.TIMES:
			term(tokenNode.getChild(0));
			term(tokenNode.getChild(1));
			instructions.add(PL0_Code._opr(PL0_Code.TIMES));
			break;
		case Token.DIVIDE:
			term(tokenNode.getChild(0));
			term(tokenNode.getChild(1));
			instructions.add(PL0_Code._opr(PL0_Code.DIVIDE));
			break;
		default:
			faktor(tokenNode);
		}
	}

	private void faktor(TokenNode tokenNode) throws Exception {
		//Na vrcholu zasobniku zbude vysledek
		switch (tokenNode.getToken().getToken()) {
		case Token.IDENT:
			//Faktor je promenna - najdeme jeji vyskyt v tabulce a ulozime ji na vrchol zasobniku
			Variable var = procNode.getVar(tokenNode.getToken().getLexem());
			int level = procNode.getLevel() - var.getLevel();
			instructions.add(PL0_Code._lod(level, var.getStackIndex()));
			break;
		case Token.INT:
			//Faktor je hodnota - dame ji na vrchol zasobniku
			instructions.add(PL0_Code._lit(Integer.parseInt(tokenNode.getToken().getLexem())));
			break;
		case Token.CALL:
			//Faktor je call
			level = call(tokenNode.getChild(0));
			//Vratili jsme se z fce - zapiseme jeji navracenou hodnotu na zasobnik
			instructions.add(PL0_Code._lod(level, 3));
			break;
		}
	}

	private int call(TokenNode tokenNode) throws Exception {
		//Zjistime jmeno metody, pocet argumentu
		String name = tokenNode.getToken().getLexem();
		int pocetArg = tokenNode.childCount();
		//Najdeme metodu v nasi tabulce, zjistime o kolik urovni musime vyse a kam ulozit argumenty
		ProcNode newProcNode = procNode.getProc(name);
		int level = procNode.getLevel() - newProcNode.getLevel() + 1;
		int index = newProcNode.getParent().getArgStartIndex();
		//Provadime ukladani argumentu
		for(int i = 0; i < pocetArg; i++) {
			vyraz(tokenNode.getChild(i));
			instructions.add(PL0_Code._sto(level, index + i));
		}
		//Zavolame fci
		instructions.add(PL0_Code._cal(level, newProcNode.getStartAddr()));
		return level;
	}

	private void createProc(TokenNode newProcNode, int level) throws Exception {
		//Vytvarime novou fci - zadame info o ni do nasi tabulky (stromu)
		ProcNode newProc = new ProcNode(newProcNode.getChild(0).getToken().getLexem(), 
				instructions.size(), level + 1);
		//Prepnuti kontextu do nove fce
		procNode = procNode.addChild(newProc);
		tokenNode = newProcNode;
		
		//Vytvorime jeji kod
		createBlock(level + 1);

		//Vytvoren, prepnuti kontextu zpet
		tokenNode = tokenNode.getParent();
		procNode = procNode.getParent();
	}

	private int createConst(TokenNode tokenNode, int stackIndex, int level) throws Exception {
		//Rekurzivne vytvarime zaznam do tabulky (stromu)
		//Pokud jsme v liste parsujeme hodnotu a vracime zpet
		if (tokenNode.childCount() == 0) return Integer.parseInt(tokenNode.getToken().getLexem());
		//Ziskavame hodnotu rekurzivnim volanim na praveho potomka
		int hodnota = createConst(tokenNode.getChild(1), stackIndex, level);
		//Podle navracene hodnoty vytvorime konstantu a pridame do tabulky
		Variable v = new Variable(tokenNode.getChild(0).getToken().getLexem(), true, stackIndex, level);
		procNode.addVariable(v);
		return hodnota;
	}
}
