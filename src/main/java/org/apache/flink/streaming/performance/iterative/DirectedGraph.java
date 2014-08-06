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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DirectedGraph implements Serializable {
	private static final long serialVersionUID = 1L;
	//node -> target neighbors
	public Map<Integer, List<Integer>> toVertices = null;
	
	public DirectedGraph() {
		toVertices = new HashMap<Integer, List<Integer>>();
	}

	public void insertEdge(int sourceNode, int targetNode) {
		if (!toVertices.containsKey(sourceNode)) {
			toVertices.put(sourceNode, new ArrayList<Integer>());
		}
		toVertices.get(sourceNode).add(targetNode);
		if (!toVertices.containsKey(targetNode)) {
			toVertices.put(targetNode, new ArrayList<Integer>());
		}
	}
	
	public List<Integer> getToEdgeList(Integer i) {
		if(toVertices.containsKey(i)) {
			return toVertices.get(i);
		} else {
			return new ArrayList<Integer>();
		}
	}

	public void removeEdge(Integer sourceNode, Integer targetNode) {
		if(toVertices.containsKey(sourceNode)) {
			toVertices.get(sourceNode).remove(targetNode);
		}
	}
}
