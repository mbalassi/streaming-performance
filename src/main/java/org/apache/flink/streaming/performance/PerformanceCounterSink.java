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

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Random;

import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.streaming.api.function.sink.SinkFunction;
import org.apache.flink.streaming.util.PerformanceCounter;

public class PerformanceCounterSink<IN extends Tuple> extends SinkFunction<IN>  {
	private static final long serialVersionUID = 1L;
	
	private PerformanceCounter pCounter;
	private String argString;
	private String csvPath;
	
	public PerformanceCounterSink(String csvPath_, String argString_) {
		csvPath = csvPath_;
		argString = argString_;
	}
	
	@Override
	public void invoke(IN tuple) {
		pCounter.count();
	}

	private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
		ois.defaultReadObject();
		
		Random rnd = new Random();
		String fileName;
		File csvFile;
		do {
			fileName = csvPath + "sink-" + argString + 
					"-" + String.valueOf(rnd.nextInt(10000000)) + ".csv";
			csvFile = new File(fileName);
		} while(csvFile.exists());
		
		pCounter = new PerformanceCounter("SplitterEmitCounter", 1000, 1000, 30000, fileName);
	}
}
