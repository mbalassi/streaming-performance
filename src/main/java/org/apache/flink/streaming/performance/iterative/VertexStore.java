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
import java.util.HashMap;
import java.util.Map;

public class VertexStore implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public Map<Integer, Integer> vertices=null;
	public int largestVertexNum;
	
	public VertexStore(){
		vertices=new HashMap<Integer, Integer>();
		largestVertexNum = 0;
	}
	
	public void setValue(int node, Integer value){
		vertices.put(node, value);
	}
	
	public void addVertex(Integer vertex) {
		if(!vertices.containsKey(vertex)) {
			vertices.put(vertex, 0);
			if(largestVertexNum < vertex) {
				largestVertexNum = vertex;
			}
		}
	}

	public void increase(Integer vertex) {
		addVertex(vertex);
		vertices.put(vertex, vertices.get(vertex) + 1);
	}
}
