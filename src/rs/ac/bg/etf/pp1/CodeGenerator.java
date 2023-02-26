package rs.ac.bg.etf.pp1;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import rs.ac.bg.etf.pp1.CounterVisitor.FormParamCounter;
import rs.ac.bg.etf.pp1.CounterVisitor.VarCounter;
import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.mj.runtime.*;
import rs.etf.pp1.symboltable.*;
import rs.etf.pp1.symboltable.concepts.*;
import rs.etf.pp1.symboltable.structure.*;

public class CodeGenerator extends VisitorAdaptor {
	private int mainPc;
	
	public int getMainPc(){
		return mainPc;
	}
	
	private Obj powObj = new Obj(Obj.Meth, "pow", new Struct(Struct.Int));
	private Obj powObjPom = new Obj(Obj.Meth, "powPom", new Struct(Struct.Int));
	
	public CodeGenerator() {
		Obj chrObj = Tab.find("chr");
        Obj ordObj = Tab.find("ord");
        chrObj.setAdr(Code.pc);
        ordObj.setAdr(Code.pc);
        Code.put(Code.enter);
        Code.put(1);
        Code.put(1);
        Code.put(Code.load_n);
        Code.put(Code.exit);
        Code.put(Code.return_);

        Obj lenObj = Tab.find("len");
        lenObj.setAdr(Code.pc);
        Code.put(Code.enter);
        Code.put(1);
        Code.put(1);
        Code.put(Code.load_n);
        Code.put(Code.arraylength);
        Code.put(Code.exit);
        Code.put(Code.return_);
        
        powObj.setAdr(Code.pc);
        Code.put(Code.enter);
        Code.put(3);
        Code.put(3);
        Code.put(Code.load_1); 
        Code.put(Code.const_1);
        Code.putFalseJump(Code.eq, 25);
        Code.put(Code.load); Code.put(0);
        Code.put(Code.exit);
        Code.put(Code.return_);
        Code.put(Code.load); Code.put(0);
        Code.put(Code.load_2);
        Code.put(Code.mul);
        Code.put(Code.load_1);
        Code.put(Code.const_1);
        Code.put(Code.sub);
        Code.put(Code.load_2);
        int offset = powObj.getAdr() - Code.pc;
		Code.put(Code.call);
		Code.put2(offset);
		Code.put(Code.exit);
        Code.put(Code.return_);
        
        powObjPom.setAdr(Code.pc);
        Code.put(Code.enter);
        Code.put(2);
        Code.put(2);
        Code.put(Code.load); Code.put(0);
        Code.put(Code.const_1);
        Code.putFalseJump(Code.eq, 50);
        Code.put(Code.const_1);
        Code.put(Code.exit);
        Code.put(Code.return_);
        Code.put(Code.load); Code.put(0);
        Code.put(Code.load_1);
        Code.put(Code.load); Code.put(0);
        
        offset = powObj.getAdr() - Code.pc;
		Code.put(Code.call);
		Code.put2(offset);
        
		Code.put(Code.exit);
        Code.put(Code.return_);

	}
	
	private void zameniDvaElementa() {// v1 v2
		Code.put(Code.dup_x1);//v2 v1 v2
		Code.put(Code.pop);// v2 v1
	}
	
	private void  dohvatiTreci() {
		// niz index value
		Code.put(Code.dup_x2);// value niz index value
		Code.put(Code.pop);// value niz index
		Code.put(Code.dup_x2);// index value niz index
		Code.put(Code.pop);// index value niz
		Code.put(Code.dup_x2);// niz index value niz
		}
		
	private void  dohvatiDrugi() {
					// niz index value
		Code.put(Code.dup_x1);// niz value index value
		Code.put(Code.pop);// niz value index
		Code.put(Code.dup_x1);// niz index value index
	}
	
	public void visit(ConstSomeNum cnst){
		Obj con = Tab.insert(Obj.Con, "$", Tab.intType);
		con.setLevel(0);
		con.setAdr(cnst.getValue());
		
		Code.load(con);
	}
	
	public void visit(ConstSomeChar cnst){
		Obj con = Tab.insert(Obj.Con, "$", Tab.charType);
		con.setLevel(0);
		con.setAdr(cnst.getValue());
		
		Code.load(con);
	}
	
	public void visit(ConstSomeBool cnst){
		Obj con = Tab.insert(Obj.Con, "$", SemanticAnalyzer.boolStructNode);
		con.setLevel(0);
		if(cnst.getValue().equals("true")) con.setAdr(1);
		else con.setAdr(0);
		
		Code.load(con);
	}
	
	public void visit(FactorNum cnst){
		Obj con = Tab.insert(Obj.Con, "$", Tab.intType);
		con.setLevel(0);
		con.setAdr(cnst.getValue());
		
		Code.load(con);
	}
	
	public void visit(FactorChar cnst){
		Obj con = Tab.insert(Obj.Con, "$", Tab.charType);
		con.setLevel(0);
		con.setAdr(cnst.getValue());
		
		Code.load(con);
	}
	
	public void visit(FactorBool cnst){
		Obj con = Tab.insert(Obj.Con, "$", SemanticAnalyzer.boolStructNode);
		con.setLevel(0);
		if(cnst.getValue().equals("true")) con.setAdr(1);
		else con.setAdr(0);
		
		Code.load(con);
	}
	
	Stack<Integer> kapicaVrednost = new Stack<>();
	boolean FinalNizAlokacija = false;
	Obj FinalNiz = null;
	
