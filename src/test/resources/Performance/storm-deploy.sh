#!/bin/bash
thisDir=$(dirname $0)
thisDir=$(readlink -f "$thisDir")

source $thisDir/load-storm-config.sh

perfDir=$1
stormDir=$2
if [ -d "${coredir}"] & [ -d "${perfDir}" ]; then
    
    scp $(ls $perfDir/target/stream* | head -n 1) $stormUser@$stormMaster:$stormDir/lib/streaming-performance-0.1-SNAPSHOT.jar

    ssh $stormUser@$stormMaster '
	for slave in '$stormSlaves';
	do
		echo -n $slave,
        scp '$stormDir'/lib/streaming-performance-0.1-SNAPSHOT.jar $slave:'$stormDir'/lib/
	done
	'
else
	echo "USAGE:"
	echo "run <perf directory> <storm directory>"
fi
