#!/bin/bash
thisDir=$(dirname $0)
thisDir=$(readlink -f "$thisDir")

source $thisDir/load-spark-config.sh

toDir=$1
testParams=$2
length=$3
sparkDir=$4
hadoopDir=$5
jarFile=$6
classPath=$7
resourceFile=$8

if [ -d "${toDir}" ] & [ $# -gt 7 ] ; then
	echo "removing files"
	./spark-remove-files.sh $hadoopDir $hdfsSaveDir

	numOfExecutors=$(echo $testParams | awk '{split($0,a,"_"); print a[1]}')
    executorCores=$(echo $testParams | awk '{split($0,a,"_"); print a[2]}')
    driverMemoryInGB=$(echo $testParams | awk '{split($0,a,"_"); print a[3]}')
    executorMemoryInGB=$(echo $testParams | awk '{split($0,a,"_"); print a[4]}')

    restArgs=$(echo $testParams | awk 'BEGIN{FS="_"}{for (i=5; i<=NF; i++) {printf "%s ", $i}}')

	rm -r $toDir/$testParams/*;
	mkdir $toDir/$testParams;

    ${thisDir}/spark-deploy-resource-if-needed.sh $resourceFile

    resourceFileName="${resourceFile##*/}"
    sparkHomeFullPath=$(ssh -n $sparkUser@$sparkMaster "pwd ~")
    resourcePathOnCluster=$sparkHomeFullPath/resources/$resourceFileName

    className="${classPath##*.}"
    
	ssh -n $sparkUser@$sparkMaster "export HADOOP_CONF_DIR=/home/hadoop2/hadoop-dist/etc/hadoop/ && ${sparkHomeFullPath}/${sparkDir}/bin/spark-submit --class ${classPath} --master yarn-cluster --num-executors ${numOfExecutors} --driver-memory ${driverMemoryInGB}g --executor-memory ${executorMemoryInGB}g --executor-cores ${executorCores} ${sparkHomeFullPath}/${sparkDir}/lib/${jarFile} cluster ${resourcePathOnCluster} ${hdfsSaveDir}/ ${length} ${restArgs}"

	echo "job finished"

	echo "copying"
	./spark-copy-files.sh $toDir/$testParams $hadoopDir $hdfsSaveDir $testParams
else
	echo "USAGE:"
	echo "run <save directory> <test params separated by _> <length of test in seconds> <spark directory> <hadoop directory> <jar file to run> <class to run (whole path)> <resource file path>"
fi
