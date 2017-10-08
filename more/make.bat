@ECHO off

REM Generate java
ECHO Generate Java
java -jar jflex-1.6.1.jar -d Comp3 LexicalAnalyzer.flex

REM Compile java
ECHO Compile Java
javac Comp3/*.java

REM Generate javadoc
ECHO Generate javadoc
javadoc -quiet -d ../doc/ Comp3/*.java

cd comp3
REM Generate jar
ECHO Generate jar
jar cfe ../../dist/impCompiler.jar Main *.class

REM Test class
ECHO Test class...
java Main ../../test/example.imp > nul
cd ..

REM Delete class files
ECHO Delete class files
del /s /q /f *.class

REM Test jar
ECHO[
ECHO Test jar
ECHO --------
java -jar ../dist/impCompiler.jar ../test/example.imp