#!/bin/bash
thisDir=$(dirname $0)
thisDir=$(readlink -f "$thisDir")

source $thisDir/load-flink-config.sh

toDir=$1
stratoDir=$2
echo COPYING:
if [ -d "${toDir}" ] ; then
	ssh $stratoUser@$stratoMaster '
	for slave in '$stratoSlaves';
	do
		echo -n $slave,
		for i in $(ssh $slave "ls '$stratoDir'/log/counter/");
			do scp $slave:'$stratoDir'/log/counter/$i '$stratoDir'/log/all_tests/counter/$i;
		done
	done
    for i in $(ls '$stratoDir'/log/counter/);
	    do cp '$stratoDir'/log/counter/$i '$stratoDir'/log/all_tests/counter/$i;
	done
	'
	echo $stratoMaster
	scp $stratoUser@$stratoMaster:$stratoDir/log/all_tests/counter/* $toDir
else
	echo "USAGE:"
	echo "run <directory> <strato directory>"
fi
