#!/bin/bash
coreDir=$1
perfDir=$2
stratoDir=$3
if [ -d "${coredir}"] & [ -d "${perfDir}" ]; then
    
    ssh -n strato@dell150.ilab.sztaki.hu "$stratoDir/bin/stop-cluster.sh; sleep 1"

    scp $(ls $coreDir/flink-streaming-core/target/flink-stream* | head -n 1) strato@dell150.ilab.sztaki.hu:$stratoDir/lib/flink-streaming-core-0.3.jar

    scp $(ls $coreDir/flink-streaming-connectors/target/flink-stream* | head -n 1) strato@dell150.ilab.sztaki.hu:$stratoDir/lib/flink-streaming-connectors-0.3.jar

    scp $(ls $perfDir/target/stream* | head -n 1) strato@dell150.ilab.sztaki.hu:$stratoDir/lib/streaming-performance-0.1-SNAPSHOT.jar

    ssh strato@dell150.ilab.sztaki.hu '
	for j in {101..125} {127..142} 144 145;
	do
		echo -n $j,
		scp '$stratoDir'/lib/flink-streaming-core-0.3.jar strato@dell$j:'$stratoDir'/lib/;
		scp '$stratoDir'/lib/flink-streaming-connectors-0.3.jar strato@dell$j:'$stratoDir'/lib/;
        scp '$stratoDir'/lib/streaming-performance-0.1-SNAPSHOT.jar strato@dell$j:'$stratoDir'/lib/
	done
	'
        
    ssh -n strato@dell150.ilab.sztaki.hu "$stratoDir/bin/start-cluster.sh"

else
	echo "USAGE:"
	echo "run <core directory> <perf directory> <strato directory>"
fi
