package com.kit.hadoop.demo;

import java.io.IOException;  
import org.apache.hadoop.conf.Configuration;  
import org.apache.hadoop.conf.Configured;  
import org.apache.hadoop.fs.Path;  
import org.apache.hadoop.io.IntWritable;  
import org.apache.hadoop.io.LongWritable;  
import org.apache.hadoop.io.Text;  
import org.apache.hadoop.mapreduce.Job;  
import org.apache.hadoop.mapreduce.Mapper;  
import org.apache.hadoop.mapreduce.Reducer;  
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;  
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;  
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;  
import org.apache.hadoop.util.Tool;  
import org.apache.hadoop.util.ToolRunner;  

/**
 * 
 * @author guohan
 * @createTime 2017-06-13
 * @description judge dos attack
 * mapreduce code test
 * 
 * 
 * 2017-06-13 05:12:28.449650 IP 192.168.5.104.58659 > 93.184.216.34.80: Flags [S], seq 343512954:343513074, win 64, length 120: HTTP
2017-06-13 05:12:28.449657 IP 192.168.5.104.58660 > 93.184.216.34.80: Flags [S], seq 567511722:567511842, win 64, length 120: HTTP
2017-06-13 05:12:28.450005 IP 192.168.5.104.58661 > 93.184.216.34.80: Flags [S], seq 318952117:318952237, win 64, length 120: HTTP
2017-06-13 05:12:28.450014 IP 192.168.5.104.58662 > 93.184.216.34.80: Flags [S], seq 1334835934:1334836054, win 64, length 120: HTTP
2017-06-13 05:12:28.450022 IP 192.168.5.104.58663 > 93.184.216.34.80: Flags [S], seq 528356681:528356801, win 64, length 120: HTTP
 *
 */
/**
 * steps:image according per sencond to count,if in  one sencond ,more than 5000 ,we defined DOS attack
 * 1  filter the data,clean the lenght is short of 68
 * 2  filter the ip and port ,the acctack port
 * 3 transform the data to(time ip,1) 
 * 4 count per second acctac ip:port counts,
 * like below!
 * 5 at last, you need to judge the count with the 
 */
//
///**
//(2017-06-13 05:12:41 192.168.5.104,7153)
//(2017-06-13 05:12:31 192.168.5.104,10438)
//(2017-06-13 05:12:36 192.168.5.104,4128)
//(2017-06-13 05:12:40 192.168.5.104,9863)
//(2017-06-13 05:12:34 192.168.5.104,10345)
//(2017-06-13 05:12:28 192.168.5.104,5100)
//(2017-06-13 05:12:29 192.168.5.104,1777)
//(2017-06-13 05:12:35 192.168.5.104,6274)
//(2017-06-13 05:12:38 192.168.5.104,9975)
//(2017-06-13 05:12:39 192.168.5.104,9887)
//(2017-06-13 05:12:32 192.168.5.104,9615)
//(2017-06-13 05:12:33 192.168.5.104,9924)
//(2017-06-13 05:12:30 192.168.5.104,8940)
//**/
//}
public class Dos_alarm_monitor extends Configured implements Tool{  
    enum Counter{  
        LINESKIP,  
    }     
    static String server_ip = "93.184.216.34";
    static String server_port = "80";
    
    public static class Map extends Mapper<LongWritable,Text,Text,IntWritable>{  
        private static final IntWritable one = new IntWritable(1);   
        public void map(LongWritable key,Text value,Context context) throws IOException, InterruptedException{  
            String line = value.toString();  
            try{  
//            	line.length>=68 && line.substring(52,65).equals(server_ip) && line.substring(66,68).equals(server_port)
//            			line =>(line.substring(0,19)+ " " +line.substring(30,43),1)
            	if(line.length()<68 ||!line.substring(52,65).equals(server_ip)||!line.substring(66,68).equals(server_port)){
            		return;
            	}
                String time_ip = line.substring(0,19)+ " " +line.substring(30,43);  
                Text out = new Text(time_ip);  
                context.write(out,one);  
            }catch(java.lang.ArrayIndexOutOfBoundsException e){  
                context.getCounter(Counter.LINESKIP).increment(1);  
            }             
        }  
    }     
    //count the 
    public static class Reduce extends Reducer<Text,IntWritable,Text,IntWritable>{          
        public void reduce(Text key, Iterable<IntWritable> values,Context context)throws IOException{  
            int count =  0;    
            for(IntWritable v : values){    
                count = count + 1;    
            }    
            try {  
                context.write(key, new IntWritable(count));  
            } catch (InterruptedException e) {  
                 e.printStackTrace();  
            }             
        }         
    }     
    @Override  
    public int run(String[] args) throws Exception {  
        Configuration conf = getConf();  
        Job job = new Job(conf, "Dos_alarm_monitor");  
        job.setJarByClass(Dos_alarm_monitor.class);          
        FileInputFormat.addInputPath(job, new Path("hdfs://kit-b1:8020/user/discover/bigfile2.txt"));  
        FileOutputFormat.setOutputPath(job, new Path("hdfs://kit-b1:8020/user/discover/output20170614dos"));       
        job.setMapperClass(Map.class);  
        job.setReducerClass(Reduce.class);  
        job.setOutputFormatClass(TextOutputFormat.class);         
        //keep the same format with the output of Map and Reduce  
        job.setOutputKeyClass(Text.class);  
        job.setOutputValueClass(IntWritable.class);       
        job.waitForCompletion(true);  
        return job.isSuccessful()?0:1;  
    }     
    public static void main(String[] args)throws Exception{       
        int res = ToolRunner.run(new Configuration(), new Dos_alarm_monitor(),args);         
        System.exit(res);  
    }  
}  
