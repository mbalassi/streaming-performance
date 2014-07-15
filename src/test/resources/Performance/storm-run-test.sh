#!/bin/bash
toDir=$1
testParams=$2
length=$3
stormDir=$4
jarFile='storm-dist'

if [ -d "${toDir}" ] ; then
	echo "removing files"
	./storm-remove-files.sh $stormDir

	paramsWithSpace="${testParams//_/ }"

	rm -r $toDir/$testParams/*;
	mkdir $toDir/$testParams;

	ssh -n storm@dell150.ilab.sztaki.hu "timeout ${length} ./$stormDir/bin/storm jar ./$stormDir/lib/${jarFile} -c storm.WordCountTopology -a cluster /home/storm/${stormDir}/resources/hamlet.txt /home/storm/${stormDir}/log/counter/ ${paramsWithSpace}"
    #ssh -n storm@dell150.ilab.sztaki.hu "$stormDir/bin/stop-cluster.sh; sleep 2; $stormDir/bin/start-cluster.sh"

	echo "job finished"

	echo "copying"
	./storm-copy-files.sh $toDir/$testParams $stormDir
else
	echo "USAGE:"
	echo "run <directory> <test params separated by _> <length of test in seconds> <storm directory>"
fi
