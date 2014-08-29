#!/bin/bash
thisDir=$(dirname $0)
thisDir=$(readlink -f "$thisDir")
configFile=$1

cp $configFile config
