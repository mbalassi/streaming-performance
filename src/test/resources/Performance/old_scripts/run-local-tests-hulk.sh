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

argsArray=("1 1 4 2 2 2" "1000 1 4 2 2 2" "10000 1 4 2 2 2")

for i in ${!argsArray[*]}; do
    timeout 320 java -cp "$javaDir"/../temp/stratosphere-streaming-0.2-SNAPSHOT-flush.jar eu.stratosphere.streaming.performance.WordCountPerformanceLocal ${argsArray[$i]}
done
