#ifndef DRIFT_H_
#define DRIFT_H_

#include <gsl/gsl_rng.h>
#include <gsl/gsl_randist.h>

class Drift {
  private:
    int *m_pop;
    int m_size;
    int m_fixed;
    int m_lost;
    int m_runs;
    int m_gens;
    double m_freq;
    bool m_save;
    const gsl_rng_type * T;
    gsl_rng * r;

    void initializePop(int N, double p);
    void initializeRNG();
    int convertToInt(double d);
    double calcFreq(int x[], int n);
    bool checkFreq(bool f_or_l, double freq, int run, int time);

    void wrightFisher(int N, double p, int gens, int runs);
    void moran(int N, double p, int gens, int runs );

    class DriftResult {
      public:
        double *time;
        double *freq;
        int *fixed;
        int *lost;
        int length;
        DriftResult();
        DriftResult(int size);
    };

    DriftResult *results;

  public:
    enum method { WRIGHT_FISHER, MORAN };
    Drift(int method, int N, double p, int gens, int runs, bool save);
    ~Drift();
    int getNumberFixed() { return m_fixed; }
    int getNumberLost() { return m_lost; }
    void printWithHeader();
    void printWithHeader(int run);
    void printWithHeader(int run_start, int run_end);
    void print(int run);
    void printHeader();
    void printInfo();
};
#endif
