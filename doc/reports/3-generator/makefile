tex: report.pdf

%.pdf: %.tex
	xelatex $<
	xelatex $< > /dev/null &

cleantex:
	rm -rf *.log */*.log
	rm -rf *.aux */*.aux
	rm -rf *.tmp */*.tmp
	rm -rf *.bbl */*.bbl
	rm -rf *.blg */*.blg

cleanbak:
	rm -rf *~ */*.~
	rm -rf *bak */*.bak
	rm -rf *default */*.default

clean: cleantex cleanbak

.PHONY: tex cleantex cleanbak clean
