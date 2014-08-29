#!/bin/bash
thisDir=$(dirname $0)
thisDir=$(readlink -f "$thisDir")

source $thisDir/load-spark-config.sh

resourceFile=$1

resourceFileName="${resourceFile##*/}"

out=$(ssh $sparkUser@$sparkMaster 'ls resources/'$resourceFileName' | wc -l')
if [ ! $out = "1" ]; then
    ${thisDir}/spark-deploy-resource.sh $resourceFile
fi
