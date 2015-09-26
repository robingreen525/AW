#ifndef RDIST_H_
#define RDIST_H_

#include <gsl/gsl_rng.h>
#include <gsl/gsl_randist.h>

class Rdist {
  private:
    const gsl_rng_type * T;
    gsl_rng * r;
  public:
    Rdist();
    ~Rdist();
    const gsl_rng * rng() { return r; }
    double rgauss(const gsl_rng * r, double mu, double sigma) {
      return mu + gsl_ran_gaussian(r,sigma);
    }
    unsigned int rpois(const gsl_rng * r, double mu) {
      return gsl_ran_poisson(r, mu);
    }
};

extern Rdist rdist;
    
#endif
