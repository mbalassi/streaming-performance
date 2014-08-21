#!/bin/bash
thisDir=$(dirname $0)
thisDir=$(readlink -f "$thisDir")

source $thisDir/load-storm-config.sh

stormDir=$1
echo REMOVING:
ssh $stormUser@$stormMaster '
for slave in '$stormSlaves';
do
	echo -n $slave,
   	$(ssh $slave "rm '$stormDir'/logs/counter/*");
done

echo '$stormMaster'
rm '$stormDir'/logs/counter/*
rm '$stormDir'/logs/all_tests/counter/*
'
