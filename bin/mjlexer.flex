package rs.ac.bg.etf.pp1;

import java_cup.runtime.Symbol;

%%

%{

	// ukljucivanje informacije o poziciji tokena
	private Symbol new_symbol(int type) {
		return new Symbol(type, yyline+1, yycolumn);
	}
	
	// ukljucivanje informacije o poziciji tokena
	private Symbol new_symbol(int type, Object value) {
		return new Symbol(type, yyline+1, yycolumn, value);
	}

%}

%cup
%line
%column

%xstate COMMENT

%eofval{
	return new_symbol(sym.EOF);
%eofval}

%%

" " 	{ }
"\b" 	{ }
"\t" 	{ }
"\r\n" 	{ }
"\f" 	{ }

// Kljucne reci:
"program"	{ return new_symbol(sym.PROGRAM, yytext()); }
"break"		{ return new_symbol(sym.BREAK, yytext()); }
"class"		{ return new_symbol(sym.CLASS, yytext()); }
"enum"		{ return new_symbol(sym.ENUM, yytext()); }
"else"		{ return new_symbol(sym.ELSE, yytext()); }
"const"		{ return new_symbol(sym.CONST, yytext()); }
"if"		{ return new_symbol(sym.IF, yytext()); }
"do"		{ return new_symbol(sym.DO, yytext()); }
"while"		{ return new_symbol(sym.WHILE, yytext()); }
"new"		{ return new_symbol(sym.NEW, yytext()); }
"print"		{ return new_symbol(sym.PRINT, yytext()); }
"read"		{ return new_symbol(sym.READ, yytext()); }
"return"	{ return new_symbol(sym.RETURN, yytext()); }
"void"		{ return new_symbol(sym.VOID, yytext()); }
"extends"	{ return new_symbol(sym.EXTENDS, yytext()); }
"continue"	{ return new_symbol(sym.CONTINUE, yytext()); }
"this"		{ return new_symbol(sym.THIS, yytext()); }
"super"		{ return new_symbol(sym.SUPER, yytext()); }
"goto"		{ return new_symbol(sym.GOTO, yytext()); }
"record"	{ return new_symbol(sym.RECORD, yytext()); }
"final"		{ return new_symbol(sym.FINAL, yytext()); }

// Operatori:
"+"		{ return new_symbol(sym.PLUS, yytext()); }
"-"		{ return new_symbol(sym.MINUS, yytext()); }
"*"		{ return new_symbol(sym.PUTA, yytext()); }
"/"		{ return new_symbol(sym.KOSACRTA, yytext()); }
"%"		{ return new_symbol(sym.PROCENAT, yytext()); }
"=="	{ return new_symbol(sym.JEDNAKOST, yytext()); }
"!="	{ return new_symbol(sym.NEJEDNAKOST, yytext()); }
">"		{ return new_symbol(sym.VECE, yytext()); }
">="	{ return new_symbol(sym.VECEJEDNAKO, yytext()); }
"<"		{ return new_symbol(sym.MANJE, yytext()); }
"<="	{ return new_symbol(sym.MANJEJEDNAKO, yytext()); }
"&&"	{ return new_symbol(sym.AND, yytext()); }
"||"	{ return new_symbol(sym.OR, yytext()); }
"="		{ return new_symbol(sym.DODELAVR, yytext()); }
"++"	{ return new_symbol(sym.PLUSPLUS, yytext()); }
"@"		{ return new_symbol(sym.MAJMUNCE, yytext()); }
"+++"	{ return new_symbol(sym.PLUSPLUSPLUS, yytext()); }
"--"	{ return new_symbol(sym.MINUSMINUS, yytext()); }
";"		{ return new_symbol(sym.TACKAZAREZ, yytext()); }
":"		{ return new_symbol(sym.DVOTACKA, yytext()); }
","		{ return new_symbol(sym.ZAREZ, yytext()); }
"."		{ return new_symbol(sym.TACKA, yytext()); }
"^"		{ return new_symbol(sym.KAPICA, yytext()); }
"#"		{ return new_symbol(sym.TARABA, yytext()); }
"("		{ return new_symbol(sym.OBICNA_ZAGRADA_L, yytext()); }
")"		{ return new_symbol(sym.OBICNA_ZAGRADA_D, yytext()); }
"["		{ return new_symbol(sym.UGLASTA_ZAGRADA_L, yytext()); }
"]"		{ return new_symbol(sym.UGLASTA_ZAGRADA_D, yytext()); }
"{"		{ return new_symbol(sym.VITICASTA_ZAGRADA_L, yytext()); }
"}"		{ return new_symbol(sym.VITICASTA_ZAGRADA_D, yytext()); }

// Komentari:
<YYINITIAL> "//" 		     { yybegin(COMMENT); }

<COMMENT> .      { yybegin(COMMENT); }
<COMMENT> "\r\n" { yybegin(YYINITIAL); }

// Vrste tokena :

//charConst = "'" printableChar "'"
"'"[\000-\176]"'" {return new_symbol (sym.charConst, new Character (yytext().charAt(1)));}

//boolConst = ("true" | "false")
("true" | "false") {return new_symbol(sym.boolConst, yytext()); }

//ident = letter {letter | digit | "_"}
([a-z]|[A-Z])[a-z|A-Z|0-9|_]* 	{return new_symbol(sym.ident, yytext()); }

// numConst = digit {digit}
[0-9]+  { return new_symbol(sym.numConst, new Integer(yytext())); }

// ostali znaci
. { System.err.println("Leksicka greska ("+yytext()+") u liniji "+(yyline+1)); }

