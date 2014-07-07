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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Random;

import eu.stratosphere.api.java.tuple.Tuple2;
import eu.stratosphere.streaming.api.SinkFunction;
import eu.stratosphere.streaming.util.PerformanceCounter;

public class WordCountPerformanceSink extends SinkFunction<Tuple2<String, Integer>>  {
	private static final long serialVersionUID = 1L;
	
	private PerformanceCounter pCounter;
	private String argString;
	
	public WordCountPerformanceSink(String[] args){
		argString = args[0];
		for(int i = 1; i < args.length; i++){
			argString += "_" + args[i];
		}
	}
	
	
	@Override
	public void invoke(Tuple2<String, Integer> tuple) {
		pCounter.count();
	}
	
	private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
		ois.defaultReadObject();
		Random rnd = new Random();
		pCounter = new PerformanceCounter("SplitterEmitCounter", 1000, 1000, 30000,
				"/home/strato/stratosphere-distrib/log/counter/sink-" + argString + "-" + String.valueOf(rnd.nextInt(10000000)) + ".csv");
	}
}
