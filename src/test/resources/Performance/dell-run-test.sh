#!/bin/bash
toDir=$1
testParams=$2
length=$3
stratoDir=$4
if [ -d "${toDir}" ] ; then
	echo "removing files"
	./dell-remove-files.sh $stratoDir

	paramsWithSpace="${testParams//_/ }"

	rm -r $toDir/$testParams/*;
	mkdir $toDir/$testParams;

	ssh -n strato@dell150.ilab.sztaki.hu "timeout ${length} ./$stratoDir/bin/flink run -j ./$stratoDir/lib/streaming-performance-0.1-SNAPSHOT.jar -c org.apache.flink.streaming.performance.WordCountPerformanceLocal -a ${paramsWithSpace} ${stratoDir}"
    ssh -n strato@dell150.ilab.sztaki.hu "$stratoDir/bin/stop-cluster.sh; sleep 2; $stratoDir/bin/start-cluster.sh"

	echo "job finished"

	echo "copying"
	./dell-copy-files.sh $toDir/$testParams $stratoDir
else
	echo "USAGE:"
	echo "run <directory> <test params separated by _> <length of test in seconds> <strato directory>"
fi
