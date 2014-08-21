#!/bin/bash
thisDir=$(dirname $0)
thisDir=$(readlink -f "$thisDir")

source $thisDir/load-storm-config.sh

filePath=$1
stormDir=$2
directory=$3
if [ "$#" = "3" ]; then
    
    fileName="${filePath##*/}"
    scp $filePath $stormUser@$stormMaster:$stormDir/$directory/$fileName

    ssh $stormUser@$stormMaster '
	for slave in '$stormSlaves';
	do
		echo -n $slave,
		scp '$stormDir'/'$directory'/'$fileName' $slave:'$stormDir'/'$directory'/;
	done
	'
else
	echo "USAGE:"
	echo "run <file> <storm directory> <directory to deploy in>"
fi
