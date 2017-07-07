package com.robinhowlett.handycapper.examples.csv;

import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.robinhowlett.chartparser.charts.pdf.RaceResult;
import com.robinhowlett.chartparser.charts.pdf.Starter;
import com.robinhowlett.chartparser.fractionals.FractionalPoint;

import java.io.IOException;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Splits {

    private static final DecimalFormat TWO_DECIMALS = new DecimalFormat("0.00");
    private static final DecimalFormat THREE_DECIMALS = new DecimalFormat("0.000");

    public static String createCSV(final CsvMapper csvMapper,
            final RaceResult raceResult) throws IOException {
        CsvSchema schema =
                CsvSchema.builder().addColumns(getColumns(raceResult)).build().withHeader();

        List<Map<String, Object>> rows = new ArrayList<>();

        List<Starter> starters = raceResult.getStarters();
        if (starters != null && !starters.isEmpty()) {
            for (Starter starter : starters) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("date", raceResult.getRaceDate().toString());
                row.put("track", raceResult.getTrack().getCode());
                row.put("raceNumber", raceResult.getRaceNumber());
                row.put("name", starter.getHorse().getName());
                row.put("pp", starter.getPostPosition());
                row.put("weight", starter.getWeight().getWeightCarried());
                row.put("odds", TWO_DECIMALS.format(starter.getOdds()));
                row.put("position", starter.getOfficialPosition());

                List<FractionalPoint.Split> splits = starter.getSplits();
                for (int i = 0; i < splits.size(); i++) {
                    FractionalPoint.Split split = splits.get(i);
                    if (split.hasFractionalValue()) {
                        row.put(split.getText(),
                                THREE_DECIMALS.format(split.getMillis() / (double) 1000));
                    }
                }

                rows.add(row);
            }
        }

        try (StringWriter stringWriter = new StringWriter()) {
            try (SequenceWriter sequenceWriter =
                         csvMapper.writer(schema).writeValues(stringWriter).writeAll(rows)) {
                sequenceWriter.flush();
            }
            return stringWriter.toString();
        }
    }

    static List<CsvSchema.Column> getColumns(final RaceResult raceResult) {
        List<CsvSchema.Column> columns = new ArrayList<>();
        List<String> asList = Arrays.asList("date", "track", "raceNumber", "name", "pp", "weight",
                "odds", "position");
        for (int i = 0; i < asList.size(); i++) {
            String column = asList.get(i);
            columns.add(new CsvSchema.Column(i, column));
        }

        List<FractionalPoint.Split> splits = raceResult.getWinners().get(0).getSplits();
        for (int i = 0; i < splits.size(); i++) {
            FractionalPoint.Split split = splits.get(i);
            columns.add(new CsvSchema.Column(columns.size(), split.getText()));
        }

        return columns;
    }

}
