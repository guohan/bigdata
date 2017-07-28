package com.kit.kafka.ws;

import java.util.*;

import java.text.SimpleDateFormat;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public class TestProducer {
	public static void main(String[] args) {
		long events = 100000L;
		Random rnd = new Random();

		Properties props = new Properties();

		// AuthenticationManager.setAuthMethod("kerberos");
		// AuthenticationManager.login("kafka@TDH", "D:\\kit\\kafka.keytab");
		//
		// //设置broker连接信息:ip:port
		props.put("metadata.broker.list", "kit-b1:9092");
		// props.put("metadata.broker.list", "192.168.56.101:9092");
		// 设置消息系列化使用的类
		props.put("serializer.class", "kafka.serializer.StringEncoder"); // 默认字符串编码消息
		// props.put("partitioner.class", "example.producer.SimplePartitioner");
		// request.required.acks 默认值：0
		// 用来控制一个produce请求怎样才能算完成，准确的说，是有多少broker必须已经提交数据到log文件，并向leader发送ack，可以设置如下的值：
		// 0，意味着producer永远不会等待一个来自broker的ack，这就是0.7版本的行为。这个选项提供了最低的延迟，但是持久化的保证是最弱的，当server挂掉的时候会丢失一些数据。
		// 1，意味着在leader
		// replica已经接收到数据后，producer会得到一个ack。这个选项提供了更好的持久性，因为在server确认请求成功处理后，client才会返回。如果刚写到leader上，还没来得及复制leader就挂了，那么消息才可能会丢失。
		// -1，意味着在所有的ISR都接收到数据后，producer才得到一个ack。这个选项提供了最好的持久性，只要还有一个replica存活，那么数据就不会丢失。
		props.put("request.required.acks", "1");

		ProducerConfig config = new ProducerConfig(props);

		Producer<String, String> producer = new Producer<String, String>(config);

		for (long nEvents = 0; nEvents < events; nEvents++) {
			Date runtime = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String now = dateFormat.format(runtime);
			String ip = "192.168.2." + rnd.nextInt(255);
			String msg =generatorData();

			KeyedMessage<String, String> data = new KeyedMessage<String, String>("kafka-websocket", "thekey", msg);
			producer.send(data);
			System.out.println(msg);
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		producer.close();
	}

	private static String generatorData() {
		String[] task_instance_id = { "201706160004", "172151889", "172359631", "172262358", "202501281149", "172217373","172172685","172337807" };
		String[] depart_id = { "080011", "080018", "080013", "080006", "080005", "080004" };
		String[] color = { "red", "green", "yellow" };
		String[] current_flow = { "受理诉求", "诉求审核", "业务处理","归档" };
		String[] workorder_id = { "201706160004", "1111111115240920", "1111111115371714","1111111115311190","1111111115259743","202501281149","1111111115357956" ,"1111111115284597"};
		String[] business_classifyid = { "01", "02", "03", "04", "05", "06", "07", "08", "09", "99" };

		int index_instance_id=(int)(Math.random()*task_instance_id.length);
    	String instance_id = task_instance_id[index_instance_id];
    	
    	int index_depart_id=(int)(Math.random()*depart_id.length);
    	String did = depart_id[index_depart_id];
    	
    	int index_color=(int)(Math.random()*color.length);
    	String cid = color[index_color];
    	
    	int index_current_flow=(int)(Math.random()*current_flow.length);
    	String fid = task_instance_id[index_current_flow];
    	
    	int index_workorder_id=(int)(Math.random()*workorder_id.length);
    	String wid = workorder_id[index_workorder_id];
    	
    	int index_business_classifyid=(int)(Math.random()*business_classifyid.length);
    	String bid = business_classifyid[index_business_classifyid];
		
    String string = "[{ workorder_id: "+wid+", color :"+cid+", current_flow :"+fid+", depart_id :"+did+", business_classifyid :"+bid+", task_instance_id :"+instance_id+" }]";
		return string;
	}
}
