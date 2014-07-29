#!/bin/bash
thisDir=$(dirname $0)
thisDir=$(readlink -f "$thisDir")
compareDir=$1
argArray=("$@")

if [ $# -gt 4 ]; then
    mkdir -p $compareDir
    for i in ${!argArray[*]}; do
        arg=${argArray[$i]}
        if [ -d $arg ] & (($i % 2 )); then
            resultDir=${argArray[$i]}
            resultPostfix=${argArray[$i + 1]}
            ${thisDir}/copy-rename-results.sh $resultDir $resultPostfix $compareDir
        fi
    done
    python ${thisDir}/PerformanceTracker.py $compareDir 5 $compareDir singleFolder
else
    echo "USAGE:"
	echo "run <save directory> <result1 directory> <result1 postfix> <result2 directory> <result2 postfix> [<result3 directory> <result3 postfix>...]"
fi


