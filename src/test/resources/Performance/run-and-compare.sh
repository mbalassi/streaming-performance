#!/bin/bash
thisDir=$(dirname $0)
thisDir=$(readlink -f "$thisDir")

stratoDir=$1
stormDir=$2
jarPath=$3
saveDir=$4

classesArray=("$@")

noStratoClass="true"
noStormClass="true"
if [ "$#" -gt 5 ]; then
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
    echo $comparedTests>$compareDir/comparedTests.txt

    ${thisDir}/compare-results.sh $compareDir ${compareArgsArray[@]}
else
    echo "USAGE:"
	echo "run <flink dir> <storm dir> <jar path> <save directory> [-f <flink class path:arguments:length:resource path>  [-f ...]] [-s <storm class path:arguments:length:resource path>  [-s ...]] [-d <result path> [-n <unique name>] [-d ...]]"
fi












