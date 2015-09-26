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

        self.program = "NoDilutionAR"

        self.n_seeds = 5
        self.seeds = []
        self.sg = SeedGenerator()

        self.migration_ranges = ['global', 'local']
        self.migration_type = ['indv']
        self.coop_freq = [0.5]
        self.initial_pop_size = [1e5]
        self.mutant_freqs = [0, 2e-5, 2e-4, 2e-3,]
        self.frac_occupied = [0.25, 0.5, 0.75, 1]
        self.coop_to_cheat = [0] 
        self.cheat_to_coop = [0]
        self.migration_rates = [0] + [10**-y for y in reversed(range(4,13))]

        self.base_km = [10]
        self.coop_release = [2.4]
        self.amount_needed = [5.5]
        self.cheat_adv = [1.2]
        self.evo_km_adv = [10]
        self.evo_death_adv = [2]
        self.evo_trade = [0.7]

        self.initial_resource = [0.0]
        self.size = [12]
        self.hours = [50000]
        self.save_every = [100]
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
            self.mem = '4000'
            self.mem2 = '5000'
            self.code_base = '/home/ajwaite/code/' + project
            self.java = ("time java -d64 -server -Xmx{0}m "
                         "-XX:+HeapDumpOnOutOfMemoryError "
                         "-Djava.io.tmpdir=$TMPDIR".format(self.mem))
            self.sbatch = ("sbatch --mem={} -n1 -t{} "
                           "--wrap='".format(self.mem2, self.time))
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

        args = list(list(i) for i in product(
                                             params['migration_ranges'], #0
                                             params['migration_type'],   #1
                                             params['initial_pop_size'], #2
                                             params['mutant_freqs'],     #3
                                             params['coop_release'],     #4
                                             params['amount_needed'],    #5
                                             params['coop_freq'],        #6
                                             params['base_km'],          #7
                                             params['cheat_adv'],        #8
                                             params['evo_km_adv'],       #9
                                             params['evo_death_adv'],    #10
                                             params['evo_trade'],        #11
                                             params['initial_resource'], #12
                                             params['size'],             #13
                                             params['frac_occupied'],    #14
                                             params['migration_rates'],  #15
                                             params['coop_to_cheat'],    #16
                                             params['cheat_to_coop']))   #17

        output = self.output
        if output == '': output = self.__class__.__name__
        outputs = []
        cmds = []
        template = '{0}_{1}_n={2}_mutant-freq={3}_mig={15}_coop-release={4}_' \
                   'km-adv={9}_death-adv={10}_' \
                   'coop-freq={6}_size={13}_occ={14}_u={17}'

        for arg in args:
            seeds = []
            if len(self.seeds)==0:
                seeds = self.sg.generate(self.n_seeds)
            elif len(self.seeds) < self.n_seeds:
                raise Exception("need " + self.n_seeds + " seeds.")
            else:
                seeds = self.seeds

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

class AdaptiveRace(AdaptiveRaceParams):
    def __init__(self):
        super(AdaptiveRace, self).__init__()
        self.migration_ranges = ['global']
        self.output='no_dilution'

class CoopToCheat(AdaptiveRaceParams):
    def __init__(self):
        super(CoopToCheat, self).__init__()
        self.coop_to_cheat = [0,5e-7,5e-6]
        self.output='no_dilution'

class ExtremePeriodicDilution(AdaptiveRace):
    def __init__(self):
        super(ExtremePeriodicDilution, self).__init__()
        self.program = "ExtremePeriodicDilutionAR"
        self.frac_occupied = [0.5]
        self.mutant_freqs = [0, 2e-5, 2e-4, 2e-3]
        self.output = 'periodic_dilution/frac=0.99_hrs=1.00e+03'

class VeryLowRelease(AdaptiveRace):
    def __init__(self):
        super(VeryLowRelease, self).__init__()
        self.migration_ranges = ['global']
        self.coop_release = [0.11]
        self.frac_occupied = [0.5]
        self.output = 'no_dilution/evo_limiting_release/50_percent_survival/'

class ReleaseTest(AdaptiveRaceParams):
    def __init__(self):
        super(ReleaseTest, self).__init__()
        self.size = [32]
        self.initial_pop_size = [1]
        self.frac_occupied = [1]
        self.migration_ranges = ['global']
        self.migration_rates = [0]
        self.coop_freq = [1]
        self.mutant_freqs = [0]
        self.coop_release = [x + y/10.0 for x in range(5) for y in range(10)] + [y for y in range(5, 1005, 5)]
        #self.coop_release = [1e5]
        self.hours = [5000]
        self.save_every = [1]
        self.output = 'release_test/anc'

class Test(AdaptiveRaceParams):
    def __init__(self):
        super(Test, self).__init__()
        self.migration_ranges = ['global']
        self.size = [2]
        self.coop_freq = [0.99]
        self.mutant_freqs = [2e-3]
        self.migration_rates = [0]
        self.frac_occupied = [0.5]
        self.initial_resource = [0.0]
        self.coop_to_cheat = [0.0]
        self.hours = [100]
        self.save_every = [1]
        self.seeds = [str(i) for i in '1'*self.n_seeds]

class Benchmark(AdaptiveRaceParams):
    def __init__(self):
        super(Benchmark, self).__init__()
        self.migration_ranges = ['global']
        self.size = [2]
        self.coop_freq = [0.99]
        self.mutant_freqs = [2e-3]
        self.migration_rates = [0]
        self.frac_occupied = [0.5]
        self.initial_resource = [0.0]
        self.coop_to_cheat = [0.0]
        self.hours = [100]
        self.save_every = [1]
        self.seeds = [str(i) for i in '1'*self.n_seeds]

if __name__ == "__main__":
    ps = Benchmark()
    #ps = Test()
    #ps = ReleaseTest()
    #ps = AdaptiveRace()
    #ps = CoopToCheat()
    #ps.test(1)
    ps.run(1)
