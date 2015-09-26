#! /usr/bin/python

import os, sys

classpath = os.environ.get('CLASSPATH')
classpath = '' if (classpath is None) else classpath + ':'

top = os.path.dirname(os.path.abspath('.'))
prog = sys.argv[1]
args = sys.argv[2]

cmd = ('time java -Xshare:off -Xmx4000m -server '
      '-cp .:{0}{1}/lib/commons-math.jar:{1}/build/classes/framework '
      'org.fhcrc.honeycomb.metapop.experiment.{2} '
      '{3}').format(classpath, top, prog, args)

os.system(cmd)
