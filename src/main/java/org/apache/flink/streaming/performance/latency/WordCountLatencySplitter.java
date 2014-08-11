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

import org.apache.flink.api.java.functions.RichFlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;

public class WordCountLatencySplitter extends RichFlatMapFunction<Tuple2<String, Long>, Tuple2<String, Long>> {

	private static final long serialVersionUID = 1L;

	private Tuple2<String, Long> outTuple = new Tuple2<String, Long>();

	@Override
	public void flatMap(Tuple2<String, Long> inValue, Collector<Tuple2<String, Long>> out) throws Exception {

		for (String word : inValue.f0.split(" ")) {
			outTuple.f0 = word;
			outTuple.f1 = inValue.f1;
			out.collect(outTuple);
		}
	}
}
