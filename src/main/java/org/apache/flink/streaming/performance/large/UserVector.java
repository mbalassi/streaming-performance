package org.apache.flink.streaming.performance.large;

import org.apache.flink.api.java.tuple.Tuple2;

public class UserVector extends Tuple2<Integer, Double[]> {
	private static final long serialVersionUID = 1L;
	
	public Integer getUser() {
		return f0;
	}
	
	public void setUser(Integer user) {
		this.f0 = user;
	}
	

	public Double[] getVector() {
		return f1;
	}
	
	public void setVector(Double[] vector) {
		this.f1 = vector;
	}
}