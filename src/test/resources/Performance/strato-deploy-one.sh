#!/bin/bash
jarPath=$1
stratoDir=$2
if [ "$#" = "2" ]; then
    
    fileName="${jarPath##*/}"
    ssh -n strato@dell150.ilab.sztaki.hu "$stratoDir/bin/stop-cluster.sh; sleep 1"

    scp $jarPath strato@dell150.ilab.sztaki.hu:$stratoDir/lib/$fileName

    ssh strato@dell150.ilab.sztaki.hu '
	for j in {101..125} {127..142} 144 145;
	do
		echo -n $j,
		scp '$stratoDir'/lib/'$fileName' strato@dell$j:'$stratoDir'/lib/;
	done
	'
        
    ssh -n strato@dell150.ilab.sztaki.hu "$stratoDir/bin/start-cluster.sh"

else
	echo "USAGE:"
	echo "run <jar file> <strato directory>"
fi