	public void visit(DesignatorOne one) {
		
//		if(one.getParent() instanceof DesAssign)
//			for (Obj o : SemanticAnalyzer.listaFinalNizova) 
//				if(one.getName().equals(o.getName())) {
//					FinalNizAlokacija = true; FinalNiz = one.obj;
//				}
			
		SyntaxNode parent = one.getParent();
		if(DesAssign.class != parent.getClass() && FactorDesActPars.class != parent.getClass() 
				&& DesActPars.class != parent.getClass() && SingleStatementRead.class != parent.getClass()){
			Code.load(one.obj);
		}
	}
	
	private void alokacijaFinalNiza() {
		FinalNizAlokacija = false;
		Code.error("ime niza  " + FinalNiz.getName() + " ");
		Obj niz = FinalNiz;//Tab.find(FinalNizIme);
		if(niz != null) {
			
			Code.load(niz);
			Code.put(Code.arraylength);//n
			Code.put(Code.dup);//n n
			Code.loadConst(2);
			Code.put(Code.div);//n {i = n/2}
			int ovdeSkok = Code.pc;//n i
			Code.put(Code.dup);//n i i
			Code.load(niz);//n i i niz
			zameniDvaElementa();//n i niz i
			Code.loadConst(5);//n i niz i 5
			Code.put(Code.astore);//n i
			Code.loadConst(1);
			Code.put(Code.add);
			Code.put(Code.dup);//n i i
			dohvatiTreci();// n i i n
			Code.putFalseJump(Code.eq, ovdeSkok);//n i
			Code.put(Code.pop);
			Code.put(Code.pop);
			
		}
	}
	
	public void visit(FactorType factor) {
		if(factor.getZagrade2() instanceof ZagradeOne2) {
			//Code.loadConst(2);// niz plus 5 dodatnih elemenata*------------------------------------------------------------------
			//Code.put(Code.mul);//Code.put(Code.add);
			Code.put(Code.newarray);
			if (factor.getType().struct.equals(Tab.charType)) Code.put(0);
			else Code.put(1);
		} else {
			int num = factor.getType().struct.getNumberOfFields() * 4;
			Code.put(Code.new_);
			Code.put2(num);
		}
	}
	
	public void visit(DesignatorArray des) {
		if(des.getParent() instanceof FactorDes) {
			if (des.getDesignator().obj.getType().getElemType().getKind() == Tab.charType.getKind()) 
				Code.put(Code.baload);
			else Code.put(Code.aload);
		}
	}
	
	public void visit(FactorDes des) {
		
	}
	
	public void visit(DesignatorField des) {
		if(des.getParent() instanceof DesignatorArray) {
			if(des.getParent().getParent() instanceof DesAssign || des.getParent().getParent() instanceof FactorDes) {
				SymbolDataStructure symbDataStr = des.getDesignator().obj.getType().getMembersTable();
				Obj found = symbDataStr.searchKey(des.getFieldName());
				Code.load(found);
				//Code.error(" " + des.getDesignator().obj.getName() + "." + found.getName());
			}
		}
		else if(des.getParent() instanceof DesignatorField || des.getParent() instanceof FactorDes) Code.load(des.obj);
	}
	
	public void visit(MethodTypeName methodTypeName){
		
		if("main".equalsIgnoreCase(methodTypeName.getMethName())){
			mainPc = Code.pc;
		}
		methodTypeName.obj.setAdr(Code.pc);
		SyntaxNode methodNode = methodTypeName.getParent();
	
		VarCounter varCnt = new VarCounter();
		methodNode.traverseTopDown(varCnt);
		
		FormParamCounter fpCnt = new FormParamCounter();
		methodNode.traverseTopDown(fpCnt);
		
		Code.put(Code.enter);
		Code.put(fpCnt.getCount());
		Code.put(fpCnt.getCount() + varCnt.getCount());
	
	}
	
	public void visit(MethodDecl methodDecl){
		Code.put(Code.exit);
		Code.put(Code.return_);
	}
	
	private void daLiJeDozvoljenUpis(Struct tmp){
							// niz index value
		dohvatiTreci();// niz index value niz
		dohvatiTreci();// niz index value niz index
		dohvatiDrugi();// niz index value niz index niz
		Code.put(Code.arraylength);// niz index value niz index length
		Code.loadConst(2);
		Code.put(Code.div);// niz index value niz index length/2
		Code.put(Code.add);// niz index value niz index+length/2
		Code.put(Code.aload);// niz index value niz[c]
		Code.loadConst(6);
		int skok = Code.pc + 1;
		Code.putFalseJump(Code.eq, 0);// niz index value
		Code.put(Code.pop);
		Code.put(Code.pop);
		Code.put(Code.pop);
		int skok2 = Code.pc + 1;
		Code.putJump(0);
		
		Code.fixup(skok); // niz index value
		//ucitaj 6 pa onda normalno ucitavanje elementa
		dohvatiTreci();// niz index value niz
		dohvatiTreci();// niz index value niz index
		Code.put(Code.dup2);// niz index value niz index --->           niz index
		dohvatiDrugi();// niz index value niz index --->                niz index niz
		Code.put(Code.arraylength);// niz index value niz index --->    niz index length
		Code.loadConst(2);
		Code.put(Code.div);// niz index value niz index --->            niz index length/2
		Code.put(Code.add);// niz index value niz index --->            niz index+length/2
		
		Code.put(Code.dup_x2);// niz index value niz index2 index --->  niz index+length/2
		
		Code.put(Code.aload);// niz index value niz index2 index --->   niz[c]
		Code.loadConst(1);
		Code.put(Code.add);// niz index value niz index2 index --->     niz[c]+1
		// niz index value niz index2 index value2
		zameniDvaElementa();// niz index value niz index2 value2 index
		Code.put(Code.pop);// niz index value niz index2 value2
		Code.put(Code.astore);
		Code.put(Code.astore);
		
		Code.fixup(skok2);
		
	}
	
