#!/bin/bash
thisDir=$(dirname $0)
thisDir=$(readlink -f "$thisDir")
saveDir=${thisDir}/../testdata
javaDir="$thisDir"/../../../../target/
stormDir='apache-storm-0.9.2-incubating'

# $defaultBatchSize $clusterSize $sourceSize $splitterSize $counterSize $sinkSize

argsArray=("4_2_2_2_2")  

mkdir -p $saveDir/$stratoDir

trap "exit" INT
for i in ${!argsArray[*]}; do
    ${thisDir}/storm-run-test.sh $saveDir/$stratoDir ${argsArray[$i]} 320 $stormDir
done
