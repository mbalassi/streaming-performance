package org.apache.flink.streaming.util;

import java.io.IOException;
import java.io.PrintWriter;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class PerformanceCounterHDFS extends PerformanceCounter {
	private static final long serialVersionUID = 1L;

	public PerformanceCounterHDFS(String name, int counterLength, int countInterval, long dumpInterval, String fname) {
		super(name, counterLength, countInterval, dumpInterval, fname);
	}

	public PerformanceCounterHDFS(String name, int counterLength, int countInterval, String fname) {
		super(name, counterLength, countInterval, fname);
	}
	
	public PerformanceCounterHDFS(String name, String fname) {
		super(name, fname);
	}
	
	@Override
	public void writeCSV() {
		try {
			PrintWriter out;
			if(firstWrite) {
				out = new PrintWriter(FileSystem.get(new Configuration()).create(new Path(fname)));
			} else {
				out = new PrintWriter(FileSystem.get(new Configuration()).append(new Path(fname)));
			}
			out.print(toString());
			out.close();

		} catch (IOException e) {
			System.out.println("CSV output file not found");
		}
	}
}