	private boolean jelImaTaraba = false;
	
	public void visit(DesAssign des){
		
		if(des.getDesignator() instanceof DesignatorArray) {
			Struct tmp = des.getDesignator().obj.getType();
			if(tmp.getKind() == Struct.Array) {
				tmp = tmp.getElemType();
			}
//			daLiJeDozvoljenUpis(tmp);
			if(tmp.getKind() == Struct.Int || tmp.getKind() == SemanticAnalyzer.boolStructNode.getKind())
				Code.put(Code.astore);
			else Code.put(Code.bastore);

			 
		} else if(des.getDesignator() instanceof DesignatorOne || des.getDesignator() instanceof DesignatorField
				|| des.getDesignator() instanceof DesignatorArray) {
			
			if(jelImaTaraba) tarabaFunkcija(des.getDesignator().obj);
			else Code.store(des.getDesignator().obj);
			//if(FinalNizAlokacija) alokacijaFinalNiza();
		}		
	}
	
	public void visit(FactorDesActPars funcCall){
		Obj functionObj = funcCall.getDesignator().obj;
		int offset = functionObj.getAdr() - Code.pc;
		Code.put(Code.call);
		Code.put2(offset);
	}
	
	public void visit(FactorDesZagr funcCall) {
		Obj functionObj = funcCall.getDesignator().obj;
		int offset = functionObj.getAdr() - Code.pc;
		Code.put(Code.call);
		Code.put2(offset);
	}
	
	public void visit(DesActPars procCall){
		Obj functionObj = procCall.getDesignator().obj;
		int offset = functionObj.getAdr() - Code.pc;
		Code.put(Code.call);
		Code.put2(offset);
		if(procCall.getDesignator().obj.getType() != Tab.noType){
			Code.put(Code.pop);
		}
	}
	
	/******************************   uslovi   *************************************/
	
	private class Petlja{
		public int adrKraj = 0;
		ArrayList<Integer> adrElse = new ArrayList<Integer>();
		Boolean elseGrana = false;
	}
	
	int pocetakDoWhile = 0, pocetakUslova = 0, krajPetlje = 0;
	//ArrayList<Integer> adrUslova = new ArrayList<Integer>();
	Boolean condition = false, doPetlja = false;
	Stack<Petlja> stack = new Stack<Petlja>();
	
	private void setAdress() {
		Code.error("setAdr");
		for(int i: stack.peek().adrElse) {
			Code.fixup(i);
		}
		stack.peek().adrElse.clear();
	}
	
	public void visit(Condition cond){	
		condition = true;
	}
	
	public void visit(IfUslov cond){	
		Code.error("Condition push");
		stack.push(new Petlja());
	}
	
	// tek kad dodjemo u klasu UnmatchedIf ili SingleStatementIf znamo adresu kraja if uslova
	
	public void visit(SingleStatementIf cond){
		//Code.error("1 " + adrKraj);
		Code.error("SingleStatementIf");
		if(!stack.empty() && stack.peek().adrKraj != 0) Code.fixup(stack.peek().adrKraj);
		//adrKraj = 0;
		condition = false;
		Code.error("SingleStatementIf pop");
		stack.pop();
	}
	
	public void visit(UnmatchedIf cond){
		Code.error("UnmatchedIf");
		if(!stack.empty() && stack.peek().adrKraj != 0) Code.fixup(stack.peek().adrKraj);
		//adrKraj = 0;
		condition = false;
		Code.error("UnmatchedIf pop");
		stack.pop();
	}
	
	public void visit(SingleStatementDes cond){
		if(cond.getParent() instanceof SingleStatementIf 
				|| cond.getParent().getParent() instanceof UnmatchedIf
				|| cond.getParent().getParent() instanceof UnmatchedIfElse
				|| cond.getParent().getParent().getParent() instanceof Statements) {
			
			if(cond.getParent() instanceof SingleStatementIf) uvekZaPetlju(true);
			else uvekZaPetlju(false);
		}
	}
	
	public void visit(CondTerm term) {
		
	}
	
	public void visit(CondFactOne cond) {
		Code.put(Code.const_1);
		Code.error("ConFactOne");
		stack.peek().adrElse.add(Code.pc + 1);
		Code.putFalseJump(Code.eq, 0);
	}
	
	public void visit(CondFactTwo cond) {
		Relop relop = cond.getRelop();
		if(doPetlja) {
			if(relop instanceof RelopJed) Code.putFalseJump(Code.inverse[Code.eq], pocetakDoWhile);
			else if(relop instanceof RelopNejed) Code.putFalseJump(Code.inverse[Code.ne], pocetakDoWhile);
			else if(relop instanceof RelopVece) Code.putFalseJump(Code.inverse[Code.gt], pocetakDoWhile);
			else if(relop instanceof RelopVecJed) Code.putFalseJump(Code.inverse[Code.ge], pocetakDoWhile);
			else if(relop instanceof RelopManje) Code.putFalseJump(Code.inverse[Code.lt], pocetakDoWhile);
			else if(relop instanceof RelopManjeJed) Code.putFalseJump(Code.inverse[Code.le], pocetakDoWhile);
			return;
		}
		Code.error("ConFactTwo");
		stack.peek().adrElse.add(Code.pc + 1);
		if(relop instanceof RelopJed) Code.putFalseJump(Code.eq, 0);
		else if(relop instanceof RelopNejed) Code.putFalseJump(Code.ne, 0);
		else if(relop instanceof RelopVece) Code.putFalseJump(Code.gt, 0);
		else if(relop instanceof RelopVecJed) Code.putFalseJump(Code.ge, 0);
		else if(relop instanceof RelopManje) Code.putFalseJump(Code.lt, 0);
		else if(relop instanceof RelopManjeJed) Code.putFalseJump(Code.le, 0);
	}
	
