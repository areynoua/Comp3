%%

%class Main
%standalone
%public

commentBegin=\(\*
commentContent=(\*[^\)]|[^*])*\*\)

%state COMMENT

%%

<YYINITIAL> {commentBegin} { /*ignore*/ yybegin(COMMENT);}

<COMMENT> {commentContent} { yybegin(YYINITIAL);}

// [^] {/*ignore*/}
