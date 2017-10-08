import java.util.ArrayList;
import java.util.List;

%%

%class Main
%standalone
%public

%{
    List<Symbol> symbols = new ArrayList<Symbol>();
%}

%eof{
    for (Symbol symbol : symbols) {
        System.out.println(symbol.toString());
    }
%eof}

commentBegin=\(\*
commentContent=(\*[^\)]|[^*])*\*\)

%state COMMENT

%%



<YYINITIAL> { 
    {commentBegin} { /*ignore*/ yybegin(COMMENT);}
    "while" { symbols.add(new Symbol(LexicalUnit.NOT,yyline, yycolumn)); }
}

<COMMENT> {commentContent} { yybegin(YYINITIAL);}

// [^] {/*ignore*/}
