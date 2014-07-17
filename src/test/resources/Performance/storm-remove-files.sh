#!/bin/bash
stormDir=$1
echo REMOVING:
ssh storm@dell150.ilab.sztaki.hu '
for j in {101..125} {127..142} 144 145;
do
	echo -n $j,
   	$(ssh dell$j "rm '$stormDir'/logs/counter/*");
done

echo 150
rm '$stormDir'/logs/counter/*
rm '$stormDir'/logs/all_tests/counter/*
'
