#!/usr/bin/python

import os
import subprocess
import socket
from itertools import product
from time import time, sleep

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
        self.time = '60'
        self.java = "time java -Xmx1000m -server"

        self.set_env(socket.gethostname())

        self.cp = ('-cp .:$CLASSPATH:{0}/lib/commons-math.jar:'
                   '{0}/{1}').format(self.code_base, self.class_base)

        self.program = "LBLStaticEnvGlobalDil"


        self.list_types = ['migration_types', 'mutant_freqs',
                           'migration_rates', 'fractional_death_rates',
                           'env_change_probs', 'u', 'cheat_adv']

        self.n_seeds = 4
        self.sg = SeedGenerator()

        self.migration_types = ['global', 'local']
        self.cheat_adv = [0.02]
        self.u = [0]
        self.mutant_freqs = [0, 1e-4]
        self.fraction_occupied = [0.1]
        self.migration_rates = [10**y for y in range(-1,-13,-1)]
        self.fractional_death_rates = [1e-5, 1e-4, 1e-3, 1e-2, 1e-1, 1]
        self.env_change_probs = [0]

        self.doubling_time = 2
        self.size = 10
        self.hours = 100000
        self.save_every = 200
        self.output = ''

        self.commands = []

    def set_env(self, hostname):
        if hostname == 'Nietzsche':
            self.code_base = '/home/ajwaite/Documents/Code/Java/metapop'
            self.sbatch = ''
            self.end = ''
            self.save_base = 'dat'
        else:
            self.code_base = '/home/ajwaite/code/metapop'
            self.sbatch = "sbatch -n1 -t{} --wrap='".format(self.time)
            self.end = "'"
            self.save_base = ('/home/ajwaite/shougroup/lab_users/Adam/'
                              'metapop_results')


    def make_params(self):
        self.params = dict((k, string(v)) for (k,v) in
                           self.__dict__.iteritems() if k != "params")
    def set_sbatch(self):
        if self.size >= 50:
            self.time = '6-0'

        if self.sbatch != '':
            self.sbatch = "sbatch -n1 -t{} --wrap='".format(self.time)

    def prep(self, reps):
        subprocess.call(['ant', '-f', '../build.xml'])
        for i in range(reps):
            self.build_commands()

    def test(self, reps):
        print self.output
        self.prep(reps)
        print "\n\n".join(self.commands)
        print len(self.commands), "runs."

    def run(self, reps):
        self.prep(reps)
        if subprocess.call(['which', 'sbatch']) == 0:
            sent = 0
            n_runs = len(self.commands)
            for cmd in self.commands:
                while True:
                    queue = check_queue()
                    if queue > 189:
                        print ("queue is {}. {} of {} jobs sent,"
                               " sleeping...".format(queue, sent, n_runs))
                        sleep(60)
                    else:
                        break

                os.system(cmd)
                sent += 1
        else:
            map(os.system, self.commands)

    def build_commands(self):
        self.output = os.path.join(self.save_base, self.output)
        self.make_params()
        params = self.params

        self.full_program_name = self.package + '.' + self.program

        self.set_sbatch()


        command =  " ".join([self.sbatch, self.java, self.cp,
                             self.full_program_name])

        seeds = self.sg.generate(self.n_seeds)

        for t in self.list_types:
            v = params.get(t)
            if type(v) != list:
                raise Exception("Value of '{}', ('{}') is not a list".
                                format(t, v))

        one = list(list(i) for i in product(params['migration_types'],
                                            params['cheat_adv'],
                                            params['u'],
                                            params['mutant_freqs']))

        one = [e + [params['doubling_time']] + [params['size']] for e in one]

        
        two = list(list(i) for i in product(params['fraction_occupied'],
                                            params['migration_rates'],
                                            params['fractional_death_rates'],
                                            params['env_change_probs']))

        three = [i[0] + i[1] for i in product(one, two)]

        four = [e + seeds + [params['hours']] +
                [params['save_every']] + [params['output']] for e in three]

        for arg_list in four:
            if float(arg_list[3]) == 0.0 and float(arg_list[9]) > 0.0:
                continue
            self.commands.append(" ".join([command, " ".join(arg_list),
                                           self.end]))

def check_queue():
    squeue = 'squeue -u ajwaite | wc -l'

    # one line is the header.
    return int(subprocess.check_output(squeue, shell=True).rstrip('\n')) - 1

def string(s):
    if type(s) == list:
        return [str(i) for i in s]
    elif type(s) != str:
        return str(s)
    else:
        return s

