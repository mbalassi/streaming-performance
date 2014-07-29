#!/bin/bash
thisDir=$(dirname $0)
thisDir=$(readlink -f "$thisDir")
resultDir=$1
resultPostfix=$2
saveDir=$3

for j in $(ls $resultDir); do 
    cp $resultDir/$j $saveDir/$(echo $j | awk '{split($0,a,"-"); print a[1] "-" a[2] "'_$resultPostfix'" "-" a[3]}'); 
done
