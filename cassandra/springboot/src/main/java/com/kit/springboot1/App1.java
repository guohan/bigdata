package com.kit.springboot1;


import org.springframework.boot.SpringApplication;  
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;  
import org.springframework.context.annotation.ComponentScan;  
import org.springframework.context.annotation.Configuration;  
  
/** 
 * 第二种方式

通过@Configuration+@ComponentScan开启注解扫描并自动注册相应的注解Bean
 */  
@Configuration  
@ComponentScan  
@EnableAutoConfiguration  
public class App1 {  
    public static void main(String[] args) {  
        SpringApplication.run(App1.class);  
    }  
}  
