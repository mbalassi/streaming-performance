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

package org.apache.storm.streaming.performance.iterative;

import java.util.Map;
import java.util.Random;

import org.apache.flink.streaming.util.PerformanceCounter;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

public class PageRankLoggerSinkBolt extends BaseRichBolt {
	private static final long serialVersionUID = 1L;
	
	private PerformanceCounter performanceCounter;
	
	private String counterPath;
	private String argString;

	public PageRankLoggerSinkBolt(String[] args, String counterPath) {
		this.counterPath = counterPath;
		this.argString = args[3];
		for(int i = 4; i < args.length; i++){
			argString += "_" + args[i];
		}
	}
	
	@Override
	public void prepare(Map map, TopologyContext context, OutputCollector collector) {
		Random rnd = new Random();
		this.performanceCounter = new PerformanceCounter("pc", 1000, 1000, 30000, counterPath
				+ "stormSink-" + argString + "-" + context.getThisTaskId() + ".csv");
	}

	@Override
	public void execute(Tuple tuple) {
		//performanceCounter.count();
		System.out.println(tuple);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
	}
}