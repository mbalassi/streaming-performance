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

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import org.apache.flink.streaming.util.PerformanceCounter;

import java.util.Map;

public class SinkBolt extends BaseRichBolt {

    private String counterPath;
    private String argString;

    public SinkBolt(String counterPath, String argString){
        this.counterPath = counterPath;
        this.argString = argString;
    }

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
