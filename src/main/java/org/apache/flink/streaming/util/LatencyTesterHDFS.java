package org.apache.flink.streaming.util;

import java.io.IOException;
import java.io.PrintWriter;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class LatencyTesterHDFS extends LatencyTester {

	public LatencyTesterHDFS(int intervalLength, String fileName) {
		super(intervalLength, fileName);
	}
	
	@Override
	public void writeLog() {
		try {
			PrintWriter out;
			out = new PrintWriter(FileSystem.get(new Configuration()).create(new Path(fileName)));
			out.print(toString());
			out.close();

		} catch (IOException e) {
			System.out.println("CSV output file not found");
		}
	}
}
