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