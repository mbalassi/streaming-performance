#!/bin/bash
thisDir=$(dirname $0)
thisDir=$(readlink -f "$thisDir")

deploy=$1
saveDir=$2
sparkDir=$3
hadoopDir=$4
jarPath=$5
classPath=$6
classArgs=$7
length=$8
resource=$9

if [ $deploy = "true" ]; then
    ${thisDir}/spark-deploy-jar.sh $jarPath $sparkDir >>log 2>&1
fi
className="${classPath##*.}"
mkdir -p $saveDir/results/$className >>log 2>&1

jarFileName="${jarPath##*/}"

${thisDir}/mkdir-rename-if-exists.sh $saveDir/results/$className/$classArgs >>log 2>&1
${thisDir}/spark-run-test.sh $saveDir/results/$className $classArgs $length $sparkDir $hadoopDir $jarFileName $classPath $resource >>log 2>&1

echo $saveDir/results/$className/$classArgs
echo $className":"$classArgs
