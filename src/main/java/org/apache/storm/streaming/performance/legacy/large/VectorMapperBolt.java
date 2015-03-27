package org.apache.storm.streaming.performance.legacy.large;

import java.util.Map;

import org.apache.flink.streaming.performance.legacy.large.util.VectorParser;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class VectorMapperBolt extends BaseRichBolt {
	private static final long serialVersionUID = 1L;

	OutputCollector collector;
	
	private Values outRecord = new Values(0, new Double[0]);
		
	@Override
	public void prepare(Map map, TopologyContext context, OutputCollector collector) {
		this.collector = collector;
	}

	@Override
	public void execute(Tuple tuple) {
		Double[] vector = (Double[]) tuple.getValue(1);
		Double[] multiplied = VectorParser.vectorMultiplyBy(VectorParser.LAMBDA, vector);
		
		outRecord.set(0, tuple.getInteger(0));
		outRecord.set(1, multiplied);

		collector.emit(outRecord);
	}

	
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("user", "vector"));
	}
}
