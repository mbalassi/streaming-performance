#!/bin/bash
thisDir=$(dirname $0)
thisDir=$(readlink -f "$thisDir")
saveDir=${thisDir}/../testdata
javaDir="$thisDir"/../../../../target/
stormDir='apache-storm-0.9.2-incubating'

# $defaultBatchSize $clusterSize $sourceSize $splitterSize $counterSize $sinkSize

#argsArray=("1000_16_100_30_10_10" "1000_16_100_10_30_10" "1000_16_100_10_10_30" "1000_16_100_20_20_5" "1000_16_100_20_5_20" "1000_16_100_5_20_20" "1000_16_100_20_20_20" "1000_16_100_30_30_30" "1000_16_100_5_5_5")

#argsArray=("1000_32_32_2_10_10" "1000_32_32_10_2_10" "1000_32_32_10_10_2" "1000_32_32_2_2_10" "1000_32_32_2_10_2" "1000_32_32_10_2_2")

#"1000_32_32_2_2_10" "1000_32_32_2_10_2" "1000_32_32_2_10_10")

argsArray=("32_32_10_10_10")  

mkdir -p $saveDir/$stratoDir

trap "exit" INT
for i in ${!argsArray[*]}; do
    ${thisDir}/storm-run-test.sh $saveDir/$stratoDir ${argsArray[$i]} 320 $stormDir
done
