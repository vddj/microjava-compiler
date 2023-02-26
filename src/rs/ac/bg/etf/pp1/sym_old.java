package rs.ac.bg.etf.pp1;

public class sym_old {
	public static final int EOF = 0;
	public static final int error = 1;
  
	//Kljucne reci:
	public static final int PROGRAM = 2;
	public static final int BREAK = 3;
	public static final int CLASS = 4;
	public static final int ENUM = 5;
	public static final int ELSE = 6;
	public static final int CONST = 7;
	public static final int IF = 8;
	public static final int DO = 9;
	public static final int WHILE = 10;
	public static final int NEW = 11;
	public static final int PRINT = 12;
	public static final int READ = 13;
	public static final int RETURN = 14;
	public static final int VOID = 15;
	public static final int EXTENDS = 16;
	public static final int CONTINUE = 17;
	public static final int THIS = 18;
	public static final int SUPER = 19;
	public static final int GOTO = 20;
	public static final int RECORD = 21;
	  
	//Vrste tokena :
	public static final int ident = 22;
	public static final int numConst = 23;
	public static final int charConst = 24;
	public static final int boolConst = 25;
	
	//Operatori:
	public static final int PLUS = 26;
	public static final int MINUS = 27;
	public static final int PUTA = 28;
	public static final int KOSACRTA = 29;
	public static final int PROCENAT = 30;
	public static final int JEDNAKOST = 31;
	public static final int NEJEDNAKOST = 32;
	public static final int VECE = 33;
	public static final int VECEJEDNAKO = 34;
	public static final int MANJE = 35;
	public static final int MANJEJEDNAKO = 36;
	public static final int AND = 37;
	public static final int OR = 38;
	public static final int DODELAVR = 39;
	public static final int PLUSPLUS = 40;
	public static final int MINUSMINUS = 41;
	public static final int TACKAZAREZ = 42;
	public static final int DVOTACKA = 43;
	public static final int ZAREZ = 44;
	public static final int TACKA = 45;
	public static final int OBICNA_ZAGRADA_L = 46;
	public static final int OBICNA_ZAGRADA_D = 47;
	public static final int UGLASTA_ZAGRADA_L = 48;
	public static final int UGLASTA_ZAGRADA_D = 49;
	public static final int VITICASTA_ZAGRADA_L = 50;
	public static final int VITICASTA_ZAGRADA_D = 51;
	  
	//Komentari:
	public static final int KOMENTAR = 52;
}