	public void visit(SingleStatementDo stm) {
		Code.error("SingleStatementDo");
		if(!stack.empty()) stack.peek().adrElse.clear();
		if(krajPetlje != 0) Code.fixup(krajPetlje);
		pocetakDoWhile = krajPetlje = pocetakUslova = 0;
		doPetlja = false;
		condition = false;
	}
	
	public void visit(WhileStatement stm) {
		doPetlja = true;
		if(pocetakUslova != 0) Code.fixup(pocetakUslova);
	}
	
	public void visit(DoStatement stm) {
		pocetakDoWhile = Code.pc;
	}
	
	public void visit(SingleStatementContinue cond) {
		pocetakUslova = Code.pc + 1;
		Code.putJump(0);
		if(cond.getParent() instanceof SingleStatementIf 
				|| cond.getParent().getParent() instanceof UnmatchedIf
				|| cond.getParent().getParent() instanceof UnmatchedIfElse
				|| cond.getParent().getParent().getParent() instanceof Statements) {
			if(cond.getParent() instanceof SingleStatementIf) uvekZaPetlju(true);
			else uvekZaPetlju(false);
		}
	}
	
	public void visit(SingleStatementBreak cond) {
		krajPetlje = Code.pc + 1;
		Code.putJump(0);
		if(cond.getParent() instanceof SingleStatementIf 
				|| cond.getParent().getParent() instanceof UnmatchedIf
				|| cond.getParent().getParent() instanceof UnmatchedIfElse
				|| cond.getParent().getParent().getParent() instanceof Statements) {
			if(cond.getParent() instanceof SingleStatementIf) uvekZaPetlju(true);
			else uvekZaPetlju(false);
		}
	}
	
	public void visit(SingleStatementPrint printStmt){
		if(printStmt.getNumConstA() instanceof NumConstOne) {
			Code.put(Code.print);
		}
		else {
			if(printStmt.getExpr().struct == Tab.intType){
				Code.loadConst(5);
				Code.put(Code.print);
			}else{
				Code.loadConst(1);
				Code.put(Code.bprint);
			}
		}
		
		if(printStmt.getParent() instanceof SingleStatementIf 
				|| printStmt.getParent().getParent() instanceof UnmatchedIf
				|| printStmt.getParent().getParent() instanceof UnmatchedIfElse
				|| printStmt.getParent().getParent().getParent() instanceof Statements) {
			//Code.error(".....");
			if(printStmt.getParent() instanceof SingleStatementIf) uvekZaPetlju(true);
			else uvekZaPetlju(false);
		}
	}
	
	public void visit(NumConstOne cnst){
		Obj con = Tab.insert(Obj.Con, "$", Tab.intType);
		con.setLevel(0);
		con.setAdr(cnst.getN1());
		
		Code.load(con);
	}
	
	private void uvekZaPetlju(Boolean sig) {
		//Code.error("uvekzauslov");
		Code.error("uvekZaPetlju");
		if(condition) {
			if(!stack.peek().elseGrana) {
				stack.peek().adrKraj = Code.pc + 1;
				Code.putJump(0);
				setAdress();
				stack.peek().elseGrana = true;
			}
			else stack.peek().elseGrana = false;
		}
	}
	
	/******************************   nesto   *************************************/
	
	public void visit(SingleStatementReturn returnExpr){
		Code.put(Code.exit);
		Code.put(Code.return_);
		if(returnExpr.getParent() instanceof SingleStatementIf) uvekZaPetlju(true);
		else uvekZaPetlju(false);
	}
	
	public void visit(SingleStatementReturnNothing returnNoExpr){
		Code.put(Code.exit);
		Code.put(Code.return_);
		if(returnNoExpr.getParent() instanceof SingleStatementIf) uvekZaPetlju(true);
		else uvekZaPetlju(false);
	}
	
	// niz@2 -> x[2]+x[n-2]
//	public void visit(Term term){
//		if(term.getTermFragment() instanceof NoTermFragment) return;
//		TermFragList frag = (TermFragList) term.getTermFragment();
//		if(frag.getMulop() instanceof MulopMajmunce) {
//			FactorNum fact = (FactorNum) frag.getFactor();
//			int num = fact.getValue(); 
//			Obj niz = ((FactorDes) term.getFactor()).getDesignator().obj;
//			// 
//			//num = Code.get(Code.pc-2);
//			Obj tmp = new Obj(Obj.Con, "$", Tab.intType);
//			tmp.setAdr(num);
//			Code.error(" " + num);
//			Code.put(Code.pop);
//			Code.load(niz);
//			Obj functionObj = Tab.find("len");
//			int offset = functionObj.getAdr() - Code.pc;
//			Code.put(Code.call);// poziv funcije len
//			Code.put2(offset); // dohvatili smo len
//			Code.load(tmp);
//			Code.put(Code.sub);// n - 2
//			Code.put(Code.aload);// x[n-2]
//			Code.load(niz);
//			Code.load(tmp);
//			Code.put(Code.aload);// x[2]
//			Code.put(Code.add);// x[n-2] + x[2]
//		}
//	}
	
