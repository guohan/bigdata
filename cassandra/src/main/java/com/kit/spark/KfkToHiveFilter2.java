package com.kit.spark;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

import kafka.javaapi.producer.Producer;
import scala.Tuple2;


import kafka.auth.AuthenticationManager;
import kafka.serializer.StringDecoder;

import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.*;
import org.apache.spark.streaming.api.java.*;
import org.apache.spark.streaming.kafka.KafkaUtils;
import org.apache.spark.streaming.Durations;

/**
 * @author gh
 * @createTime 2017/01/10
 * @description 通过spark streaming 读取kafka消息后入库到hive数据库 匹配规则 得到结果入库
 */

public final class KfkToHiveFilter2 {
	private static final Pattern SPACE = Pattern.compile(" ");
	private static Logger logger = Logger.getLogger(KfkToHiveFilter2.class);
	private static final JavaSparkContext sc = new JavaSparkContext(
			new SparkConf().setMaster("local[*]")
					.setAppName("kfkToHiveFilter"));
//	private static final SQLContext sqlContext = new SQLContext(sc);

	public static void main(String[] args) throws IOException {
		
		String brokers = "kit-b1:9092,kit-b2:9092,kit-b3:9092";// kafka集群配置
		final String topics = "system,Disconnector,Breaker,OGG_TOPIC,oggtopic,topic1019001";// 需要处理的对应主题
		InputStream is = KfkToHiveFilter2.class.getClassLoader()
				.getResourceAsStream("kfk-test.properties");
		Properties p = new Properties();
		// 加载资源
		p.load(is);
		AuthenticationManager.setAuthMethod("kerberos");
		AuthenticationManager.login("kitdev",
				Producer.class.getClassLoader().getResource("kitdev.keytab").getPath()
				// p.getProperty("kafka_key_path").trim()
//				"/home/kitdev/kitdev.keytab"
		);
		JavaStreamingContext jssc = new JavaStreamingContext(sc,
				Durations.seconds(1));
		logger.debug("加载上下文成功！");
		HashSet<String> topicsSet = new HashSet<String>(
				Arrays.asList(topics.split(",")));
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		HashMap<String, String> kafkaParams = new HashMap<String, String>();
		kafkaParams.put("metadata.broker.list", brokers);
		// Create direct kafka stream with brokers and topics
		JavaPairInputDStream<String, String> messages = KafkaUtils
				.createDirectStream(jssc, String.class, String.class,
						StringDecoder.class, StringDecoder.class, kafkaParams,
						topicsSet);
		logger.debug("加载kafka配置成功！");
		/**
		 * 
		 * 这里采用foreacherdd 遍历kafka数据
		 */
		messages.foreachRDD(new Function<JavaPairRDD<String, String>, Void>() {

			@Override
			public Void call(JavaPairRDD<String, String> rdd) throws Exception {

				rdd.foreach(new VoidFunction<Tuple2<String, String>>() {
					@Override
					public void call(Tuple2<String, String> tuple)
							throws Exception {
						logger.debug("ogg mes" + tuple._2());
						String[] kafka = SPACE.split(tuple._2());
						String json = tuple._2();
						// pareDataToHive(json);
						long beginTime = System.currentTimeMillis();
						checkData(json);
						long endTime = System.currentTimeMillis();
						logger.debug("共计花费时间 多少毫秒" + (endTime - beginTime));
						logger.debug("the foreach kafka data is:" + json);
					}
				});
				return null;
			}
		});
		jssc.start();
		jssc.awaitTermination();
	}
	
	/**
	 * 匹配规则，校验sql，生成结果
	 * @param kafka
	 * @throws SQLException
	 */
		private static void checkData(String kafka) throws SQLException {

				
		}
	
				


}
