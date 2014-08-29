#!/bin/bash
thisDir=$(dirname $0)
thisDir=$(readlink -f "$thisDir")

source $thisDir/load-flink-config.sh

jarPath=$1
stratoDir=$2
if [ "$#" = "2" ]; then
    fileName="${jarPath##*/}"
    localMd5=$(md5sum $jarPath | awk '{print $1}')
    remoteMd5=$(ssh $stratoUser@$stratoMaster "md5sum $stratoDir/lib/$fileName" | awk '{print $1}')
    if [ ! $localMd5 = $remoteMd5 ]; then
        ${thisDir}/strato-deploy-one.sh $jarPath $stratoDir lib
    else
        echo no deploy needed
    fi
else
	echo "USAGE:"
	echo "run <jar file> <strato directory>"
fi
