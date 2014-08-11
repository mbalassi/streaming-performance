#!/bin/bash
thisDir=$(dirname $0)
thisDir=$(readlink -f "$thisDir")
saveDir=/home/tofi/git/streaming-performance/src/test/resources/testdata
javaDir="$thisDir"/../../../../target/
stratoDir='flink-0.6-incubating-SNAPSHOT-streaming-new'
jarFile='streaming-performance-0.1-SNAPSHOT.jar'
classPath='org.apache.flink.streaming.performance.iterative.PageRankIterativeMain'

argsArray=("10_10")  

mkdir -p $saveDir/$stratoDir

trap "exit" INT
for i in ${!argsArray[*]}; do
    ${thisDir}/strato-run-test.sh $saveDir/$stratoDir ${argsArray[$i]} 320 $stratoDir $jarFile $classPath
done
