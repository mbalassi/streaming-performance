package org.apache.storm.streaming.performance.legacy.large;

import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;
import cern.colt.Arrays;

public class PrintSinkBolt extends BaseRichBolt {
	private static final long serialVersionUID = 1L;

	OutputCollector collector;
	
	@Override
	public void prepare(Map map, TopologyContext context, OutputCollector collector) {
		this.collector = collector;
	}

	@Override
	public void execute(Tuple tuple) {
		System.out.println(tuple.getValues().get(0) + " " + Arrays.toString((Double[]) tuple.getValues().get(1)));
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
	}
}
