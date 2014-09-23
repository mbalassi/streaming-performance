package org.apache.flink.streaming.performance.large;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class VectorLocal {
	public static void main(String args[]) {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironment(1);

		env.addSource(new RandomVectorSource(10, 200))
				.map(new MapFunction<UserVector, UserVector>() {
					private static final long serialVersionUID = 1L;

					@Override
					public UserVector map(UserVector arg0) throws Exception {
						for (int i = 0; i < arg0.getVector().length; i++) {
							arg0.getVector()[i] *= 0.2;
						}
						return arg0;
					}
				}).groupBy(0).reduce(new ReduceFunction<UserVector>() {
					private static final long serialVersionUID = 1L;

					@Override
					public UserVector reduce(UserVector arg0, UserVector arg1) throws Exception {
						for (int i = 0; i < arg0.getVector().length; i++) {
							arg0.getVector()[i] += arg1.getVector()[i];
						}
						return arg0;
					}
				}).print();

		env.execute();
	}
}
