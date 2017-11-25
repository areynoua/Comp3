set -e

# Generate java
echo Generate Java
java -jar jflex-1.6.1.jar -d lexer lexer/LexicalAnalyzer.flex

# Compile java
echo Compile Java
javac lexer/*.java parser/*.java Main.java

# Generate javadoc
echo Generate javadoc
#javadoc -quiet -d ../doc/ lexer/*.java

# Generate jar
echo Generate jar
jar cfe ../dist/impCompiler.jar Main lexer/*.class parser/*.class Main.class

# Test class
echo Test class
echo ----------
java Main ../test/test-1.imp

## Test jar
#echo
#echo Test jar
#echo --------
##for file in ../test/*.imp; do
#file="../test/test-1.imp"
##	echo $file
##	java -jar ../dist/impCompiler.jar $file;
##	java -jar ../dist/impCompiler.jar $file > /tmp/imp.out;
##	if ! diff --suppress-common-lines -b -W 85 -y /tmp/imp.out ${file%.imp}.out ; then
##		echo
##		echo vimdiff /tmp/imp.out ${file%.imp}.out
##		echo
##		exit 1;
##	fi
##done
