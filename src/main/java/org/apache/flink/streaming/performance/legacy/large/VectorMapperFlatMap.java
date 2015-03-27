package org.apache.flink.streaming.performance.legacy.large;

import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.streaming.performance.legacy.large.util.VectorParser;
import org.apache.flink.util.Collector;

public class VectorMapperFlatMap extends RichFlatMapFunction<UserVector, UserVector> {
	private static final long serialVersionUID = 1L;

	@Override
	public void flatMap(UserVector userVector, Collector<UserVector> out) throws Exception {
		Double[] multiplied = VectorParser.vectorMultiplyBy(VectorParser.LAMBDA, userVector.getVector());
		userVector.setVector(multiplied);
		out.collect(userVector);
	}
}
