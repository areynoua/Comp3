@ECHO off

REM Generate java
ECHO Generate Java
java -jar jflex-1.6.1.jar -d lexer lexer/LexicalAnalyzer.flex

REM Compile java
ECHO Compile Java
javac lexer/*.java parser/*.java Main.java

REM Generate javadoc
REM ECHO Generate javadoc
REM javadoc -quiet -d ../doc/ lexer/*.java

REM Generate jar
ECHO Generate jar
jar cfe  ../dist/impCompiler.jar Main lexer/*.class parser/*.class Main.class

del /s /q /f grammars/imp_ru.grammar grammars/imp_ll.grammar tree.tex

REM Test class
ECHO Test class...
ECHO
ECHO $ java Main
java Main
ECHO
ECHO $ java Main --ru grammars/imp.grammar -o grammars/imp_ru.grammar
java Main --ru grammars/imp.grammar -o grammars/imp_ru.grammar
ECHO
ECHO $ java Main --ll grammars/imp_prim.grammar -o grammars/imp_ll.grammar
java Main --ll grammars/imp_prim.grammar -o grammars/imp_ll.grammar
ECHO
ECHO $ java Main --at grammars/imp_ll.grammar
java Main --at grammars/imp_ll.grammar
ECHO
ECHO $ java Main grammars/imp_ll.grammar ../test/test-1.imp -o tree.tex
java Main grammars/imp_ll.grammar ../test/test-1.imp -o tree.tex

REM Delete class files
ECHO Delete class files
del /s /q /f *.class

REM Test jar
REM ECHO[
REM ECHO Test jar
REM ECHO --------
REM java -jar ../dist/impCompiler.jar ../test/test-1.imp
