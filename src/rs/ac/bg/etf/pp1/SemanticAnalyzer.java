package rs.ac.bg.etf.pp1;

import org.apache.log4j.Logger;

import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.symboltable.*;
import rs.etf.pp1.symboltable.concepts.*;
import rs.etf.pp1.symboltable.structure.HashTableDataStructure;
import rs.etf.pp1.symboltable.structure.SymbolDataStructure;

import java.util.*;

public class SemanticAnalyzer extends VisitorAdaptor {

	/******************************  atributi  ****************************************/
	
	int printCallCount = 0;
	int varDeclCount = 0;
	Obj currentMethod = null;
	Obj currentClass = null;
	Obj currentRecord = null;
	boolean returnFound = false;
	boolean errorDetected = false;
	int nVars;
	int nFormPars = 0;
	public static Struct boolStructNode = null;
	public static Struct recordStructNode = null;
	public static Struct matrixStructNode = null;
	Struct arrType = new Struct(Struct.Array, Tab.nullType);
	ArrayList<Obj> VarTempList = new ArrayList<Obj>();
	ArrayList<Obj> ConstTempList = new ArrayList<Obj>();
	ArrayList<Struct> ExprTempList = new ArrayList<Struct>();
	//SymbolDataStructure fieldsOfCurrClass = null;
	
	/******************************  pomocne funkcije  ****************************************/
	
	SemanticAnalyzer() {
		super();
		boolStructNode = new Struct(5);
		Tab.insert(Obj.Type, "bool", boolStructNode);
		recordStructNode = new Struct(6);
		Tab.insert(Obj.Type, "Record", recordStructNode);
		matrixStructNode = new Struct(7);
		Tab.insert(Obj.Type, "Matrix", matrixStructNode);
	}
	
	Logger log = Logger.getLogger(getClass());

	public void report_error(String message, SyntaxNode info) {
		errorDetected = true;
		StringBuilder msg = new StringBuilder(message);
		int line = (info == null) ? 0: info.getLine();
		if (line != 0)
			msg.append (" na liniji ").append(line);
		log.error(msg.toString());
	}

	public void report_info(String message, SyntaxNode info) {
		StringBuilder msg = new StringBuilder(message); 
		int line = (info == null) ? 0: info.getLine();
		if (line != 0)
			msg.append (" na liniji ").append(line);
		log.info(msg.toString());
	}
	
	/******************************  program  ****************************************/
    //public static Obj pomocnaPromenljiva = null;
	
    public void visit(ProgName progName){
    	progName.obj = Tab.insert(Obj.Prog, progName.getProgName(), Tab.noType);
    	Tab.openScope();
    	//pomocnaPromenljiva = Tab.insert(Obj.Var, "pomocna", Tab.intType);
    }
    
    public void visit(Program program){
    	
    	Obj mainMethod = Tab.find("main");
		if (mainMethod.equals(Tab.noObj))
			report_error("Nije pronadjena main funkcija", null);
		if (mainMethod.getType() != Tab.noType)
			report_error("Main mora biti deklarisana kao void funkcija", null);
		if (mainMethod.getLevel() != 0)
			report_error("Greska: Main ima " + mainMethod.getLevel() + " formalnih parametara, a ocekivano 0", null);
    	
    	nVars = Tab.currentScope.getnVars();
    	Tab.chainLocalSymbols(program.getProgName().obj);
    	Tab.closeScope();
    }
    
    /******************************  konstante  ****************************************/

    public void visit(ConstDeclNum decl){
    	String ime = decl.getConstName();
		if (Tab.find(ime) != Tab.noObj) {
			report_error("Greska: ime " + ime + ", vec postoji! ", decl);
			return;
		}
		
		if (decl.getType().struct == Tab.intType) {
			report_info("Deklarisana konstanta " + ime + " tipa int vrednosti " + decl.getValue(), decl);
			Obj myObj = Tab.insert(Obj.Con, ime, Tab.intType);
			myObj.setAdr(decl.getValue());
		} else {
			report_error("Greska, tipovi sa leve i desne strane se ne slazu", decl);
		}
		
		if(decl.getConstDeclList() instanceof ConstDeclListNum) {
			for(int i = 0; i < ConstTempList.size(); i++) {
				int vrednost = ConstTempList.get(i).getAdr();
				String ime1 = ConstTempList.get(i).getName();
				report_info("Deklarisana konstanta " + ime1 + " tipa int vrednosti " + vrednost, decl);
				Tab.insert(Obj.Con, ime1, decl.getType().struct);
				Tab.find(ime1).setAdr(vrednost);
			}
			ConstTempList.clear();
		}
    }
    
