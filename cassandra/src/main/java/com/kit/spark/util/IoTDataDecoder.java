package com.kit.spark.util;

import com.fasterxml.jackson.databind.ObjectMapper;


import com.kit.spark.vo.IoTData;
import kafka.serializer.Decoder;
import kafka.utils.VerifiableProperties;

/**
 * Class to deserialize JSON string to IoTData java object
 * 
 * @author gh
 * @createtime 2017-04-01
 * @desc 解析json数据
 *
 */
public class IoTDataDecoder implements Decoder<IoTData> {
	
	private static ObjectMapper objectMapper = new ObjectMapper();
	
	public IoTDataDecoder(VerifiableProperties verifiableProperties) {

    }
	public IoTData fromBytes(byte[] bytes) {
		try {
			return objectMapper.readValue(bytes, IoTData.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
