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

package org.apache.spark.streaming.performance.general;

import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;

public class SparkWordCountPairer implements PairFunction<String, String, Integer> {
	private static final long serialVersionUID = 1L;

	@Override
	public Tuple2<String, Integer> call(String s) {
		return new Tuple2<String, Integer>(s, 1);
	}
}