package com.robinhowlett.handycapper.examples;

import com.robinhowlett.chartparser.ChartParser;
import com.robinhowlett.chartparser.charts.pdf.RaceResult;
import com.robinhowlett.handycapper.examples.csv.Splits;

import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class PDFtoCSV_SplitTimes {

    public static void main(String[] args) throws IOException {
        File chart = ResourceUtils.getFile("classpath:examples/ARP_2016-07-24_race-charts.pdf");
        List<RaceResult> results = ChartParser.create().parse(chart);

        RaceResult raceResult = results.stream().filter(
                result -> (result.getRaceNumber() == 9)).findFirst().get();

        String csv = Splits.createCSV(ChartParser.getCsvMapper(), raceResult);

        System.out.println(csv);
    }
}
