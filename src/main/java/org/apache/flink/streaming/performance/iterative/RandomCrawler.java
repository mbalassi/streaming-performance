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
import org.apache.flink.util.Collector;

public class RandomCrawler extends RichFlatMapFunction<Tuple3<Integer, Integer, Integer>, Tuple3<Integer, Integer, Integer>> {
	private static final long serialVersionUID = 1L;
	
	private Tuple3<Integer, Integer, Integer> retval;
	private DirectedGraph graph;
	private VertexStore numberOfVisits;
	private Random rnd;
	private int startingEdgeNum = 0;
	private int sendThreshold = 0;
	private int numOfCrawlers = 0;
	
	public RandomCrawler(int startingEdgeNum_, int sendThreshold_, int numOfCrawlers_) {
		this.startingEdgeNum = startingEdgeNum_;
		this.sendThreshold = sendThreshold_;
		this.numOfCrawlers = numOfCrawlers_;
	}
	
	@Override
	public void open(Configuration parameters) throws Exception {
		rnd = new Random();
		numberOfVisits = new VertexStore();
		graph = new DirectedGraph();
		retval = new Tuple3<Integer, Integer, Integer>();
	};

	@Override
	public void flatMap(Tuple3<Integer, Integer, Integer> inValue,
			Collector<Tuple3<Integer, Integer, Integer>> out) throws Exception {
		if (inValue.f2.equals(1)) {
			graph.insertEdge(inValue.f0, inValue.f1);
			numberOfVisits.addVertex(inValue.f0);
			numberOfVisits.addVertex(inValue.f1);
			if(startingEdgeNum < graph.toVertices.size() && graph.toVertices.size() < startingEdgeNum + numOfCrawlers) {
				int randomIndex = rnd.nextInt(numberOfVisits.vertices.size());
				retval.f0 = randomIndex;
				retval.f1 = 0;
				retval.f2 = 0;
				out.collect(retval);
			}
		} else if(inValue.f2.equals(0)) {
			numberOfVisits.increase(inValue.f0);
			if(numberOfVisits.vertices.get(inValue.f0) % sendThreshold == 0) {
				retval.f0 = inValue.f0;
				retval.f1 = sendThreshold;
				retval.f2 = 2;
				out.collect(retval);
			}
			
			List<Integer> availablePaths = graph.toVertices.get(inValue.f0);
			boolean doJump = rnd.nextFloat() > 0.85;
			if(availablePaths.size() == 0 || doJump) {
				int randomIndex = rnd.nextInt(numberOfVisits.vertices.size());
				retval.f0 = randomIndex;
			} else {
				int randomIndex = rnd.nextInt(availablePaths.size());
				retval.f0 = availablePaths.get(randomIndex);
			}
			retval.f1 = 0;
			retval.f2 = 0;
			
			out.collect(retval);
		}
	}
}
