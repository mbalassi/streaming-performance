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

import java.util.HashMap;
import java.util.Map;

import org.apache.flink.api.java.functions.RichMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;

public class WordCountLatencyCounter extends RichMapFunction<Tuple2<String, Long>, Tuple3<String, Integer, Long>> {
	private static final long serialVersionUID = 1L;

	private Map<String, Integer> wordCounts = new HashMap<String, Integer>();
	private String word = "";
	private Integer count = 0;

	private Tuple3<String, Integer, Long> outTuple = new Tuple3<String, Integer, Long>();
	
	// Increments the counter of the occurrence of the input word
	@Override
	public Tuple3<String, Integer, Long> map(Tuple2<String, Long> inTuple) throws Exception {
		word = inTuple.f0;

		if (wordCounts.containsKey(word)) {
			count = wordCounts.get(word) + 1;
			wordCounts.put(word, count);
		} else {
			count = 1;
			wordCounts.put(word, 1);
		}

		outTuple.f0 = word;
		outTuple.f1 = count;
		outTuple.f2 = inTuple.f1;

		return outTuple;
	}

}