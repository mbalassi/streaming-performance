#!/bin/bash

hadoopUser=$(./load-config.sh hadoop-user)
hadoopMaster=$(./load-config.sh hadoop-master)
hdfsSaveDir=$(./load-config.sh hadoop-hdfssavedir)
sparkUser=$(./load-config.sh spark-user)
sparkMaster=$(./load-config.sh spark-master)
sparkSlaves=$(./load-config.sh spark-slaves)
