package com.robinhowlett.handycapper.examples;

import com.robinhowlett.chartparser.ChartParser;
import com.robinhowlett.chartparser.charts.pdf.RaceResult;
import com.robinhowlett.handycapper.examples.csv.Summary;

import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PDFtoCSV_RaceCardSummary {

    public static void main(String[] args) throws IOException {
        File chart = ResourceUtils.getFile("classpath:examples/ARP_2016-07-24_race-charts.pdf");
        List<RaceResult> results = ChartParser.create().parse(chart);

        String csv = Summary.createCSV(ChartParser.getCsvMapper(), results);

        System.out.println(csv);
    }
}