    public void visit(ConstDeclListNum decl){
		Obj tmp = new Obj(Obj.Con, decl.getConstName(), Tab.intType);
		tmp.setAdr(decl.getValue());
		ConstTempList.add(tmp);
    }
    
    public void visit(ConstDeclChar decl){
    	String ime = decl.getConstName();
		if (Tab.find(ime) != Tab.noObj) {
			report_error("Greska: ime " + ime + ", vec postoji! ", decl);
			return;
		}
		
		if (decl.getType().struct == Tab.charType) {
			report_info("Deklarisana konstanta " + ime + " tipa char vrednosti " + decl.getValue(), decl);
			Obj myObj = Tab.insert(Obj.Con, ime, Tab.charType);
			myObj.setAdr(decl.getValue());
		} else {
			report_error("Greska, tipovi sa leve i desne strane se ne slazu", decl);
		}
		
		if(decl.getConstDeclList() instanceof ConstDeclListChar) {
			for(int i = 0; i < ConstTempList.size(); i++) {
				report_info("Deklarisana konstanta "+ ConstTempList.get(i).getName(), decl);
				int vrednost = ConstTempList.get(i).getAdr();
				String ime1 = ConstTempList.get(i).getName();
				Tab.insert(Obj.Con, ime1, decl.getType().struct);
				Tab.find(ime1).setAdr(vrednost);
			}
			ConstTempList.clear();
		}
    }
    
    public void visit(ConstDeclListChar decl){
		Obj tmp = new Obj(Obj.Con, decl.getConstName(), Tab.charType);
		tmp.setAdr(decl.getValue());
		ConstTempList.add(tmp);
    }
    
    public void visit(ConstDeclBool decl){
    	String ime = decl.getConstName();
		if (Tab.find(ime) != Tab.noObj) {
			report_error("Greska: ime " + ime + ", vec postoji! ", decl);
			return;
		}
		
		if (decl.getType().struct == Tab.find("bool").getType()) {
			report_info("Deklarisana konstanta " + ime + " tipa bool vrednosti " + decl.getValue(), decl);
			Obj myObj = Tab.insert(Obj.Con, ime, Tab.find("bool").getType());
			if (decl.getValue().equals("true")) myObj.setAdr(1);
			else myObj.setAdr(0);
		} else {
			report_error("Greska, tipovi sa leve i desne strane se ne slazu", decl);
		}
		
		if(decl.getConstDeclList() instanceof ConstDeclListBool) {
			for(int i = 0; i < ConstTempList.size(); i++) {
				int vrednost = ConstTempList.get(i).getAdr();
				String ime1 = ConstTempList.get(i).getName();
				report_info("Deklarisana konstanta " + ime1 + " tipa bool vrednosti " + vrednost, decl);
				Tab.insert(Obj.Con, ime1, decl.getType().struct);
				Tab.find(ime1).setAdr(vrednost);
			}
			ConstTempList.clear();
		}
    }
    
    public void visit(ConstDeclListBool decl){
		Obj tmp = new Obj(Obj.Con, decl.getConstName(), Tab.find("bool").getType());
		if (decl.getValue().equals("true")) tmp.setAdr(1);
		else tmp.setAdr(0);
		ConstTempList.add(tmp);
    }
    
    /******************************  promenljive  ****************************************/
    
    public static List<Obj> listaFinalNizova = new ArrayList<>();
    
