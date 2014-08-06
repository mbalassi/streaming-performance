/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.streaming.performance.iterative;

import java.util.List;
import java.util.Random;

import org.apache.flink.api.java.functions.RichFlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.util.DirectedGraph;
import org.apache.flink.streaming.util.VertexStore;
import org.apache.flink.util.Collector;

public class RandomCrawler extends RichFlatMapFunction<Tuple3<Integer, Integer, Integer>, Tuple3<Integer, Integer, Integer>> {
	private static final long serialVersionUID = 1L;
	
	private Tuple3<Integer, Integer, Integer> retval;
	private DirectedGraph graph;
	private VertexStore vertexStore;
	private Random rnd;
	
	private int sendThreshold = 0;
	private int numOfCrawlers = 0;
	private int iterationSinceLastSend = 0;
	
	private boolean crawlersStarted;
	
	private int dummyNum = 0;
	
	public RandomCrawler(int sendThreshold_, int numOfCrawlers_) {
		this.sendThreshold = sendThreshold_;
		this.numOfCrawlers = numOfCrawlers_;
	}
	
	@Override
	public void open(Configuration parameters) throws Exception {
		rnd = new Random();
		vertexStore = new VertexStore();
		graph = new DirectedGraph();
		retval = new Tuple3<Integer, Integer, Integer>();
		crawlersStarted = false;
	};

	@Override
	public void flatMap(Tuple3<Integer, Integer, Integer> inValue,
			Collector<Tuple3<Integer, Integer, Integer>> out) throws Exception {
		switch (inValue.f2) {
		case 1:
			addEdge(inValue.f0, inValue.f1);
			sendDummyIterationIfNeeded(out);
			break;
		case -1:
			removeEdge(inValue.f0, inValue.f1);
			break;
		case -2:
			startCrawlers(out);
			break;
		case 0:
			crawl(inValue, out);
			sendPageRankIfNeeded(out);
			break;
		case -3:
			dummyNum--;
			sendDummyIterationIfNeeded(out);
			break;
		default:
			System.out.println("Bad crawler operation.");
		}
	}

	private void sendDummyIterationIfNeeded(Collector<Tuple3<Integer, Integer, Integer>> out) {
		if(!crawlersStarted && dummyNum < 10000) {
			dummyNum++;
			retval.f0 = 0;
			retval.f1 = 0;
			retval.f2 = -3;
			
			out.collect(retval);
		}
	}

	private void sendPageRankIfNeeded(
			Collector<Tuple3<Integer, Integer, Integer>> out) {
		if(iterationSinceLastSend > sendThreshold) {
			sendPageRank(out);
			iterationSinceLastSend = 0;
		}
		iterationSinceLastSend++;
	}

	private void sendPageRank(Collector<Tuple3<Integer, Integer, Integer>> out) {
		for(Integer key : vertexStore.vertices.keySet()) {
			retval.f0 = key;
			retval.f1 = vertexStore.vertices.get(key);
			retval.f2 = 2;
			out.collect(retval);
		}
	}

	private void crawl(Tuple3<Integer, Integer, Integer> inValue,
			Collector<Tuple3<Integer, Integer, Integer>> out) {
		vertexStore.increase(inValue.f0);
		
		List<Integer> availableVertices = graph.getToEdgeList(inValue.f0);
		boolean doJump = rnd.nextFloat() > 0.85;
		int toVertex;
		if(availableVertices.size() == 0 || doJump) {
			toVertex = rnd.nextInt(vertexStore.largestVertexNum);
		} else {
			int randomIndex = rnd.nextInt(availableVertices.size());
			toVertex = availableVertices.get(randomIndex);
		}
		sendVertexToNextIteration(out, toVertex);
	}

	private void sendVertexToNextIteration(
			Collector<Tuple3<Integer, Integer, Integer>> out, int toVertex) {
		retval.f0 = toVertex;
		retval.f1 = 0;
		retval.f2 = 0;
		
		out.collect(retval);
	}

	private void startCrawlers(Collector<Tuple3<Integer, Integer, Integer>> out) {
		crawlersStarted = true;
		for(int i = 0; i < numOfCrawlers; i++) {
			int randomIndex = rnd.nextInt(vertexStore.largestVertexNum);
			sendVertexToNextIteration(out, randomIndex);
		}
	}

	private void addEdge(Integer from, Integer to) {
		graph.insertEdge(from, to);
		vertexStore.addVertex(from);
	}
	
	private void removeEdge(Integer from, Integer to) {
		graph.removeEdge(from, to);
	}
}
