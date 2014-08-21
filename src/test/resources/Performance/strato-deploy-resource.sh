#!/bin/bash
thisDir=$(dirname $0)
thisDir=$(readlink -f "$thisDir")

source $thisDir/load-flink-config.sh

resourcePath=$1
stratoDir=$2
if [ "$#" = "2" ]; then
    ${thisDir}/strato-deploy-one.sh $resourcePath $stratoDir resources
else
	echo "USAGE:"
	echo "run <resource file> <strato directory>"
fi
