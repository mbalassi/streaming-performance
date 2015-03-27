package org.apache.flink.streaming.performance.legacy.large;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class VectorPerformanceLocal {
	public static void main(String args[]) {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment(1);

		env.addSource(new CachedVectorSource("/home/hermann/performance/vector-small.txt"))
				.groupBy(0)
				.flatMap(new VectorAdderFlatMap())
				.flatMap(new VectorMapperFlatMap())
				.print();

		try {
			env.execute();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
