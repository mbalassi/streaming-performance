#!/bin/bash
thisDir=$(dirname $0)
thisDir=$(readlink -f "$thisDir")

source $thisDir/load-flink-config.sh

stratoDir=$1

if [ $# = "1" ]; then
    ssh -n $stratoUser@$stratoMaster '
        mkdir -p '$stratoDir'/log/counter;
        mkdir -p '$stratoDir'/log/all_tests/counter; 
        for slave in '$stratoSlaves'; do
            ssh $slave "mkdir -p '$stratoDir'/log/counter"
        done
        '
else
    echo "USAGE:"
	echo "run <strato dir>"
fi
