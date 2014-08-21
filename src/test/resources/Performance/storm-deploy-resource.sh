#!/bin/bash
thisDir=$(dirname $0)
thisDir=$(readlink -f "$thisDir")

resourcePath=$1
stormDir=$2
if [ "$#" = "2" ]; then
    ${thisDir}/storm-deploy-one.sh $resourcePath $stormDir resources
else
	echo "USAGE:"
	echo "run <resource file> <storm directory>"
fi
