package com.robinhowlett.handycapper.examples;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.robinhowlett.chartparser.ChartParser;
import com.robinhowlett.chartparser.charts.pdf.RaceResult;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;

public class PDFtoJSON {

    public static void main(String[] args) throws URISyntaxException, JsonProcessingException {
        File chart = Paths.get(PDFtoJSON.class.getClassLoader()
                .getResource("examples/ARP_2016-07-24_race-charts.pdf").toURI()).toFile();

        List<RaceResult> results = ChartParser.create().parse(chart);

        System.out.println(ChartParser.getObjectMapper()
                .writerWithDefaultPrettyPrinter().writeValueAsString(results));
    }
}
