#!/bin/bash
thisDir=$(dirname $0)
thisDir=$(readlink -f "$thisDir")

source $thisDir/load-flink-config.sh

stratoDir=$1
echo REMOVING:
ssh $stratoUser@$stratoMaster '
for slave in '$stratoSlaves';
do
	echo -n $slave,
   	$(ssh $slave "rm '$stratoDir'/log/counter/*");
done

echo '$stratoMaster'
rm '$stratoDir'/log/counter/*
rm '$stratoDir'/log/all_tests/counter/*
'