class ChangeOccupancy(AdaptiveRaceParams):
    def __init__(self):
        super(ChangeOccupancy, self).__init__()
        self.mutant_freqs = [0, 1e-5, 1e-4, 1e-3]
        self.fraction_occupied = [0.25, 0.50, 0.75, 1.0]
        #self.output = os.path.join(self.fred,
        #                            "fitness_before_load/static_env/"
        #                            "alter_occupancy/fast_doubling/10x10")

class ChangeOccAndCheatAdv(ChangeOccupancy):
    def __init__(self):
        super(ChangeOccAndCheatAdv, self).__init__()
        self.cheat_adv = [0.02, 0.2]

class ChangeOccWithMutation(ChangeOccupancy):
    def __init__(self):
        super(ChangeOccWithMutation, self).__init__()
        self.u = [1e-4/self.doubling_time, 1e-5/self.doubling_time]
        #self.output = os.path.join(self.fred,
        #                           "fitness_before_load/static_env/"
        #                           "alter_occupancy/fast_doubling/"
        #                           "coops_can_mutate/10x10")

class Both(ChangeOccWithMutation, ChangeOccAndCheatAdv):
    def __init__(self):
        super(Both, self).__init__()


class ChangeOccHighCheatAdv(ChangeOccupancy):
    def __init__(self):
        super(ChangeOccHighCheatAdv, self).__init__()
        self.cheat_adv = [0.2]
        #self.output = os.path.join(self.fred,
        #                           "fitness_before_load/static_env/"
        #                           "alter_occupancy/fast_doubling/"
        #                           "high_cheat_adv/10x10")

class ChangeOccFitnessAfter(ChangeOccupancy):
    def __init__(self):
        super(ChangeOccFitnessAfter, self).__init__()
        self.program = "LinearAfterLoadAR"
        #self.output = os.path.join(self.fred,
        #                           "fitness_after_load/static_env/"
        #                           "alter_occupancy/fast_doubling/10x10")

class ChangeOccLarge(ChangeOccupancy):
    def __init__(self):
        super(ChangeOccLarge, self).__init__()
        self.size = 50
        #self.output = os.path.join(self.fred,
        #                           "fitness_before_load/static_env/"
        #                           "alter_occupancy/fast_doubling/50x50")

class ChangingEnv(AdaptiveRaceParams):
    def __init__(self):
        super(ChangingEnv, self).__init__()
        self.env_change_probs = [0, 1e-5, 2.5e-5, 5e-5, 1e-4] 
        self.output = os.path.join(self.fred,
                                   "fitness_before_load/changing_env/"
                                   "change_at_same_time/fast_doubling/10x10")

class LargeGrid(ChangingEnv):
    def __init__(self):
        super(LargeGrid, self).__init__()
        self.size = 100
        self.output = os.path.join(self.fred,
                                   "fitness_before_load/changing_env/"
                                   "change_at_same_time/fast_doubling/100x100")

class HighDensity(AdaptiveRaceParams):
    def __init__(self):
        super(HighDensity, self).__init__()
        self.sbatch = ''
        self.migration_types = ['global','local']
        self.cheat_adv = [0.2]
        self.mutant_freqs = [0]
        self.migration_rates = [0.1]
        self.fractional_death_rates = [1]
        self.fraction_occupied = [1.0]
        self.hours = 243
        self.save_every = 1.0/60.0 
        #self.output = os.path.join(self.fred,
        #                           "fitness_before_load/static_env/"
        #                           "alter_occupancy/fast_doubling/10x10")

class Test(AdaptiveRaceParams):
    def __init__(self):
        super(Test, self).__init__()
        self.sbatch = ''
        self.migration_types = ['global','local']
        self.mutant_freqs = [0]
        self.env_change_probs = [0]
        self.u = [1e-4/self.doubling_time]
        self.fraction_occupied = [0.1]
        self.migration_rates = [1e-12]
        self.fractional_death_rates = [1e-6]
        self.hours = 1
        self.save_every = 1
        self.output='output_test'

class GlobalFullPeriodicDil(AdaptiveRaceParams):
    def __init__(self):
        super(GlobalFullPeriodicDil, self).__init__()

        self.run_type = 'local'
        self.sbatch = ''
        self.mutant_freqs = [0, 1e-5, 1e-4, 1e-3]
        self.fraction_occupied = [1.0]
        self.migration_types = ['global']
        self.cheat_adv = [0.2]
        self.program = "LBLStaticEnvPeriodicDil"
        self.output = ("fitness_before_load/static_env/static_occupancy/"
                       "periodic_dilution/0.99_10000hrs/10x10")

if __name__ == "__main__":
    ps = GlobalFullPeriodicDil()
    ps.test(1)
    #ps.run(1)
