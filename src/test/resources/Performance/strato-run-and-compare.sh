#!/bin/bash
thisDir=$(dirname $0)
thisDir=$(readlink -f "$thisDir")

stratoDir='flink-0.6-incubating-SNAPSHOT'
jarFile='streaming-performance-0.1-SNAPSHOT.jar'
#classPath  ='org.apache.flink.streaming.performance.WordCountPerformanceLocal'

jarPath=$1
classPath1=$2
postfix1=$3
classPath2=$4
postfix2=$5
saveDir=$6
args=$7
length=$8

if [ "$#" = "8" ]; then

    if [ -d $saveDir/$stratoDir ]; then
        mv $saveDir/$stratoDir $saveDir/$stratoDir-$(date +"%Y_%m_%d_%T")
    fi

    mkdir -p $saveDir/$stratoDir
    ${thisDir}/strato-deploy-one.sh $jarPath $stratoDir

    mkdir -p $saveDir/$stratoDir/$postfix1
    ${thisDir}/strato-run-test.sh $saveDir/$stratoDir/$postfix1 $args $length $stratoDir $jarFile $classPath1

    mkdir -p $saveDir/$stratoDir/$postfix2
    ${thisDir}/strato-run-test.sh $saveDir/$stratoDir/$postfix2 $args $length $stratoDir $jarFile $classPath2

    ${thisDir}/compare-results.sh $saveDir/$stratoDir/$postfix1/$args $postfix1  $saveDir/$stratoDir/$postfix2/$args $postfix2 $saveDir
else
    echo "USAGE:"
	echo "run <jar path> <classpath1> <postfix1> <classpath2> <postfix2> <save directory> <test params separated by _> <length of test>"
fi
