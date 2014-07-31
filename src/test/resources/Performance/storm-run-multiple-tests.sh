#!/bin/bash
thisDir=$(dirname $0)
thisDir=$(readlink -f "$thisDir")
saveDir=${thisDir}/../testdata
javaDir="$thisDir"/../../../../target/
stormDir='storm-dist'
#stormDir='storm-0.9.0.1'

# $workercount $sourceSize $splitterSize $counterSize $sinkSize

argsArray=("24_2_8_16_16")  

mkdir -p $saveDir/$stormDir

trap "exit" INT
for i in ${!argsArray[*]}; do
    ${thisDir}/storm-run-test.sh $saveDir/$stormDir ${argsArray[$i]} 320 $stormDir
done
