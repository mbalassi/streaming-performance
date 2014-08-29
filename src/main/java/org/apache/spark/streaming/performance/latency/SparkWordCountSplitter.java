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

import java.util.ArrayList;
import java.util.List;

import org.apache.spark.api.java.function.FlatMapFunction;

import scala.Tuple2;

public class SparkWordCountSplitter implements FlatMapFunction<Tuple2<String, Long>, Tuple2<String, Long>> {
	private static final long serialVersionUID = 1L;
	
	private Tuple2<String, Long> out;
	
	@Override
	public Iterable<Tuple2<String, Long>> call(Tuple2<String, Long> tuple) {
		List<Tuple2<String, Long>> outList = new ArrayList<Tuple2<String, Long>>();
		for (String word : tuple._1.split(" ")) {
			out = new Tuple2<String, Long>(word, tuple._2);
			outList.add(out);
		}
		return outList;
	}
}
