#include <iostream>
#include "drift.hh"
#include <gsl/gsl_rng.h>
#include <gsl/gsl_randist.h>
#include <cmath>

/****
 * Compile with '-lgsl -lgslcblas'
 */


Drift::Drift(int method, int N, double p, int gens, int runs, bool save) {
  m_size = N;
  m_runs = runs;
  m_freq = p;
  m_gens = gens;
  m_save = save;

  m_fixed = 0;
  m_lost  = 0;
  initializePop(N,p);
  initializeRNG();

  switch(method) {
    case WRIGHT_FISHER : wrightFisher(m_size, m_freq, m_gens, m_runs); 
                         break;
    case MORAN         : moran(m_size, m_freq, m_gens, m_runs); break;
  }
}

Drift::~Drift() {
  delete [] m_pop;

  if (m_save) {
    for (int i=0; i<m_runs; i++) {
      delete [] results[i].time;
      delete [] results[i].freq;
    }
    delete [] results;
  }
}


void Drift::moran(int N, double p, int gens, int runs ) {
  if (m_save) results = new DriftResult[runs];

  int pick = 1;
  int trials = 1;
  int *cell  = new int;
  int *index = new int[N];
  int *run_pop = new int[N];
  for (int i=0; i<N; i++) index[i] = i;

  for (int run=0; run<runs; run++) {
    if (m_save) results[run] = DriftResult(gens*N);
    bool fixed_or_lost = false;

    for (int i=0;i<N;i++) run_pop[i] = m_pop[i];
    double freq = calcFreq(run_pop,N);

    for (int gen=0; gen<gens; gen++) {
      for (int member=0; member<N; member++) {
        int t = gen*N+member;
        if (m_save && member==0) {
          results[run].time[t] = (double) t/N;
          results[run].freq[t] = freq;
        }

        gsl_ran_choose(r,cell,pick,index,N,sizeof(int));
        run_pop[*cell] = gsl_ran_binomial(r,freq,trials);
        freq = calcFreq(run_pop,N);
        fixed_or_lost = checkFreq(fixed_or_lost, freq, run, t);
      }
    }
  }
  delete cell;
  delete [] index;
  delete [] run_pop;
}

void Drift::wrightFisher(int N, double p, int gens, int runs) {
  if (m_save) results = new DriftResult[runs];
  int *new_pop = new int[N];
  int *old_pop = new int[N];

  for (int run=0; run<runs; run++) {
    bool fixed_or_lost = false;
    if (m_save) results[run] = DriftResult(gens);

    for (int i=0;i<N;i++) old_pop[i] = m_pop[i];
    double freq = calcFreq(old_pop,N);
    for (int gen=0; gen<gens; gen++) {
      fixed_or_lost = checkFreq(fixed_or_lost,freq,run,gen);
      if (m_save) {
        results[run].time[gen] = gen;
        results[run].freq[gen] = freq;
      }

      //for (int i=0; i<N; i++) std::cout << run_pop[i];
      //std::cout << std::endl;
      gsl_ran_sample(r,new_pop,N,old_pop,N,sizeof(int));
      freq = calcFreq(new_pop,N);
      for (int i=0;i<N;i++) old_pop[i] = new_pop[i];
    }
  }
  delete [] new_pop;
  delete [] old_pop;
}

void Drift::initializePop(int N, double p) {
  m_pop   = new int[N];
  double Np = N*p;

  for (int i=0; i<N; i++) m_pop[i]   = -1;

  if (N % convertToInt(p) != 0) 
    std::cout << "Warning: can't initialize with exact frequency!\n";

  for (int i=0; i<Np; i++) m_pop[i]   = 0;
  for (int i=Np;i<N; i++)  m_pop[i]   = 1;
}

void Drift::initializeRNG() {
  gsl_rng_env_setup();
  T = gsl_rng_default;
  r = gsl_rng_alloc(T);
}

bool Drift::checkFreq(bool f_or_l, double f, int run, int t) {
  if (!f_or_l) {
    if (f == 1 || f == 0) {
      if (f == 1) {
        ++m_fixed;
        if (m_save) results[run].fixed[t]++;
      } else if (f == 0) {
        ++m_lost;
        if (m_save) results[run].lost[t]++;
      }
      return true;
    } else { 
      return false;
    }
  } else {
    return true;
  }
}

int Drift::convertToInt(double d) {
  while (floor(d) != d) d *= 10;
  return (int) d;
}

void Drift::printWithHeader() {
  printInfo();
  printHeader();
  for (int i=0; i<m_runs; i++) print(i);
}

void Drift::printWithHeader(int run_start, int run_end) {
  printInfo();
  printHeader();
  for (int i=run_start-1; i<run_end; i++) print(i);
}

void Drift::print(int run) {
  if (run > m_runs) {
    std::cerr << "There are only " << m_runs 
              << " runs (" << run << ") requested.\n";
  }

  for (int i=0;i<results[run].length;i++) {
    std::cout << run+1                << "\t" 
              << results[run].time[i] << "\t" 
              << results[run].freq[i] << "\t"
              << results[run].fixed[i] << "\t"
              << results[run].lost[i]  << "\n";
  }
}

void Drift::printInfo() {
  std::cout << "# N\tp\tgens\truns\n";
  std::cout << "# " << m_size << "\t" << m_freq << "\t" 
            << m_gens << "\t" << m_runs << "\t" << std::endl;
}

void Drift::printHeader() { 
  std::cout << "run\tt\tfreq\tfixed\tlost\n";
}

double Drift::calcFreq(int x[], int n) {
  double cnt = 0;
  for (int i=0; i<n; i++) {
    if (x[i] == 1) cnt++;
  }
  return cnt/n;
}

Drift::DriftResult::DriftResult() {
  DriftResult(0);
}

Drift::DriftResult::DriftResult(int size) {
  length = size;
  time  = new double[size];
  freq  = new double[size];
  fixed = new int[size];
  lost  = new int[size];
}
