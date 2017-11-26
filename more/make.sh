set -e

# Generate java
echo Generate Java
java -jar jflex-1.6.1.jar -d lexer lexer/LexicalAnalyzer.flex

# Compile java
echo Compile Java
javac lexer/*.java parser/*.java Main.java

# Generate javadoc
#echo Generate javadoc
#javadoc -quiet -d ../doc/ lexer/*.java

# Generate jar
echo Generate jar
jar cfe ../dist/impCompiler.jar Main lexer/*.class parser/*.class Main.class

rm -f grammars/imp_ru.grammar grammars/imp_ll.grammar tree.tex

# Test class
echo Test class
echo ----------
echo
echo $ java Main
java Main
echo
echo $ java Main --ru grammars/imp.grammar -o grammars/imp_ru.grammar
java Main --ru grammars/imp.grammar -o grammars/imp_ru.grammar
echo
echo $ java Main --ll grammars/imp_prim.grammar -o grammars/imp_ll.grammar
java Main --ll grammars/imp_prim.grammar -o grammars/imp_ll.grammar
echo
echo $ java Main --at grammars/imp_ll.grammar
java Main --at grammars/imp_ll.grammar > /dev/null
echo
echo $ java Main grammars/imp_ll.grammar ../test/test-1.imp -o tree.tex
java Main grammars/imp_ll.grammar ../test/test-10.imp -o tree.tex
pdflatex tree.tex

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
