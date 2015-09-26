#!/bin/bash

TOP='/home/ajwaite/Documents/Code/Java/metapop2'

time java -Xshare:off -Xmx1000m -server -cp .:$CLASSPATH:$TOP/lib/commons-math.jar:$TOP/build/classes/framework org.fhcrc.honeycomb.metapop.experiment.$1 $2
