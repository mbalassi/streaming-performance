#!/bin/bash
thisDir=$(dirname $0)
thisDir=$(readlink -f "$thisDir")

source $thisDir/load-flink-config.sh

filePath=$1
stratoDir=$2
directory=$3
if [ "$#" = "3" ]; then
    
    fileName="${filePath##*/}"
    ssh -n $stratoUser@$stratoMaster "$stratoDir/bin/stop-cluster.sh; sleep 1"

    scp $filePath $stratoUser@$stratoMaster:$stratoDir/$directory/$fileName

    ssh $stratoUser@$stratoMaster '
	for slave in '$stratoSlaves';
	do
		echo -n $slave,
		scp '$stratoDir'/'$directory'/'$fileName' $slave:'$stratoDir'/'$directory'/;
	done
	'
        
    ssh -n $stratoUser@$stratoMaster "$stratoDir/bin/start-cluster.sh"

else
	echo "USAGE:"
	echo "run <file> <strato directory> <directory to deploy in>"
fi
