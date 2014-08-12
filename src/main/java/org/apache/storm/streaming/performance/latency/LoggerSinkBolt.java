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

package org.apache.storm.streaming.performance.latency;

import java.util.Map;

import org.apache.flink.streaming.util.LatencyTester;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

public class LoggerSinkBolt extends BaseRichBolt {
	private static final long serialVersionUID = 1L;
	
	private String counterPath;
	private String argString;
	
	private LatencyTester latencyTester;
	
	private int intervalLength;

	public LoggerSinkBolt(String[] args, String counterPath, int intervalLength_) {
		this.counterPath = counterPath;
		this.argString = args[3];
		for(int i = 4; i < args.length; i++){
			argString += "_" + args[i];
		}
		intervalLength = intervalLength_;
	}
	
	@Override
	public void prepare(Map map, TopologyContext context, OutputCollector collector) {
		String fileName = counterPath
				+ "stormLatencySink-" + argString + "-" + context.getThisTaskId() + ".csv";
		latencyTester = new LatencyTester(intervalLength, fileName);
	}

	@Override
	public void execute(Tuple tuple) {
		long arrivalTime = System.nanoTime();
		latencyTester.add(tuple.getLong(2), arrivalTime);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
	}
}