package org.apache.flink.streaming.performance.iterative;

import java.util.Collection;

import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.collector.OutputSelector;

public class PageRankOutputSelector extends
		OutputSelector<Tuple3<Integer, Integer, Integer>> {
	private static final long serialVersionUID = 1L;

	@Override
	public void select(Tuple3<Integer, Integer, Integer> tuple,
			Collection<String> outputs) {
		if(tuple.f2.equals(0)) {
			outputs.add("iterate");
		} else if(tuple.f2.equals(2)) {
			outputs.add("sink");
		}
	}
}
