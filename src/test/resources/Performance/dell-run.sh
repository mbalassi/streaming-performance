#!/bin/bash
commands=$1

ssh strato@dell150.ilab.sztaki.hu '
	for j in {101..125} {127..142} 144 145;
	do
		echo $j
        ssh dell$j ' $commands '
    done
'
