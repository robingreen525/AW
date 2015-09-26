#ifndef POP_H_
#define CELL_H_

#include <vector>

namespace SCALES 
{

class Pop
{
    private:
        std::vector<Cell *> pop;
        double bud_freq;
        double bud_freq_sd;
    public:
        Pop( int init, double bf, double bf_sd );
        ~Pop();
        void popGrow(vector<Cell *> & p);
        int size() { return pop.size(); }
};

#endif



