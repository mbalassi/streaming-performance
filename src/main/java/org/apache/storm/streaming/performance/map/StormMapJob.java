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
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import org.apache.flink.streaming.util.PerformanceCounter;

import java.util.Map;

public class StormMapJob {

    private static boolean runOnCluster;
    private static int sourceDop;
    private static int mapDop;
    private static int sinkDop;
    private static int workersNum;
    private static String counterPath;
    private static String argString;

    public static void main(String[] args) throws Exception {

        if (args.length != 6){
            System.out.println("USAGE: MapJob <executor> <source dop> <map dop> <sink dop> <workers num> <counter path>");
            return;
        } else {
            runOnCluster = args[0].equals("cluster");
            sourceDop = Integer.parseInt(args[1]);
            mapDop = Integer.parseInt(args[2]);
            sinkDop = Integer.parseInt(args[3]);
            workersNum = Integer.parseInt(args[4]);
            counterPath = args[5];
            argString = args[1];
            for (int i = 1; i < 4; i++) {
                argString += "_" + args[i];
            }
        }

        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("spout", new GenerateSpout(), sourceDop);
        builder.setBolt("map", new MapBolt(), mapDop)
                .localOrShuffleGrouping("spout"); //this is the closest grouping to our forward
        builder.setBolt("sink", new SinkBolt(), sinkDop)
                .localOrShuffleGrouping("map");

        Config conf = new Config();
        conf.setNumAckers(0);
        conf.setDebug(false);
        conf.setNumWorkers(workersNum);

        if (runOnCluster) {
            StormSubmitter.submitTopology("Storm Map", conf, builder.createTopology());
        } else {
            // running locally for 70 seconds

            conf.setMaxTaskParallelism(3);
            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("StormMap", conf, builder.createTopology());
            Thread.sleep(70000);

            cluster.shutdown();
        }
    }

    public static class GenerateSpout extends BaseRichSpout {

        private transient SpoutOutputCollector collector;
        private Values out = new Values(0);

        @Override
        public void declareOutputFields(OutputFieldsDeclarer declarer) {
            declarer.declare(new Fields("int"));
        }

        @Override
        public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
            collector = spoutOutputCollector;
        }

        @Override
        public void nextTuple() {
            collector.emit(out);
        }
    }

    public static class MapBolt extends BaseRichBolt {

        private transient OutputCollector collector;
        private Values out = new Values(0);

        @Override
        public void declareOutputFields(OutputFieldsDeclarer declarer) {
            declarer.declare(new Fields("int"));
        }

        @Override
        public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
            collector = outputCollector;
        }

        @Override
        public void execute(Tuple tuple) {
            collector.emit(out);
        }
    }

    public static class SinkBolt extends BaseRichBolt {

        private transient PerformanceCounter pCounter;

        @Override
        public void declareOutputFields(OutputFieldsDeclarer declarer) {
            declarer.declare(new Fields("int"));
        }

        @Override
        public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
            pCounter = new PerformanceCounter("pc", 1000, 1000, 30000,
                    counterPath + "storm-" + argString + "-" + topologyContext.getThisTaskId() + ".csv");
        }

        @Override
        public void execute(Tuple tuple) {
            pCounter.count();
        }
    }
}
