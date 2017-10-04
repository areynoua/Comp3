set -e

# Generate java
echo Generate Java
java -jar jflex LexicalAnalyzer.flex

# Compile java
echo Compile Java
javac Main.java

# Generate javadoc
echo Generate javadoc
javadoc -quiet -d ../doc/ *.java

# Generate jar
echo Generate jar
jar cfe ../dist/impCompiler.jar Main Main.class

# Test class
echo Test
java Main ../test/example.imp > /dev/null

# Test jar
echo
echo Test
echo ----
java -jar ../dist/impCompiler.jar ../test/example.imp
