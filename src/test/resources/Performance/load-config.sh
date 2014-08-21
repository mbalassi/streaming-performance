#!/bin/bash
thisDir=$(dirname $0)
thisDir=$(readlink -f "$thisDir")

if [ $# = 1 ]; then
    arg=$1
    if [ $arg = "storm-user" ]; then
        sed -n '1{p;q}' ${thisDir}/config
    elif [ $arg = "storm-master" ]; then
        sed -n '2{p;q}' ${thisDir}/config
    elif [ $arg = "storm-slaves" ]; then
        sed -n '3{p;q}' ${thisDir}/config
    elif [ $arg = "strato-user" ]; then
        sed -n '4{p;q}' ${thisDir}/config
    elif [ $arg = "strato-master" ]; then
        sed -n '5{p;q}' ${thisDir}/config
    elif [ $arg = "strato-slaves" ]; then
        sed -n '6{p;q}' ${thisDir}/config
    fi
fi
