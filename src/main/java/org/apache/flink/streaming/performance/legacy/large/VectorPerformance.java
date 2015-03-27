package org.apache.flink.streaming.performance.legacy.large;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class VectorPerformance {

	public static void main(String args[]) {
		if (args != null && args.length == 11) {
			try {
				boolean runOnCluster = args[0].equals("cluster");
				String sourcePath = args[1];
				String csvPath = args[2];
				String jarPath = args[3];
				String host = args[4];
				int port = Integer.valueOf(args[5]);

				if (!(new File(sourcePath)).exists()) {
					throw new FileNotFoundException();
				}

				int clusterSize = Integer.valueOf(args[6]);
				int sourceSize = Integer.valueOf(args[7]);
				int addSize = Integer.valueOf(args[8]);
				int mapSize = Integer.valueOf(args[9]);
				int sinkSize = Integer.valueOf(args[10]);

				StreamExecutionEnvironment env;
				if (runOnCluster) {
					env = StreamExecutionEnvironment.createRemoteEnvironment(host, port,
							clusterSize, jarPath);
				} else {
					env = StreamExecutionEnvironment.createLocalEnvironment(clusterSize);
				}

				env.addSource(new CachedVectorSource(sourcePath)).setParallelism(sourceSize)
						.groupBy(0)
						.flatMap(new VectorAdderFlatMap()).shuffle().setParallelism(addSize)
						.flatMap(new VectorMapperFlatMap()).shuffle().setParallelism(mapSize)
						.addSink(new VectorPerformanceCounterSink(args, csvPath)).setParallelism(sinkSize);

				try {
					env.execute();
				} catch (Exception e) {
					e.printStackTrace();
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
		System.out.println("Usage:");
		System.out
				.println("\trun <cluster/local> <source path> <csv path> <jar path> <host> <port> <cluster size> <source size> <add size> <map size> <sink size>");
	}
}