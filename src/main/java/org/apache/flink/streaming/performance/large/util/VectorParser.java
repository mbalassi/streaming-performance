package org.apache.flink.streaming.performance.large.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class VectorParser {

	static public final int VECTOR_LENGTH = 128;
	static public final int VECTOR_REFRESH_NUMBER = 1000;
	static public final double LAMBDA = 0.9;

	public static ArrayList<ArrayList<Object>> getCollection(String sourcePath) {

		ArrayList<ArrayList<Object>> result = new ArrayList<ArrayList<Object>>();
		Integer user;
		String line;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(sourcePath));
			while ((line = reader.readLine()) != null) {
				String[] splitted = line.split("\t");
				user = Integer.parseInt(splitted[0]);
				Double[] vector = new Double[VECTOR_LENGTH];
				for (int i = 1; i < splitted.length; i++) {
					vector[i - 1] = Double.parseDouble(splitted[i]);
				}
				ArrayList<Object> userVector = new ArrayList<Object>(2);
				userVector.add(user);
				userVector.add(vector);
				result.add(userVector);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	public static Double[] vectorAdd(Double[] v1, Double[] v2) {
		for (int i = 0; i < VECTOR_LENGTH; i++) {
			v1[i] += v2[i];
		}
		return v1;
	}
	
	public static Double[] vectorMultiplyBy(double lambda, Double[] vector) {
		for (int i = 0; i < VECTOR_LENGTH; i++) {
			vector[i] *= lambda;
		}
		return vector;
	}
}
