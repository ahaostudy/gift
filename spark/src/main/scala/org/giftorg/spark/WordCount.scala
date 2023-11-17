/**
 * Copyright 2023 GiftOrg Authors
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.giftorg.spark

import org.apache.spark.{SparkConf, SparkContext}

/**
 * WordCount 测试 Spark 运行
 */
object WordCount {
  def main(args: Array[String]): Unit = {
    val sparConf = new SparkConf()
      .setAppName("WordCount")
      .setMaster("local[*]")
    val sc: SparkContext = new SparkContext(sparConf)

    val inputRDD = sc.textFile("/test/word_count/text.txt")
    val resultRDD = inputRDD
      .flatMap(line => line.split("\\s+"))
      .map(word => (word, 1))
      .reduceByKey((tmp, item) => tmp + item)

    resultRDD.foreach(tuple => println(tuple))
    resultRDD.saveAsTextFile(s"/test/word_count/output_${System.currentTimeMillis()}")

    sc.stop()
  }
}