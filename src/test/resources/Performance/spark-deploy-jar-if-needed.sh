#!/bin/bash
thisDir=$(dirname $0)
thisDir=$(readlink -f "$thisDir")

source $thisDir/load-spark-config.sh

filePath=$1
sparkDir=$2
if [ "$#" = "2" ]; then
    
    fileName="${filePath##*/}"
    localMd5=$(md5sum $filePath | awk '{print $1}')
    remoteMd5=$(ssh $sparkUser@$sparkMaster "md5sum $sparkDir/lib/$fileName" | awk '{print $1}')
    if [ ! $localMd5 = $remoteMd5 ]; then
        ${thisDir}/spark-deploy-jar.sh $filePath $sparkDir
    else
        echo no deploy needed
    fi
else
	echo "USAGE:"
	echo "run <file> <spark directory>"
fi
