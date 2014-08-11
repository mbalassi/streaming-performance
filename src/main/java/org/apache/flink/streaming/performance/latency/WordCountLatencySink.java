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

package org.apache.flink.streaming.performance.latency;

import java.io.File;
import java.util.Random;

import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.function.sink.RichSinkFunction;
import org.apache.flink.streaming.util.LatencyTester;

public class WordCountLatencySink extends RichSinkFunction<Tuple3<String, Integer, Long>>  {
	private static final long serialVersionUID = 1L;
	
	private String argString;
	private String csvPath;
	
	private LatencyTester latencyTester;
	
	private int intervalLength;
	
	public WordCountLatencySink(String[] args, String csvPath_, int intervalLength_){
		csvPath = csvPath_;
		argString = args[4];
		for(int i = 5; i < args.length; i++){
			argString += "_" + args[i];
		}
		intervalLength = intervalLength_;
	}
	
	@Override
	public void open(Configuration parameters) throws Exception {
		String fileName = getFileName();
		latencyTester = new LatencyTester(intervalLength, fileName);
	}

	private String getFileName() {
		Random rnd = new Random();
		String fileName;
		File histFile;
		do {
			fileName = csvPath + "histogramPart-" + argString + 
					"-" + String.valueOf(rnd.nextInt(10000000)) + ".csv";
			histFile = new File(fileName);
		} while(histFile.exists());
		return fileName;
	}
	
	@Override
	public void invoke(Tuple3<String, Integer, Long> inValue) {
		long arrivalTime = System.nanoTime();
		latencyTester.add(inValue.f2, arrivalTime);
	}
}
