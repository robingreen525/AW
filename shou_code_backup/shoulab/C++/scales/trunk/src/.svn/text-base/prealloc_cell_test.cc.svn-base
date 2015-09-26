#include <iostream>
#include <vector>
#include <cmath>
#include <ctime>
#include "cell.hh"
#include "rdist.hh"

using std::vector;
using SCALES::Cell;
Rdist rdist;

void growth(vector<Cell *> & pop, int* pop_size, double bf, double bfsd, int ma=40);

int main() {
  using std::cout;
  using std::endl;

  const unsigned int MAX_POP = 10000000;
  const int MAX_AGE = 40;

  const double pop1_bf = M_LN2/20;
  const double pop1_bfsd = 0.1;

  const double pop2_bf = pop1_bf;
  const double pop2_bfsd = 0.3;


  const int steps = 411;


  double pop1_init_growth_rate = rdist.rgauss(rdist.rng(),pop1_bf,pop1_bf*pop1_bfsd);
  double pop2_init_growth_rate = rdist.rgauss(rdist.rng(),pop2_bf,pop2_bf*pop2_bfsd);


  int pop1_size = 0;
  int pop2_size = 0;

  int p1_size_est = ceil(2*exp(pop1_bf*steps));
  int p2_size_est = ceil(2*exp(pop2_bf*steps));

  vector<Cell *> pop1 (p1_size_est);
  vector<Cell *> pop2 (p2_size_est);
  Cell * cell1;
  Cell * cell2;


  try {
    cell1 = new Cell(0,pop1_init_growth_rate,MAX_AGE,0.3);
    cell2 = new Cell(0,pop2_init_growth_rate,MAX_AGE,0.3);
  }
  catch (std::bad_alloc & ba) {
    cout << "Vector (population) out of memory!" << endl;
    exit(EXIT_FAILURE);
  }

  cell1->set_id(pop1_size++);
  cell2->set_id(pop2_size++);

  cell1->info();
  cell2->info();
  pop1[0] = cell1;
  pop2[0] = cell2;

  cout << "Pre allocation of vector\n\n";
  cout << "\tgrowth rate\tsd\n";
  cout << "pop1\t" << pop1_bf << "\t" << pop1_bfsd << endl;
  cout << "pop2\t" << pop2_bf << "\t" << pop2_bfsd << endl;

  cout << steps << " time steps\n";
  cout << "time\tpop1\tpop2\n";
  for (unsigned int i=0; i < steps; i++) {
    growth(pop1, &pop1_size, pop1_bf, pop1_bfsd);
    growth(pop2, &pop2_size, pop2_bf, pop2_bfsd);

    cout << i << "\t" << pop1_size << "\t" << pop2_size <<  endl;
    if ( pop1_size > MAX_POP ) break;
  }

  delete cell1;
  delete cell2;

  return 0;
}

void growth(vector<Cell *> & pop, int* pop_size, double bf, double bfsd, int ma) {

  int ps = *pop_size;
  vector<Cell *> new_daughters;
  for (unsigned int j=0;j<ps;j++) {
    double gr = rdist.rgauss(rdist.rng(),bf,bf*bfsd);
    bool budded = pop[j]->grow(0,gr,ma,0.3);
    //pop[j]->info();
    if (budded == true) {
      //cout << "BUDDING!" << endl;
      Cell * daughter = pop[j]->last_daughter();
      //daughter->info();
      daughter->set_id((*pop_size)++);
      //daughter->info();
      new_daughters.push_back(daughter);
    }
  }

  for (unsigned int k=0; k<new_daughters.size(); k++) {
    pop[new_daughters[k]->get_id()] = new_daughters[k];
  }
}
