#include <iostream>
#include "drift.hh"

int main(int argc, char* argv[]) {
  using std::cout; using std::cerr; using std::endl;

  if (argc < 7) {
    cerr << 
      "Usage: compfix N frequency gen_start gen_end gen_step runs  \n";
    exit(1);
  }

  int pop_size     = atoi(argv[1]);
  double frequency = atof(argv[2]);
  int gen_start    = atoi(argv[3]);
  int gen_end      = atoi(argv[4]);
  int gen_step     = atoi(argv[5]);
  int runs         = atoi(argv[6]);
  bool save = false;

  for (int gens=gen_start; gens<=gen_end; gens+=gen_step) {
    Drift wf = 
      Drift(Drift::WRIGHT_FISHER, pop_size, frequency, gens, runs, save);
    Drift moran = 
      Drift(Drift::MORAN, pop_size, frequency, gens, runs, save);

    if (gens==gen_start) {
      wf.printInfo();
      cout << "gen\tmodel\tfixed\tlost\n";
    }
    cout << gens << "\t" << "M" << "\t" << moran.getNumberFixed() << "\t" 
                         << moran.getNumberLost() << endl;
    cout << gens << "\t" << "WF" << "\t" << wf.getNumberFixed() << "\t" 
                         << wf.getNumberLost() << endl;
  }
}
