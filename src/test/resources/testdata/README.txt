Every file here has the following naming convention:

(prefix)-#1_#2_#3_#4_#5_#6-(postfix)
#1 defaultBatchSize (where applicable)
#2 execultionParallelism
#3 source parallelism
#4 splitter parallelism
#5 counter parallelism
#6 sink parallelism

The postfix is a random number for csv files, C or D for image files.

The job was a wordcount with 4 nodes, source -> splitter -> counter -> sink, running for 5 minutes.
