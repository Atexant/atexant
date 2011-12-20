
all: presentation.pdf

clean:
	/bin/rm -f *.mps *.gps *.log *.aux *.toc *.dvi *.ps *.pdf *.nav *.snm *.out *.vrb *.mpx

presentation.pdf: presentation.tex
	/bin/rm -rf *.log *.aux *.toc  *.dvi *.ps *.pdf *.nav *.snm *.out *.mps *.vrb *.mpx
#	TEX=latex mpost graphics end
	latex presentation.tex
	latex presentation.tex
	dvips presentation.dvi
	ps2pdf presentation.ps presentation.pdf
	/bin/rm -rf *.log *.aux *.toc  *.ps *.nav *.snm *.out *.mps *.vrb *.mpx *.gps
