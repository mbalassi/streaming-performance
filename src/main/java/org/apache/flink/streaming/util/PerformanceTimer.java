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

public class PerformanceTimer extends PerformanceTracker {

	private static final long serialVersionUID = 1L;
	
	long timer;
	boolean millis;

	public PerformanceTimer(String name, int counterLength, int countInterval, boolean millis,
			long dumpInterval, String fname) {
		super(name, counterLength, countInterval, dumpInterval, fname);
		this.millis = millis;
	}

	public PerformanceTimer(String name, int counterLength, int countInterval, boolean millis,
			String fname) {
		super(name, counterLength, countInterval, fname);
		this.millis = millis;
	}

	public PerformanceTimer(String name, boolean millis, String fname) {
		super(name, fname);
		this.millis = millis;
	}

	public void startTimer() {
		if (millis) {
			timer = System.currentTimeMillis();
		} else {
			timer = System.nanoTime();
		}

	}

	public void stopTimer(String label) {

		if (millis) {
			track(System.currentTimeMillis() - timer, label);
		} else {
			track(System.nanoTime() - timer, label);
		}
	}

	public void stopTimer() {
		stopTimer("timer");
	}

}
