#!/bin/bash
thisDir=$(dirname $0)
thisDir=$(readlink -f "$thisDir")

source $thisDir/load-storm-config.sh

toDir=$1
testParams=$2
length=$3
stormDir=$4
jarFile=$5
classPath=$6
resourceFile=$7

if [ -d "${toDir}" ] ; then
	echo "removing files"
	./storm-remove-files.sh $stormDir

	paramsWithSpace="${testParams//_/ }"

	rm -r $toDir/$testParams/*;
	mkdir $toDir/$testParams;

    ${thisDir}/storm-create-logging-folders.sh $stormDir
    ${thisDir}/storm-deploy-resource-if-needed.sh $stormDir $resourceFile

    resourceFileName="${resourceFile##*/}"
    stormHomeFullPath=$(ssh -n $stormUser@$stormMaster "pwd $stormDir")
    resourcePathOnCluster=$stormHomeFullPath/$stormDir/resources/$resourceFileName

    className="${classPath##*.}"
    topologyName=$className"-"$testParams"-"$RANDOM
    
	ssh -n $stormUser@$stormMaster "${stormHomeFullPath}/${stormDir}/bin/storm jar ${stormHomeFullPath}/$stormDir/lib/${jarFile} $classPath cluster $resourcePathOnCluster ${stormHomeFullPath}/${stormDir}/logs/counter/ $topologyName ${paramsWithSpace}"
    sleep ${length}
    ssh -n $stormUser@$stormMaster "${stormHomeFullPath}/$stormDir/bin/storm kill $topologyName -w 1"

	echo "job finished"

	echo "copying"
	./storm-copy-files.sh $toDir/$testParams $stormDir
else
	echo "USAGE:"
	echo "run <save directory> <test params separated by _> <length of test in seconds> <storm directory> <jar file to run> <class to run (whole path)> <resource file path>"
fi
