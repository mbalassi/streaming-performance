#!/bin/bash
thisDir=$(dirname $0)
thisDir=$(readlink -f "$thisDir")

jarPath=$1
stormDir=$2
if [ "$#" = "2" ]; then
    ${thisDir}/storm-deploy-one.sh $jarPath $stormDir lib
else
	echo "USAGE:"
	echo "run <jar file> <storm directory>"
fi
