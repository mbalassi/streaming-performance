#!/bin/bash
toDir=$1
stratoDir=$2
echo COPYING:
if [ -d "${toDir}" ] ; then
	ssh strato@dell150.ilab.sztaki.hu '
	for j in {101..125} {127..142} 144 145;
	do
		echo -n $j,
		for i in $(ssh dell$j "ls '$stratoDir'/log/counter/");
			do scp strato@dell$j:'$stratoDir'/log/counter/$i '$stratoDir'/log/all_tests/counter/$i;
		done
		for i in $(ls '$stratoDir'/log/counter/);
			do cp '$stratoDir'/log/counter/$i '$stratoDir'/log/all_tests/counter/$i;
		done
	done
	'
	echo 150
	scp strato@dell150.ilab.sztaki.hu:$stratoDir/log/all_tests/counter/* $toDir
else
	echo "USAGE:"
	echo "run <directory> <strato directory>"
fi
