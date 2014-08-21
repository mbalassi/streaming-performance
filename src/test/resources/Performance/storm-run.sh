#!/bin/bash
thisDir=$(dirname $0)
thisDir=$(readlink -f "$thisDir")

source $thisDir/load-storm-config.sh

commands=$1

ssh $stormUser@$stormMaster '
	for slave in '$stormSlaves';
	do
		echo $slave
        ssh $slave ' $commands '
    done
'
