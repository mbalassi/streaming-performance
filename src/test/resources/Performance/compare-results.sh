#!/bin/bash
thisDir=$(dirname $0)
thisDir=$(readlink -f "$thisDir")
result1Dir=$1
result1Postfix=$2
result2Dir=$3
result2Postfix=$4
saveDir=$5

if [ -d $result1Dir ] & [ -d $result2Dir ] & [ "$#" = "5" ]; then

    mkdir -p $saveDir

    for j in $(ls $result1Dir); do 
        cp $result1Dir/$j $saveDir/$(echo $j | awk '{split($0,a,"-"); print a[1] "-" a[2] "'_$result1Postfix'" "-" a[3]}'); 
    done

    for j in $(ls $result2Dir); do 
        cp $result2Dir/$j $saveDir/$(echo $j | awk '{split($0,a,"-"); print a[1] "-" a[2] "'_$result2Postfix'" "-" a[3]}'); 
    done

    python PerformanceTracker.py $saveDir 5 $saveDir singleFolder

else
    echo "USAGE:"
	echo "run <result1 directory> <result1 postfix> <result2 directory> <result2 postfix> <save directory>"
fi
