set -e

# Generate java
echo Generate Java
java -jar jflex-1.6.1.jar -d Comp3 LexicalAnalyzer.flex

# Compile java
echo Compile Java
javac Comp3/*.java

# Generate javadoc
echo Generate javadoc
javadoc -quiet -d ../doc/ Comp3/*.java

# Generate jar
cd Comp3
echo Generate jar
jar cfe ../dist/impCompiler.jar Main Main.class

# Test class
echo Test
java Main ../test/example.imp > /dev/null
cd ..

# Test jar
echo
echo Test
echo ----
java -jar ../dist/impCompiler.jar ../test/example.imp
