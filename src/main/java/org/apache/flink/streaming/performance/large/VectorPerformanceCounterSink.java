package org.apache.flink.streaming.performance.large;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Random;

import org.apache.flink.streaming.api.function.sink.SinkFunction;
import org.apache.flink.streaming.util.PerformanceCounter;

public class VectorPerformanceCounterSink implements SinkFunction<UserVector> {
	private static final long serialVersionUID = 1L;
		
	private PerformanceCounter pCounter;
	private String argString;
	private String csvPath;
	
	public VectorPerformanceCounterSink(String[] args, String csvPath){
		this.csvPath = csvPath;
		argString = args[6];
		for(int i = 7; i < args.length; i++){
			argString += "_" + args[i];
		}
	}
	
	
	@Override
	public void invoke(UserVector vector) {
		pCounter.count();
	}
	
	private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
		ois.defaultReadObject();
		
		Random rnd = new Random();
		String fileName;
		File csvFile;
		do {
			fileName = csvPath + "sink-" + argString + 
					"-" + String.valueOf(rnd.nextInt(10000000)) + ".csv";
			csvFile = new File(fileName);
		} while(csvFile.exists());
		
		pCounter = new PerformanceCounter("SplitterEmitCounter", 1000, 1000, 30000, fileName);
	}
}