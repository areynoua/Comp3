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

REM Test class
ECHO Test class...
java Main ../test/test-1.imp

REM Delete class files
ECHO Delete class files
del /s /q /f *.class

REM Test jar
REM ECHO[
REM ECHO Test jar
REM ECHO --------
REM java -jar ../dist/impCompiler.jar ../test/test-1.imp
