/**
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.flink.streaming.performance.map;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.function.sink.RichSinkFunction;
import org.apache.flink.streaming.api.function.source.ParallelSourceFunction;
import org.apache.flink.streaming.util.PerformanceCounter;
import org.apache.flink.util.Collector;

public class MapJob {

    private static int sourceDop;
    private static int mapDop;
    private static int sinkDop;
    private static String counterPath;

    public static void main(String[] args) {

        if (args.length != 4){
            System.out.println("USAGE: MapJob <source dop> <map dop> <sink dop> <counter path>");
            return;
        } else {
            sourceDop = Integer.parseInt(args[0]);
            mapDop = Integer.parseInt(args[1]);
            sinkDop = Integer.parseInt(args[2]);
            counterPath = args[3];
        }

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        DataStream<Integer> dataStream = env.addSource(new ParallelSourceFunction<Integer>() {
            @Override
            public void run(Collector<Integer> collector) throws Exception {
                while (true) {
                    collector.collect(0);
                }
            }

            @Override
            public void cancel() {
            }
        }).setParallelism(sourceDop)
        .map(new MapFunction<Integer, Integer>() {
            @Override
            public Integer map(Integer val) throws Exception {
                return val;
            }
        }).setParallelism(mapDop)
        .addSink(new RichSinkFunction<Integer>() {

            private transient PerformanceCounter pCounter;

            public void open(Configuration parameters) throws Exception {
                pCounter = new PerformanceCounter("pc", 1000, 1000, 30000,
                        counterPath + getRuntimeContext().getIndexOfThisSubtask() + ".csv");
            }

            @Override
            public void invoke(Integer aInteger) throws Exception {
                pCounter.count();
            }
        }).setParallelism(sinkDop);

        try {
            env.execute("Streaming MapJob");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
