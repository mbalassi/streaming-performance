#!/bin/bash
perfDir=$1
stormDir=$2
if [ -d "${coredir}"] & [ -d "${perfDir}" ]; then
    
    #ssh -n storm@dell150.ilab.sztaki.hu "$stormDir/bin/stop-cluster.sh; sleep 2; $stormDir/bin/stop-cluster.sh"

    scp $(ls $perfDir/target/stream* | head -n 1) storm@dell150.ilab.sztaki.hu:$stormDir/lib/streaming-performance-0.1-SNAPSHOT.jar

    ssh storm@dell150.ilab.sztaki.hu '
	for j in {101..125} {127..142} 144 145;
	do
		echo -n $j,
        scp '$stormDir'/lib/streaming-performance-0.1-SNAPSHOT.jar storm@dell$j:'$stormDir'/lib/
	done
	'
        
    #ssh -n storm@dell150.ilab.sztaki.hu "$stormDir/bin/start-cluster.sh"

else
	echo "USAGE:"
	echo "run <perf directory> <storm directory>"
fi
