@ECHO off

REM Generate java
ECHO Generate Java
java -jar jflex-1.6.1.jar LexicalAnalyzer.flex

REM Compile java
ECHO Compile Java
javac Main.java

REM Generate javadoc
ECHO Generate javadoc
javadoc -quiet -d ../doc/ *.java

REM Generate jar
ECHO Generate jar
jar cfe ../dist/impCompiler.jar Main Main.class

REM Test class
ECHO Test class...
java Main ../test/example.imp > nul

REM Test jar
ECHO[
ECHO Test jar
ECHO --------
java -jar ../dist/impCompiler.jar ../test/example.imp