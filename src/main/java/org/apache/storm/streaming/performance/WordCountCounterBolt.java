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

package org.apache.storm.streaming.performance;

import java.util.HashMap;
import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class WordCountCounterBolt extends BaseRichBolt {
	private static final long serialVersionUID = 1L;

	OutputCollector _collector;
	
	private Map<String, Integer> wordCounts = new HashMap<String, Integer>();
	private String word = "";
	private Integer count = 0;

	private Values outRecord = new Values("", 0);
	
	@Override
	public void prepare(Map map, TopologyContext context, OutputCollector collector) {
		_collector = collector;
	}

	@Override
	public void execute(Tuple tuple) {
		word = tuple.getString(0);

		if (wordCounts.containsKey(word)) {
			count = wordCounts.get(word) + 1;
			wordCounts.put(word, count);
		} else {
			count = 1;
			wordCounts.put(word, 1);
		}

		outRecord.set(0, word);
		outRecord.set(1, count);

		_collector.emit(outRecord);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("word", "count"));
	}
}