	public void visit(VarDecl varDecl){
		int kind;
		if(currentClass != null || currentRecord != null) kind = Obj.Fld;
		else {
			kind = Obj.Var;
			if(currentMethod != null)varDeclCount++;
		}
		if(Tab.find(varDecl.getVarName()) != Tab.noObj) {
			report_error("promenljiva pod imenom " + varDecl.getVarName() + " je vec definisana", null);
		}
		else {
			if(varDecl.getZagrade() instanceof ZagradeOne) {
				ZagradeOne z = (ZagradeOne) varDecl.getZagrade();
				Struct arrType = new Struct(Struct.Array, varDecl.getType().struct);
				Obj o = Tab.insert(kind, varDecl.getVarName(), arrType);
				if(varDecl.getFinalDef() instanceof FinalDefYes) listaFinalNizova.add(o);
				report_info("Deklarisana promenljiva "+ varDecl.getVarName() + " tipa niz " + arrType.getKind() + ":" +arrType.getElemType().getKind() +":" + Tab.find(varDecl.getVarName()).getType().getKind(), varDecl);
			}
			else {
				report_info("Deklarisana promenljiva "+ varDecl.getVarName() + " tipa " + varDecl.getType().struct.getKind(), varDecl);
				Tab.insert(kind, varDecl.getVarName(), varDecl.getType().struct);
			}
		}
		
		
		if(varDecl.getVarList() instanceof VarListYes) {
			for(int i = 0; i < VarTempList.size(); i++) {
				if(currentClass != null || currentRecord != null);
				else if(currentMethod != null)varDeclCount++;
				if(Tab.find(VarTempList.get(i).getName()) != Tab.noObj) {
					report_error("promenljiva pod imenom " + VarTempList.get(i).getName() + " je vec definisana", varDecl);
					continue;
				}
				if(VarTempList.get(i).getType() == arrType) {
					Struct arrType = new Struct(Struct.Array, varDecl.getType().struct);
					Tab.insert(kind, VarTempList.get(i).getName(), arrType);
					report_info("Deklarisana promenljiva "+ VarTempList.get(i).getName() + " tipa niz " + arrType.getKind() + ":" +arrType.getElemType().getKind() +":" + Tab.find( VarTempList.get(i).getName()).getType().getKind(), varDecl);
				}
				else {
					Tab.insert(kind, VarTempList.get(i).getName(), varDecl.getType().struct);
					report_info("Deklarisana promenljiva "+ VarTempList.get(i).getName() + " tipa " + varDecl.getType().struct.getKind(), varDecl);
				}
			}
			VarTempList.clear();
		}
	}
    
	public void visit(VarListYes varListYes){
		Obj varNode;
		if(varListYes.getZagrade() instanceof ZagradeOne) {
			varNode = new Obj(Obj.Var, varListYes.getVarName(), arrType);
		}
		else varNode = new Obj(Obj.Var, varListYes.getVarName(), Tab.nullType);
		VarTempList.add(varNode);
		//fieldsOfCurrClass.insertKey(varNode);
	}
	
	public void visit(VarDeclListYes varDeclList){
    	if(currentClass != null || currentRecord != null) {
    		Obj tmp = new Obj(Obj.Fld, varDeclList.getVarDecl().getVarName(), varDeclList.getVarDecl().getType().struct);
    	}
    }
    
    /******************************  klase i strukture  ****************************************/
    
    public void visit(ClassDeclName classDeclName){
    	currentClass = Tab.insert(Obj.Type, classDeclName.getClassName(), new Struct(Struct.Class));
    	classDeclName.obj = currentClass;
    	Tab.openScope();
		report_info("Obradjuje se klasa " + classDeclName.getClassName(), classDeclName);
    }
    
    public void visit(ClassDecl classDecl) {
    	Tab.chainLocalSymbols(currentClass.getType());
    	Tab.closeScope();
    	report_info("Izlazak iz klase " + classDecl.getClassDeclName().getClassName()+ " " + currentClass.getType().getNumberOfFields(), classDecl);
    	currentClass = null;
    }
    
    public void visit(RecordDeclName recordDeclName){
    	currentRecord = Tab.insert(Obj.Type, recordDeclName.getRecordName(), new Struct(recordStructNode.getKind()));
    	recordDeclName.obj = currentRecord;
    	Tab.openScope();
		report_info("Obradjuje se record " + recordDeclName.getRecordName(), recordDeclName);
    }
    
    public void visit(RecordDecl recordDecl){
    	Tab.chainLocalSymbols(currentRecord.getType());
    	Tab.closeScope();
    	report_info("Izlazak iz recorda " + currentRecord.getName() + " " + currentRecord.getType().getNumberOfFields(), recordDecl);
    	currentRecord = null;
    }
    
    public void visit(ExtendsOne extendsOne){ 
    	if(extendsOne.getType().struct.compatibleWith(recordStructNode)) {
    		report_error("Greska: record " + extendsOne.getType().getTypeName() + " se ne moze nasledjivati ", extendsOne);
    	}
    }
    
    /******************************  metode  ****************************************/
	
