#!/bin/bash
thisDir=$(dirname $0)
thisDir=$(readlink -f "$thisDir")

source $thisDir/load-storm-config.sh

stormDir=$1
resourceFile=$2

resourceFileName="${resourceFile##*/}"

out=$(ssh $stormUser@$stormMaster 'ls ~/'$stormDir'/resources/'$resourceFileName' | wc -l')
if [ ! $out = "1" ]; then
    ${thisDir}/storm-deploy-resource.sh $resourceFile $stormDir
fi
