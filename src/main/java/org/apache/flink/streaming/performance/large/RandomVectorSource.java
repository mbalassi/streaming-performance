package org.apache.flink.streaming.performance.large;

import java.util.Random;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.function.source.RichSourceFunction;
import org.apache.flink.util.Collector;

public class RandomVectorSource extends RichSourceFunction<UserVector> {
	private static final long serialVersionUID = 1L;
	
	private int numberOfUsers;
	private int sizeOfVector;
	
	private transient Random random;
	
	public RandomVectorSource(int numberOfUsers, int sizeOfVector) {
		this.numberOfUsers = numberOfUsers;
		this.sizeOfVector = sizeOfVector;
	}
	
	@Override
	public void open(Configuration parameters) throws Exception {
		random = new Random();
	}
	
	long timer = System.currentTimeMillis();
	
	@Override
	public void invoke(Collector<UserVector> out) throws Exception {
		UserVector value = new UserVector();
		Double[] vector = new Double[sizeOfVector];
		for (int j = 0; j < 10; j++) {
			for (int i = 0; i < sizeOfVector; i++) {
				vector[i] = random.nextDouble();
			}
			
			value.setVector(vector);
			value.setUser(random.nextInt(numberOfUsers));
			out.collect(value);
			
			long current = System.currentTimeMillis();
			System.out.println(current - timer);
			timer = current;
		}
	}
}