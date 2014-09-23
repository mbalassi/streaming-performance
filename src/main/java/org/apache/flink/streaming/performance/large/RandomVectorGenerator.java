package org.apache.flink.streaming.performance.large;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class RandomVectorGenerator {
	private int numUsers;
	private int vectorLength;

	public RandomVectorGenerator(int numUsers, int vectorLength) {
		this.numUsers = numUsers;
		this.vectorLength = vectorLength;
	}

	public void print(String targetPath) {
		PrintWriter writer = null;
		Random r = new Random();
		try {
			writer = new PrintWriter(targetPath);
			for (int i = 0; i < numUsers; i++) {
				for (int j = 0; j < vectorLength; j++) {
					writer.print(r.nextDouble() + "\t");
				}
				writer.println();
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public ArrayList<Tuple2<Integer, Double[]>> getCollection(String sourcePath) {

		ArrayList<Tuple2<Integer, Double[]>> result = new ArrayList<Tuple2<Integer, Double[]>>();
		Integer counter = 0;
		String line;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(sourcePath));
			while ((line = reader.readLine()) != null) {
				String[] splitted = line.split("\t");
				Double[] vector = new Double[vectorLength];
				for (int i = 0; i < vector.length; i++) {
					vector[i] = Double.parseDouble(splitted[i]);
				}
				result.add(new Tuple2<Integer, Double[]>(counter, vector));
				counter++;
			}
		} catch (Exception e) {

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

	private class VectorMapper implements MapFunction<String, Tuple2<Integer, Double[]>> {
		private static final long serialVersionUID = 1L;

		Integer counter = -1;
		Double[] vector = new Double[vectorLength];

		@Override
		public Tuple2<Integer, Double[]> map(String value) throws Exception {
			counter++;
			String[] splitted = value.split("\t");
			for (int i = 0; i < vector.length; i++) {
				vector[i] = Double.parseDouble(splitted[i]);
			}
			return new Tuple2<Integer, Double[]>(counter, vector);
		}
	}

	public DataStream<Tuple2<Integer, Double[]>> getVectors(StreamExecutionEnvironment env,
			String sourcePath) {
		return env.readTextStream(sourcePath).map(new VectorMapper());
	}
}
