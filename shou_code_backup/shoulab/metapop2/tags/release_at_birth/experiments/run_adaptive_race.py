#!/usr/bin/python

import os
import sys
import subprocess
import socket
from itertools import product, chain
from time import time, sleep
from numbers import Number

class SeedGenerator(object):
    base = None 
    def __init__(self):
        if SeedGenerator.base is None:
            SeedGenerator.base = int(time())
        else:
            SeedGenerator.base += 1

    def generate(self,n):
        SeedGenerator.base += 10
        return [str(SeedGenerator.base+i) for i in range(n)]

class AdaptiveRaceParams(object):
    def __init__(self):

        self.class_base = 'build/classes/framework'
        self.package = 'org.fhcrc.honeycomb.metapop.experiment'
        self.time = '4-0'
        self.java = "time java -Xmx1000m -server"

        self.program = "GlobalDilutionAR"

        self.n_seeds = 5
        self.seeds = []
        self.sg = SeedGenerator()

        self.migration_ranges = ['global', 'local']
        self.migration_type = ['indv']
        self.initial_pop_size = [1e5]
        self.mutant_freqs = [0, 2e-5, 1e-4, 2e-4, 1e-3, 2e-3, 2e-2]
        self.frac_occupied = [0.25, 0.5, 0.75, 1]
        self.migration_rates = [0] + [10**y for y in range(-1,-13,-1)]

        self.base_km = [10]
        self.coop_release = [2.4]
        self.amount_needed = [5.5]
        self.cheat_adv = [1.2]
        self.evo_adv = [10]
        self.evo_trade = [0.7]
        self.coop_freq = [0.5]
        self.initial_resource = [0.0]
        self.size = [15]
        self.hours = [100000]
        self.save_every = [200]
        self.randomize = False
        self.output = ''

        self.commands = []

    def set_env(self, hostname):
        project = 'metapop2'
        if hostname == 'Nietzsche':
            self.code_base = '/home/ajwaite/Documents/Code/Java/' + project
            self.sbatch = ''
            self.end = ''
            self.save_base = 'dat'
        else:
            self.code_base = '/home/ajwaite/code/' + project
            self.java = "time java -Xmx2000m -server -Djava.io.tmpdir=$TMPDIR"
            self.sbatch = "sbatch --mem=2000 -n1 -t{} --wrap='".format(self.time)
            self.end = "'"
            self.save_base = ('/home/ajwaite/shougroup/lab_users/Adam/'
                              'metapop_results')


    def make_params(self):
        self.set_env(socket.gethostname())
        self.cp = ('-cp .:$CLASSPATH:{0}/lib/commons-math.jar:'
                   '{0}/{1}').format(self.code_base, self.class_base)

        self.params = dict((k, string(v)) for (k,v) in
                           self.__dict__.iteritems() if k != "params")

    def prep(self, reps):
        subprocess.call(['ant', '-f', '../build.xml'])
        for i in range(reps):
            self.build_commands()

    def test(self, reps):
        self.prep(reps)
        print "\n\n".join(self.commands)
        print len(self.commands), "runs."

    def run(self, reps):
        self.prep(reps)
        if self.sbatch:
            sent = 0
            n_runs = len(self.commands)
            for cmd in self.commands:
                while True:
                    queue = check_queue()
                    if queue > 189:
                        print ("queue is {}. {} of {} jobs sent."
                               " Sleeping...".format(queue, sent, n_runs))
                        sys.stdout.flush()
                        sleep(60)
                    else:
                        break

                os.system(cmd)
                sent += 1
        else:
            map(os.system, self.commands)

    def build_commands(self):
        self.make_params()
        params = self.params

        self.full_program_name = self.package + '.' + self.program
        command =  " ".join([self.sbatch, self.java, self.cp,
                             self.full_program_name])

        seeds = []
        if len(self.seeds)==0:
            seeds = self.sg.generate(self.n_seeds)
        elif len(self.seeds) < self.n_seeds:
            raise Exception("need " + self.n_seeds + " seeds.")
        else:
            seeds = self.seeds

        args = list(list(i) for i in product(
                                             params['migration_ranges'],
                                             params['migration_type'],
                                             params['initial_pop_size'],
                                             params['mutant_freqs'],
                                             params['coop_release'],
                                             params['amount_needed'],
                                             params['coop_freq'],
                                             params['base_km'],
                                             params['cheat_adv'],
                                             params['evo_adv'],
                                             params['evo_trade'],
                                             params['initial_resource'],
                                             params['size'],
                                             params['frac_occupied'],
                                             params['migration_rates']))

        output = self.output
        if output == '': output = self.__class__.__name__
        outputs = []
        cmds = []
        template = '{}_{}_n={}_mutant-freq={}_coop-release={}_' \
                   'gamma={}_coop-freq={}_km={}_cheat-adv={}_evo-adv={}_' \
                   'evo-trade={}_resource={}_size={}_occ={}_mig={}'
        for arg in args:
            cmds.append(arg + [str(self.randomize)] + seeds +
                        params['hours'] + params['save_every'])
            out_string = (template.format(*arg) + 
                          '_hrs={}'.format(params['hours'][0]))
            outputs.append(os.path.join(self.save_base, output,
                           out_string))

        for cmd, out in zip(cmds, outputs):
            self.commands.append(
                    " ".join([command, " ".join(cmd), out, self.end]))


def check_queue():
    squeue = 'squeue -u ajwaite | wc -l'

    # one line is the header.
    return int(subprocess.check_output(squeue, shell=True).rstrip('\n')) - 1

def string(s):
    if type(s) == list and len(s) > 0:
        if isinstance(s[0], Number) and type(s[0]) != bool:
            return ["{:.2e}".format(i) for i in s]
        else:
            return s
    elif type(s) != str:
        return str(s)
    else:
        return s