	public void visit(TermFragList mulOp){
		if(mulOp.getMulop() instanceof MulopPuta)
			Code.put(Code.mul);
		else if(mulOp.getMulop() instanceof MulopKosa)
			Code.put(Code.div);
		else if(mulOp.getMulop() instanceof MulopProc)
			Code.put(Code.rem);
		else if(mulOp.getMulop() instanceof MulopMajmunce){
			// x@y = x^2 + 2xy + y^2
			//Code.put(Code.mul); xy
			Code.put(Code.dup2);// xy xy
			Code.put(Code.dup2);// xy xy xy
			Code.put(Code.dup);// x y x y x y y
			Code.put(Code.mul);// xy xy x y2
			Code.put(Code.dup_x1);// xy xy y2 x y2
			Code.put(Code.pop);// xy xy y2 x
			Code.put(Code.dup);// xy xy y2 x x
			Code.put(Code.mul);// x y x y y2 x2
			Code.put(Code.add);//x y x y y2+x2
			Code.put(Code.dup_x2);//x y y2+x2 x y y2+x2
			Code.put(Code.pop);//x y y2+x2 x y
			Code.put(Code.mul);//x y y2+x2 xy
			Code.put(Code.add);//x y y2+x2+xy
			Code.put(Code.dup_x2);
			Code.put(Code.pop);
			Code.put(Code.mul);
			Code.put(Code.add);//xy+xy+y2+x2
		}
	}
	
	public void visit(ExprFragmentListYes addExpr){
		if(addExpr.getAddop() instanceof AddopPlus)
			Code.put(Code.add);
		else if(addExpr.getAddop() instanceof AddopMinus)
			Code.put(Code.sub);
	}
	
	public void visit(DesPlus plus){
		// da li je niz ili nije
		if(plus.getDesignator().obj.getType().getKind() == Struct.Array) {
			Code.put(Code.dup2);
			Code.put(Code.aload);
			Code.put(Code.const_1);
			Code.put(Code.add);
			Code.put(Code.astore);
		}
		else {
			Code.put(Code.const_1);
			Code.put(Code.add);
			Code.store(plus.getDesignator().obj);
		}
	}
	
	public void visit(DesPlusPlus plus){
		// da li je niz ili nije
		if(plus.getDesignator().obj.getType().getKind() == Struct.Array) {
			Code.put(Code.dup2);
			Code.put(Code.aload);
			Code.put(Code.const_2);
			Code.put(Code.add);
			Code.put(Code.astore);
		}
		else {
			Code.put(Code.const_2);
			Code.put(Code.add);
			Code.store(plus.getDesignator().obj);
		}
	}
	
	public void visit(DesMinus minus){
		// da li je niz ili nije
		if(minus.getDesignator().obj.getType().getKind() == Struct.Array) {
			Code.put(Code.dup2);
			Code.put(Code.aload);
			Code.put(Code.const_1);
			Code.put(Code.sub);
			Code.put(Code.astore);
		}
		else {
			Code.put(Code.const_1);
			Code.put(Code.sub);
			Code.store(minus.getDesignator().obj);
		}
	}
	
	public void visit(SingleStatementRead stm) {
		Designator designator = stm.getDesignator();
        if (designator.obj.getType() == Tab.charType) {
            Code.put(Code.bread);
        } else {
            Code.put(Code.read);
        }
        Obj designatorObj = designator.obj;
        Code.store(designatorObj);
	}
	
	public void visit(KapicaExpr expr) {

	}

	
	public void visit(BaseExpFragmentList lst) {
		
		int offset = powObjPom.getAdr() - Code.pc;
		Code.put(Code.call);
		Code.put2(offset);
		
	}
	
