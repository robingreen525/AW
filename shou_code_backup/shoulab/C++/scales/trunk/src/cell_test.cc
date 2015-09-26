#include <iostream>
#include <vector>
#include <cmath>
#include <ctime>
#include "cell.hh"
#include "rdist.hh"

using std::vector;
using SCALES::Cell;
Rdist rdist;

void growth(vector<Cell *> & pop, int * pop_size, double bf, double bfsd, int ma=40);

const unsigned int MAX_POP = 10000000;
const int MAX_AGE = 40;
const double MAX_GROWTH_RATE = M_LN2/15;

int main(int argc, char* argv[]) {
  using std::cout;
  using std::endl;


  int runs = 1;
  if (argc > 1) runs = atoi(argv[1]);

  const double pop1_bf = M_LN2/20;
  const double pop1_bfsd = 0.1;

  const double pop2_bf = pop1_bf;
  const double pop2_bfsd = 0.1;


  const int steps = 420;



  int p1_size_est = ceil(2*exp(pop1_bf*steps));
  int p2_size_est = ceil(2*exp(pop2_bf*steps));



  cout << "\nDynamically growing vector\n\n";
  cout << "\tgrowth rate\tsd\n";
  cout << "pop1\t" << pop1_bf << "\t" << pop1_bfsd << endl;
  cout << "pop2\t" << pop2_bf << "\t" << pop2_bfsd << endl << endl;

  cout << steps << " time steps\n\n";
  cout << "Running " << runs << " iterations.\n\n";
  cout << "time\tpop1\tpop2\tseed\n";

  for (int i=0; i<runs; i++) {

    vector<Cell *> pop1;
    vector<Cell *> pop2;
    Cell * cell1;
    Cell * cell2;
    
    int pop1_size = 0;
    int pop2_size = 0;

    gsl_rng_default_seed = time(0);

    try {
      cell1 = new Cell(0,pop1_bf,MAX_AGE,0.3);
      cell2 = new Cell(0,pop2_bf,MAX_AGE,0.3);
    }
    catch (std::bad_alloc & ba) {
      cout << "Vector (population) out of memory!" << endl;
      exit(EXIT_FAILURE);
    }

    cell1->set_id(pop1_size++);
    cell2->set_id(pop2_size++);
    pop1.push_back(cell1);
    pop2.push_back(cell2);

    int j = 0;
    while (pop1_size <= MAX_POP && pop2_size <= MAX_POP) {
      growth(pop1, &pop1_size, pop1_bf, pop1_bfsd);
      growth(pop2, &pop2_size, pop2_bf, pop2_bfsd);

      cout << j++ << "\t" << pop1_size << "\t" << pop2_size << "\t" << gsl_rng_default_seed << endl;
    }

    for (int k=0; k<pop1.size(); k++) {
      delete pop1[k];
    }
    for (int k=0; k<pop2.size(); k++) {
      delete pop2[k];
    }
  }
  return 0;
}

void growth(vector<Cell *> & pop, int* pop_size, double bf, double bfsd, int ma) {

  int ps = *pop_size;
  vector<Cell *> new_daughters;
  for (unsigned int j=0;j<ps;j++) {
    double gr = rdist.rgauss(rdist.rng(),bf,bf*bfsd);
    if (gr > MAX_GROWTH_RATE) gr = MAX_GROWTH_RATE;
    bool budded = pop[j]->grow(0,gr,ma,0.3);
    //pop[j]->info();
    if (budded == true) {
      //std::cout << "BUDDING!" << std::endl;
      Cell * daughter = pop[j]->get_last_daughter();
      daughter->set_id((*pop_size)++);
      //daughter->info();
      new_daughters.push_back(daughter);
    }
  }

  for (unsigned int k=0; k<new_daughters.size(); k++) {
    pop.push_back(new_daughters[k]);
  }
}
