package org.apache.flink.streaming.performance.iterative;

import org.apache.flink.api.java.functions.RichMapFunction;
import org.apache.flink.api.java.tuple.Tuple3;

public class DummyForwarderMap extends RichMapFunction<Tuple3<Integer, Integer, Integer>, Tuple3<Integer, Integer, Integer>> {
	private static final long serialVersionUID = 1L;
	
	@Override
	public Tuple3<Integer, Integer, Integer> map(Tuple3<Integer, Integer, Integer> value)
			throws Exception {
		return value;
	}

}
