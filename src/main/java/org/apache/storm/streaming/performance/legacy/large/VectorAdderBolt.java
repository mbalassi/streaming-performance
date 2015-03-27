package org.apache.storm.streaming.performance.legacy.large;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.flink.streaming.performance.legacy.large.util.VectorParser;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class VectorAdderBolt extends BaseRichBolt {
	private static final long serialVersionUID = 1L;

	OutputCollector collector;
	
	private Map<Integer, Double[]> userVectors = new HashMap<Integer, Double[]>();
	private Integer user  = 0;
	private Double[] vector = new Double[0];
	private Values outRecord = new Values(0, new Double[0]);
	
	private int counter = 0;
	
	@Override
	public void prepare(Map map, TopologyContext context, OutputCollector collector) {
		this.collector = collector;
	}

	@Override
	public void execute(Tuple tuple) {
		if (counter++ > VectorParser.VECTOR_REFRESH_NUMBER) {
			userVectors.clear();
			counter = 0;
		}
		
		user = tuple.getInteger(0);
		Double[] newVector = (Double[]) tuple.getValue(1);
		
		vector = userVectors.get(user);
		if (vector == null) {
			vector = newVector;
		} else {
			vector = VectorParser.vectorAdd(vector, newVector);
		}
		
		userVectors.put(user, vector);
		
		outRecord.set(0, user);
		outRecord.set(1, Arrays.copyOf(vector, vector.length));

		collector.emit(outRecord);
	}

	
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("user", "vector"));
	}
}
