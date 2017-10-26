#!/bin/bash
xelatex report.tex
xelatex report.tex
rm -f *.aux *.log *.toc
