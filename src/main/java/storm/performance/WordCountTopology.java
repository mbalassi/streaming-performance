
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

package storm.performance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.flink.streaming.util.PerformanceCounter;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class WordCountTopology {
	public static class TextSpout extends BaseRichSpout {
		private static final long serialVersionUID = 1L;

		SpoutOutputCollector _collector;

		private String path;
		
		BufferedReader br = null;
		private String line = new String();
		private Values outRecord = new Values("");
		
		private PerformanceCounter performanceCounter;
		
		private String counterPath;
		private String argString;

		public TextSpout(String path, String[] args, String counterPath) {
			this.path = path;
			this.counterPath = counterPath;
			this.argString = args[3];
			for(int i = 4; i < args.length; i++){
				argString += "_" + args[i];
			}
		}

		@Override
		public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
			Random rnd = new Random();
			_collector = collector;
			try {
				br = new BufferedReader(new FileReader(path));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			this.performanceCounter = new PerformanceCounter("pc", 1000, 1000, 30000, counterPath
					+ "stormSpout-" + argString + "-" + String.valueOf(rnd.nextInt(10000000)) + ".csv");
		}

		@Override
		public void nextTuple() {
			try {
				line = br.readLine();
				while (line == "" || line == null) {
					if (line == null) {
						br = new BufferedReader(new FileReader(path));
					}
					line = br.readLine();
				}
				outRecord.set(0, line);
				_collector.emit(outRecord);
				performanceCounter.count();
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
			declarer.declare(new Fields("line"));
		}
	}
	
	public static class Splitter extends BaseRichBolt {
		private static final long serialVersionUID = 1L;

		OutputCollector _collector;
		
		private Values outRecord = new Values("");
		
		private PerformanceCounter performanceCounter;
		
		private String counterPath;
		private String argString;

		public Splitter(String[] args, String counterPath) {
			this.counterPath = counterPath;
			this.argString = args[3];
			for(int i = 4; i < args.length; i++){
				argString += "_" + args[i];
			}
		}
		
		@Override
		public void prepare(Map map, TopologyContext context, OutputCollector collector) {
			Random rnd = new Random();
			_collector = collector;
			this.performanceCounter = new PerformanceCounter("pc", 1000, 1000, 30000, counterPath
					+ "stormSplitter-" + argString + "-" + String.valueOf(rnd.nextInt(10000000)) + ".csv");
		}

		@Override
		public void execute(Tuple tuple) {
			for (String word : tuple.getString(0).split(" ")) {
				outRecord.set(0, word);
				_collector.emit(outRecord);
				performanceCounter.count();
			}
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			declarer.declare(new Fields("word"));
		}
	}

	public static class WordCount extends BaseRichBolt {
		private static final long serialVersionUID = 1L;

		OutputCollector _collector;
		
		private Map<String, Integer> wordCounts = new HashMap<String, Integer>();
		private String word = "";
		private Integer count = 0;

		private Values outRecord = new Values("", 0);
		
		private PerformanceCounter performanceCounter;
		
		private String counterPath;
		private String argString;

		public WordCount(String[] args, String counterPath) {
			this.counterPath = counterPath;
			this.argString = args[3];
			for(int i = 4; i < args.length; i++){
				argString += "_" + args[i];
			}
		}
		
		@Override
		public void prepare(Map map, TopologyContext context, OutputCollector collector) {
			Random rnd = new Random();
			_collector = collector;
			this.performanceCounter = new PerformanceCounter("pc", 1000, 1000, 30000, counterPath
					+ "stormCounter-" + argString + "-" + String.valueOf(rnd.nextInt(10000000)) + ".csv");
		}

		@Override
		public void execute(Tuple tuple) {
			word = tuple.getString(0);

			if (wordCounts.containsKey(word)) {
				count = wordCounts.get(word) + 1;
				wordCounts.put(word, count);
			} else {
				count = 1;
				wordCounts.put(word, 1);
			}

			outRecord.set(0, word);
			outRecord.set(1, count);

			_collector.emit(outRecord);
			performanceCounter.count();
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
			declarer.declare(new Fields("word", "count"));
		}
	}

	public static class Sink extends BaseRichBolt {
		private static final long serialVersionUID = 1L;
		
		private PerformanceCounter performanceCounter;
		
		private String counterPath;
		private String argString;

		public Sink(String[] args, String counterPath) {
			this.counterPath = counterPath;
			this.argString = args[3];
			for(int i = 4; i < args.length; i++){
				argString += "_" + args[i];
			}
		}
		
		@Override
		public void prepare(Map map, TopologyContext context, OutputCollector collector) {
			Random rnd = new Random();
			this.performanceCounter = new PerformanceCounter("pc", 1000, 1000, 30000, counterPath
					+ "stormSink-" + argString + "-" + /*context.getThisTaskId()*/ String.valueOf(rnd.nextInt(10000000)) + ".csv");
		}

		@Override
		public void execute(Tuple tuple) {
			performanceCounter.count();
		}

		@Override
		public void declareOutputFields(OutputFieldsDeclarer declarer) {
		}
	}

	public static void main(String[] args) throws Exception {

		if (args != null && args.length == 8) {
			try {
				boolean runOnCluster = args[0].equals("cluster");
				String fileName = args[1];
				String counterPath = args[2];
				
				if (!(new File(fileName)).exists()) {
					throw new FileNotFoundException();
				}

				int numberOfWorkers = Integer.parseInt(args[3]);
				int spoutParallelism = Integer.parseInt(args[4]);
				int splitterParallelism = Integer.parseInt(args[5]);
				int counterParallelism = Integer.parseInt(args[6]);
				int sinkParallelism = Integer.parseInt(args[7]);

				TopologyBuilder builder = new TopologyBuilder();
				builder.setSpout("spout", new TextSpout(fileName, args, counterPath), spoutParallelism);
				builder.setBolt("split", new Splitter(args, counterPath), splitterParallelism).shuffleGrouping("spout");
				builder.setBolt("count", new WordCount(args, counterPath), counterParallelism).fieldsGrouping(
						"split", new Fields("word"));
				builder.setBolt("sink", new Sink(args, counterPath), sinkParallelism).shuffleGrouping("count");

				Config conf = new Config();
				conf.setDebug(false);
				conf.setNumWorkers(numberOfWorkers);

				if (runOnCluster) {
					StormSubmitter.submitTopology("wordcountperformance", conf, builder.createTopology());
				} else {
					// running locally for 40 seconds

					conf.setMaxTaskParallelism(3);
					LocalCluster cluster = new LocalCluster();
					cluster.submitTopology("word-count-performance", conf, builder.createTopology());
					Thread.sleep(40000);

					cluster.shutdown();
				}

			} catch (NumberFormatException e) {
				printUsage();
			} catch (FileNotFoundException e) {
				printUsage();
			}
		} else {
			printUsage();
		}
	}

	private static void printUsage() {
		System.out
				.println("USAGE:\n run <local/cluster> <performance counter path> <source file> <number of workers> <spout parallelism> <splitter parallelism> <counter parallelism> <sink parallelism>");
	}
}