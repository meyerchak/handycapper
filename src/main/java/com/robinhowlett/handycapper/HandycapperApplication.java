package com.robinhowlett.handycapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.robinhowlett.chartparser.ChartParser;
import com.robinhowlett.chartparser.charts.pdf.RaceResult;
import com.robinhowlett.handycapper.examples.JSONtoObject;
import com.robinhowlett.handycapper.storage.StorageProperties;
import com.robinhowlett.handycapper.storage.StorageService;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class HandycapperApplication {

    public static void main(String[] args) throws URISyntaxException, IOException {
        SpringApplication.run(HandycapperApplication.class, args);
    }

    @Bean
    public ChartParser chartParser() {
        return ChartParser.create();
    }

    @Bean
    CommandLineRunner init(StorageService storageService) {
        return (args) -> {
            storageService.deleteAll();
            storageService.init();
        };
    }

}
