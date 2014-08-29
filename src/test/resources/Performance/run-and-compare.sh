#!/bin/bash
thisDir=$(dirname $0)
thisDir=$(readlink -f "$thisDir")

stratoDir=$1
stormDir=$2
sparkDir=$3
hadoopDir=$4
configFile=$5
jarPath=$6
saveDir=$7
compareMode=$8

classesArray=("$@")

noStratoClass="true"
noStormClass="true"
noSparkClass="true"
if [ "$#" -gt 9 ]; then
    ${thisDir}/switch-config.sh $configFile
    mkdir -p $saveDir
    compareArgsArray=()
    comparedTests=""
    for i in ${!classesArray[*]}; do
        arg=${classesArray[$i]}
        if [ $arg = "-f" ]; then
            nextArgSeparatedByColon=${classesArray[$i+1]}
            nextArgSeparatedBySpaces=$(echo $nextArgSeparatedByColon | tr : ' ')
            if [ $noStratoClass = "true" ]; then
                noStratoClass="false"
                deploy="true"
            else
                deploy="false"
            fi
            echo "Running flink test: "$nextArgSeparatedBySpaces
            out=$(${thisDir}/strato-test-to-compare.sh $deploy $saveDir $stratoDir $jarPath $nextArgSeparatedBySpaces)
            
            outArray=($out)
            csvDir=${outArray[0]}
            testName=${outArray[1]}

            compareArgsArray+=($csvDir)
            compareArgsArray+=($testName)
            comparedTests=$comparedTests' '$testName
        fi
        if [ $arg = "-s" ]; then
            nextArgSeparatedByColon=${classesArray[$i+1]}
            nextArgSeparatedBySpaces=$(echo $nextArgSeparatedByColon | tr : ' ')
            if [ $noStormClass = "true" ]; then
                noStormClass="false"
                deploy="true"
            else
                deploy="false"
            fi

            echo "Running storm test: "$nextArgSeparatedBySpaces
            out=$(${thisDir}/storm-test-to-compare.sh $deploy $saveDir $stormDir $jarPath $nextArgSeparatedBySpaces)

            outArray=($out)
            csvDir=${outArray[0]}
            testName=${outArray[1]}

            compareArgsArray+=($csvDir)
            compareArgsArray+=($testName)
            comparedTests=$comparedTests' '$testName
        fi
        if [ $arg = "-p" ]; then
            nextArgSeparatedByColon=${classesArray[$i+1]}
            nextArgSeparatedBySpaces=$(echo $nextArgSeparatedByColon | tr : ' ')
            if [ $noSparkClass = "true" ]; then
                noSparkClass="false"
                deploy="true"
            else
                deploy="false"
            fi

            echo "Running spark test: "$nextArgSeparatedBySpaces
            out=$(${thisDir}/spark-test-to-compare.sh $deploy $saveDir $sparkDir $hadoopDir $jarPath $nextArgSeparatedBySpaces)

            outArray=($out)
            csvDir=${outArray[0]}
            testName=${outArray[1]}

            compareArgsArray+=($csvDir)
            compareArgsArray+=($testName)
            comparedTests=$comparedTests' '$testName
        fi
        if [ $arg = "-d" ]; then
            resultPath=${classesArray[$i + 1]}
            if [ -d $resultPath ]; then
                compareArgsArray+=($resultPath)

                parentDir="$(dirname "$resultPath")"
                resultName="${parentDir##*/}"
                if [ $# -gt $(($i + 2)) ]; then
                    nextArg=${classesArray[$i + 2]}
                    if [ $nextArg = "-n" ]; then
                        resultName=${classesArray[$i + 3]}
                    fi
                fi
                compareArgsArray+=($resultName)
                comparedTests=$comparedTests' '$resultName
            fi
        fi
    done
    comparedTests="${comparedTests:1:${#comparedTests}-1}"
    compareDirName=result
    compareDir=$saveDir/comparisons/$compareDirName
    mkdir -p $compareDir
    echo $comparedTests > $compareDir/comparedTests.txt
    if [ $compareMode = "throughput" ]; then
        ${thisDir}/compare-throughput-results.sh $compareDir ${compareArgsArray[@]}
    elif [ $compareMode = "latency" ]; then
        ${thisDir}/plot-latency-results.sh $compareDir ${compareArgsArray[@]}
    fi
else
    echo "USAGE:"
	echo "run <flink dir> <storm dir> <spark dir> <hadoop dir> <config file> <jar path> <save directory> <plot mode: throughput/latency> [-f <flink class path:arguments:length:resource path>  [-f ...]] [-s <storm class path:arguments:length:resource path>  [-s ...]] [-p <spark class path:arguments:length:resource path>  [-p ...]] [-d <result path> [-n <unique name>] [-d ...]]"
fi












