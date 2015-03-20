package org.apache.flink.streaming.performance.large;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.flink.streaming.api.function.source.SourceFunction;
import org.apache.flink.streaming.performance.large.util.VectorParser;
import org.apache.flink.util.Collector;

public class CachedVectorSource implements SourceFunction<UserVector> {
	private static final long serialVersionUID = 1L;

	private String filePath;
	private transient ArrayList<ArrayList<Object>> cache;

	public CachedVectorSource(String filePath) {
		this.filePath = filePath;
	}

	@Override
	public void run(Collector<UserVector> out) throws Exception {
		cache = VectorParser.getCollection(filePath);

		UserVector userVector = new UserVector();

		while (true) {
			for (ArrayList<Object> tuple : cache) {
				userVector.setUser((Integer) tuple.get(0));
				Double[] vector = (Double[]) tuple.get(1);
				userVector.setVector(Arrays.copyOf(vector, vector.length));
				out.collect(userVector);
			}
		}
	}

	@Override
	public void cancel() {

	}

}
