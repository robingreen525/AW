#ifndef CELL_H_
#define CELL_H_

#include <vector>

namespace SCALES {
  class Cell
  {

    private:
      std::vector<Cell *> daughters;
      
      int id;
      int parent;
      int age;
      int max_age;
      double next_bud;
      double bud_freq;
      double lag_time;
      double lag_left;
      double prob_death;
      bool alive;
    public:
      Cell(double lag_time, double bud_freq, int max_age, double prob_death, int par = 0);
      ~Cell();
      void change_bud_freq(double bf) { bud_freq = bf; }
      void change_death_prob(double dp) { prob_death = dp; }
      void set_id(int d) { id = d; }
      double get_bud_freq() { return bud_freq; }
      double get_lag_time() { return lag_time; }
      int get_id() { return id; }
      int get_age() { return age; }
      int get_max_age() { return max_age; }
      double get_parent() { return parent; }
      int ndaughters() { return daughters.size(); }
      void info();
      void die();
      bool grow(double lag_time, double bud_freq, int max_age, double prob_death);
      Cell * last_daughter(); 
  };
}
#endif
