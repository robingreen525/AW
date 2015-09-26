CFOBJ = compfix.o drift.o
WFOBJ = wf.o drift.o
MOBJ  = moran.o drift.o
OPT = -O1 -Wall
LIBS = -lgsl -lgslcblas
CF = compfix
WF = wf
MO = moran

compfix: $(CFOBJ)
	g++ $(OPT) -o $(CF) $(CFOBJ) $(LIBS)

wf: $(WFOBJ)
	g++ $(OPT) -o $(WF) $(WFOBJ) $(LIBS)

moran: $(MOBJ)
	g++ $(OPT) -o $(MO) $(MOBJ) $(LIBS)

moran.o: drift.hh
wf.o: drift.hh
compfix.o: drift.hh
drift.o: drift.hh

.PHONY: clean
clean:
	-rm *.o
