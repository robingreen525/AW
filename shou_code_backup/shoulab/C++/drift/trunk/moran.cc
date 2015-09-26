#include <iostream>
#include "drift.hh"

int main(int argc, char* argv[]) {
  using std::cout; using std::cerr; using std::endl;

  if (argc < 5) {
    cerr << "Usage: moran N frequency generations runs save\n";
    exit(1);
  }

  int pop_size     = atoi(argv[1]);
  double frequency = atof(argv[2]);
  int generations  = atoi(argv[3]);
  int runs         = atoi(argv[4]);
  int save         = atoi(argv[5]);

  Drift moran = 
    Drift(Drift::MORAN, pop_size, frequency, generations, runs, save);

  moran.printWithHeader();
}
