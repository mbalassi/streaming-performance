The job is a streaming wordcount with 4 nodes, running for 3 minutes. The topology looks like this:

source --shuffle--> splitter --partition--> counter --shuffle--> sink

The sink contains a logger that logs the words recieved every second, and outputs the data into a csv file every 30 seconds.

In both cases the following settings were used for parallelism:

source: 2
splitter: 8
counter: 16
sink: 16

The number of workers were 24, running on a cluster of 43 machines each with 2 CPUs at 3GHz and 4GB of RAM.
