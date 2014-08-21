#!/bin/bash
thisDir=$(dirname $0)
thisDir=$(readlink -f "$thisDir")

source $thisDir/load-flink-config.sh

stratoPath=$1
dirOnCluster=$2
additionalLibs=$@

if [ "$#" = "2" ]; then
    
    distributionPath=${stratoPath}/flink-dist/target/flink-dist-0.6-incubating-SNAPSHOT-bin/flink-0.6-incubating-SNAPSHOT
    coreJarPath=${stratoPath}/flink-addons/flink-streaming/flink-streaming-core/target
    connectorsJarPath=${stratoPath}/flink-addons/flink-streaming/flink-streaming-connectors/target
    coreJarName=$(ls $coreJarPath | grep .jar$ | head -n 1)
    connectorsJarName=$(ls $connectorsJarPath | grep .jar$ | head -n 1)
    coreJar=$coreJarPath/$coreJarName
    connectorsJar=$connectorsJarPath/$connectorsJarName

    ssh -n $stratoUser@$stratoMaster "$dirOnCluster/bin/stop-cluster.sh; sleep 1"

    ssh -n $stratoUser@$stratoMaster "rm -rf ~/temp/$dirOnCluster"
    ssh -n $stratoUser@$stratoMaster "mv ~/$dirOnCluster ~/temp/"
    scp -r $distributionPath $stratoUser@$stratoMaster:~/$dirOnCluster
    ssh -n $stratoUser@$stratoMaster "cp ~/temp/$dirOnCluster/conf/* ~/$dirOnCluster/conf/"
    scp $coreJar $stratoUser@$stratoMaster:~/$dirOnCluster/lib/
    scp $connectorsJar $stratoUser@$stratoMaster:~/$dirOnCluster/lib/

    ssh -n $stratoUser@$stratoMaster "mkdir ~/$dirOnCluster/log/counter"

    ssh -n $stratoUser@$stratoMaster '
	for slave in '$stratoSlaves';
	do
		echo -n $slave,
        ssh -n $slave "rm -rf ~/'$dirOnCluster'" 
		scp -r ~/'$dirOnCluster' $slave:~/;
	done
	'
    ssh -n $stratoUser@$stratoMaster "mkdir ~/$dirOnCluster/log/all_tests"
    ssh -n $stratoUser@$stratoMaster "mkdir ~/$dirOnCluster/log/all_tests/counter"
        
    ssh -n $stratoUser@$stratoMaster "$dirOnCluster/bin/start-cluster.sh"

else
	echo "USAGE:"
	echo "run <jar file> <strato directory>"
fi