	public void visit(FormPars formPars){
		report_info("parametar f-je: "+ formPars.getVarName(), formPars);
		nFormPars++;
		if(formPars.getZagrade() instanceof ZagradeOne) {
			Struct arrType = new Struct(Struct.Array, formPars.getType().struct);
			Tab.insert(Obj.Var, formPars.getVarName(), arrType);
		}
		else Tab.insert(Obj.Var, formPars.getVarName(), formPars.getType().struct);
		
    }
    
    public void visit(FormListYes formPars){
    	report_info("parametar f-je: "+ formPars.getVarName(), formPars);
    	nFormPars++;
		if(formPars.getZagrade() instanceof ZagradeOne) {
			Struct arrType = new Struct(Struct.Array, formPars.getType().struct);
			Tab.insert(Obj.Var, formPars.getVarName(), arrType);
		}
		else Tab.insert(Obj.Var, formPars.getVarName(), formPars.getType().struct);
    }
    
    public void visit(TypeWhatType typeWhat){
    	Obj typeNode = Tab.find(typeWhat.getType().getTypeName());
    	if(typeNode == Tab.noObj){
    		report_error("Nije pronadjen tip " + typeWhat.getType().getTypeName() + " u tabeli simbola! ", null);
    		typeWhat.struct = Tab.noType;
    	}else{
    		if(Obj.Type == typeNode.getKind()){
    			typeWhat.struct = typeNode.getType();
    		}else{
    			report_error("Greska: Ime " + typeWhat.getType().getTypeName() + " ne predstavlja tip!", typeWhat);
    			typeWhat.struct = Tab.noType;
    		}
    	}
    }
    
    public void visit(TypeWhatVoid typeWhatVoid){
    	typeWhatVoid.struct = Tab.noType;
    }
   
    public void visit(MethodTypeName methodTypeName){
    	currentMethod = Tab.insert(Obj.Meth, methodTypeName.getMethName(), methodTypeName.getTypeWhat().struct);
    	methodTypeName.obj = currentMethod;
    	Tab.openScope();
		report_info("Obradjuje se funkcija " + methodTypeName.getMethName(), methodTypeName);
    }
    
    public void visit(MethodDecl methodDecl){
    	if(!returnFound && currentMethod.getType() != Tab.noType){
			report_error("Semanticka greska na liniji " + methodDecl.getLine() + ": funkcija " + currentMethod.getName() + " nema return iskaz!", null);
    	}
    	Tab.chainLocalSymbols(currentMethod);
    	Tab.closeScope();
    	currentMethod.setLevel(nFormPars);
    	report_info("deklariana fja " + currentMethod.getName() + ", broj argumenata " + nFormPars, methodDecl);
    	
    	returnFound = false;
    	currentMethod = null;
    	nFormPars = 0;
    }
    
	public void visit(ActParsOne par){
		ExprTempList.add(par.getExpr().struct);
		//report_info("parametar--- " + par.getExpr().struct.getKind() , null);
	}
    
    public void visit(ActParsMany list){
    	ExprTempList.add(list.getExpr().struct);
    	//report_info("parametar----- " + list.getExpr().struct.getKind() , null);
    }
    
    /******************************  izrazi  ****************************************/
    
    public void visit(SingleStatementReturn returnExpr){
    	returnFound = true;
    	Struct currMethType = currentMethod.getType();
    	int kindOfRet = returnExpr.getExpr().getTerm().getFactor().struct.getKind();
    	if(kindOfRet == Struct.Array) {
    		kindOfRet = returnExpr.getExpr().getTerm().getFactor().struct.getElemType().getKind();
    	}
    	if(currMethType.getKind() != kindOfRet){
			report_error("Greska na liniji " + returnExpr.getLine() + " : " 
					+ "tip izraza u return naredbi ne slaze se sa tipom povratne vrednosti funkcije "
					+ currMethType.getKind() + " " + returnExpr.getExpr().getTerm().getFactor().struct.getKind() + " " + currentMethod.getName(), returnExpr);
		}
    }
    
    public void visit(SingleStatementReturnNothing returnExpr){
    	returnFound = true;
    	Struct currMethType = currentMethod.getType();
    	if(!currMethType.compatibleWith(Tab.noType)) {
    		report_error("Greska na liniji " + returnExpr.getLine() + " : " 
    				+ "tip izraza u return naredbi ne slaze se sa tipom povratne vrednosti funkcije " + currentMethod.getName(), returnExpr);
    	}
    }
    
