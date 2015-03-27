package org.apache.flink.streaming.performance.legacy.large;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.streaming.performance.legacy.large.util.VectorParser;
import org.apache.flink.util.Collector;

public class VectorAdderFlatMap extends RichFlatMapFunction<UserVector, UserVector> {
	private static final long serialVersionUID = 1L;
	
	private Map<Integer, Double[]> userVectors = new HashMap<Integer, Double[]>();
	private Integer user  = 0;
	private Double[] vector = new Double[0];

	private UserVector outUserVector = new UserVector();

	private int counter = 0;
	
	@Override
	public void flatMap(UserVector userVector, Collector<UserVector> out) throws Exception {
		if (counter++ > VectorParser.VECTOR_REFRESH_NUMBER) {
			userVectors.clear();
			counter = 0;
		}
		
		user = userVector.getUser();
		Double[] newVector = userVector.getVector();
		
		vector = userVectors.get(user);
		
		if (vector == null) {
			vector = newVector;
		} else {
			vector = VectorParser.vectorAdd(vector, newVector);
		}

		userVectors.put(user, vector);
		
		outUserVector.setField(user, 0);
		outUserVector.setField(Arrays.copyOf(vector, vector.length), 1);

		out.collect(outUserVector);
	}
}
