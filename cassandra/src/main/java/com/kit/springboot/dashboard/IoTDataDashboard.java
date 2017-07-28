package com.kit.springboot.dashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Spring boot application class for Dashboard.
 * 
 * @author gh
 * @createTime 2017-04-05
 * @description  工程启动入口
 *
 */
@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = {"com.kit.springboot.dashboard", "com.kit.springboot.dao"})
public class IoTDataDashboard {
	  public static void main(String[] args) {
	        SpringApplication.run(IoTDataDashboard.class, args);
	    }
	}

