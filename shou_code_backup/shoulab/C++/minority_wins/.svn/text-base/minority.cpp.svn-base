#include<iostream>
#include<cstdlib>
#include<gsl/gsl_sf_gamma.h>
#include<gsl/gsl_math.h>

/****
 * Compile with '-lgsl -lgslcblas'
 */

double calc_prob(unsigned int l, unsigned int h, double p);
int main(int argc, char* argv[]) {
  using std::cout; using std::cerr; using std::endl;
  if (argc < 5) {
    cerr << "Usage: minority N pmin pmax pstep\n";
    exit(1);
  }

  int N;
  double pmin, pmax, pstep;
  N = atof(argv[1]);
  pmin  = atof(argv[2]);
  pmax  = atof(argv[3]);
  pstep = atof(argv[4]);

  cout << "#N = " << N << " pmin = "
       << pmin << " pmax = " << pmax << " pstep = " << pstep << endl;

  cout << "high\tlow\tmax_p\tmax_P\n";
  for (int high=2; high<=N; high++) {
    for (int low=1; low<high; low++) {
      double maxp = 0;
      double maxP = 0;
      //cout << "High = " << high << " Low = " << low << endl;
      for (double p=pmin; p<=pmax; p+=pstep) {
        //cout << p << endl;
        double currP = calc_prob(low,high,p);
        if (currP > maxP) {
          maxP = currP;
          maxp = p;
        }
      }
      cout << high << "\t" << low << "\t" << maxp << "\t" << maxP << endl;
    }
  }
}

double calc_prob(unsigned int l, unsigned int h, double p) {
  double prob = 0;
  for (unsigned int n=1; n<=l; n++) {
    double x = gsl_sf_choose(l,n) * gsl_pow_int(p,n) * gsl_pow_int((1-p),(l-n));
    //std::cout << x << std::endl;
    double y = 0;
    for (unsigned int m=0; m<n; m++) {
      y = gsl_sf_choose(h,m) * gsl_pow_int(p,m) * gsl_pow_int((1-p),(h-m));
     // std::cout << y << std::endl;
    }
    prob = prob + x*y;
  }
  return prob;
}

