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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.function.sink.RichSinkFunction;
import org.apache.flink.streaming.util.VertexStore;

public class PageRankSink extends RichSinkFunction<Tuple3<Integer, Integer, Integer>> {
	private static final long serialVersionUID = 1L;
	private String logPath;
	private PrintWriter output;
	
	private VertexStore vertexStore;
	
	private long lastWrite;
	private boolean firstInvoke;
	private int writeInterval;

	public PageRankSink(String logPath_, int writeInterval_) {
		this.logPath = logPath_;
		this.writeInterval = writeInterval_;
	}
	
	@Override
	public void open(Configuration parameters) throws Exception {
		vertexStore = new VertexStore();
		firstInvoke = true;
	}

	@Override
	public void invoke(Tuple3<Integer, Integer, Integer> tuple) {
		if(firstInvoke) {
			lastWrite = System.currentTimeMillis();
			firstInvoke = false;
		}
		int vertex = tuple.f0;
		int visits = tuple.f1;
		vertexStore.setValue(vertex, visits);
		
		writeToLogIfNeeded();
	}

	@Override
	public void cancel() {

	}

	private void writeToLogIfNeeded() {
		long now = System.currentTimeMillis();
		if(lastWrite + writeInterval < now) {
			writeToLog();
		}
	}
	
	private void writeToLog() {
		try {
			output = new PrintWriter(new BufferedWriter(new FileWriter(logPath + "/visits.txt")));
			output.print(vertexStore.toString());
			output.close();
			System.out.println("Logged pagerank");
		} catch (IOException e) {
			System.out.println("Log output file not found");
		}
		lastWrite = System.currentTimeMillis();
	}
}
