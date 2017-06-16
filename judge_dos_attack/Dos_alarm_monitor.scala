package com.huateng.parseAlarm.main

import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.mllib.clustering.KMeans
import org.apache.spark.mllib.linalg.Vectors

/**
  * Created by long on 2017/6/13.
  */
object Dos_alarm_monitor {
  def main(args: Array[String]) {
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF)

    val conf = new SparkConf()
      .setMaster("local[2]")
      .setAppName("Dos_alarm_monitor")
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .set("spark.kryoserializer.buffer", "64k")
    val sc = new SparkContext(conf)

    val monitor_txt = sc.textFile("C:\\Users\\long\\Desktop\\bigfile2.txt", 2)

    //2017-06-13 05:12:28.452910 IP 192.168.5.104.58684 > 93.184.216.34.80
    val server_ip = "93.184.216.34"
    val server_port = "80"

    //假设按照秒来统计，即每秒钟访问93.184.216.34.80 超过5000次即为doc攻击
    /**
      * 1.过滤长度不够的数据
      * 2.过滤出制定的服务器ip及端口
      * 3.转换成(yyyy-mm-dd hh:mm:ss ip,1)
      */
    val monitor_perminut_filter = monitor_txt
       .filter{line =>
         //这里过滤长度不够的数据 并且ip等于指定server_ip 并且 端口等于指定端口server_port
        line.length>=68 && line.substring(52,65).equals(server_ip) && line.substring(66,68).equals(server_port)
      }
      .map{line =>(line.substring(0,19)+ " " +line.substring(30,43),1)}

    //统计出每秒同一个ip访问93.184.216.34.80的次数
    val monitor_perminut_result = monitor_perminut_filter.reduceByKey(_+_)
    monitor_perminut_result.foreach(line => println(line))

    //结果过滤即可
    /**
    (2017-06-13 05:12:41 192.168.5.104,7153)
    (2017-06-13 05:12:31 192.168.5.104,10438)
    (2017-06-13 05:12:36 192.168.5.104,4128)
    (2017-06-13 05:12:40 192.168.5.104,9863)
    (2017-06-13 05:12:34 192.168.5.104,10345)
    (2017-06-13 05:12:28 192.168.5.104,5100)
    (2017-06-13 05:12:29 192.168.5.104,1777)
    (2017-06-13 05:12:35 192.168.5.104,6274)
    (2017-06-13 05:12:38 192.168.5.104,9975)
    (2017-06-13 05:12:39 192.168.5.104,9887)
    (2017-06-13 05:12:32 192.168.5.104,9615)
    (2017-06-13 05:12:33 192.168.5.104,9924)
    (2017-06-13 05:12:30 192.168.5.104,8940)
    **/
    }

}
