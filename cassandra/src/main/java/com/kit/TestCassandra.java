package com.kit;

import com.datastax.driver.core.*;

/**
 * Created by Administrator on 2017/4/1 0001.
 *
 *
 * http://stackoverflow.com/questions/37588733/datastax-cassandra-driver-throwing-codecnotfoundexception
 *
 * Exception in thread "main" com.datastax.driver.core.exceptions.CodecNotFoundException: Codec not found for requested operation:
 */

public class TestCassandra {

    public Cluster cluster;
    public Session session;

    public void connect() {
        PoolingOptions poolingOptions = new PoolingOptions();
        poolingOptions.setMaxRequestsPerConnection(HostDistance.LOCAL, 32);
        poolingOptions.setCoreConnectionsPerHost(HostDistance.LOCAL, 1).setMaxConnectionsPerHost(HostDistance.LOCAL, 4);
        cluster = Cluster.builder().addContactPoints("192.168.56.101").withPort(9042).withCredentials("cassandra", "cassandra").withPoolingOptions(poolingOptions).build();
        session = cluster.connect();
    }


    /**
     * 创建键空间
     */
    public void createKeyspace() {
        // 单数据中心 复制策略 ：1
        String cql = "CREATE KEYSPACE if not exists mydb WITH replication = {'class': 'SimpleStrategy', 'replication_factor': '1'}";
        session.execute(cql);
    }

    /**
     * 创建表
     */
    public void createTable() {
        // a,b为复合主键 a：分区键，b：集群键
        String cql = "CREATE TABLE if not exists mydb.test (a text,b int,c text,d int,PRIMARY KEY (a, b))";
        session.execute(cql);
    }

    public void insert()
     {
            String cql = "INSERT INTO mydb.test (a , b , c , d ) VALUES ( 'a2','4,'c2',6);";
            session.execute(cql);
        }

    public void update(){
        String cql = "UPDATE mydb.test SET d = 12345  ,c='c3' WHERE a='a2' and b=2;";// a,b是复合主键 所以条件都要带上，少一个都会报错，而且update不能修改主键的值，这应该和cassandra的存储方式有关
        String cql2 = "INSERT INTO mydb.test (a,b,c) VALUES ( 'aa',2,'c234');";// 也可以这样 cassandra插入的数据如果主键已经存在，其实就是更新操作 当未操作的那个字段有值 而执行的这条sql未操作那个字段 则保留原来的值
        session.execute(cql2);
    }
    public void delete(){
//        String sql ="delete d from mydb.test where  a='a2' and b=2;";
        String sql ="delete  from mydb.test where  a='a2';";
        session.execute(sql);
    }

    public void getDataList(){
        String cql ="select * from mydb.test";
        ResultSet resultSet = session.execute(cql);
         System.out.print("这里是字段名：");
          for (ColumnDefinitions.Definition definition : resultSet.getColumnDefinitions())
                {
                     System.out.print(definition.getName() + " ");
              }
            System.out.println();
            System.out.println(String.format("%s\t%s\t%s\t%s\t\n%s", "a", "b", "c", "d",
                           "--------------------------------------------------------------------------"));
            for (Row row : resultSet)
               {
                    System.out.println(String.format("%s\t%d\t%s\t%d\t", row.getString("a"), row.getInt("b"),
                                   row.getString("c"), row.getInt("d")));
              }
//        session.close();
    }

    public void getDataList1(){
        String cql ="select * from TrafficKeySpace.Window_Traffic";
        ResultSet resultSet = session.execute(cql);
        System.out.print("这里是字段名：");
        for (ColumnDefinitions.Definition definition : resultSet.getColumnDefinitions())
        {
            System.out.print(definition.getName() + " ");
        }
        System.out.println();
        System.out.println(String.format("%s\t%s\t%s\t%s\t\n%s", "routeid", "recorddate", "vehicletype", "timestamp",
                "--------------------------------------------------------------------------"));
        for (Row row : resultSet)
        {
            System.out.println(String.format("%s\t%s\t%s\t%s\t", row.getString("routeid"), row.getString("recorddate"),
                    row.getString("vehicletype"), row.getDate("timestamp")));
        }
//        session.close();
    }
    public static void main(String[] args) {

        TestCassandra test =new TestCassandra();
        test.connect();
//        test.createKeyspace();
//        System.out.print("create space success!");
//        test.createTable();
//        System.out.print("create table success!");
//        test.insert();
        System.out.print("insert values success!");

//        test.update();
        System.out.print("update values success!");
//        test.delete();
        System.out.print("delete d success!");

        test.getDataList();
    }
}