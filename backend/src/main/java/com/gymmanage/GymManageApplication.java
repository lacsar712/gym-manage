package com.gymmanage;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.gymmanage", annotationClass = org.apache.ibatis.annotations.Mapper.class)
public class GymManageApplication {
    public static void main(String[] args) {
        SpringApplication.run(GymManageApplication.class, args);
    }
}
