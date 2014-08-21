#!/bin/bash
thisDir=$(dirname $0)
thisDir=$(readlink -f "$thisDir")

source $thisDir/load-flink-config.sh

coreDir=$1
perfDir=$2
stratoDir=$3
if [ -d "${coredir}"] & [ -d "${perfDir}" ]; then
    
    ssh -n $stratoUser@$stratoMaster "$stratoDir/bin/stop-cluster.sh; sleep 1"

    scp $(ls $coreDir/flink-streaming-core/target/flink-stream* | head -n 1) $stratoUser@$stratoMaster:$stratoDir/lib/flink-streaming-core-0.6-incubating-SNAPSHOT.jar

    scp $(ls $coreDir/flink-streaming-connectors/target/flink-stream* | head -n 1) $stratoUser@$stratoMaster:$stratoDir/lib/flink-streaming-connectors-0.6-incubating-SNAPSHOT.jar

    scp $(ls $perfDir/target/stream* | head -n 1) $stratoUser@$stratoMaster:$stratoDir/lib/streaming-performance-0.1-SNAPSHOT.jar

    ssh $stratoUser@$stratoMaster '
	for slave in '$stratoSlaves';
	do
		echo -n $slave,
		scp '$stratoDir'/lib/flink-streaming-core-0.6-incubating-SNAPSHOT.jar $slave:'$stratoDir'/lib/;
		scp '$stratoDir'/lib/flink-streaming-connectors-0.6-incubating-SNAPSHOT.jar $slave:'$stratoDir'/lib/;
        scp '$stratoDir'/lib/streaming-performance-0.1-SNAPSHOT.jar $slave:'$stratoDir'/lib/
	done
	'
        
    ssh -n $stratoUser@$stratoMaster "$stratoDir/bin/start-cluster.sh"

else
	echo "USAGE:"
	echo "run <core directory> <perf directory> <strato directory>"
fi
