package com.daken;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.daken.mapper")
public class DakenAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(DakenAdminApplication.class,args);
    }
}