    public void visit(Expr expr){
    	expr.struct = expr.getTerm().struct;
    	if(expr.struct != Tab.intType) {
    		if(expr.getMinusA() instanceof MinusOne || expr.getExprFragmentList() instanceof ExprFragmentListYes) {
    			report_error("Greska: tip koji nije int ne moze u aritmetickom izrazu ", expr);
    			//return;
    		}
    	}
    	//report_info("Expr......." + expr.getTerm().struct.getKind() + " tip je u izrazu ", expr);
//    	if(!(expr.getTerm().getFactor() instanceof FactorNum)) {
//    		report_error("Greska: tipovi moraju biti int", term);
//    	}
    }
    
    public void visit(DesignatorOne designator) {
    	designator.obj = Tab.noObj;
    	if(Tab.find(designator.getName()) == Tab.noObj) {
    		report_error("Greska na liniji " + designator.getLine() + " : " + " promenljiva " + designator.getName() + " nije deklarisana ", null);
    	}
    	designator.obj = Tab.find(designator.getName());
    	//report_info( designator.getName() + ",,,,,,,,,,,," + designator.obj.getType().getKind(), null);
    }
    
    public void visit(DesignatorArray desList) {
    	desList.obj = Tab.noObj;
    	String ime = desList.getDesignator().obj.getName();
    	if(desList.getDesignator().obj.getType().getKind() != Struct.Array && desList.getParent() instanceof DesignatorArray) {
    		report_error("Greska: promenljiva " + ime + " nije nizovskog tipa, vec tipa " + Tab.find(ime).getKind(), desList);
    		return;
    	}
    	Struct praviTip = desList.getDesignator().obj.getType().getElemType();
    	desList.obj = new Obj(Obj.Var, "", praviTip);
    }
    
    public void visit(DesignatorField desList) {
    	String imePolja = desList.getFieldName();
    	String imeKlase = desList.getDesignator().obj.getName();
    	desList.obj = Tab.noObj;
    	int tip = Tab.find(imeKlase).getType().getKind();
    	
    	if(tip != Struct.Class && tip != recordStructNode.getKind()) {
    		report_error("Greska: promenljiva " + imeKlase + " nije klasnog tipa, vec " + tip , desList);
    		return;
    	}
    	
    	SymbolDataStructure symbDataStr = desList.getDesignator().obj.getType().getMembersTable();
    	HashTableDataStructure ss = (HashTableDataStructure) symbDataStr;
		Obj found = symbDataStr.searchKey(imePolja);
		
		if(found == null) {
			report_error("greska: Objekat " + imeKlase + " " + desList.getDesignator().obj.getType().getKind() + " nema polje " + imePolja + ", greska", desList);
			return;
		}
		
    	desList.obj = found;
    	//report_info("......" + desList.obj.getName(), null);
    }
    
	public void visit(CondFactOne cond) {
    	if(cond.getExpr().struct.getKind() != boolStructNode.getKind()) {
    		report_error("Tip unutar uslova nije bool, vec " + cond.getExpr().struct.getKind(), cond);
    	}
	}
	
//	public void visit(DesignatorKapica des) {
//		des.obj = Tab.noObj;
//    	String ime = des.getDesignator().obj.getName();
//    	if(des.getDesignator().obj.getType().getKind() != Struct.Array) {
//    		report_error("Greska: promenljiva " + ime + " nije nizovskog tipa, vec tipa " + Tab.find(ime).getKind(), des);
//    		return;
//    	}
//    	des.obj = des.getDesignator().obj;
//    	
//	}
	
	public void visit(CondFactTwo cond) {
    	Struct leftExpr1 = cond.getExpr1().struct;
    	Struct leftExpr = cond.getExpr().struct;
    	if(!leftExpr.compatibleWith(leftExpr1)) {
    		report_error("Tipovi koji se porede nisu kompatibilni", cond);
    	}
    	if(leftExpr.isRefType() || leftExpr1.isRefType()) {
    		if(!(cond.getRelop() instanceof RelopJed) && !(cond.getRelop() instanceof RelopNejed))
    		report_error("Uz promenljive tipa klase ili niza, od relacionih operatora, mogu se koristiti samo != i ==", cond);
    	}
	}
	
	public void visit(ExprInt expr) {
		expr.struct = expr.getExpr().struct;
		if(expr.getExpr().struct.getKind() != Struct.Int) {
			report_error("Greska na liniji " + expr.getLine() + ": promenljiva unutar [] nije tipa int, vec " + expr.getExpr().struct.getKind(), null);
			return;
		}
	}
    
