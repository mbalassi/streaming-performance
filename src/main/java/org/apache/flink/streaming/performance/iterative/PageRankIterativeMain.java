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
import org.apache.flink.streaming.api.datastream.IterativeDataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.SplitDataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class PageRankIterativeMain {

	public static void main(String[] args) {

		if (args != null && args.length == 8) {
			try {
				//System.setOut(new PrintStream("/home/tofi/git/streaming-performance/src/test/resources/Performance/graphgenerator/out"));
				//System.setErr(new PrintStream("/home/tofi/git/streaming-performance/src/test/resources/Performance/graphgenerator/out"));
				
				boolean runOnCluster = args[0].equals("cluster");
				String edgeSourcePath = args[1];
				String logPath = args[2];
				String jarPath = args[3];
				String host = args[4];
				int port = Integer.valueOf(args[5]);
				
				if (!(new File(edgeSourcePath)).exists()) {
					throw new FileNotFoundException();
				}
		
				int clusterSize = Integer.valueOf(args[6]);
				int crawlerSize = Integer.valueOf(args[7]);
				int edgeAddRemoveSleep = 100;
		
				StreamExecutionEnvironment env;
				if (runOnCluster) {
					env = StreamExecutionEnvironment.createRemoteEnvironment(
							host, port, clusterSize, jarPath);
				} else {
					env = StreamExecutionEnvironment.createLocalEnvironment(clusterSize);
				}
				
				env.setBufferTimeout(100);
				
				IterativeDataStream<Tuple3<Integer, Integer, Integer>> iterateBegin = env
						.addSource(new EdgeSource(edgeSourcePath, crawlerSize, edgeAddRemoveSleep))
							.setParallelism(1).groupBy(0).iterate();

				SingleOutputStreamOperator<Tuple3<Integer, Integer, Integer>, ?> iterateEnd = iterateBegin
						.flatMap(new RandomCrawler(1000000, 10000)).setParallelism(crawlerSize).distribute();
				
				SplitDataStream<Tuple3<Integer, Integer, Integer>> splitDS = 
						iterateEnd.split(new PageRankOutputSelector());
				
				iterateBegin.closeWith(splitDS.select("iterate"));
				
				splitDS.select("sink").addSink(
						//new PerformanceCounterSink<Tuple3<Integer, Integer, Integer>>(args, csvPath){
						//	private static final long serialVersionUID = 1L;}).setParallelism(1);
						new PageRankSink(logPath, 30 * 1000)).setParallelism(1);
				
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
		System.out
				.println("USAGE:\n run <local/cluster> <source path> <csv path> <jar path> <number of workers> <crawler parallelism>");
	}
}
