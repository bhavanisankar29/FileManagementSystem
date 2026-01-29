package com.application.filemanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class FilemanagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(FilemanagementApplication.class, args);
    }

}
