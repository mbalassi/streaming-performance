#!/bin/bash
thisDir=$(dirname $0)
thisDir=$(readlink -f "$thisDir")

source $thisDir/load-spark-config.sh

hadoopDir=$1
hdfsDir=$2

if [ $# -gt 1 ]; then
    echo REMOVING:
    ssh $hadoopUser@$hadoopMaster ''$hadoopDir'/bin/hdfs dfs -rm -r '$hdfsDir'/*.csv'
else
	echo "USAGE:"
	echo "run <hadoop directory> <hdfs directory>"
fi
