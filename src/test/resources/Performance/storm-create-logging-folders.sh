#!/bin/bash
thisDir=$(dirname $0)
thisDir=$(readlink -f "$thisDir")

source $thisDir/load-storm-config.sh

stormDir=$1

if [ $# = "1" ]; then
    ssh -n $stormUser@$stormMaster '
        mkdir -p '$stormDir'/logs/counter;
        mkdir -p '$stormDir'/logs/all_tests/counter; 
        for slave in '$stormSlaves'; do
            ssh $slave "mkdir -p '$stormDir'/logs/counter"
        done
        '
else
    echo "USAGE:"
	echo "run <storm dir>"
fi
