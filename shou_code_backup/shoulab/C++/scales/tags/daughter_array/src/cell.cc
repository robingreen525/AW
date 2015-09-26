#include <iostream>
#include <cstdlib>
#include <cmath>
#include "cell.hh"
#include "rdist.hh"

namespace SCALES 
{

  using std::cout;
  using std::endl;

  Rdist rdist;

  Cell::Cell(double lt, double bf, int ma, double pd, int par) 
  {
    id = -1;
    alive = true;
    age = 0;

    max_age = ma;
    parent = par;
    bud_freq = bf;
    next_bud = M_LN2/bud_freq;
    lag_time = lt;
    lag_left = lt;
    prob_death = pd;
  }

  Cell::~Cell() {
    if (daughters.size() > 0) {
      //std::cout << "Deleting Cell " << id << " daughters\n";
      for (unsigned int i=0; i<daughters.size(); i++) {
        delete daughters[i];
      }
    }
  }

  void Cell::die() {
    alive = false;
  }

  bool Cell::grow(double lt, double bf, int ma, double pd) {
    if (alive == true && lag_left > 0) {
      lag_left--;
      return false;
    }
    if (lag_left <= 0) {
      next_bud += lag_left;
      next_bud--;
      lag_left = 0;
      if ( next_bud <= 0 ) {
        //cout << "Budding!" << endl;
        try {
          Cell * daughter = new Cell(lt, bf, ma, pd, id);
          daughters.push_back(daughter);

          next_bud = M_LN2/bud_freq + next_bud;
          if (++age > max_age) {
            die();
          }
          return true;
        } 
        catch (std::bad_alloc & ba) {
          cout << "Daughter stack out of room!" << endl;
          exit(EXIT_FAILURE);
        }
      }
      return false;
    }
    return false;
  }

  void Cell::info() {
    cout << "Cell " << id << endl;
    cout << "  Parent: " << parent << endl;
    cout << "  Number of daughters: " << daughters.size() << endl;
    cout << endl;
    cout << "  Age:  " << age << endl;
    cout << "  Max Age:  " << max_age << endl;
    cout << "  Lag time: " << lag_time << endl;
    cout << "  Lag time left: " << lag_left << endl;
    cout << "  Budding frequency: " << bud_freq << endl;
    cout << "  Probability of death: " << prob_death << endl;
    if (alive == true) {
      cout << "  Time to next bud: " << next_bud << endl;
    } else {
      cout << "This cell is dead." << endl;
    }
  }

  Cell * Cell::last_daughter() {
    return daughters[daughters.size()-1];
  }
} // End of namespace SCALES
