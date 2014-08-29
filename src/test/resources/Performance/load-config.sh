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
    elif [ $arg = "strato-host" ]; then
        sed -n '7{p;q}' ${thisDir}/config
    elif [ $arg = "strato-master-rcp-port" ]; then
        sed -n '8{p;q}' ${thisDir}/config
    elif [ $arg = "hadoop-user" ]; then
        sed -n '9{p;q}' ${thisDir}/config
    elif [ $arg = "hadoop-master" ]; then
        sed -n '10{p;q}' ${thisDir}/config
    elif [ $arg = "hadoop-hdfssavedir" ]; then
        sed -n '11{p;q}' ${thisDir}/config
    elif [ $arg = "spark-user" ]; then
        sed -n '12{p;q}' ${thisDir}/config
    elif [ $arg = "spark-master" ]; then
        sed -n '13{p;q}' ${thisDir}/config
    elif [ $arg = "spark-slaves" ]; then
        sed -n '14{p;q}' ${thisDir}/config
    fi
fi
