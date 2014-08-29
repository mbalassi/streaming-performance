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

package org.apache.spark.streaming.performance.general;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

public final class SparkWordCountPerformanceMain {

	public static void main(String[] args) {
		if (args != null && args.length == 6) {
			boolean runOnCluster = args[0].equals("cluster");
			String fileName = args[1];
			String counterPath = args[2];
			Integer timeout = Integer.valueOf(args[3]);
			String parallelism = args[4];
			Integer sourceNum = Integer.valueOf(args[5]);
			
			Logger.getLogger("org").setLevel(Level.WARN);
			Logger.getLogger("akka").setLevel(Level.WARN);

			SparkConf sparkConf = new SparkConf().setAppName("SparkWordCountPerformanceMain");
			if (Integer.valueOf(parallelism) > 0) {
				sparkConf.set("spark.default.parallelism", parallelism);
			}
			
			JavaStreamingContext ssc = new JavaStreamingContext(sparkConf, new Duration(1000));

			JavaDStream<String> sources = ssc.receiverStream(new StreamingTextReceiver(fileName));
			for(int i = 1; i < sourceNum; i++) {
				JavaDStream<String> lines = ssc.receiverStream(new StreamingTextReceiver(fileName));
				sources = sources.union(lines);
			}
			
			JavaDStream<String> words = sources.flatMap(new SparkWordCountSplitter());
			JavaPairDStream<String, Integer> wordCounts = words.mapToPair(new SparkWordCountPairer()).reduceByKey(
					new SparkWordCountCounter());

			wordCounts.foreachRDD(new SparkPerformanceSink(runOnCluster, counterPath));
			ssc.start();
			//ssc.awaitTermination();
			try {
				Thread.sleep(timeout * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			ssc.stop();
		} else {
			printUsage();
		}
	}
	
	private static void printUsage() {
		System.out
				.println("USAGE:\n run <local/cluster> <source file> <performance counter path> <test length> "
						+ "<parallelism> <number of sources>");
	}
}
