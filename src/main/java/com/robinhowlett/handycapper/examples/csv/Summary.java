package com.robinhowlett.handycapper.examples.csv;

import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.robinhowlett.chartparser.charts.pdf.RaceResult;
import com.robinhowlett.chartparser.charts.pdf.RaceTypeNameBlackTypeBreed;
import com.robinhowlett.chartparser.charts.pdf.Starter;

import java.io.IOException;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Summary {

    private static final DecimalFormat TWO_DECIMALS = new DecimalFormat("0.00");
    private static final DecimalFormat THREE_DECIMALS = new DecimalFormat("0.000");

    public static String createCSV(final CsvMapper csvMapper,
            final List<RaceResult> raceResults) throws IOException {
        if (raceResults != null && !raceResults.isEmpty()) {
            RaceResult result = raceResults.get(0);
            CsvSchema schema =
                    CsvSchema.builder().addColumns(getColumns(result)).build().withHeader();

            List<Map<String, Object>> rows = new ArrayList<>();

            for (RaceResult raceResult : raceResults) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("date", raceResult.getRaceDate().toString());
                row.put("track", raceResult.getTrack().getCode());
                row.put("raceNumber", raceResult.getRaceNumber());

                RaceTypeNameBlackTypeBreed typeBreedName =
                        raceResult.getRaceConditions().getRaceTypeNameBlackTypeBreed();
                row.put("breed", typeBreedName.getBreed());
                row.put("type", typeBreedName.getType());
                row.put("name", typeBreedName.getName());

                row.put("distance", TWO_DECIMALS.format((double) raceResult
                        .getDistanceSurfaceTrackRecord().getRaceDistance().getValue() / 660));
                row.put("surface", raceResult.getDistanceSurfaceTrackRecord().getSurface());
                row.put("trackCondition",
                        raceResult.getDistanceSurfaceTrackRecord().getTrackCondition());
                row.put("runners", raceResult.getNumberOfRunners());

                Starter winner = raceResult.getWinners().get(0);
                row.put("winner", winner.getHorse().getName());
                row.put("pp", winner.getPostPosition());
                row.put("jockey", winner.getJockey().getName());
                row.put("trainer", winner.getTrainer().getName());
                row.put("odds", TWO_DECIMALS.format(winner.getOdds()));

                row.put("time", raceResult.getWinningTime());

                rows.add(row);
            }

            try (StringWriter stringWriter = new StringWriter()) {
                try (SequenceWriter sequenceWriter =
                             csvMapper.writer(schema).writeValues(stringWriter).writeAll(rows)) {
                    sequenceWriter.flush();
                }
                return stringWriter.toString();
            }
        }
        return null;
    }

    static List<CsvSchema.Column> getColumns(final RaceResult raceResult) {
        List<CsvSchema.Column> columns = new ArrayList<>();
        List<String> asList = Arrays.asList("date", "track", "raceNumber", "breed", "type",
                "name", "distance", "surface", "trackCondition", "runners", "winner", "pp",
                "jockey", "trainer", "odds", "time");

        for (int i = 0; i < asList.size(); i++) {
            String column = asList.get(i);
            columns.add(new CsvSchema.Column(i, column));
        }

        return columns;
    }

}