class Test(AdaptiveRaceParams):
    def __init__(self):
        super(Test, self).__init__()
        self.migration_ranges = ['global']
        self.coop_freq = [0.5]
        self.mutant_freqs = [0.0]
        self.migration_rates = [0.0]
        self.frac_occupied = [0.5]
        self.initial_resource = [0.0]
        self.hours = [100]
        self.save_every = [1/100.0]
        #self.seeds = [str(i) for i in '1'*self.n_seeds]

class AdaptiveRace(AdaptiveRaceParams):
    def __init__(self):
        super(AdaptiveRace, self).__init__()
        self.migration_rates = [0] + [10**y for y in range(-4,-13,-1)]
        self.output='limiting_release/individual_migration'

class IndvHighMig(AdaptiveRace):
    def __init__(self):
        super(IndvHighMig, self).__init__()
        self.time = '8-0'
        self.frac_occupied = [0.5]
        self.mutant_freqs = [0, 2e-5, 2e-4, 2e-3]
        self.migration_rates = [10**-y for y in reversed(range(1,4))]

class ExtremePeriodicDilution(AdaptiveRace):
    def __init__(self):
        super(ExtremePeriodicDilution, self).__init__()
        self.program = "ExtremePeriodicDilutionAR"
        self.frac_occupied = [0.5]
        self.mutant_freqs = [0, 2e-5, 2e-4, 2e-3]
        self.migration_rates = [0] + [10**-y for y in reversed(range(4,13))]
        self.output = 'limiting_release/individual_migration/' \
                      'death_during_saturation/periodic_dilution/' \
                      'frac=0.99_hrs=1.00e+04'

class NoDilution(ExtremePeriodicDilution):
    def __init__(self):
        super(NoDilution, self).__init__()
        self.program = "NoDilutionAR"
        self.output = 'limiting_release/individual_migration/' \
                      'death_during_saturation/no_dilution/'

class AdaptiveRaceProp(AdaptiveRaceParams):
    def __init__(self):
        super(AdaptiveRaceProp, self).__init__()
        self.migration_type = ['prop']
        self.mutant_freqs = [0, 5e-5, 5e-4, 1e-3, 1e-2]
        self.output='non-limiting_release/propagule_migration/same_start'

class PropaguleRerun(AdaptiveRace):
    def __init__(self):
        super(AdaptiveRace, self).__init__()
        self.randomize = True
        self.migration_type = ['prop']
        self.mutant_freqs=[1e-5]
        self.frac_occupied = [0.5]
        self.migration_rates = [0.1]
        self.time = '1-0'
        self.output='non-limiting_release/propagule_migration'

class LimitingRelease(AdaptiveRace):
    def __init__(self):
        super(LimitingRelease, self).__init__()
        self.coop_release = [0.923]
        self.output='limiting_release'

class PDtoSD(AdaptiveRaceParams):
    def __init__(self):
        super(PDtoSD, self).__init__()

        self.migration_rates.append(0.0)
        self.mutant_freqs = [0, 1e-5, 1e-4, 1e-3]
        self.frac_occupied = [0.5]

class SameStartSize(AdaptiveRace):
    def __init__(self):
        super(SameStartSize, self).__init__()
        self.mutant_freqs = [0, 5e-5, 5e-4, 1e-3, 1e-2]
        self.output='non-limiting_release/propagule_migration'

class IndividualMigration(AdaptiveRace):
    def __init__(self):
        super(IndividualMigration, self).__init__()
        self.frac_occupied = [0.5]
        self.output = 'non-limiting_release/individual_migration'

class SmallSizeNoMutsIndv(AdaptiveRaceParams):
    def __init__(self):
        super(SmallSizeNoMutsIndv, self).__init__()
        self.migration_type = ['indv']
        self.migration_rates = [10**y for y in range(-1,-3,-1)]
        self.initial_pop_size = [1, 5, 10, 50, 100, 1000]
        self.frac_occupied = [0.25, 0.5]
        self.mutant_freqs = [0]
        self.output = 'non-limiting_release/individual_migration/' \
                      'small_size_no_muts'

class SmallSizeNoMutsProp(AdaptiveRaceParams):
    def __init__(self):
        super(SmallSizeNoMutsProp, self).__init__()
        self.migration_type = ['prop']
        self.time = '1-0'
        self.migration_rates = [0] + [10**y for y in range(-3,-9,-1)]
        self.initial_pop_size = [1, 5, 10, 50, 100, 1000]
        self.mutant_freqs = [0]
        self.output = 'non-limiting_release/propagule_migration/' \
                      'small_size_no_muts'
class ReleaseTest(AdaptiveRaceParams):
    def __init__(self):
        super(ReleaseTest, self).__init__()
        self.size = [20]
        self.initial_pop_size = [1]
        self.frac_occupied = [1]
        self.migration_ranges = ['global']
        self.migration_rates = [0]
        self.coop_freq = [1]
        self.mutant_freqs = [1]
        self.initial_pop_size = [1]
        self.coop_release = [x + y/10.0 for x in range(10) for y in range(10)]
        self.hours = [5000]
        self.save_every = [1]
        self.seeds = [str(i) for i in '1'*self.n_seeds]
        self.output = 'release_test/evo'



if __name__ == "__main__":
    #ps = Test()
    #ps = ReleaseTest()
    #ps = PropaguleRerun()
    #ps = IndividualMigration()
    #ps = SmallSizeNoMutsIndv()
    #ps = SmallSizeNoMutsProp()
    #ps = AdaptiveRaceProp()
    #ps = IndvHighMig()
    ps = ExtremePeriodicDilution()
    ps.test(1)
    #ps.run(1)
