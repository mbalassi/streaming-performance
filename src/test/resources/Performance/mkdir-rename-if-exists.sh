#!/bin/bash
thisDir=$(dirname $0)
thisDir=$(readlink -f "$thisDir")
dirPath=$1

if [ "$#" == "1" ]; then
    if [ -d $dirPath/$filePath ]; then
        ${thisDir}/append-date.sh $thisDir/$dirPath
    fi

    mkdir $thisDir/$dirPath
else
    echo "USAGE:"
	echo "run <dir path>"
fi
