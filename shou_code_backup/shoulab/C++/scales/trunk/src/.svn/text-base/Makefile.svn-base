OBJ = rdist.o cell.o cell_test.o
OBJ2 = rdist.o cell.o prealloc_cell_test.o
OPT = g++ -O1 -Wall
LIBS = -lgsl -lgslcblas

test: $(OBJ)
	$(OPT) -o test $(OBJ) $(LIBS)

pa_test: $(OBJ2)
	$(OPT) -o pa_test $(OBJ2) $(LIBS)

prealloc_cell_test.o: rdist.hh cell.hh
cell_test.o: rdist.hh cell.hh
cell.o: rdist.hh cell.hh
rdist.o: rdist.hh

.PHONY: clean
clean:
	-rm test *.o
