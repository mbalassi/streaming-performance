#!/bin/bash
thisDir=$(dirname $0)
thisDir=$(readlink -f "$thisDir")

source $thisDir/load-spark-config.sh

toDir=$1
hadoopDir=$2
hdfsDir=$3
testParams=$4
echo COPYING:
if [ -d "${toDir}" ] ; then
	tempDir=$(ssh $hadoopUser@$hadoopMaster 'mktemp -d')
    output=$(ssh $hadoopUser@$hadoopMaster ''$hadoopDir'/bin/hdfs dfs -ls '$hdfsDir'/*.csv')
    for out in $output; do
        file=$(echo $out | grep .csv)
        if [ ! -z $file ]; then
            fileName="${file##*/}"
            newFileName=$(echo $fileName | awk '{split($0,a,"-"); print a[1]"-""'$testParams'""-"a[3]}')
            ssh $hadoopUser@$hadoopMaster ''$hadoopDir'/bin/hdfs dfs -getmerge '$file' '$tempDir'/'$newFileName''
        fi
    done
	scp $hadoopUser@$hadoopMaster:$tempDir/* $toDir
    ssh $hadoopUser@$hadoopMaster 'rm -rf '$tempDir''
else
	echo "USAGE:"
	echo "run <directory> <hadoop directory> <hdfs directory> <test parameters>"
fi
