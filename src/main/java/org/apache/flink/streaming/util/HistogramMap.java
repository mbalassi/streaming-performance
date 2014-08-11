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

package org.apache.flink.streaming.util;

import java.util.HashMap;
import java.util.Map;

public class HistogramMap {

	private Map<Integer, Integer> records;
	private long maxFound;
	private long intervalLength;
	
	public HistogramMap(int intervalLength) {
		this.intervalLength = intervalLength;
		
		this.records = new HashMap<Integer, Integer>();
		
		this.maxFound = Long.MIN_VALUE;
	}
	
	public void add(long value) {
		int index = getIntervalIndex(value);
		add(value, index);
	}
	
	private void add(long value, int index) {
		initializeIntervalIfNeeded(index);
		increase(index);
		updateMaxFound(value);
	}
	
	private void initializeIntervalIfNeeded(int index) {
		if(!records.containsKey(index)) {
			records.put(index, 0);
		}
	}

	private void increase(int index) {
		int value = records.get(index);
		records.put(index, value + 1);
	}
	
	private void updateMaxFound(long value) {
		if(maxFound < value) {
			maxFound = value;
		}
	}
	
	private int getIntervalIndex(long value) {
		return (int)(value / intervalLength);
	}
	
	private long getStartingLongFromIndex(int index) {
		return index * intervalLength;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("interval_min,interval_max,counter\n");
		for(Integer i : records.keySet()) {
			long from = getStartingLongFromIndex(i);
			long to = getStartingLongFromIndex(i) + intervalLength;
			sb.append(from);
			sb.append(",");
			sb.append(to);
			sb.append(",");
			sb.append(records.get(i));
			sb.append("\n");
		}
		return sb.toString();
	}
}
