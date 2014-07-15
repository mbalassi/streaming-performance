/***********************************************************************************************************************
 *
 * Copyright (C) 2010-2014 by the Stratosphere project (http://stratosphere.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 **********************************************************************************************************************/

package org.apache.flink.streaming.util;

import java.io.Serializable;

public class PerformanceCounter extends PerformanceTracker implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private long lastAdd;
	
	public PerformanceCounter(String name, int counterLength, int countInterval, String fname) {
		super(name, counterLength, countInterval, fname);
		lastAdd = startTime;
	}

	public PerformanceCounter(String name, int counterLength, int countInterval, long dumpInterval,
			String fname) {
		super(name, counterLength, countInterval, dumpInterval, fname);
		lastAdd = startTime;
	}

	public PerformanceCounter(String name, String fname) {
		super(name, fname);
		lastAdd = startTime;
	}

	public void count(long i, String label) {
		long ctime = System.currentTimeMillis();
		long dtime = ctime - lastAdd;
		buffer = buffer + i;
		if (dtime > interval) {
			lastAdd = ctime - (ctime % interval);
			add(buffer, label);
		}
	}

	public void count(long i) {
		count(i, "counter");
	}

	public void count(String label) {
		count(1, label);
	}

	public void count() {
		count(1, "counter");
	}
}
