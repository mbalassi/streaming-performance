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

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class MapJob {

    private static int sourceDop;
    private static int mapDop;
    private static int sinkDop;
    private static long bufferTimeout;
    private static boolean chainingEnabled;
    private static boolean shufflingEnabled;
    private static String counterPath;
    private static String argString;

    public static void main(String[] args) {

        if (args.length != 7){
            System.out.println("USAGE: MapJob <source dop> <map dop> <sink dop> <buffer timeout> <chaining> <shuffle> <counter path>");
            return;
        } else {
            sourceDop = Integer.parseInt(args[0]);
            mapDop = Integer.parseInt(args[1]);
            sinkDop = Integer.parseInt(args[2]);
            bufferTimeout = Long.parseLong(args[3]);
            chainingEnabled = args[4].equals("true");
            shufflingEnabled = args[5].equals("true");
            counterPath = args[6];

            argString = args[0];
            for (int i = 1; i < 5; i++) {
                argString += "_" + args[i];
            }
        }

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.getStreamGraph().setChaining(chainingEnabled);
        env.setBufferTimeout(bufferTimeout);

        DataStream<Integer> src = env.addSource(new GenerateSource()).setParallelism(sourceDop);

        if (shufflingEnabled) {
            src = src.shuffle();
        }

        DataStream<Integer> dataStream = src
        .map(new IdentityMap()).setParallelism(mapDop)
        .addSink(new PerformanceSink(counterPath, argString)).setParallelism(sinkDop);

        try {
            env.execute("Streaming MapJob");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
