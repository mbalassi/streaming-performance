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

package org.apache.storm.streaming.performance.map;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import org.apache.flink.streaming.util.PerformanceCounter;

import java.util.Map;

public class StormSpoutJob {

    private static boolean runOnCluster;
    private static int sourceDop;
    private static int workersNum;
    private static String counterPath;
    private static String argString;

    public static void main(String[] args) throws Exception {

        if (args.length != 4){
            System.out.println("USAGE: MapJob <executor> <source dop> <workers num> <counter path>");
            return;
        } else {
            runOnCluster = args[0].equals("cluster");
            sourceDop = Integer.parseInt(args[1]);
            workersNum = Integer.parseInt(args[2]);
            counterPath = args[3];

            argString = args[1];
            for (int i = 1; i < 2; i++) {
                argString += "_" + args[i];
            }
        }

        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("spout", new SpoutSink(), sourceDop);

        Config conf = new Config();
        conf.setNumAckers(0);
        conf.setDebug(false);
        conf.setNumWorkers(workersNum);

        if (runOnCluster) {
            StormSubmitter.submitTopology("StormSpout", conf, builder.createTopology());
        } else {
            // running locally for 70 seconds

            conf.setMaxTaskParallelism(3);
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("StormMap", conf, builder.createTopology());
            Thread.sleep(70000);

            cluster.shutdown();
        }
    }

    public static class SpoutSink extends BaseRichSpout {

        private transient PerformanceCounter pCounter;

        @Override
        public void declareOutputFields(OutputFieldsDeclarer declarer) {
            declarer.declare(new Fields("int"));
        }

        @Override
        public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
            pCounter = new PerformanceCounter("pc", 1000, 1000, 30000,
                    counterPath + "storm-" + argString + "-" + topologyContext.getThisTaskId() + ".csv");
        }

        @Override
        public void nextTuple() {
            pCounter.count();
        }
    }
}
