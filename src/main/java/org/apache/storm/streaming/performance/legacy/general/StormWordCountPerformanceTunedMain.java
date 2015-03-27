/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.storm.streaming.performance.legacy.general;

import java.io.File;
import java.io.FileNotFoundException;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;

public class StormWordCountPerformanceTunedMain {
	
	public static void main(String[] args) throws Exception {

		if (args != null && args.length == 9) {
			try {
				boolean runOnCluster = args[0].equals("cluster");
				String fileName = args[1];
				String counterPath = args[2];
				String topologyName = args[3];
				
				if (!(new File(fileName)).exists()) {
					throw new FileNotFoundException();
				}

				int numberOfWorkers = Integer.parseInt(args[4]);
				int spoutParallelism = Integer.parseInt(args[5]);
				int splitterParallelism = Integer.parseInt(args[6]);
				int counterParallelism = Integer.parseInt(args[7]);
				int sinkParallelism = Integer.parseInt(args[8]);

				TopologyBuilder builder = new TopologyBuilder();
				builder.setSpout("spout", new StreamingTextSpout(fileName), spoutParallelism);
				builder.setBolt("split", new WordCountSplitterBolt(), splitterParallelism).shuffleGrouping("spout");
				builder.setBolt("count", new WordCountCounterBolt(), counterParallelism).fieldsGrouping(
						"split", new Fields("word"));
				builder.setBolt("sink", new LoggerSinkBolt(args, counterPath), sinkParallelism).shuffleGrouping("count");

				Config conf = new Config();
				conf.setNumAckers(0);
				conf.setDebug(false);
				conf.setNumWorkers(numberOfWorkers);
				conf.put(Config.TOPOLOGY_RECEIVER_BUFFER_SIZE,             8);
				conf.put(Config.TOPOLOGY_TRANSFER_BUFFER_SIZE,            32);
				conf.put(Config.TOPOLOGY_EXECUTOR_RECEIVE_BUFFER_SIZE, 16384);
				conf.put(Config.TOPOLOGY_EXECUTOR_SEND_BUFFER_SIZE,    16384);

				if (runOnCluster) {
					StormSubmitter.submitTopology(topologyName, conf, builder.createTopology());
				} else {
					// running locally for 70 seconds

					conf.setMaxTaskParallelism(3);
					LocalCluster cluster = new LocalCluster();
					cluster.submitTopology("word-count-performance", conf, builder.createTopology());
					Thread.sleep(70000);

					cluster.shutdown();
				}

			} catch (NumberFormatException e) {
				printUsage();
			} catch (FileNotFoundException e) {
				printUsage();
			}
		} else {
			printUsage();
		}
	}

	private static void printUsage() {
		System.out
				.println("USAGE:\n run <local/cluster> <performance counter path> <source file> <topology name> <number of workers> <spout parallelism> <splitter parallelism> <counter parallelism> <sink parallelism>");
	}
}