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

package org.apache.flink.streaming.performance.latency.latencytest;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.flink.api.java.tuple.Tuple1;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class LatencyTestMain {

	public static void main(String[] args) {

		if (args != null && args.length == 11) {
			try {
				boolean runOnCluster = args[0].equals("cluster");
				String sourcePath = args[1];
				String csvPath = args[2];
				String jarPath = args[3];
				String host = args[4];
				int port = Integer.valueOf(args[5]);
				
				if (!(new File(sourcePath)).exists()) {
					throw new FileNotFoundException();
				}
		
				int clusterSize = Integer.valueOf(args[6]);
				int sourceSize = Integer.valueOf(args[7]);
				int sinkSize = Integer.valueOf(args[8]);
				int bufferTimeOut = Integer.valueOf(args[9]);
				int intervalLength = Integer.valueOf(args[10]);
		
				StreamExecutionEnvironment env;
				if (runOnCluster) {
					env = StreamExecutionEnvironment.createRemoteEnvironment(
							host, port, clusterSize, // TODO ezt kiemelni parameternek
							jarPath);
				} else {
					env = StreamExecutionEnvironment.createLocalEnvironment(clusterSize);
				}
				
				if(bufferTimeOut != 0) {
					//env.setBufferTimeout(bufferTimeOut);
				}
				
				@SuppressWarnings("unused")
				DataStream<Tuple1<Long>> dataStream = env
						.addSource(new LatencyTestSource(), sourceSize).shuffle()
						.addSink(new LatencyTestSink(args, csvPath, intervalLength))
							.setParallelism(sinkSize);
				
				env.setExecutionParallelism(clusterSize);
				try {
					env.execute();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
		// local /home/tofi/git/streaming-performance/src/test/resources/testdata/hamlet.txt /home/tofi/git/streaming-performance/src/test/resources/testdata/ none 1 1 1 1 1 0 10
		System.out.println("USAGE:\n run <local/cluster> <source path> <csv path> <jar path>"
				+ " <number of workers> <spout parallelism>"
				+ " <sink parallelism> <buffertimeout in milliseconds>"
				+ " <interval length for the histogram>");
	}
}