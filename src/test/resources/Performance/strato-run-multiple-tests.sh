#!/bin/bash
thisDir=$(dirname $0)
thisDir=$(readlink -f "$thisDir")
saveDir=/home/tofi/git/streaming-performance/src/test/resources/testdata
javaDir="$thisDir"/../../../../target/
stratoDir='flink-0.6-incubating-SNAPSHOT-streaming-new'
jarFile='streaming-performance-0.1-SNAPSHOT.jar'
classPath='org.apache.flink.streaming.performance.latency.WordCountLatencyMain'
#classPath='org.apache.flink.streaming.performance.general.WordCountPerformanceMain'
resourceFile=/home/strato/${stratoDir}/resources/hamlet.txt
#resourceFile=/home/strato/${stratoDir}/resources/edgeList

argsArray=("10_10_10_10_10_0_10" "10_10_10_10_10_1_10" "10_10_10_10_10_10_10") # "10_10_10_10_10_1_1" "10_10_10_10_10_100_1" "10_10_10_10_10_1000_1" "10_10_10_10_2_0_1" "2_10_10_10_10_0_1")

mkdir -p $saveDir/$stratoDir

trap "exit" INT
for i in ${!argsArray[*]}; do
    ${thisDir}/strato-run-test.sh $saveDir/$stratoDir ${argsArray[$i]} 140 $stratoDir $jarFile $classPath $resourceFile
done
