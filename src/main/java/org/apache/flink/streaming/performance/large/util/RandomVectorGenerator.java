package org.apache.flink.streaming.performance.large.util;

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
	private int numVectors;
	private int vectorLength;
	private Random random;

	public RandomVectorGenerator(int numUsers, int numVectors, int vectorLength) {
		this.numUsers = numUsers;
		this.vectorLength = vectorLength;
		this.numVectors = numVectors;
		this.random = new Random();
	}

	public static void main(String[] args) {
		RandomVectorGenerator gen = new RandomVectorGenerator(1, 1, 128);
		gen.print("/home/hermann/performance/vector-small.txt");
	}

	public void print(String targetPath) {
		PrintWriter writer = null;
		try {
			int sign = 1;
			writer = new PrintWriter(targetPath);
			for (int i = 0; i < numVectors; i++) {
				writer.print(random.nextInt(numUsers) + "\t");
				for (int j = 0; j < vectorLength; j++) {
					sign = random.nextInt(2) * 2 - 1;
					String out = Double.toString(sign * random.nextDouble());
					writer.print(out + "\t");
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
		Integer user;
		String line;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(sourcePath));
			while ((line = reader.readLine()) != null) {
				String[] splitted = line.split("\t");
				user = Integer.parseInt(splitted[0]);
				Double[] vector = new Double[vectorLength];
				for (int i = 1; i < splitted.length; i++) {
					vector[i - 1] = Double.parseDouble(splitted[i]);
				}
				result.add(new Tuple2<Integer, Double[]>(user, vector));
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

		Double[] vector = new Double[vectorLength];
		Integer user;

		@Override
		public Tuple2<Integer, Double[]> map(String value) throws Exception {
			String[] splitted = value.split("\t");
			user = Integer.parseInt(splitted[0]);
			for (int i = 1; i < splitted.length; i++) {
				vector[i - 1] = Double.parseDouble(splitted[i]);
			}
			return new Tuple2<Integer, Double[]>(user, vector);
		}
	}

	public DataStream<Tuple2<Integer, Double[]>> getVectors(StreamExecutionEnvironment env,
			String sourcePath) {
		return env.readTextFile(sourcePath).map(new VectorMapper());
	}
}
