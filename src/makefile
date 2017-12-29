VM-AS = llvm-as
I = lli

all: jar

lexer/LexicalAnalyzer.java: lexer/LexicalAnalyzer.flex
	cd ../more/ && java -jar jflex-1.6.1.jar -d lexer ../src/lexer/LexicalAnalyzer.flex

lexer/%.class: lexer/%.java
	javac $<

generator/%.class: generator/%.java
	javac $<

parser/%.class: parser/%.java
	javac $<

Main.class: Main.java
	javac $<

.javadoc: lexer/*.java generator/*.java parser/*.java *.java
	javadoc -quiet -d ../doc/ $?
	@touch .javadoc
	@echo 'maybe you should git add ../doc/ javadoc && git commit -m"Javadoc"'

../dist/part3.jar: lexer/*.class parser/*.class generator/*.class Main.class
	jar cfe ../dist/part3.jar Main lexer/*.class parser/*.class generator/*.class Main.class

jar: ../dist/part3.jar

../more/grammars/imp_ru.grammar: Main.class parser/*.class ../more/grammars/imp.grammar
	java Main --ru ../more/grammars/imp.grammar -o ../more/grammars/imp_ru.grammar

../more/grammars/imp_ru.grammar.tex: Main.class parser/*.class ../more/grammars/imp.grammar
	java Main --ru ../more/grammars/imp.grammar -o ../more/grammars/imp_ru.grammar

../more/grammars/imp_ll.grammar: Main.class parser/*.class ../more/grammars/imp_prim.grammar
	java Main --ll ../more/grammars/imp_prim.grammar -o ../more/grammars/imp_ll.grammar

../more/grammars/imp_ll.grammar.tex: Main.class parser/*.class ../more/grammars/imp_prim.grammar
	java Main --ll ../more/grammars/imp_prim.grammar -o ../more/grammars/imp_ll.grammar

../more/action_table.tex: Main.class parser/*.class ../more/grammars/imp_ll.grammar
	java Main --at ../more/grammars/imp_ll.grammar -o ../more/action_table.tex
	mv first_follow.tex ../more/

ru: ../more/grammars/imp_ru.grammar ../more/grammars/imp_ru.grammar.tex

ll: ../more/grammars/imp_ll.grammar ../more/grammars/imp_ll.grammar.tex

at: ../more/action_table.tex

grammar:
	make ru
	make ll
	make at

test-%: *.class */*.class ../dist/part3.jar
	@echo $@
	@if java -jar ../dist/part3.jar ../more/grammars/imp_ll.grammar ../test/$@.imp -t /tmp/tree > /tmp/out.out ; then \
		if ${VM-AS} main.ll -o=main.bc ; then \
			if ${I} main.bc < ../test/$@.in >> /tmp/out.out ; then \
				echo success >> /tmp/out.out ; \
			fi ; \
		fi ; \
	fi
	@diff -q /tmp/out.out ../test/$@.out

run-%:  *.class */*.class
	java Main ../more/grammars/imp_ll.grammar ../test/$(subst run,test,$@).imp -t /tmp/tree
	${VM-AS} main.ll -o=main.bc
	${I} main.bc < ../test/$(subst run,test,$@).in

.PHONY: jar all ru ll at test-* grammar
