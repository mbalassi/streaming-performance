#!/bin/bash
thisDir=$(dirname $0)
thisDir=$(readlink -f "$thisDir")

source $thisDir/load-flink-config.sh

toDir=$1
testParams=$2
length=$3
stratoDir=$4
jarFile=$5
classPath=$6
resourceFile=$7

if [ -d "${toDir}" ] ; then
	echo "removing files"
	./strato-remove-files.sh $stratoDir

	paramsWithSpace="${testParams//_/ }"

	rm -r $toDir/$testParams/*;
	mkdir $toDir/$testParams;

    ${thisDir}/strato-create-logging-folders.sh $stratoDir
    ${thisDir}/strato-deploy-resource-if-needed.sh $stratoDir $resourceFile

    resourceFileName="${resourceFile##*/}"
    resourcePathOnCluster=$stratoDir/resources/$resourceFileName

	ssh -n $stratoUser@$stratoMaster "timeout ${length} ./$stratoDir/bin/flink run -v -j ./$stratoDir/lib/${jarFile} -c $classPath -a cluster $resourcePathOnCluster ./${stratoDir}/log/counter/ ./${stratoDir}/lib/${jarFile} ${stratoMaster} ${stratoRcpPort} ${paramsWithSpace}"
    ssh -n $stratoUser@$stratoMaster "./$stratoDir/bin/stop-cluster.sh; sleep 2; $stratoDir/bin/start-cluster.sh"

	echo "job finished"

	echo "copying"
	./strato-copy-files.sh $toDir/$testParams $stratoDir
else
	echo "USAGE:"
	echo "run <save directory> <test params separated by _> <length of test in seconds> <strato directory> <jar file to run> <class to run (whole path)> <resource file path>"
fi
