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

package org.apache.spark.streaming.performance.latency;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.receiver.Receiver;

import scala.Tuple2;

class StreamingTextReceiver extends Receiver<Tuple2<String, Long>> {
	private static final long serialVersionUID = 1L;
	
	private String path;
	private BufferedReader br = null;
	private String line = new String();
	private Tuple2<String, Long> out;

	public StreamingTextReceiver(String path) {
		super(StorageLevel.MEMORY_AND_DISK_2());
		this.path = path;
	}

	public void onStart() {
		try {
			br = new BufferedReader(new FileReader(path));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		new Thread() {
			@Override 
			public void run() {
				receive();
			}
		}.start();
	}

	public void onStop() {
		// Cleanup stuff (stop threads, close sockets, etc.) to stop receiving
		// data.
	}

	private void receive() {
		try {
			while (!isStopped()) {
				try {
					line = br.readLine();
					if (line == null) {
						br = new BufferedReader(new FileReader(path));
						line = br.readLine();
					}
					long currentTime = System.currentTimeMillis();
					out = new Tuple2<String, Long>(line, currentTime);
					store(out);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			restart("Trying to connect again");
		} catch (Throwable t) {
			restart("Error receiving data", t);
		}
	}
}