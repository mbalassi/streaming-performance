package org.apache.storm.streaming.performance.legacy.large;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import org.apache.flink.streaming.performance.legacy.large.util.VectorParser;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

public class CachedVectorSpout extends BaseRichSpout {
	private static final long serialVersionUID = 1L;

	SpoutOutputCollector collector;

	private String filePath;
	private transient ArrayList<ArrayList<Object>> cache;
	private Values out;
	
	public CachedVectorSpout(String filePath) {
		this.filePath = filePath;
	}
	
	@Override
	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		this.collector = collector;
		cache = VectorParser.getCollection(filePath);
		out = new Values(0, new Double[0]);
	}

	@Override
	public void nextTuple() {
		for (ArrayList<Object> tuple : cache) {
			out.set(0, (Integer) tuple.get(0));
			Double[] vector = (Double[]) tuple.get(1);
			out.set(1, Arrays.copyOf(vector, vector.length));
			collector.emit(out);
		}
	}

	@Override
	public void ack(Object id) {
	}

	@Override
	public void fail(Object id) {
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("user", "vector"));
	}
}
