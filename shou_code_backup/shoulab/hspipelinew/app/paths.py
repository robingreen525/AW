import os
from getpass import getuser

class Paths(object):
    local_data = '/media/Data/hspipeline_test'
    local_r    = '/home/ajwaite/Documents/Code/hspipeline'

    shouweb_data = '/home/shouweb/shougroup'

    silo_base = '/shared/silo_researcher/Shou_W'
    silo_data = os.path.join(silo_base, 'shougroup')
    silo_r = os.path.join(silo_base, 'code/hspipeline')

    if getuser() == 'ajwaite':
        data = local_data
        data_web = local_data
        r = local_r
    else:
        data = silo_data
        data_web = shouweb_data
        r = silo_r

    def __init__(self, proj=None, anc=None, ref=None):
        self.outlog = ''
        self.outlog_final = ''

        (self.proj, self.anc, self.ref) = [os.path.join(Paths.data, p) 
                                           for p in [proj, anc, ref]]
        self.proj_web = os.path.join(Paths.data_web, proj)
