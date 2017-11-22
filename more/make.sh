set -e

# Generate java
echo Generate Java
java -jar jflex-1.6.1.jar -d lexer lexer/LexicalAnalyzer.flex

# Compile java
echo Compile Java
javac lexer/*.java

# Generate javadoc
echo Generate javadoc
javadoc -quiet -d ../doc/ lexer/*.java

# Generate jar
cd lexer
echo Generate jar
jar cfe ../../dist/impCompiler.jar Main *.class

# Test class
echo Test class
echo ----------
java Main ../../test/example.imp > /dev/null
cd ..

# Test jar
echo
echo Test jar
echo --------
for file in ../test/*.imp; do
	echo $file
	java -jar ../dist/impCompiler.jar $file;
	java -jar ../dist/impCompiler.jar $file > /tmp/imp.out;
	if ! diff --suppress-common-lines -b -W 85 -y /tmp/imp.out ${file%.imp}.out ; then
		echo
		echo vimdiff /tmp/imp.out ${file%.imp}.out
		echo
		exit 1;
	fi
done
