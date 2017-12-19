package io.transwarp.decoder.kit;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import kafka.utils.VerifiableProperties;
import io.transwarp.streaming.sql.api.decoder.ArrayDecoder;
/**
 * 
 * @updater guohan
 * @createTime 2017-02-24
 *处理json数据，生成对应的stream结构格式数据
 */
public class KitCheckDecoder extends ArrayDecoder<byte[]> {

	private final static String FIELD_SEP = ";";
	private static ObjectMapper objectMapper;
	private static final Logger logger = Logger.getLogger(KitCheckDecoder.class);

	private static String keyTable = "tablekey";

	private static Properties properties = new Properties();
	private static DataSource dataSource;
	// 加载DBCP配置文件
	static {
		// try{
		// FileInputStream is = new FileInputStream("dbcp.properties");
		// properties.load(is);
		// }catch(IOException e){
		// e.printStackTrace();
		// }

		properties.setProperty("url", "jdbc:mysql://172.16.19.104:3306/test");
		properties.setProperty("username", "root");
		properties.setProperty("password", "123456");
		properties.setProperty("initialSize", "1");
		properties.setProperty("maxTotal", "4");
		properties.setProperty("maxIdle", "10");
		properties.setProperty("minIdle", "5");
		properties.setProperty("maxWaitMillis", "1000");
		properties.setProperty("removeAbandonedOnMaintenance", "true");
		properties.setProperty("removeAbandonedOnBorrow", "true");
		properties.setProperty("removeAbandonedTimeout", "1");

		try {
			dataSource = BasicDataSourceFactory.createDataSource(properties);
		} catch (Exception e) {
			logger.debug("");
			e.printStackTrace();
		}

	}

	// 从连接池中获取一个连接
	public static Connection getConnection() {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return connection;
	}

	// 在构造函数中进行一些初始化操作
	public KitCheckDecoder(VerifiableProperties props) {
		super(props);
		// TODO Auto-generated constructor stub
		objectMapper = new ObjectMapper();
	}
/**
 * 转换时间类型
 * @param timestamp
 * @return
 */
	private long getTimeStamp(String timestamp) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss.SSS");
		Date date = null;
		try {
			date = simpleDateFormat
					.parse(timestamp.replace('T', ' ').substring(0, 23));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (date == null)
			return System.currentTimeMillis();
		return date.getTime();
	}

	/**
	 * 根据表名获取主键值
	 * @param tablename
	 * @return
	 */
	private String[] getKeyColumns(String tablename) {
		String columns = new String();
		String sql = "select keyColumns from " + keyTable
				+ " where tablename = '" + tablename + "'";
		Connection conn = getConnection();
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = null;
			rs = stmt.executeQuery(sql);

			if (rs.next())
				columns = rs.getString(1);

			rs.close();
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return columns.split(",");

	}
	
	
	/**
	 * 封装集合
	 * 
	 * 这里需要对kafka生成的json信息做处理，
	 * insert update 都含有after 而delete包含的是before字段
	 */

	@SuppressWarnings("unchecked")
	@Override
	public Object arrayFromBytes(byte[] arg0) {
		// TODO Auto-generated method stub
		String json = new String(arg0);

		json = json.replace(FIELD_SEP, "\\。");// 避免字段里面出现分隔符，造成同一个字段被切分

		Map<String, Object> map;
		Map<String, Object> column_map;
		String tableName = null;
		String operation;

		byte[][] result = null;

		try {
			map = objectMapper.readValue(json, Map.class);

			operation = map.get("op_type").toString();
			tableName = map.get("table").toString();
			column_map = (Map<String, Object>) map.get("after");
//			column_map = (Map<String, Object>) map.get("before"); //op_type:delete 

			String[] keyColumns = getKeyColumns(tableName);
			String key = new String();
			for (String col : keyColumns)
				key = key + column_map.get(col);

			long timestamp = getTimeStamp(map.get("current_ts").toString());
			result = new byte[column_map.size()][];
			int i = 0;

			for (Map.Entry<String, Object> m : column_map.entrySet()) {
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append(operation).append(FIELD_SEP);
				stringBuilder.append(tableName).append(FIELD_SEP);
				stringBuilder.append(m.getKey()).append(FIELD_SEP);
				stringBuilder.append(m.getValue()).append(FIELD_SEP);
				stringBuilder.append(key).append(FIELD_SEP);
				stringBuilder.append(timestamp);
				result[i++] = stringBuilder.toString().getBytes();
			}
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	public static void main(String[] args) {

	}
}
