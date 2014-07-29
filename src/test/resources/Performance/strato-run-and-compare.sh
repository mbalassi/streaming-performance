#!/bin/bash
thisDir=$(dirname $0)
thisDir=$(readlink -f "$thisDir")

stratoDir='flink-0.6-incubating-SNAPSHOT'
jarFile='streaming-performance-0.1-SNAPSHOT.jar'
#classPath  ='org.apache.flink.streaming.performance.WordCountPerformanceLocal'

jarPath=$1
saveDir=$2
args=$3
length=$4
classesArray=("$@")

noclass="true"
if [ "$#" -gt 5 ]; then
    mkdir -p $saveDir
    compareArgsArray=()
    compareDirName=""
    for i in ${!classesArray[*]}; do
        arg=${classesArray[$i]}
        if [ $arg = "-c" ]; then
            if [ $noclass = "true" ]; then
                noclass="false"
                ${thisDir}/strato-deploy-one.sh $jarPath $stratoDir
            fi
            classPath=${classesArray[$i + 1]}
            className="${classPath##*.}"
            mkdir -p $saveDir/results/$className

            classArgs=$args
            if [ $# -gt $(($i + 2)) ]; then
                nextArg=${classesArray[$i + 2]}
                if [ $nextArg = "-a" ]; then
                    classArgs=${classesArray[$i + 3]}
                fi
            fi

            ${thisDir}/mkdir-rename-if-exists.sh $saveDir/results/$className/$classArgs
            ${thisDir}/strato-run-test.sh $saveDir/results/$className $classArgs $length $stratoDir $jarFile $classPath
            compareArgsArray+=($saveDir/results/$className/$classArgs)
            compareArgsArray+=($className)
            compareDirName=$compareDirName'_'$className
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
                compareDirName=$compareDirName'_'$resultName
            fi
        fi
    done
    compareDirName="${compareDirName:1:${#compareDirName}-1}"
    compareDir=$saveDir/comparisons/$compareDirName
    ${thisDir}/compare-results.sh $compareDir ${compareArgsArray[@]}
else
    echo "USAGE:"
	echo "run <jar path> <save directory> <test params separated by _> <length of test> [-c <class path> [-a <unique args>] [-c ...]] [-d <result path> [-n <unique name>] [-d ...]]"
fi












