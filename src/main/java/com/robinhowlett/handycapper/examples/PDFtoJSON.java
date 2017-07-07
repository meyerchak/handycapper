package com.robinhowlett.handycapper.examples;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.robinhowlett.chartparser.ChartParser;
import com.robinhowlett.chartparser.charts.pdf.RaceResult;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;

public class PDFtoJSON {

    public static void main(String[] args) throws JsonProcessingException {
        File chart = Paths.get("/Users/rhowlett/ARP_2016-07-24_race-charts.pdf").toFile();

        List<RaceResult> results = ChartParser.create().parse(chart);

        System.out.println(ChartParser.getObjectMapper()
                .writerWithDefaultPrettyPrinter().writeValueAsString(results));
    }
}
