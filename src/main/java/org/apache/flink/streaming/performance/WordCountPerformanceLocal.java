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

package org.apache.flink.streaming.performance;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.DataStream;
import org.apache.flink.streaming.api.StreamExecutionEnvironment;

public class WordCountPerformanceLocal {

	public static void main(String[] args) {

		if (args.length < 7) {
			System.out.println("Arguments");
			return;
		}
		
		int defaultBatchSize = Integer.valueOf(args[0]);
		int clusterSize = Integer.valueOf(args[1]);
		int sourceSize = Integer.valueOf(args[2]);
		int splitterSize = Integer.valueOf(args[3]);
		int counterSize = Integer.valueOf(args[4]);
		int sinkSize = Integer.valueOf(args[5]);
		String stratoDir = args[6];

		StreamExecutionEnvironment env = StreamExecutionEnvironment.createRemoteEnvironment(
				"10.1.3.150", 6123, clusterSize, 
				"/home/strato/" + stratoDir + "/lib/streaming-performance-0.1-SNAPSHOT.jar");
				//"/home/tofi/git/streaming-performance/target/streaming-performance-0.1-SNAPSHOT.jar");
		
		//StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment(clusterSize);
		//env.setDefaultBatchSize(defaultBatchSize);
		
		@SuppressWarnings("unused")
		DataStream<Tuple2<String, Integer>> dataStream = env
				.readTextStream("/home/strato/" + stratoDir + "/resources/hamlet.txt", sourceSize)
				//.readTextStream("/home/tofi/git/streaming-performance/src/test/resources/testdata/hamlet.txt", sourceSize)
				.flatMap(new WordCountPerformanceSplitter()).setParallelism(splitterSize)
				.partitionBy(0)
				.map(new WordCountPerformanceCounter()).setParallelism(counterSize)
				.addSink(new WordCountPerformanceSink(args, stratoDir)).setParallelism(sinkSize);
		
		env.setExecutionParallelism(clusterSize);
		env.execute();
	}
}
