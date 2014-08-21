#!/bin/bash
thisDir=$(dirname $0)
thisDir=$(readlink -f "$thisDir")

source $thisDir/load-flink-config.sh

stratoDir=$1
resourceFile=$2

resourceFileName="${resourceFile##*/}"

out=$(ssh $stratoUser@$stratoMaster 'ls ~/'$stratoDir'/resources/'$resourceFileName' | wc -l')
if [ ! $out = "1" ]; then
    ${thisDir}/strato-deploy-resource.sh $resourceFile $stratoDir
fi
