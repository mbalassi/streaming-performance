#!/bin/bash
stratoDir=$1
echo REMOVING:
ssh strato@dell150.ilab.sztaki.hu '
for j in {101..125} {127..142} 144 145;
do
	echo -n $j,
   	$(ssh dell$j "rm '$stratoDir'/log/counter/*");
done

echo 150
rm '$stratoDir'/log/counter/*
rm '$stratoDir'/log/all_tests/counter/*
'
