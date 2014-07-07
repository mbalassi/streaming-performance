/***********************************************************************************************************************
 *
 * Copyright (C) 2010-2014 by the Stratosphere project (http://stratosphere.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 **********************************************************************************************************************/

package eu.stratosphere.streaming.performance;

import eu.stratosphere.api.java.tuple.Tuple2;
import eu.stratosphere.streaming.api.DataStream;
import eu.stratosphere.streaming.api.StreamExecutionEnvironment;

public class WordCountPerformanceLocal {

	public static void main(String[] args) {

		if (args.length < 6) {
			System.out.println("Arguments");
			return;
		}
		
		int defaultBatchSize = Integer.valueOf(args[0]);
		int clusterSize = Integer.valueOf(args[1]);
		int sourceSize = Integer.valueOf(args[2]);
		int splitterSize = Integer.valueOf(args[3]);
		int counterSize = Integer.valueOf(args[4]);
		int sinkSize = Integer.valueOf(args[5]);
		
		StreamExecutionEnvironment env = new StreamExecutionEnvironment(defaultBatchSize, 1).setClusterSize(clusterSize);

		@SuppressWarnings("unused")
		DataStream<Tuple2<String, Integer>> dataStream = env
				.readTextStream("/home/strato/stratosphere-distrib/resources/hamlet.txt", sourceSize)
				.flatMap(new WordCountPerformanceSplitter(), splitterSize).partitionBy(0)
				.map(new WordCountPerformanceCounter(), counterSize).addSink(new WordCountPerformanceSink(args), sinkSize);
		env.executeCluster();
	}
}
