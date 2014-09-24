package org.apache.storm.streaming.performance.large;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.storm.streaming.performance.general.LoggerSinkBolt;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;

public class StormVectorPerformance {
	public static void main(String[] args) throws Exception {

		if (args != null && args.length == 9) {
			try {
				boolean runOnCluster = args[0].equals("cluster");
				String fileName = args[1];
				String counterPath = args[2];
				String topologyName = args[3];

				if (!(new File(fileName)).exists()) {
					throw new FileNotFoundException();
				}

				int numberOfWorkers = Integer.parseInt(args[4]);
				int spoutParallelism = Integer.parseInt(args[5]);
				int addParallelism = Integer.parseInt(args[6]);
				int mapParallelism = Integer.parseInt(args[7]);
				int sinkParallelism = Integer.parseInt(args[8]);

				TopologyBuilder builder = new TopologyBuilder();

				builder.setSpout("spout", new CachedVectorSpout(fileName), spoutParallelism);
				builder.setBolt("add", new VectorAdderBolt(), addParallelism).fieldsGrouping(
						"spout", new Fields("user"));
				builder.setBolt("mapper", new VectorMapperBolt(), mapParallelism)
						.shuffleGrouping("add");
				// builder.setBolt("sink", new PrintSinkBolt(),
				// sinkParallelism).shuffleGrouping(
				// "add");
				builder.setBolt("sink", new LoggerSinkBolt(args, counterPath), sinkParallelism)
						.shuffleGrouping("mapper");

				Config conf = new Config();
				conf.setNumAckers(0);
				conf.setDebug(false);
				conf.setNumWorkers(numberOfWorkers);

				if (runOnCluster) {
					StormSubmitter.submitTopology(topologyName, conf, builder.createTopology());
				} else {
					// running locally
					conf.setMaxTaskParallelism(3);
					LocalCluster cluster = new LocalCluster();
					cluster.submitTopology("vector-performance", conf, builder.createTopology());
					Thread.sleep(35000);

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
				.println("USAGE:\n run <local/cluster> <performance counter path> <source file> <topology name> <number of workers> <spout parallelism> <splitter parallelism> <counter parallelism> <sink parallelism>");
	}
}
