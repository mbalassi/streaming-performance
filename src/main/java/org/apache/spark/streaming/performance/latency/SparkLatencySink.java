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

package org.apache.spark.streaming.performance.latency;

import java.util.Random;

import org.apache.flink.streaming.util.LatencyTester;
import org.apache.flink.streaming.util.PerformanceCounter;
import org.apache.flink.streaming.util.PerformanceCounterHDFS;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.Function;

import scala.Tuple2;

public class SparkLatencySink implements Function<JavaPairRDD<String, Tuple2<Long, Integer>>, Void> {
	private static final long serialVersionUID = 1L;
	
	private PerformanceCounter performanceCounter;
	private String csvPath;
	private Integer intervalLength;
	
	private LatencyTester latencyTester;
	
	public SparkLatencySink(boolean runOnCluster, String csvPath_, Integer intervalLength_){
		csvPath = csvPath_;
		intervalLength = intervalLength_;
		Random rnd = new Random();
		String filePath = csvPath + "sparkLatencySink-" + rnd.nextInt() + ".csv";
//		if(runOnCluster) {
//			this.performanceCounter = new PerformanceCounterHDFS("pc", 1000, 1000, 30000, filePath);
//		} else {
//			this.performanceCounter = new PerformanceCounter("pc", 1000, 1000, 30000, filePath);
//		}
		latencyTester = new LatencyTester(intervalLength, filePath);
	}
	
	@Override
	public Void call(JavaPairRDD<String, Tuple2<Long, Integer>> v1) throws Exception {
		for(Tuple2<String, Tuple2<Long, Integer>> tup : v1.collect()) {
			//performanceCounter.count(tup._2._2);
			long arrivalTime = System.nanoTime();
			latencyTester.add(tup._2._1, arrivalTime);
		}
		return null;
	}
}
