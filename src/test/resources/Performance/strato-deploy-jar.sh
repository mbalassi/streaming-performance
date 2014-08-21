#!/bin/bash
thisDir=$(dirname $0)
thisDir=$(readlink -f "$thisDir")

source $thisDir/load-flink-config.sh

jarPath=$1
stratoDir=$2
if [ "$#" = "2" ]; then
    ${thisDir}/strato-deploy-one.sh $jarPath $stratoDir lib
else
	echo "USAGE:"
	echo "run <jar file> <strato directory>"
fi
