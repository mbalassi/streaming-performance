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

package org.apache.flink.streaming.performance.iterative;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.function.source.RichSourceFunction;
import org.apache.flink.util.Collector;

public class EdgeSource extends RichSourceFunction<Tuple3<Integer, Integer, Integer>>{
	private static final long serialVersionUID = 1L;
	
	private final String path;
	private Tuple3<Integer, Integer, Integer> outTuple = new Tuple3<Integer, Integer, Integer>();

	private int crawlerSize;
	private Random rnd;
	private int vertexNum;

	private long addRemoveSleep;

	private long current;
	
	public EdgeSource(String path, int crawlerSize_, long addRemoveSleep_) {
		this.vertexNum = 0;
		this.path = path;
		this.crawlerSize = crawlerSize_;
		this.rnd = new Random();
		this.addRemoveSleep = addRemoveSleep_;
		this.current = System.currentTimeMillis();
	}
	
	@Override
	public void invoke(Collector<Tuple3<Integer, Integer, Integer>> collector) throws IOException {
		buildGraphFromFile(collector);
		//doRandomAddsRemoves(collector);
	}

	private void doRandomAddsRemoves(Collector<Tuple3<Integer, Integer, Integer>> collector) {
		while(true) {
			if(current < System.currentTimeMillis() - addRemoveSleep) {
				outTuple.f0 = rnd.nextInt(vertexNum);
				outTuple.f1 = rnd.nextInt(vertexNum);
				if(outTuple.f0.equals(outTuple.f1)) {
					continue;
				}
				outTuple.f2 = rnd.nextInt(2);
				if(outTuple.f2.equals(0)) {
					outTuple.f2 = -1;
				}
				collector.collect(outTuple);
				
				current = System.currentTimeMillis();
			}
		}
	}

	private void buildGraphFromFile(
			Collector<Tuple3<Integer, Integer, Integer>> collector)
			throws FileNotFoundException, IOException {
		BufferedReader br = new BufferedReader(new FileReader(path));
		String line = br.readLine();
		while (line != null) {
			sendOperation(collector, line);

			line = br.readLine();
		}
		br.close();
	}

	private void sendOperation(
			Collector<Tuple3<Integer, Integer, Integer>> collector, String line) {
		String[] words = line.split(" |\\t");
		Integer from = Integer.valueOf(words[0]);
		Integer to = Integer.valueOf(words[1]);
		Integer operation = Integer.valueOf(words[2]);
		
		updateVertexNum(from, to);
		
		if(operation != -2) {
			sendAddRemoveEdge(collector, from, to, operation);
		} else {
			sendCrawlerStart(collector, operation);
		}
	}

	private void updateVertexNum(Integer v1, Integer v2) {
		if(vertexNum < v1) {
			vertexNum = v1;
		}
		if(vertexNum < v2) {
			vertexNum = v2;
		}
	}

	private void sendCrawlerStart(
			Collector<Tuple3<Integer, Integer, Integer>> collector,
			Integer operation) {
		for(int i = 0; i < crawlerSize; i++) {
			outTuple.f0 = i;
			outTuple.f1 = 0;
			outTuple.f2 = operation;
			collector.collect(outTuple);
		}
	}

	private void sendAddRemoveEdge(
			Collector<Tuple3<Integer, Integer, Integer>> collector,
			Integer from, Integer to, Integer operation) {
		outTuple.f0 = from;
		outTuple.f1 = to;
		outTuple.f2 = operation;
		collector.collect(outTuple);
	}
}