    /******************************  tipovi  ****************************************/
    
    public void visit(Type type){
    	Obj typeNode = Tab.find(type.getTypeName());
    	if(typeNode == Tab.noObj){
    		report_error("Nije pronadjen tip " + type.getTypeName() + " u tabeli simbola! ", null);
    		type.struct = Tab.noType;
    	}else{
    		if(Obj.Type == typeNode.getKind()){
    			type.struct = typeNode.getType();
    		}else{
    			report_error("Greska: Ime " + type.getTypeName() + " ne predstavlja tip!", type);
    			type.struct = Tab.noType;
    		}
    	}
    }
    
    public void visit(FactorDes fact){ 
    	fact.struct = fact.getDesignator().obj.getType();
    	//report_info(fact.getDesignator().obj.getName() +"............" + fact.getDesignator().obj.getType().getKind(), null);
    }
    
    public void visit(FactorDesZagr fact) {
    	fact.struct = fact.getDesignator().obj.getType();
    	//report_info(fact.getDesignator().obj.getName() +"............" + fact.struct.getKind(), null);
    }
    
    public void visit(FactorDesActPars fact){ 
    	Obj metod = fact.getDesignator().obj;
    	fact.struct = Tab.noType;
    	
    	if (Tab.find(metod.getName()) == Tab.noObj || Tab.find(metod.getName()).getKind() != Obj.Meth) {
			report_error("Metod sa imenom " + metod.getName() + " ne postoji", fact);
		}
    	else {
    		Collection<Obj> argumenti = metod.getLocalSymbols();
    		int j = 0;
    		if(metod.getLevel() != ExprTempList.size()) {
    			//report_error("Metod sa imenom " + metod.getName() + " ima drugaciji broj argumenata", fact);
    			//report_error("Metod sa imenom " + metod.getName() + " ima " + argumenti.size() + " argumenata, a ne " + ExprTempList.size(), fact);
    			report_error("Metod sa imenom " + metod.getName() + " ima " + metod.getLevel() + " argumenata, a ne " + ExprTempList.size(), fact);
    		}
    		else {
    			if(!metod.getName().equals("len")) {
    				for(Obj izraz: argumenti) {
        				//report_info("Metod sa imenom " + metod.getName() + " ima arg " + izraz.getType().getKind() + " " + ExprTempList.get(j).getKind(), fact);
            			if(!izraz.getType().compatibleWith(ExprTempList.get(j++))) {
            				report_error("Metod sa imenom " + metod.getName() + " ima drugacije argumente", fact);
            			}
            			if(j == metod.getLevel()) break;
            		}
    			}
    		}
    		//report_info("Metod sa imenom " + metod.getName() + " " + metod.getLevel() + "  " + ExprTempList.size() + " " + argumenti.size(), fact);
    	}
    	
    	fact.struct = fact.getDesignator().obj.getType();
    	ExprTempList = new ArrayList<Struct>();
    }
    
    public void visit(DesActPars fact){
    	Obj metod = fact.getDesignator().obj;
    	fact.struct = Tab.noType;
    	
    	if (Tab.find(metod.getName()) == Tab.noObj || Tab.find(metod.getName()).getKind() != Obj.Meth) {
			report_error("Metod sa imenom " + metod.getName() + " ne postoji", fact);
		}
    	else {
    		Collection<Obj> argumenti = metod.getLocalSymbols();
    		int j = 0;
    		if(metod.getLevel() != ExprTempList.size()) {
    			report_error("Metod sa imenom " + metod.getName() + " ima drugaciji broj argumenata", fact);
    		}
    		else {
    			if(!metod.getName().equals("len")) {
	    			for(Obj izraz: argumenti) {
	    				//report_info("Metod sa imenom " + metod.getName() + " ima arg " + izraz.getType().getKind() + " " + ExprTempList.get(j).getKind(), fact);
	        			if(!izraz.getType().compatibleWith(ExprTempList.get(j++))) {
	        				report_error("Metod sa imenom " + metod.getName() + " ima drugacije argumente", fact);
	        			}
	        			if(j == metod.getLevel()) break;
	        		}
    			}
    		}
    	}
    	ExprTempList  = new ArrayList<Struct>();
		fact.struct = fact.getDesignator().obj.getType();
    }
    
