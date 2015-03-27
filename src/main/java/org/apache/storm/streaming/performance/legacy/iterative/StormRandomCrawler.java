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

package org.apache.storm.streaming.performance.legacy.iterative;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.flink.streaming.util.DirectedGraph;
import org.apache.flink.streaming.util.VertexStore;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class StormRandomCrawler extends BaseRichBolt {
	private static final long serialVersionUID = 1L;

	OutputCollector _collector;
	
	private Values outRecord = new Values(0, 0, 0);
	
	private DirectedGraph graph;
	private VertexStore vertexStore;
	private Random rnd;
	
	private int sendThreshold = 0;
	private int numOfCrawlers = 0;
	private int iterationSinceLastSend = 0;
	
	public StormRandomCrawler(int sendThreshold_, int numOfCrawlers_) {
		this.sendThreshold = sendThreshold_;
		this.numOfCrawlers = numOfCrawlers_;
	}
	
	@Override
	public void prepare(Map map, TopologyContext context, OutputCollector collector) {
		_collector = collector;
		rnd = new Random();
		vertexStore = new VertexStore();
		graph = new DirectedGraph();
	}

	@Override
	public void execute(Tuple inValue) {
		switch (inValue.getInteger(2)) {
		case 1:
			addEdge(inValue.getInteger(0), inValue.getInteger(1));
			break;
		case -1:
			removeEdge(inValue.getInteger(0), inValue.getInteger(1));
			break;
		case -2:
			startCrawlers();
			break;
		case 0:
			crawl(inValue);
			sendPageRankIfNeeded();
			break;
		default:
			System.out.println("Bad crawler operation.");
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declareStream("tocrawler", new Fields("v1", "v2", "operation"));
		declarer.declareStream("tosink", new Fields("v1", "v2", "operation"));
	}
	
	private void sendPageRankIfNeeded() {
		if(iterationSinceLastSend > sendThreshold) {
			sendPageRank();
			iterationSinceLastSend = 0;
		}
		iterationSinceLastSend++;
	}

	private void sendPageRank() {
		for(Integer key : vertexStore.vertices.keySet()) {
			outRecord.set(0, key);
			outRecord.set(1, vertexStore.vertices.get(key));
			outRecord.set(2, 2);
			_collector.emit("tosink", (Values)outRecord.clone());
		}
	}

	private void crawl(Tuple inValue) {
		vertexStore.increase(inValue.getInteger(0));
		
		List<Integer> availableVertices = graph.getToEdgeList(inValue.getInteger(0));
		boolean doJump = rnd.nextFloat() > 0.85;
		int toVertex;
		if(availableVertices.size() == 0 || doJump) {
			toVertex = rnd.nextInt(vertexStore.largestVertexNum);
		} else {
			int randomIndex = rnd.nextInt(availableVertices.size());
			toVertex = availableVertices.get(randomIndex);
		}
		sendVertexToNextIteration(toVertex);
	}

	private void sendVertexToNextIteration(int toVertex) {
		outRecord.set(0, toVertex);
		outRecord.set(1, 0);
		outRecord.set(2, 0);
		
		_collector.emit("tocrawler", (Values)outRecord.clone());
	}

	private void startCrawlers() {
		for(int i = 0; i < numOfCrawlers; i++) {
			int randomIndex = rnd.nextInt(vertexStore.largestVertexNum);
			sendVertexToNextIteration(randomIndex);
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