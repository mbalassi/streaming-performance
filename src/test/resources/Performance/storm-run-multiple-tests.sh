#!/bin/bash
thisDir=$(dirname $0)
thisDir=$(readlink -f "$thisDir")
saveDir=${thisDir}/../testdata
javaDir="$thisDir"/../../../../target/
stormDir='storm-dist'
jarFile='streaming-performance-0.1-SNAPSHOT.jar'
classPath=org.apache.storm.streaming.performance.general.StormWordCountPerformanceMain
resourceFile=/home/storm/${stormDir}/resources/hamlet.txt
#resourceFile=/home/storm/${stormDir}/resources/edgeList

# $workercount $sourceSize $splitterSize $counterSize $sinkSize

argsArray=("32_10_10_10_10")  

mkdir -p $saveDir/$stormDir

trap "exit" INT
for i in ${!argsArray[*]}; do
    ${thisDir}/storm-run-test.sh $saveDir/$stormDir ${argsArray[$i]} 140 $stormDir $jarFile $classPath $resourceFile
done
