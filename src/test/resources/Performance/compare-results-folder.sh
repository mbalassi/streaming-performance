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

    for i in $(ls $result1Dir); do 
        for j in $(ls $result1Dir/$i/); do
            mkdir $saveDir/$i;
            cp $result1Dir/$i/$j $saveDir/$i/$(echo $j | awk '{split($0,a,"-"); print a[1] "-" a[2] "'_$result1Postfix'" "-" a[3]}'); 
        done;
    done

    for i in $(ls $result2Dir); do 
        for j in $(ls $result2Dir/$i/); do
            mkdir $saveDir/$i;
            cp $result2Dir/$i/$j $saveDir/$i/$(echo $j | awk '{split($0,a,"-"); print a[1] "-" a[2] "'_$result2Postfix'" "-" a[3]}'); 
        done;
    done

    python PerformanceTracker.py $saveDir 5 $saveDir multipleFolders

else
    echo "USAGE:"
	echo "run <result1 directory> <result1 postfix> <result2 directory> <result2 postfix> <save directory>"
fi
