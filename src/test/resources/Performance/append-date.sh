#!/bin/bash
thisDir=$(dirname $0)
thisDir=$(readlink -f "$thisDir")
filePath=$1

if [ "$#" == "1" ]; then
    postfix=$(ls -al --time-style=+"%Y-%m-%d-%H:%M:%S" $filePath | head -n 2 | tail -n 1 | awk '{print $6}')
    mv $filePath ${filePath}-${postfix}
else
    echo "USAGE:"
	echo "run <file/dir path>"
fi
