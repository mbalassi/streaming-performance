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

package org.apache.storm.streaming.performance.iterative;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.Random;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

public class StreamingEdgeSpout extends BaseRichSpout {
	private static final long serialVersionUID = 1L;

	SpoutOutputCollector _collector;

	private String path;

	BufferedReader br = null;
	private Values outRecord = new Values(0, 0, 0);

	private boolean graphBuilt = false;
	
	private int crawlerSize;
	private Random rnd;
	private int vertexNum;

	private long addRemoveSleep;

	private long current;

	public StreamingEdgeSpout(String path, int crawlerSize_, long addRemoveSleep_) {
		this.path = path;
		this.vertexNum = 0;
		this.crawlerSize = crawlerSize_;
		this.rnd = new Random();
		this.addRemoveSleep = addRemoveSleep_;
		this.current = System.currentTimeMillis();
	}

	@Override
	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		_collector = collector;
		try {
			br = new BufferedReader(new FileReader(path));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void nextTuple() {
		try {
			if(!graphBuilt) {
				buildGraphFromFile();
			}
			if(graphBuilt) {
				doRandomAddsRemoves();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void ack(Object id) {
	}

	@Override
	public void fail(Object id) {
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("v1", "v2", "operation"));
	}

	private void doRandomAddsRemoves() {
		while (true) {
			if (current < System.currentTimeMillis() - addRemoveSleep) {
				outRecord.set(0, rnd.nextInt(vertexNum));
				outRecord.set(1, rnd.nextInt(vertexNum));
				if (outRecord.get(0).equals(outRecord.get(1))) {
					continue;
				}
				outRecord.set(2, rnd.nextInt(2));
				if (outRecord.get(2).equals(0)) {
					outRecord.set(2, -1);
				}
				_collector.emit((Values)outRecord.clone());

				current = System.currentTimeMillis();
			}
		}
	}

	private void buildGraphFromFile() throws FileNotFoundException, IOException {
		String line = br.readLine();
		if (line != null) {
			sendOperation(line);
		} else {
			br.close();
			graphBuilt = true;
		}
	}

	private void sendOperation(String line) {
		String[] words = line.split(" |\\t");
		Integer from = Integer.valueOf(words[0]);
		Integer to = Integer.valueOf(words[1]);
		Integer operation = Integer.valueOf(words[2]);

		updateVertexNum(from, to);

		if (operation != -2) {
			sendAddRemoveEdge(from, to, operation);
		} else {
			sendCrawlerStart(operation);
		}
	}

	private void updateVertexNum(Integer v1, Integer v2) {
		if (vertexNum < v1) {
			vertexNum = v1;
		}
		if (vertexNum < v2) {
			vertexNum = v2;
		}
	}

	private void sendCrawlerStart(Integer operation) {
		for (int i = 0; i < crawlerSize; i++) {
			outRecord.set(0, i);
			outRecord.set(1, 0);
			outRecord.set(2, operation);
			_collector.emit((Values)outRecord.clone());
		}
	}

	private void sendAddRemoveEdge(Integer from, Integer to, Integer operation) {
		outRecord.set(0, from);
		outRecord.set(1, to);
		outRecord.set(2, operation);
		_collector.emit((Values)outRecord.clone());
	}
}