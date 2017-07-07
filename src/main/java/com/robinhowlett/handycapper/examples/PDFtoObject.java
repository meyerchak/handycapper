package com.robinhowlett.handycapper.examples;

import com.robinhowlett.chartparser.ChartParser;

import java.io.File;
import java.nio.file.Paths;

public class PDFtoObject {

    public static void main(String[] args) {
        File chart = Paths.get("ARP_2016-07-24_race-charts.pdf").toFile();

        ChartParser.create().parse(chart).stream().forEach(
                result -> {
                    System.out.println(String.format("Race %d, purse: %s, %d runners",
                            result.getRaceNumber(),
                            result.getPurse().getText(),
                            result.getNumberOfRunners()));
                });
    }
}
