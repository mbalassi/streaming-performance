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

package org.apache.flink.streaming.performance.iterative;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.DataStream;
import org.apache.flink.streaming.api.IterativeDataStream;
import org.apache.flink.streaming.api.SplitDataStream;
import org.apache.flink.streaming.api.StreamExecutionEnvironment;
import org.apache.flink.streaming.performance.PerformanceCounterSink;

public class PagerankIterative {

	public static void main(String[] args) {

		if (args != null && args.length == 7) {
			try {
				//System.setOut(new PrintStream("/home/tofi/git/streaming-performance/src/test/resources/Performance/graphgenerator/out"));
				//System.setErr(new PrintStream("/home/tofi/git/streaming-performance/src/test/resources/Performance/graphgenerator/out"));
				
				boolean runOnCluster = args[0].equals("cluster");
				String edgeSourcePath = args[1];
				String csvPath = args[2];
				String jarPath = args[3];
				
				if (!(new File(edgeSourcePath)).exists()) {
					throw new FileNotFoundException();
				}
		
				int clusterSize = Integer.valueOf(args[4]);
				int crawlerSize = Integer.valueOf(args[5]);
				int startingEdgeNum = Integer.valueOf(args[6]);
		
				StreamExecutionEnvironment env;
				if (runOnCluster) {
					env = StreamExecutionEnvironment.createRemoteEnvironment(
							"10.1.3.150", 6123, clusterSize, 
							jarPath);
				} else {
					env = StreamExecutionEnvironment.createLocalEnvironment(clusterSize);
				}
				
				IterativeDataStream<Tuple3<Integer, Integer, Integer>> iterateBegin = env
						.addSource(new EdgeSource(edgeSourcePath)).broadcast()
						.map(new DummyForwarderMap()).setParallelism(crawlerSize).forward().iterate();
				
				DataStream<Tuple3<Integer, Integer, Integer>> iterateEnd = iterateBegin
						.flatMap(new RandomCrawler(startingEdgeNum, 1, 10000)).setParallelism(crawlerSize).distribute()
						.map(new DummyForwarderMap()).setParallelism(crawlerSize).distribute();
				
				SplitDataStream<Tuple3<Integer, Integer, Integer>> splitDS = 
						iterateBegin.closeWith(iterateEnd).split(new PageRankOutputSelector());
				
				splitDS.select("sink").addSink(
						new PerformanceCounterSink<Tuple3<Integer, Integer, Integer>>(args, csvPath){
							private static final long serialVersionUID = 1L;});
				
				env.setExecutionParallelism(clusterSize);
				env.execute();
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
				.println("USAGE:\n run <local/cluster> <source path> <csv path> <jar path> <number of workers> <spout parallelism> <splitter parallelism> <counter parallelism> <sink parallelism>");
	}
}
