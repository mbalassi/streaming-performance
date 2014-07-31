package org.apache.storm.streaming.performance;

import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class WordCountSplitterBolt extends BaseRichBolt {
	private static final long serialVersionUID = 1L;

	OutputCollector _collector;
	
	private Values outRecord = new Values("");
	
	@Override
	public void prepare(Map map, TopologyContext context, OutputCollector collector) {
		_collector = collector;
	}

	@Override
	public void execute(Tuple tuple) {
		for (String word : tuple.getString(0).split(" ")) {
			outRecord.set(0, word);
			_collector.emit(outRecord);
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("word"));
	}
}