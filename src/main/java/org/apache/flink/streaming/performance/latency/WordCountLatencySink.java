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
import org.apache.flink.streaming.util.Histogram;
import org.apache.flink.streaming.util.HistogramHelper;

public class WordCountLatencySink extends RichSinkFunction<Tuple3<String, Integer, Long>>  {
	private static final long serialVersionUID = 1L;
	
	private static enum State {STARTING, HISTHELP, HISTOGRAM, PRINT};
	
	private String fileName;
	private String argString;
	private String csvPath;
	private Histogram histogram;
	private HistogramHelper histogramHelper;
	
	private State state;
	
	private int partitions;
	
	private long lastStateChange;
	private long startingTime = 30 * 1000;
	private long histHelpTime = 30 * 1000;
	private long histogramTime = 30 * 1000;
	
	public WordCountLatencySink(String[] args, String csvPath_){
		partitions = 100;
		
		csvPath = csvPath_;
		argString = args[4];
		for(int i = 5; i < args.length; i++){
			argString += "_" + args[i];
		}
	}
	
	@Override
	public void open(Configuration parameters) throws Exception {
		Random rnd = new Random();
		File histFile;
		do {
			fileName = csvPath + "histogramPart-" + argString + 
					"-" + String.valueOf(rnd.nextInt(10000000)) + ".csv";
			histFile = new File(fileName);
		} while(histFile.exists());
		
		histogramHelper = new HistogramHelper();
		lastStateChange = System.currentTimeMillis();
		state = State.STARTING;
	}
	
	@Override
	public void invoke(Tuple3<String, Integer, Long> inValue) {
		long nowNano = System.nanoTime();
		long diff = nowNano - inValue.f2;
		
		switch(state) {
		case STARTING:
			state = handleStarting();
			break;
		case HISTHELP:
			state = histHelperAdd(diff);
			break;
		case HISTOGRAM:
			state = histogramAdd(diff);
			break;
		case PRINT:
			state = print();
			break;
		}
	}
	
	private State handleStarting() {
		long nowMilli = System.currentTimeMillis();
		State retval;
		
		if(nowMilli < lastStateChange + startingTime) {
			retval = State.STARTING;
		} else {
			lastStateChange = nowMilli;
			retval = State.HISTHELP;
		}
		return retval;
	}
	
	private State histHelperAdd(long value) {
		long nowMilli = System.currentTimeMillis();
		State retval;
		
		if(nowMilli < lastStateChange + histHelpTime) {
			histogramHelper.add(value);
			retval = State.HISTHELP;
		} else {
			lastStateChange = nowMilli;
			retval = State.HISTOGRAM;
			initializeHistogram();
		}
		return retval;
	}
	
	private State histogramAdd(long value) {
		long nowMilli = System.currentTimeMillis();
		State retval;
		
		if(nowMilli < lastStateChange + histogramTime) {
			histogram.add(value);
			retval = State.HISTOGRAM;
		} else {
			lastStateChange = nowMilli;
			retval = State.PRINT;
		}
		return retval;
	}
	
	private State print() {
		System.out.println(histogram);
		lastStateChange = System.currentTimeMillis();
		return State.HISTOGRAM;
	}
	
	private void initializeHistogram() {
		long minValue = histogramHelper.getMin();
		long maxValue = histogramHelper.getMax();
		histogram = new Histogram(minValue, maxValue, partitions, fileName);
	}
}
