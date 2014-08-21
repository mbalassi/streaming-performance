#!/bin/bash
thisDir=$(dirname $0)
thisDir=$(readlink -f "$thisDir")

source $thisDir/load-flink-config.sh

commands=$1

ssh $stratoUser@$stratoMaster '
	for slave in '$stratoSlaves';
	do
		echo $slave
        ssh $slave ' $commands '
    done
'
