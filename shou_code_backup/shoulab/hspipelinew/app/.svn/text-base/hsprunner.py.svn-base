import os
import time
from getpass import getuser
from shutil import move
from time import localtime, strftime
from subprocess import Popen, PIPE, STDOUT, call
from paths import Paths

class Hsprunner(object):
    def __init__(self, form):
        self.timestamp = strftime("%Y%m%d%H%M", localtime())                   
        self.type = 'p'
        self.slurm_threads = 8 
        self.time = '1-0'

        self.paths = Paths(proj=form.project_path.data, 
                           anc=form.anc_path.data, ref=form.ref_path.data)

        self.cmd_name = os.path.join(self.paths.silo_base, 'bin/hspipeline')

        (self.paths.outlog,
         self.paths.outlog_final) = [os.path.join(self.paths.proj_web, 
                                                  self.timestamp + p)
                                                  for p in
                                                  ['-outlog.txt',
                                                   '-outlog-final.txt']]

        self.parts_to_run = _make_command_string(align=form.align.data,
                                                 trim=form.trim.data,
                                                 find=form.find.data,
                                                 compare=form.compare.data,
                                                 plot=form.plot.data)

        self.run_args = [self.parts_to_run, self.type, self.paths.proj,
                         self.paths.ref, self.paths.anc, self.paths.r]

    def run(self):
        run_cmd = []
        if getuser() == 'ajwaite':
            self.cmd_name = 'hspipeline'
            run_cmd = [self.cmd_name, '-t2'] 
        else:
            setenv = os.path.join(self.paths.silo_base, 'bin/setenv.sh')
            run_cmd = ['srun', 
                       #'--slurmd-debug=4',
                       '--task-prolog={}'.format(setenv),
                       '-t{}'.format(self.time),
                       '-c{}'.format(self.slurm_threads),
                       self.cmd_name, '-t{}'.format(self.slurm_threads)]

        run_cmd.extend(self.run_args)

        outfile = None
        try:
            outfile = open(self.paths.outlog, 'w', 0)
            outfile.write("command: " + " ".join(run_cmd) + '\n')
            outfile.flush()
        except IOError as e:
            outfile.close()
            print 'opening outfile failed {0}: {1}'.format(e.errno, e.strerror)

        time.sleep(0.5)

        try:
            call(run_cmd, stdout=outfile, stderr=STDOUT)
        except Exception as e:
            err = 'call failed {0}: {1}'.format(e.errno, e.strerror)
            outfile.write(err + '\n')
            outfile.flush()
            outfile.close()
            print err

        try:
            outfile.close()
        except IOError as e:
            print 'closing outfile failed {0}: {1}'.format(e.errno, e.strerror)


        move(self.paths.outlog, self.paths.outlog_final)

def _make_command_string(align, trim, find, compare, plot):
    align = align and 'A' or ''
    trim = trim and 'x' or ''
    find = find and 'F' or ''
    compare = compare and 'C' or ''
    plot = plot and 'P' or ''
    commands = "".join([align, trim, find, compare, plot])

    if commands != "": 
        commands = '-' + commands

    return commands

if __name__ == "__main__":
    pass

