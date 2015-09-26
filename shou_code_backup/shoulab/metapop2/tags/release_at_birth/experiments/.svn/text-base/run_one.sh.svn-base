#!/bin/bash

TOP='/home/ajwaite/Documents/Code/Java/metapop2'

if [ $1 = 'same' ] ; then
    TOP='/home/ajwaite/Documents/Code/Java/metapop2_with_saturation'
fi

time java -Xmx1000m -server -cp .:$CLASSPATH:$TOP/lib/commons-math.jar:$TOP/build/classes/framework org.fhcrc.honeycomb.metapop.experiment.$2 $3
