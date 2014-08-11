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

public class Histogram {

	private int[] records;
	private long maxFound;
	private long minValue;
	private long maxValue;
	private double intervalLength;
	
	public Histogram(long minValue_, long maxValue_, int partitions, String fileName) {
		this.minValue = minValue_;
		this.maxValue = maxValue_;
		this.intervalLength = (double)(maxValue - minValue) / (double)partitions;
		
		initializeRecords(partitions);
		
		this.maxFound = minValue;
	}
	
	public void add(long value) {
		int index = getIntervalIndex(value);
		add(value, index);
	}
	
	private void add(long value, int index) {
		records[index]++;
		updateMaxFound(value);
	}
	
	private void updateMaxFound(long value) {
		if(maxFound < value) {
			maxFound = value;
		}
	}
	
	private void initializeRecords(int partitions) {
		this.records = new int[partitions + 2];
		for(int i = 0; i < records.length; i++) {
			records[i] = 0;
		}
	}
	
	private int getIntervalIndex(long value) {
		int index;
		if(value < minValue) {
			index = 0;
		} else if(maxValue < value) {
			index = records.length - 1;
		} else {
			index = (int)((value - minValue) / intervalLength) + 1;
		}
		return index;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < records.length; i++) {
			sb.append(records[i]);
			sb.append("\n");
		}
		
		return sb.toString();
	}
}
