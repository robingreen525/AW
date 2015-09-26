#include "rdist.hh"

Rdist::Rdist() {
  gsl_rng_env_setup();
  T = gsl_rng_default;
  r = gsl_rng_alloc(T);
}

Rdist::~Rdist() {
  gsl_rng_free(r);
}
