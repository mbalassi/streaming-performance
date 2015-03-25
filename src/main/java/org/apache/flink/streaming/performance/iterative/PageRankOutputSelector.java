//package org.apache.flink.streaming.performance.iterative;
//
//import java.util.Arrays;
//
//import org.apache.flink.api.java.tuple.Tuple3;
//import org.apache.flink.streaming.api.collector.OutputSelector;
//
//public class PageRankOutputSelector implements
//		OutputSelector<Tuple3<Integer, Integer, Integer>> {
//	private static final long serialVersionUID = 1L;
//
//
//	@Override
//	public Iterable<String> select(Tuple3<Integer, Integer, Integer> tuple) {
//		if (tuple.f2.equals(0) || tuple.f2.equals(-3)) {
//			return Arrays.asList("iterate");
//		} else if (tuple.f2.equals(2)) {
//			return Arrays.asList("sink");
//		}
//		return Arrays.asList();
//	}
//}
