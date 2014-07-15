#!/bin/bash
thisDir=$(dirname $0)
thisDir=$(readlink -f "$thisDir")
javaDir="$thisDir"/../../../../target/

#defaultBatchSize=100
#clusterSize=1
#sourceSize=4
#splitterSize=2
#counterSize=2
#sinkSize=2

# $defaultBatchSize $clusterSize $sourceSize $splitterSize $counterSize $sinkSize

argsArray=("1 2 2 2 2 2" "1 4 4 2 2 2" "1 4 4 4 2 2" "1 4 4 4 4 2" "1 4 4 4 4 4")

for i in ${!argsArray[*]}; do
    timeout 320 java -cp "$javaDir"/streaming-performance-0.1-SNAPSHOT-jar-with-dependencies.jar org.apache.flink.streaming.performance.WordCountPerformanceLocal ${argsArray[$i]} felesleges
done
