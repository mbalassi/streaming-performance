#!/bin/bash
thisDir=$(dirname $0)
thisDir=$(readlink -f "$thisDir")
saveDir=/home/tofi/git/streaming-performance/src/test/resources/testdata
javaDir="$thisDir"/../../../../target/
stratoDir='flink-0.6-incubating-SNAPSHOT'

# $defaultBatchSize $clusterSize $sourceSize $splitterSize $counterSize $sinkSize

#argsArray=("1000_16_100_30_10_10" "1000_16_100_10_30_10" "1000_16_100_10_10_30" "1000_16_100_20_20_5" "1000_16_100_20_5_20" "1000_16_100_5_20_20" "1000_16_100_20_20_20" "1000_16_100_30_30_30" "1000_16_100_5_5_5")

#argsArray=("1000_32_32_2_10_10" "1000_32_32_10_2_10" "1000_32_32_10_10_2" "1000_32_32_2_2_10" "1000_32_32_2_10_2" "1000_32_32_10_2_2")

#"1000_32_32_2_2_10" "1000_32_32_2_10_2" "1000_32_32_2_10_10")

argsArray=("1000_32_32_2_10_2" "1000_32_32_2_10_10" "1000_32_32_10_2_2" "1000_32_32_10_2_10" "1000_32_32_10_10_2" "1000_4_4_2_2_2")  

mkdir -p $saveDir/$stratoDir

trap "exit" INT
for i in ${!argsArray[*]}; do
    ${thisDir}/dell-run-test.sh $saveDir/$stratoDir ${argsArray[$i]} 320 $stratoDir
done
