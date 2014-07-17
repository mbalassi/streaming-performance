#!/bin/bash
toDir=$1
stormDir=$2
echo COPYING:
if [ -d "${toDir}" ] ; then
	ssh storm@dell150.ilab.sztaki.hu '
	for j in {101..125} {127..142} 144 145;
	do
		echo -n $j,
		for i in $(ssh dell$j "ls '$stormDir'/logs/counter/");
			do scp storm@dell$j:'$stormDir'/logs/counter/$i '$stormDir'/logs/all_tests/counter/$i;
		done
		for i in $(ls '$stormDir'/logs/counter/);
			do cp '$stormDir'/logs/counter/$i '$stormDir'/logs/all_tests/counter/$i;
		done
	done
	'
	echo 150
	scp storm@dell150.ilab.sztaki.hu:$stormDir/logs/all_tests/counter/* $toDir
else
	echo "USAGE:"
	echo "run <directory> <storm directory>"
fi