	public void visit(DesTaraba taraba) {
		Obj niz = taraba.getDesignator().obj;
    	// obrni niz
    	//Code.load(niz);
    	Code.put(Code.arraylength);
    	Code.loadConst(1);
    	Code.put(Code.sub);// n-1
    	Code.loadConst(0);// n-1 0
    	//petlja
    	int adr2 = Code.pc;// n i
    	Code.put(Code.dup2);// n i n i
    	Code.load(niz);// n i n i niz
    	zameniDvaElementa();// n i n niz i
    	Code.put(Code.aload);// n i n niz[i]
    	zameniDvaElementa();// n i niz[i] n
    	Code.load(niz);// n i niz[i] n niz
    	zameniDvaElementa();// n i niz[i] niz n
    	Code.put(Code.aload);// n i niz[i] niz[n]
    	dohvatiTreci();// n i niz[i] niz[n] i 
    	Code.load(niz);// n i niz[i] niz[n] i niz
    	Code.put(Code.dup_x2);// n i niz[i] niz niz[n] i niz
    	Code.put(Code.pop);// n i niz[i] niz niz[n] i
    	zameniDvaElementa();// n i niz[i] niz i niz[n]
    	Code.put(Code.astore);// n i niz[i]
    	dohvatiTreci();// n i niz[i] n
    	Code.load(niz);// n i niz[i] n niz
    	Code.put(Code.dup_x2);// n i niz niz[i] n niz
    	Code.put(Code.pop);// n i niz niz[i] n 
    	zameniDvaElementa();// n i niz n niz[i]
    	Code.put(Code.astore);//n i
    	
    	Code.loadConst(1);
    	Code.put(Code.add);// n i+1 
    	zameniDvaElementa();//i+1 n
    	Code.loadConst(1);
    	Code.put(Code.sub);//i+1 n-1
    	zameniDvaElementa();//n i
    	Code.put(Code.dup2);// n i n i
    	Code.putFalseJump(Code.le, adr2);
    	
	}
/*	
    public void visit(DesTaraba taraba){ 
    	//sortiramo niz sa 5 dodatnih elemenata
    	Obj niz = taraba.getDesignator().obj;
    	
    	storeNizOd(niz, 1, 0);// niz[lenght-1] = 0
    	//for i
    	int pocetakIpetlje = Code.pc;
    	
    	loadNizOd(niz, 1);
    	Code.load(niz);
    	Code.put(Code.arraylength);
    	Code.loadConst(6);
    	Code.put(Code.sub);
    	int pocetakI = Code.pc + 1;
    	Code.putFalseJump(Code.lt, Code.pc);// niz[lenght-1] < lenght-5-1;
    	
    	loadStoreNiz(niz, 4, 1);//niz[lenght-4] = niz[lenght-1];
    	
    	loadStoreStoreNiz(niz, 3, 1);//niz[lenght-3] = niz[niz[lenght-1]];
    	
    	//for j
    	Code.load(niz);
    	
    	Code.load(niz);
    	Code.put(Code.arraylength);
    	Code.loadConst(2);
    	Code.put(Code.sub);
    	
    	Code.load(niz);
    	Code.load(niz);
    	Code.put(Code.arraylength);
    	Code.loadConst(1);
    	Code.put(Code.sub);
    	Code.put(Code.aload);
    	Code.loadConst(1);
    	Code.put(Code.add);
    	
    	Code.put(Code.astore);//niz[lenght-2] = niz[lenght-1]+1
    	
    	//for j
    	int pocetakJpetlje = Code.pc;
    	
    	loadNizOd(niz, 2);
    	Code.load(niz);
    	Code.put(Code.arraylength);
    	Code.loadConst(5);
    	Code.put(Code.sub);
    	int pocetakJ = Code.pc + 1;
    	Code.putFalseJump(Code.lt, Code.pc); //niz[lenght-2] < lenght-5;
    	
    	loadLoadNiz(niz, 2);
    	loadNizOd(niz, 3);
    	int pocetakIf = Code.pc + 1;
    	Code.putFalseJump(Code.lt, Code.pc); //if(niz[niz[lenght-2]] < niz[lenght-3])
    	
    	loadStoreStoreNiz(niz, 3, 2);//niz[lenght-3] = niz[niz[lenght-2]];
    	
    	loadStoreNiz(niz, 4, 2);//niz[lenght-4] = niz[lenght-2];
    	
    	Code.fixup(pocetakIf);
    	// kraj for j
    	Code.load(niz);
    	
    	Code.load(niz);
    	Code.put(Code.arraylength);
    	Code.loadConst(2);
    	Code.put(Code.sub);
    	
    	loadNizOd(niz, 2);
    	Code.loadConst(1);
    	Code.put(Code.add);
    	
    	Code.put(Code.astore);//niz[lenght-2]++
    	
    	Code.putJump(pocetakJpetlje);
    	Code.fixup(pocetakJ);
    	// kraj for j
    	
    	loadStoreStoreNiz(niz, 5, 1);//      niz[lenght-5] = niz[niz[lenght-1]];

    	loadLoadStoreNiz(niz, 1, 3);//      niz[niz[lenght-1]] = niz[lenght-3];
    	
    	loadLoadStoreNiz(niz, 4, 5);//      niz[niz[lenght-4]] = niz[lenght-5];
    	// kraj for i
    	Code.load(niz);
    	
    	Code.load(niz);
    	Code.put(Code.arraylength);
    	Code.loadConst(1);
    	Code.put(Code.sub);
    	
    	loadNizOd(niz, 1);
    	Code.loadConst(1);
    	Code.put(Code.add);
    	
    	Code.put(Code.astore);//niz[lenght-1]++
    	
    	Code.putJump(pocetakIpetlje);
    	Code.fixup(pocetakI);
    }
    */
//  int lenght = 6+5;// i = niz[lenght-1], j = niz[lenght-2], min = niz[lenght-3], poz = niz[lenght-4], tmp = niz[lenght-5]
//  int niz[] = {33,5,999,11,6,92, 0, 0, 0, 0, 0};
//  
//  for(niz[lenght-1] = 0; niz[lenght-1] < lenght-5-1; niz[lenght-1]++){
//      niz[lenght-4] = niz[lenght-1];
//      niz[lenght-3] = niz[niz[lenght-1]];
//      for(niz[lenght-2] = niz[lenght-1]+1; niz[lenght-2] < lenght-5; niz[lenght-2]++){
//          if(niz[niz[lenght-2]] < niz[lenght-3]){
//             niz[lenght-3] = niz[niz[lenght-2]];
//             niz[lenght-4] = niz[lenght-2];
//          }
//      }
//      niz[lenght-5] = niz[niz[lenght-1]];
//      niz[niz[lenght-1]] = niz[lenght-3];
//      niz[niz[lenght-4]] = niz[lenght-5];
//  }
    
    private void loadNizOd(Obj niz, int off) { // niz[lenght-off]
    	Code.load(niz);
    	Code.load(niz);
    	Code.put(Code.arraylength);
    	Code.loadConst(off);
    	Code.put(Code.sub);
    	Code.put(Code.aload);
    }
    
