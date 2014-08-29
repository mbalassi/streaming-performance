#!/bin/bash
thisDir=$(dirname $0)
thisDir=$(readlink -f "$thisDir")

source $thisDir/load-spark-config.sh

filePath=$1
sparkDir=$2
if [ "$#" = "2" ]; then
    
    fileName="${filePath##*/}"
    scp $filePath $sparkUser@$sparkMaster:$sparkDir/lib/$fileName
else
	echo "USAGE:"
	echo "run <file> <spark directory>"
fi
