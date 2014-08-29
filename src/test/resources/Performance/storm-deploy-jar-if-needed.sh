#!/bin/bash
thisDir=$(dirname $0)
thisDir=$(readlink -f "$thisDir")

source $thisDir/load-storm-config.sh

jarPath=$1
stormDir=$2
if [ "$#" = "2" ]; then
    fileName="${jarPath##*/}"
    localMd5=$(md5sum $jarPath | awk '{print $1}')
    remoteMd5=$(ssh $stormUser@$stormMaster "md5sum $stormDir/lib/$fileName" | awk '{print $1}')
    if [ ! $localMd5 = $remoteMd5 ]; then
        ${thisDir}/storm-deploy-one.sh $jarPath $stormDir lib
    else
        echo no deploy needed
    fi
else
	echo "USAGE:"
	echo "run <jar file> <storm directory>"
fi
