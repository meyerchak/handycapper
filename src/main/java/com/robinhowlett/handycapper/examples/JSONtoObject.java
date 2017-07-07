package com.robinhowlett.handycapper.examples;

import com.fasterxml.jackson.core.type.TypeReference;
import com.robinhowlett.chartparser.ChartParser;
import com.robinhowlett.chartparser.charts.pdf.RaceResult;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;

public class JSONtoObject {

    public static void main(String[] args) throws URISyntaxException, IOException {
        File jsonFile = Paths.get(JSONtoObject.class.getClassLoader()
                .getResource("examples/ARP_2016-07-24_results.json").toURI()).toFile();

        List<RaceResult> results = ChartParser.getObjectMapper().readValue(jsonFile,
                new TypeReference<List<RaceResult>>() {
                });

        System.out.println(results.size());
    }
}
