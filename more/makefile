VM-AS = llvm-as
I = lli

all: jar

lexer/LexicalAnalyzer.java: lexer/LexicalAnalyzer.flex
	java -jar jflex-1.6.1.jar -d lexer lexer/LexicalAnalyzer.flex

lexer/%.class: lexer/%.java
	javac $<

generator/%.class: generator/%.java
	javac $<

parser/%.class: parser/%.java
	javac $<

Main.class: Main.java
	javac $<

javadoc: lexer/*.java generator/*.java parser/*.java
	javadoc -quiet -d ../doc/ $?
	touch javadoc

../dist/impCompiler.jar: lexer/*.class parser/*.class generator/*.class Main.class 
	jar cfe ../dist/impCompiler.jar Main lexer/*.class parser/*.class generator/*.class Main.class

jar: ../dist/impCompiler.jar

grammars/imp_ru.grammar: Main.class parser/*.class grammars/imp.grammar
	java Main --ru grammars/imp.grammar -o grammars/imp_ru.grammar

grammars/imp_ll.grammar: Main.class parser/*.class grammars/imp_prim.grammar
	java Main --ll grammars/imp_prim.grammar -o grammars/imp_ll.grammar

action_table.tex: Main.class parser/*.class grammars/imp_ll.grammar
	java Main --at grammars/imp_ll.grammar -o action_table.tex

ru: grammars/imp_ru.grammar

ll: grammars/imp_ll.grammar

at: action_table.tex

test-%: *.class */*.class
	java Main grammars/imp_ll.grammar ../test/$@.imp -o tree
	${VM-AS} main.ll -o=main.bc
	${I} main.bc < ../test/$@.in > /tmp/out.out
	diff -q /tmp/out.out ../test/$@.out

.PHONY: jar all ru ll at
