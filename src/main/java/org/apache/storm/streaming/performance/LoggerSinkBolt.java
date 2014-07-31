package org.apache.storm.streaming.performance;

import java.util.Map;
import java.util.Random;

import org.apache.flink.streaming.util.PerformanceCounter;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

public class LoggerSinkBolt extends BaseRichBolt {
	private static final long serialVersionUID = 1L;
	
	private PerformanceCounter performanceCounter;
	
	private String counterPath;
	private String argString;

	public LoggerSinkBolt(String[] args, String counterPath) {
		this.counterPath = counterPath;
		this.argString = args[3];
		for(int i = 4; i < args.length; i++){
			argString += "_" + args[i];
		}
	}
	
	@Override
	public void prepare(Map map, TopologyContext context, OutputCollector collector) {
		Random rnd = new Random();
		this.performanceCounter = new PerformanceCounter("pc", 1000, 1000, 30000, counterPath
				+ "stormSink-" + argString + "-" + context.getThisTaskId() + ".csv");
	}

	@Override
	public void execute(Tuple tuple) {
		performanceCounter.count();
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
	}
}