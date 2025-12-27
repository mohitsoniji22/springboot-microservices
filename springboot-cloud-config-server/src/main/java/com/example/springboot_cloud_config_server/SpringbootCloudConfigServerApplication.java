package com.example.springboot_cloud_config_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.*;

@SpringBootApplication
@EnableConfigServer
public class SpringbootCloudConfigServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootCloudConfigServerApplication.class, args);
    }

}
