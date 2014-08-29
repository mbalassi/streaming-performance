#!/bin/bash
thisDir=$(dirname $0)
thisDir=$(readlink -f "$thisDir")

source $thisDir/load-spark-config.sh

filePath=$1
directory=resources
if [ "$#" = "1" ]; then
    
    fileName="${filePath##*/}"
    ssh $sparkUser@$sparkMaster 'mkdir -p '$directory''

    scp $filePath $sparkUser@$sparkMaster:$directory/$fileName
    ssh $sparkUser@$sparkMaster '
	for slave in '$sparkSlaves';
	do
		echo -n $slave,
        ssh $slave mkdir -p '$directory'
		scp '$directory'/'$fileName' $slave:'$directory'/;
	done
	'
else
	echo "USAGE:"
	echo "run <file>"
fi
