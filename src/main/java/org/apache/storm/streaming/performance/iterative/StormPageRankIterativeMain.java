package org.apache.storm.streaming.performance.iterative;

import java.io.File;
import java.io.FileNotFoundException;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.BoltDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;

public class StormPageRankIterativeMain {
	public static void main(String[] args) throws Exception {

		if (args != null && args.length == 5) {
			try {
				//System.setOut(new PrintStream("/home/tofi/git/streaming-performance/src/test/resources/Performance/graphgenerator/out"));
				//System.setErr(new PrintStream("/home/tofi/git/streaming-performance/src/test/resources/Performance/graphgenerator/out"));
				
				boolean runOnCluster = args[0].equals("cluster");
				String fileName = args[1];
				String csvPath = args[2];

				if (!(new File(fileName)).exists()) {
					throw new FileNotFoundException();
				}

				int numberOfWorkers = Integer.parseInt(args[3]);
				int crawlerSize = Integer.parseInt(args[4]);
				int edgeAddRemoveSleep = 100;

				TopologyBuilder builder = new TopologyBuilder();
				builder.setSpout("spout", new StreamingEdgeSpout(fileName, crawlerSize, edgeAddRemoveSleep), 1);
				BoltDeclarer crawler = builder.setBolt("crawler", new StormRandomCrawler(1000000, 100), crawlerSize);
				BoltDeclarer sink = builder.setBolt("sink", new PageRankLoggerSinkBolt(args, csvPath), 1);

				crawler.fieldsGrouping("spout", new Fields("v1"));
				crawler.fieldsGrouping("crawler", "tocrawler", new Fields("v1"));
				sink.shuffleGrouping("crawler", "tosink");
				
				Config conf = new Config();
				conf.setNumAckers(0);
				conf.setDebug(false);
				conf.setNumWorkers(numberOfWorkers);

				if (runOnCluster) {
					StormSubmitter.submitTopology("performancetestertobekilled", conf, builder.createTopology());
				} else {
					// running locally for 70 seconds

					conf.setMaxTaskParallelism(3);
					LocalCluster cluster = new LocalCluster();
					cluster.submitTopology("pagerank-iteration-performance", conf, builder.createTopology());
					Thread.sleep(70000);

					//cluster.shutdown();
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
		System.out.println("USAGE:\n run <local/cluster> <performance counter path> "
				+ "<source file> <number of workers> <spout parallelism> "
				+ "<splitter parallelism> <counter parallelism> <sink parallelism>");
	}
}