    public void visit(DesAssign designator){ 
    	designator.struct = designator.getDesignator().obj.getType();
    	if(designator.getDesignator().obj.getType().getKind() != designator.getExpr().struct.getKind()) {
    		report_error("tipovi se ne podudaraju: " + designator.getDesignator().obj.getType().getKind() + " i " + designator.getExpr().struct.getKind(), designator);
    	}
    }
    
    public void visit(DesZagrade designator){ designator.struct = designator.getDesignator().obj.getType(); }
    
    public void visit(DesPlus designator){ 
    	designator.struct = designator.getDesignator().obj.getType(); 
    	if(designator.getDesignator().obj.getType() != Tab.intType) {
			report_error("greska1: tip operanada mora biti int", designator);
		}
    }
    
    public void visit(DesPlusPlus designator){ 
    	designator.struct = designator.getDesignator().obj.getType(); 
    	if(designator.getDesignator().obj.getType() != Tab.intType) {
			report_error("greska1: tip operanada mora biti int", designator);
		}
    }
    
    public void visit(DesMinus designator){ 
    	designator.struct = designator.getDesignator().obj.getType(); 
    	if(designator.getDesignator().obj.getType() != Tab.intType) {
			report_error("greska2: tip operanada mora biti int", designator);
		}
    }
    
    public void visit(FactorNum fact){ fact.struct = Tab.intType; }
    
    public void visit(FactorChar fact){ fact.struct = Tab.charType; }
    
    public void visit(FactorBool fact){ fact.struct = boolStructNode; }
    
    public void visit(FactorType fact){ 
    	Obj tmp = Tab.find(fact.getType().getTypeName());
    	if(tmp == Tab.noObj) {
    		report_error("greska: tip " + fact.getType().getTypeName() + " nije definisan", fact);
    	}
    	if(fact.getZagrade2() instanceof ZagradeOne2) {
    		fact.struct = new Struct(Struct.Array, fact.getType().struct);
    	}
    	else fact.struct = fact.getType().struct;
    	//report_info( "FactorType......tip je " + fact.struct.getKind(), fact);
    }
    
    public void visit(FactorExpr fact){ fact.struct = Tab.intType; }
    
    public void visit(FactorTaraba fact){ fact.struct = /*new Struct(Struct.Array, Tab.intType);*/Tab.intType; }
    
    public void visit(Factor fact){ fact.struct = fact.getBaseExp().struct; }
    
    public void visit(Term term){ 
    	term.struct = term.getFactor().struct; 
    	
    	if(term.getTermFragment() instanceof TermFragList) {
    		TermFragList list = (TermFragList) term.getTermFragment();
//    		if(list.getMulop() instanceof MulopMajmunce) {
//    			if(term.getFactor().struct.getKind() != Struct.Array) {
//        			report_error("greska: tip prvog operanada mora biti niz, a ne " + term.getFactor().struct.getKind(), term);
//        		}
//    			else if(!(list.getFactor() instanceof FactorNum)) {
//    				report_error("greska: tip drugog operanada mora biti int, a ne " + list.getFactor().struct.getKind(), term);
//    			}
//    			else term.struct = term.getFactor().struct.getElemType();
//    			
//    		}
    		if(term.getFactor().struct != Tab.intType) {
    			report_error("greska3: tip operanada mora biti int, a ne " + term.getFactor().struct.getKind(), term);
    		}
    	}
    	//report_info( "Term......tip je " + term.struct.getKind(), term);
    }
    
    public void visit(TermFragList term){ 
    	if(term.getFactor().struct != Tab.intType) {
			report_error("greska4: tip operanada mora biti int", term);
		}
    }
    
    public void visit(ExprFragmentListYes list){ 
    	if(list.getTerm().getFactor().struct != Tab.intType) {
			report_error("greska5: tip operanada mora biti int", list);
		}
    }
  /*  
    public void visit(DesTaraba taraba){ 
    	//samo niz moze ovde
    	if(taraba.getDesignator().obj.getType().getKind() != Struct.Array) {
    		report_error("greska6: tip operanada mora biti niz", taraba);
    	}
    	
    }
 */   
    
//	public void visit(FactorFrag2Yes frag) {
//	if(frag.getExpr().struct.getKind() != Struct.Int) {
//		report_error("Greska na liniji " + frag.getLine() + ": promenljiva unutar [] nije tipa int", frag);
//		return;
//	}
//}
        
    public boolean passed(){
    	return !errorDetected;
    }
}
