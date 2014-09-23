package org.apache.flink.streaming.performance.large;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class VectorPerformance {
	private static int numberOfUsers = 10;
	private static int lengthOfVector = 32;

	private static double lambda;

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
				int mapSize = Integer.valueOf(args[8]);
				int reduceSize = Integer.valueOf(args[9]);
				int sinkSize = Integer.valueOf(args[10]);

				lambda = 0.2;

				StreamExecutionEnvironment env;
				if (runOnCluster) {
					env = StreamExecutionEnvironment.createRemoteEnvironment(
							host, port, clusterSize, 
							jarPath);
				} else {
					env = StreamExecutionEnvironment.createLocalEnvironment(clusterSize);
				}

				env.addSource(new RandomVectorSource(numberOfUsers, lengthOfVector))
						.setParallelism(sourceSize)
						.map(new MapFunction<UserVector, UserVector>() {
							private static final long serialVersionUID = 1L;

							@Override
							public UserVector map(UserVector arg0) throws Exception {
								for (int i = 0; i < arg0.getVector().length; i++) {
									arg0.getVector()[i] *= lambda;
								}
								return arg0;
							}
						})
						.setParallelism(mapSize)
						.groupBy(0)
						.reduce(new ReduceFunction<UserVector>() {
							private static final long serialVersionUID = 1L;

							@Override
							public UserVector reduce(UserVector arg0, UserVector arg1)
									throws Exception {
								for (int i = 0; i < arg0.getVector().length; i++) {
									arg0.getVector()[i] += arg1.getVector()[i];
								}
								return arg0;
							}
						}).setParallelism(reduceSize)
						.addSink(new PerformanceCounterSink(args, csvPath));

				env.execute();
			} catch (NumberFormatException e) {
				printUsage();
			} catch (FileNotFoundException e) {
				printUsage();
			}
		} else {
			System.out.println(args.length);
			printUsage();
		}
	}
	
	private static void printUsage() {
		System.out.println("Usage:");
		System.out
				.println("\trun <jar path> <host> <port> <number of users> <length of vector> <number of workers> <spout parallelism> <map parallelism> <reduce parallelism> <sink parallelism>");
	}
}