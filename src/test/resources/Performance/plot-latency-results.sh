#!/bin/bash
thisDir=$(dirname $0)
thisDir=$(readlink -f "$thisDir")
compareDir=$1
argArray=("$@")

if [ $# -gt 2 ]; then
    ${thisDir}/mkdir-rename-if-exists.sh $compareDir
    for i in ${!argArray[*]}; do
        arg=${argArray[$i]}
        if [ -d $arg ] & (($i % 2 )); then
            resultDir=${argArray[$i]}
            resultPostfix=${argArray[$i + 1]}
            python ${thisDir}/LatencyHistogram.py $resultDir $compareDir $resultPostfix True False
        fi
    done
else
    echo "USAGE:"
	echo "run <save directory> <result1 directory> <result1 postfix> <result2 directory> <result2 postfix> [<result3 directory> <result3 postfix>...]"
fi


