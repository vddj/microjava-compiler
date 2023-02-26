package rs.ac.bg.etf.pp1;


import rs.ac.bg.etf.pp1.ast.*;
public class CounterVisitor extends VisitorAdaptor {

	protected int count = 0;
	
	public int getCount(){
		return count;
	}
	
	public static class FormParamCounter extends CounterVisitor{
	
		public void visit(FormListYes formParamDecl){
			if(count == 0) count++;
			count++;
		}
	}
	
	public static class VarCounter extends CounterVisitor{
		
		public void visit(VarDecl varDecl){
			count++;
		}
	}
}