    private void storeNizOd(Obj niz, int off, int value) { // niz[lenght-off] = value;
    	Code.load(niz);
    	Code.load(niz);
    	Code.put(Code.arraylength);
    	Code.loadConst(off);
    	Code.put(Code.sub);
    	Code.loadConst(value);
    	Code.put(Code.astore);
    }
    
    private void loadStoreNiz(Obj niz, int off1, int off2) {// niz[lenght-off1] = niz[lenght-off2];
    	Code.load(niz);
    	
    	Code.load(niz);
    	Code.put(Code.arraylength);
    	Code.loadConst(off1);
    	Code.put(Code.sub);
    	
    	Code.load(niz);
    	Code.load(niz);
    	Code.put(Code.arraylength);
    	Code.loadConst(off2);
    	Code.put(Code.sub);
    	Code.put(Code.aload);
    	
    	Code.put(Code.astore);
    }
    
    private void loadLoadNiz(Obj niz, int off) {// niz[niz[lenght-off]]
    	Code.load(niz);
    	
    	Code.load(niz);
    	Code.load(niz);
    	Code.put(Code.arraylength);
    	Code.loadConst(off);
    	Code.put(Code.sub);
    	Code.put(Code.aload);
    
    	Code.put(Code.aload);
    }
    
    
    private void loadLoadStoreNiz(Obj niz, int off1, int off2) {// niz[niz[lenght-off1]] = niz[lenght-off2];
    	Code.load(niz);
    	
    	Code.load(niz);
    	Code.load(niz);
    	Code.put(Code.arraylength);
    	Code.loadConst(off1);
    	Code.put(Code.sub);
    	Code.put(Code.aload);
    
    	loadNizOd(niz, off2);
    	
    	Code.put(Code.astore);
    }
    
    private void loadStoreStoreNiz(Obj niz, int off1, int off2) {// niz[lenght-off1] = niz[niz[lenght-off2]];
    	Code.load(niz);
    	
    	Code.load(niz);
    	Code.put(Code.arraylength);
    	Code.loadConst(off1);
    	Code.put(Code.sub);
    	
    	loadLoadNiz(niz, off2);
    	
    	Code.put(Code.astore);
    }
    
//  for(int niz[len-1] = 0; niz[len-1] < arraylen-1; niz[len-1]++){ 
//  int poz = niz[len-1];
//  int min = niz[niz[len-1]];
//  for(int j = i+1; j < arraylen; j++){
//      if(niz[j] < min){
//         min = niz[j];
//         poz = j;
//      }
//  }
//  int tmp = niz[i];
//  niz[i] = min;
//  niz[poz] = tmp;
//}
    
/*
	public void visit(FactorTaraba taraba) { // trazimo minimum sa dodatna 3 elementa u nizu
		Obj niz = taraba.getDesignator().obj;
		//int num = taraba.getN2(); 
		int skok1, skok2;
		
//		 [java] 128: getstatic 0
//    	[java] 131: call -125 (=6)
//    	[java] 134: store_0        // n = len(niz);
		
		
//	 [java] 135: getstatic 0
//     [java] 138: load_0
//     [java] 139: const_3
//     [java] 140: sub
//     [java] 141: load_0
//     [java] 142: astore         // niz[n-3] = n;
     
		Code.error("1  " + Code.pc);
		Code.load(niz);
		Code.load(niz);
		Code.put(Code.arraylength);
		Code.loadConst(3);
		Code.put(Code.sub);
		Code.load(niz);
		Code.put(Code.arraylength);
		Code.put(Code.astore);
		
//		[java] 135: getstatic 0
//     [java] 138: load_0
//     [java] 139: const_1
//     [java] 140: sub
//     [java] 141: const_0
//     [java] 142: astore        // niz[n-1] = 0;
     
		Code.error("2  " + Code.pc);
		Code.load(niz);
		Code.load(niz);
		Code.put(Code.arraylength);
		Code.loadConst(1);
		Code.put(Code.sub);
		Code.loadConst(0);
		Code.put(Code.astore);
		
//		[java] 143: getstatic 0
//     [java] 146: load_0
//     [java] 147: const_2
//     [java] 148: sub
//     [java] 149: const 1000
//     [java] 154: astore       // niz[n-2] = 1000;
     
		Code.error("3  " + Code.pc);
		Code.load(niz);
		Code.load(niz);
		Code.put(Code.arraylength);
		Code.loadConst(2);
		Code.put(Code.sub);
		Code.loadConst(1000);
		Code.put(Code.astore);
		skok2 = Code.pc;
		
		
//	[java] 155: getstatic 0
//     [java] 158: load_0
//     [java] 159: const_2
//     [java] 160: sub
//     [java] 161: aload
//     [java] 162: getstatic 0
//     [java] 165: getstatic 0
//     [java] 168: load_0
//     [java] 169: const_1
//     [java] 170: sub
//     [java] 171: aload
//     [java] 172: aload
//     [java] 173: jle 24 (=197)   // if(niz[n-2] > niz[niz[n-1]])
     
		Code.error("4  " + Code.pc);
		Code.load(niz);
		Code.load(niz);
		Code.put(Code.arraylength);
		Code.loadConst(2);
		Code.put(Code.sub);
		Code.put(Code.aload);
		Code.load(niz);
		Code.load(niz);
		Code.load(niz);
		Code.put(Code.arraylength);
		Code.loadConst(1);
		Code.put(Code.sub);
		Code.put(Code.aload);
		Code.put(Code.aload);
		skok1 = Code.pc + 1;
		Code.putFalseJump(Code.inverse[Code.le], 0); // skok
		
//		[java] 176: getstatic 0
//     [java] 179: load_0
//     [java] 180: const_2
//     [java] 181: sub
//     [java] 182: getstatic 0
//     [java] 185: getstatic 0
//     [java] 188: load_0
//     [java] 189: const_1
//     [java] 190: sub
//     [java] 191: aload
//     [java] 192: aload
//     [java] 193: astore
//     [java] 194: jmp 3 (=197)    // niz[n-2] = niz[niz[n-1]];
     
		Code.error("5  " + Code.pc);
		Code.load(niz);
		Code.load(niz);
		Code.put(Code.arraylength);
		Code.loadConst(2);
		Code.put(Code.sub);
		Code.load(niz);
		Code.load(niz);
		Code.load(niz);
		Code.put(Code.arraylength);
		Code.loadConst(1);
		Code.put(Code.sub);
		Code.put(Code.aload);
		Code.put(Code.aload);
		Code.put(Code.astore);
		Code.fixup(skok1);
		
//	[java] 197: getstatic 0
//     [java] 200: load_0
//     [java] 201: const_1
//     [java] 202: sub
//     [java] 203: getstatic 0
//     [java] 206: load_0
//     [java] 207: const_1
//     [java] 208: sub
//     [java] 209: aload
//     [java] 210: const_1
//     [java] 211: add
//     [java] 212: astore         //  niz[n-1] = niz[n-1] + 1
     
		Code.error("6  " + Code.pc);
		Code.load(niz);
		Code.load(niz);
		Code.put(Code.arraylength);
		Code.loadConst(1);
		Code.put(Code.sub);
		Code.load(niz);
		Code.load(niz);
		Code.put(Code.arraylength);
		Code.loadConst(1);
		Code.put(Code.sub);
		Code.put(Code.aload);
		Code.loadConst(1);
		Code.put(Code.add);
		Code.put(Code.astore);
		
		
//	     [java] 213: getstatic 0
//	     [java] 216: load_0
//	     [java] 217: const_1
//	     [java] 218: sub
//	     [java] 219: aload
//	     [java] 220: const 10
//	     [java] 225: jlt -70 (=155)   // while(niz[n-1] < 10)
	 
		Code.error("7  " + Code.pc);
		Code.load(niz);
		Code.load(niz);
		Code.put(Code.arraylength);
		Code.loadConst(1);
		Code.put(Code.sub);
		Code.put(Code.aload);
		Code.load(niz);
		Code.put(Code.arraylength);
		Code.loadConst(3);
		Code.put(Code.sub);
		Code.putFalseJump(Code.inverse[Code.lt], skok2); // skok
		
		//Code.error(" " + num);
		//[java] 26: getstatic 0
	    //[java] 29: putstatic 0
		Code.load(niz);
		Code.load(niz);
		Code.put(Code.arraylength);
		Code.loadConst(2);
		Code.put(Code.sub);
		Code.put(Code.aload);
	}*/
    
