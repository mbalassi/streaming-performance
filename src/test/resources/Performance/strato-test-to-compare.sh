#!/bin/bash
thisDir=$(dirname $0)
thisDir=$(readlink -f "$thisDir")

deploy=$1
saveDir=$2
stratoDir=$3
jarPath=$4
classPath=$5
classArgs=$6
length=$7
resource=$8

if [ $deploy = "true" ]; then
    ${thisDir}/strato-deploy-jar-if-needed.sh $jarPath $stratoDir >>log 2>&1
fi
className="${classPath##*.}"
mkdir -p $saveDir/results/$className >>log 2>&1

jarFileName="${jarPath##*/}"

${thisDir}/mkdir-rename-if-exists.sh $saveDir/results/$className/$classArgs >>log 2>&1
${thisDir}/strato-run-test.sh $saveDir/results/$className $classArgs $length $stratoDir $jarFileName $classPath $resource >>log 2>&1

echo $saveDir/results/$className/$classArgs
echo $className":"$classArgs
