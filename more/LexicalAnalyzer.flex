/**
* Imp Lexical analyser
*
* @author Antoine Passemiers
* @author Alexis Reynouard
*/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

%%

%class Main
%standalone
%public
%line
%column

%{
    // A list that contains all the recognized symbols
    List<Symbol> symbols = new ArrayList<Symbol>();

    // A LinkedHashMap is used to preserve the order of recognized identifiers
    HashMap<Integer, Symbol> identifiers = new LinkedHashMap<Integer, Symbol>();

    void addSymbol(LexicalUnit unit) {
        /**
         * Adds a new symbol to the list of recognized symbols, with the current
         * line number and column number. If the symbol is a VARNAME token,
         * it is considered as an identifier and added to the identifiers hashmap,
         * except if the identifier has already been encountered.
         * This makes the assumption that the line number never decreases as the scanner runs.
         *
         * @param  unit   The recognized lexical unit
         */
        Symbol symbol = new Symbol(unit, yyline, yycolumn, yytext());
        symbols.add(symbol);
        if (symbol.getType() == LexicalUnit.VARNAME) {
            // Calling the Symbol.hashCode() explicitly because the Symbol.equals(Symbol symbol) 
            // method has not been implemented by default
            if (!identifiers.containsKey(symbol.hashCode())) identifiers.put(symbol.hashCode(), symbol);
        }
    }
%}

%eof{
    // Print matched lexical units
    for (Symbol symbol : symbols) {
        System.out.println(symbol.toString());
    }

    // Print the content of the symbol table
    System.out.println("\nIdentifiers");
    for (Integer index : identifiers.keySet()) {
        Symbol identifier = identifiers.get(index);
        int line = identifier.getLine();
        String token = identifier.getValue().toString();
        System.out.println(String.format("%-15s", token) + " " + line);
    }
%eof}

%eofval{
    return 0; // symbols.add(new Symbol(LexicalUnit.EOS, yyline, yycolumn)); // TODO: what to do with end of stream ?
%eofval}

CommentBegin   = \(\*
CommentContent = (\*[^\)]|[^*])*\*\)

AlphaUpperCase = [A-Z]
AlphaLowerCase = [a-z]
Alpha          = {AlphaUpperCase}|{AlphaLowerCase}
Numeric        = [0-9]
AlphaNumeric   = {Alpha}|{Numeric}

Sign           = [+-]
Integer        = {Sign}?(([1-9][0-9]*)|0)
Decimal        = \.[0-9]*
Exponent       = [eE]{Integer}
Real           = {Integer}{Decimal}?{Exponent}?
Identifier     = {Alpha}{AlphaNumeric}*

%state COMMENT

%%



<YYINITIAL> { 
    "begin"        { addSymbol(LexicalUnit.BEGIN); }
    "end"          { addSymbol(LexicalUnit.END); }
    ";"            { addSymbol(LexicalUnit.SEMICOLON); }
    ":="           { addSymbol(LexicalUnit.ASSIGN); }
    "("            { addSymbol(LexicalUnit.LPAREN); }
    ")"            { addSymbol(LexicalUnit.RPAREN); }
    "-"            { addSymbol(LexicalUnit.MINUS); }
    "+"            { addSymbol(LexicalUnit.PLUS); }
    "*"            { addSymbol(LexicalUnit.TIMES); }
    "/"            { addSymbol(LexicalUnit.DIVIDE); }
    "if"           { addSymbol(LexicalUnit.IF); }
    "then"         { addSymbol(LexicalUnit.THEN); }
    "endif"        { addSymbol(LexicalUnit.ENDIF); }
    "else"         { addSymbol(LexicalUnit.ELSE); }
    "not"          { addSymbol(LexicalUnit.NOT); }
    "and"          { addSymbol(LexicalUnit.AND); }
    "or"           { addSymbol(LexicalUnit.OR); }
    "="            { addSymbol(LexicalUnit.EQ); }
    ">="           { addSymbol(LexicalUnit.GEQ); }
    ">"            { addSymbol(LexicalUnit.GT); }
    "<="           { addSymbol(LexicalUnit.LEQ); }
    "<"            { addSymbol(LexicalUnit.LT); }
    "<>"           { addSymbol(LexicalUnit.NEQ); }
    "while"        { addSymbol(LexicalUnit.WHILE); }
    "do"           { addSymbol(LexicalUnit.DO); }
    "done"         { addSymbol(LexicalUnit.DONE); }
    "for"          { addSymbol(LexicalUnit.FOR); }
    "from"         { addSymbol(LexicalUnit.FROM); }
    "by"           { addSymbol(LexicalUnit.BY); }
    "to"           { addSymbol(LexicalUnit.TO); }
    "print"        { addSymbol(LexicalUnit.PRINT); }
    "read"         { addSymbol(LexicalUnit.READ); }
    {Identifier}   { addSymbol(LexicalUnit.VARNAME); }
    {Real}         { addSymbol(LexicalUnit.NUMBER); }
    {CommentBegin} { yybegin(COMMENT);}
}

<COMMENT> {CommentContent} { yybegin(YYINITIAL);}

[^] { /*ignore*/ }