    private void tarabaFunkcija(Obj niz) {//niz = 5#; ---> niz = [1,0,1]
    	jelImaTaraba = false;
    	//n
    	Code.put(Code.dup);
    	Code.loadConst(1);//n 1
    	zameniDvaElementa();//1 n
    	Code.loadConst(2);//1 n 2
    	
    	int adr = Code.pc;//br n st
    	Code.put(Code.dup2);//br n st n st
    	int adr1 = Code.pc + 1;
    	Code.putFalseJump(Code.gt, 0);//br n st
    	Code.loadConst(2);//br n st 2
    	Code.put(Code.mul);//br n st^2
    	Code.put(Code.dup_x2);//st br n st
    	Code.put(Code.pop);//st br n
    	Code.put(Code.dup_x2);//n st br n
    	Code.put(Code.pop);//n st br 
    	Code.loadConst(1);
    	Code.put(Code.add);//n st br+1
    	Code.put(Code.dup_x2);//br+1 n st br+1
    	Code.put(Code.pop);//br+1 n st 
    	Code.putJump(adr);//br n st
    	Code.fixup(adr1);
    	Code.put(Code.pop);
    	Code.put(Code.pop);
    	
    	Code.put(Code.newarray);
    	Code.put(1);
    	Code.store(niz);// n
    	//n
    	Code.loadConst(0);// n 0
    	zameniDvaElementa();// i n
    	//petlja // n i
    	int skok = Code.pc;
    	zameniDvaElementa();// n i
    	
    	Code.put(Code.dup2);//n i n i
    	zameniDvaElementa();// n i i n
    	Code.loadConst(2);
    	Code.put(Code.rem);// n i i n%2
    	Code.load(niz);// n i i n%2 niz
    	Code.put(Code.dup_x2);// n i niz i n%2 niz
    	Code.put(Code.pop);//n i niz i n%2
    	Code.put(Code.astore);// n i
    	Code.loadConst(1);
    	Code.put(Code.add);// n i+1
    	zameniDvaElementa();// i+1 n
    	Code.loadConst(2);// i+1 n 2
    	Code.put(Code.div);// i+1 n/2
    	
    	Code.put(Code.dup);// i n n
    	Code.loadConst(0);
    	Code.putFalseJump(Code.eq, skok);//n
    	
    }
    public void visit(FactorTaraba taraba) {
    	jelImaTaraba = true;
    }
    
    
}

//n = len(niz);
//
//niz[n-3] = n;length
//niz[n-1] = 0;i
//niz[n-2] = 1000;min
//do
//	if(niz[n-2] > niz[niz[n-1]])
//		niz[n-2] = niz[niz[n-1]];
//		niz[n-1] = niz[n-1] + 1
//while(niz[n-1] < n-3)


