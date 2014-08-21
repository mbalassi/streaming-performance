#!/bin/bash
thisDir=$(dirname $0)
thisDir=$(readlink -f "$thisDir")

source $thisDir/load-storm-config.sh

toDir=$1
stormDir=$2
echo COPYING:
if [ -d "${toDir}" ] ; then
	ssh $stormUser@$stormMaster '
	for slave in '$stormSlaves';
	do
		echo -n $slave,
		for i in $(ssh $slave "ls '$stormDir'/logs/counter/");
			do scp $slave:'$stormDir'/logs/counter/$i '$stormDir'/logs/all_tests/counter/$i;
		done
	done
    for i in $(ls '$stormDir'/logs/counter/);
		do cp '$stormDir'/logs/counter/$i '$stormDir'/logs/all_tests/counter/$i;
	done
	'
	echo $stormMaster
	scp $stormUser@$stormMaster:$stormDir/logs/all_tests/counter/* $toDir
else
	echo "USAGE:"
	echo "run <directory> <storm directory>"
fi
