package com.daken;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@MapperScan("com.daken.mapper")
@EnableScheduling
@EnableSwagger2
public class DakenBlogApplication {

    public static void main(String[] args) {
        SpringApplication.run(DakenBlogApplication.class,args);
    }
}
