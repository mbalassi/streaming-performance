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

package org.apache.flink.streaming.performance.legacy.latency.wordcount;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.function.source.RichSourceFunction;
import org.apache.flink.util.Collector;

public class WordCountLatencySource extends RichSourceFunction<Tuple2<String, Long>>{
	private static final long serialVersionUID = 1L;

	private final String path;
	private Tuple2<String, Long> outValue = new Tuple2<String, Long>();

	public WordCountLatencySource(String path) {
		this.path = path;
	}

	@Override
	public void run(Collector<Tuple2<String, Long>> collector) throws IOException {
		while (true) {
			BufferedReader br = new BufferedReader(new FileReader(path));
			outValue.f0 = br.readLine();
			while (outValue.f0 != null) {
				if (outValue.f0 != "") {
					outValue.f1 = System.currentTimeMillis();
					collector.collect(outValue);
				}
				outValue.f0 = br.readLine();
			}
			br.close();
		}
	}

	@Override
	public void cancel() {

	}
}
