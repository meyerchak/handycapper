package com.robinhowlett.handycapper.xlsx;

import com.robinhowlett.chartparser.ChartParser;
import com.robinhowlett.chartparser.charts.pdf.DistanceSurfaceTrackRecord.RaceDistance;
import com.robinhowlett.chartparser.charts.pdf.RaceResult;
import com.robinhowlett.chartparser.charts.pdf.RaceTypeNameBlackTypeBreed;
import com.robinhowlett.chartparser.charts.pdf.Starter;
import com.robinhowlett.chartparser.charts.pdf.running_line.LastRaced;
import com.robinhowlett.chartparser.charts.pdf.wagering.WagerPayoffPools;
import com.robinhowlett.chartparser.fractionals.FractionalPoint.Fractional;
import com.robinhowlett.chartparser.fractionals.FractionalPoint.Split;
import com.robinhowlett.chartparser.points_of_call.PointsOfCall.PointOfCall;
import com.robinhowlett.chartparser.points_of_call.PointsOfCall.PointOfCall.RelativePosition;
import com.robinhowlett.chartparser.points_of_call.PointsOfCall.PointOfCall.RelativePosition
        .TotalLengthsBehind;
import com.robinhowlett.handycapper.examples.PDFtoJSON;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.time.Period;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.robinhowlett.handycapper.xlsx.XSSFCellCreator.asBoolean;
import static com.robinhowlett.handycapper.xlsx.XSSFCellCreator.asDate;
import static com.robinhowlett.handycapper.xlsx.XSSFCellCreator.asNumber;
import static com.robinhowlett.handycapper.xlsx.XSSFCellCreator.asText;

/**
 * Created by rhowlett on 7/10/17.
 */
public class ThreeTypeSummary {

    private static CreationHelper helper;
    private static CellStyle dateFormat;
    private static CellStyle twoDigitFormat;
    private static CellStyle threeDigitFormat;
    private static CellStyle commaNumberFormat;
    private static CellStyle twoDigitCommaFormat;

    static XSSFWorkbook createResultsSheets(List<RaceResult> raceResults,
            XSSFWorkbook workbook) {
        for (int i = 0; i < raceResults.size(); i++) {
            RaceResult raceResult = raceResults.get(i);
            validate(raceResult);

            // Results
            XSSFSheet sheet = workbook.createSheet("Race " + (i + 1));
            int row = 0;
            int col = 0;

            createResultsHeaderRow(sheet, row++, col, raceResult);

            for (Starter starter : raceResult.getStarters()) {
                int startersColNum = 0;
                XSSFRow starterRow = sheet.createRow(row++);
                RaceDistance raceDistance =
                        raceResult.getDistanceSurfaceTrackRecord().getRaceDistance();

                startersColNum = fillStandardCols(raceResult, starter, startersColNum,
                        starterRow, raceDistance);

                // weight
                asNumber(starterRow, startersColNum++, starter.getWeight().getWeightCarried());

                // runUp
                asNumber(starterRow, startersColNum++, raceDistance.getRunUp());

                // # of runners
                asNumber(starterRow, startersColNum++, raceResult.getNumberOfRunners());

                // last raced track
                LastRaced lastRaced = starter.getLastRaced();
                asText(starterRow, startersColNum++, (lastRaced != null ?
                        lastRaced.getLastRacePerformance().getTrack().getCode() : null));

                // days since
                asNumber(starterRow, startersColNum++, (lastRaced != null ?
                        lastRaced.getDaysSince() : null));

                // individual fractionals
                List<Fractional> fractionals = starter.getFractionals();
                if (fractionals != null) {
                    for (int j = 0; j < fractionals.size(); j++) {
                        Fractional fractional = fractionals.get(j);
                        Long millis = fractional.getMillis();
                        asNumber(starterRow, startersColNum++,
                                (millis != null ? (double) millis / 1000 : null))
                                .setCellStyle(threeDigitFormat);
                    }
                }

                // individual splits
                List<Split> splits = starter.getSplits();
                if (splits != null) {
                    for (int j = 0; j < splits.size(); j++) {
                        Split split = splits.get(j);
                        Long millis = split.getMillis();
                        asNumber(starterRow, startersColNum++,
                                (millis != null ? (double) millis / 1000 : null))
                                .setCellStyle(threeDigitFormat);
                    }
                }

                // points of call
                List<PointOfCall> pointsOfCall = starter.getPointsOfCall();
                if (pointsOfCall != null) {
                    // position
                    for (int j = 0; j < pointsOfCall.size(); j++) {
                        PointOfCall pointOfCall = pointsOfCall.get(j);
                        RelativePosition relativePosition = pointOfCall.getRelativePosition();
                        asNumber(starterRow, startersColNum++,
                                (relativePosition != null ? relativePosition.getPosition() : null));
                    }
                    // lengths behind
                    for (int j = 0; j < pointsOfCall.size(); j++) {
                        PointOfCall pointOfCall = pointsOfCall.get(j);
                        RelativePosition relativePosition = pointOfCall.getRelativePosition();
                        TotalLengthsBehind totalLengthsBehind =
                                relativePosition.getTotalLengthsBehind();
                        asNumber(starterRow, startersColNum++,
                                (relativePosition != null && totalLengthsBehind != null) ?
                                        totalLengthsBehind.getLengths() : 0)
                                .setCellStyle(twoDigitFormat);
                    }
                }

                // leader fractionals
                List<Fractional> leaderFractionals = raceResult.getFractionals();
                if (leaderFractionals != null) {
                    for (int j = 0; j < leaderFractionals.size(); j++) {
                        Fractional fractional = leaderFractionals.get(j);
                        Long millis = fractional.getMillis();
                        asNumber(starterRow, startersColNum++,
                                (millis != null ? (double) millis / 1000 : null))
                                .setCellStyle(threeDigitFormat);
                    }
                }

                // leader splits
                List<Split> leaderSplits = raceResult.getSplits();
                if (leaderSplits != null) {
                    for (int j = 0; j < leaderSplits.size(); j++) {
                        Split split = leaderSplits.get(j);
                        Long millis = split.getMillis();
                        asNumber(starterRow, startersColNum++,
                                (millis != null ? (double) millis / 1000 : null))
                                .setCellStyle(threeDigitFormat);
                    }
                }
            }
        }
        return workbook;
    }

    public static int fillStandardCols(RaceResult raceResult, Starter starter, int startersColNum,
            XSSFRow starterRow, RaceDistance raceDistance) {
        startersColNum = fillSeedCols(raceResult, startersColNum, starterRow, raceDistance);

        // (horse) name
        asText(starterRow, startersColNum++, starter.getHorse().getName());

        // pp
        asNumber(starterRow, startersColNum++, starter.getPostPosition());

        // odds
        asNumber(starterRow, startersColNum++, starter.getOdds())
                .setCellStyle(twoDigitFormat);

        // favorite
        asBoolean(starterRow, startersColNum++, starter.isFavorite());

        // choice
        asNumber(starterRow, startersColNum++, starter.getChoice());

        // position
        asNumber(starterRow, startersColNum++, starter.getOfficialPosition());

        // trainer
        asText(starterRow, startersColNum++, starter.getTrainer().getName());

        // jockey
        asText(starterRow, startersColNum++, starter.getJockey().getName());

        // final time
        Fractional finishFractional = starter.getFinishFractional();
        asText(starterRow, startersColNum++,
                (finishFractional != null ? finishFractional.getTime() : null))
                .setCellStyle(threeDigitFormat);

        // seconds
        asNumber(starterRow, startersColNum++,
                (finishFractional != null && finishFractional.getMillis() != null ?
                        (double) finishFractional.getMillis() / 1000 : null))
                .setCellStyle(threeDigitFormat);
        return startersColNum;
    }

    public static int fillSeedCols(RaceResult raceResult, int startersColNum, XSSFRow starterRow,
            RaceDistance raceDistance) {
        // date
        asDate(starterRow, startersColNum++, raceResult.getRaceDate())
                .setCellStyle(dateFormat);

        // track
        asText(starterRow, startersColNum++, raceResult.getTrack().getCode());

        // raceNumber
        asNumber(starterRow, startersColNum++, raceResult.getRaceNumber());

        RaceTypeNameBlackTypeBreed typeBreed =
                raceResult.getRaceConditions().getRaceTypeNameBlackTypeBreed();
        // type
        asText(starterRow, startersColNum++, typeBreed.getType());

        // raceName
        asText(starterRow, startersColNum++, typeBreed.getName());

        // grade
        asNumber(starterRow, startersColNum++, typeBreed.getGrade());

        // purse
        asNumber(starterRow, startersColNum++, raceResult.getPurse().getValue())
                .setCellStyle(commaNumberFormat);
        ;

        // distance
        asNumber(starterRow, startersColNum++,
                raceDistance.getFurlongs()).setCellStyle(twoDigitFormat);
        asText(starterRow, startersColNum++, raceDistance.getCompact());

        // surface
        asText(starterRow, startersColNum++,
                raceResult.getDistanceSurfaceTrackRecord().getSurface());
        return startersColNum;
    }

    public static XSSFRow createResultsHeaderRow(XSSFSheet sheet, int row, int col,
            RaceResult raceResult) {
        XSSFRow headerRow = sheet.createRow(row++);

        col = standardCols(col, headerRow);

        List<String> columns = Arrays.asList("weight", "runUp", "# of runners",
                "track last raced", "days since");
        for (String column : columns) {
            CellUtil.createCell(headerRow, col++, column);
        }

        List<Starter> winners = raceResult.getWinners();

        Starter winner = winners.get(0);

        // individual fractionals
        List<Fractional> fractionals = winner.getFractionals();
        for (int j = 0; j < fractionals.size(); j++) {
            Fractional fractional = fractionals.get(j);
            CellUtil.createCell(headerRow, col++, "Indiv " + fractional.getText());
        }

        // splits
        List<Split> splits = winner.getSplits();
        for (int j = 0; j < splits.size(); j++) {
            Split split = splits.get(j);
            CellUtil.createCell(headerRow, col++, "Indiv " + split.getText());
        }

        // points of call
        List<PointOfCall> pointsOfCall = winner.getPointsOfCall();
        if (pointsOfCall != null) {
            // position
            for (int j = 0; j < pointsOfCall.size(); j++) {
                PointOfCall pointOfCall = pointsOfCall.get(j);
                CellUtil.createCell(headerRow, col++, "Pos " + pointOfCall.getText());
            }
            // lengths behind
            for (int j = 0; j < pointsOfCall.size(); j++) {
                PointOfCall pointOfCall = pointsOfCall.get(j);
                CellUtil.createCell(headerRow, col++, "LenBhd " + pointOfCall.getText());
            }
        }

        // leader fractionals
        List<Fractional> leaderFractionals = winner.getFractionals();
        for (int j = 0; j < leaderFractionals.size(); j++) {
            Fractional fractional = leaderFractionals.get(j);
            CellUtil.createCell(headerRow, col++, "Leader " + fractional.getText());
        }

        // leader splits
        List<Split> leaderSplits = winner.getSplits();
        for (int j = 0; j < leaderSplits.size(); j++) {
            Split split = leaderSplits.get(j);
            CellUtil.createCell(headerRow, col++, "Leader " + split.getText());
        }

        return headerRow;
    }

    static void createBreedingSheet(List<RaceResult> raceResults, XSSFWorkbook workbook) {
        // Breeding
        XSSFSheet sheet = workbook.createSheet("Breeding");
        int row = 0;
        int col = 0;

        createBreedingHeaderRow(sheet, row++, col);

        for (int i = 0; i < raceResults.size(); i++) {
            RaceResult raceResult = raceResults.get(i);
            validate(raceResult);

            List<Starter> winners = raceResult.getWinners();
            if (winners != null) {
                for (Starter winner : winners) {
                    int winnersColNum = 0;
                    XSSFRow winnerRow = sheet.createRow(row++);
                    RaceDistance raceDistance =
                            raceResult.getDistanceSurfaceTrackRecord().getRaceDistance();

                    winnersColNum = fillStandardCols(raceResult, winner, winnersColNum,
                            winnerRow, raceDistance);

                    // sex
                    asText(winnerRow, winnersColNum++, winner.getHorse().getSex());

                    // sire
                    asText(winnerRow, winnersColNum++, winner.getHorse().getSire().getName());

                    // dam
                    asText(winnerRow, winnersColNum++, winner.getHorse().getDam().getName());

                    // damSire
                    asText(winnerRow, winnersColNum++, winner.getHorse().getDamSire().getName());

                    // foalDate
                    asDate(winnerRow, winnersColNum++, winner.getHorse().getFoalingDate())
                            .setCellStyle(dateFormat);

                    // age
                    asNumber(winnerRow, winnersColNum++,
                            (winner.getHorse().getFoalingDate() != null ?
                                    Period.between(winner.getHorse().getFoalingDate(),
                                            raceResult.getRaceDate()).getYears() : null));
                }
            }
        }
    }

    public static XSSFRow createBreedingHeaderRow(XSSFSheet sheet, int row, int col) {
        XSSFRow headerRow = sheet.createRow(row++);

        col = standardCols(col, headerRow);

        List<String> columns = Arrays.asList("sex", "sire", "dam", "damSire", "foalDate", "age");
        for (String column : columns) {
            CellUtil.createCell(headerRow, col++, column);
        }

        return headerRow;
    }

    static void createWageringSheet(List<RaceResult> raceResults, XSSFWorkbook workbook) {
        // Breeding
        XSSFSheet sheet = workbook.createSheet("Wagering");
        int row = 0;
        int col = 0;

        createWageringHeaderRow(sheet, row++, col);

        for (int i = 0; i < raceResults.size(); i++) {
            RaceResult raceResult = raceResults.get(i);
            validate(raceResult);

            int raceColNum = 0;
            XSSFRow raceRow = sheet.createRow(row++);
            RaceDistance raceDistance =
                    raceResult.getDistanceSurfaceTrackRecord().getRaceDistance();

            raceColNum = fillSeedCols(raceResult, raceColNum, raceRow, raceDistance);

//            List<String> columns = Arrays.asList("totalWpsPool", "winPayoff", "placePayoff",
//                    "showPayoff", "doublePayoff", "doublePool", "exactaPayoff", "exactaPool",
//                    "trifectaPayoff", "trifectaPool", "superfectaPayoff", "superfectaPool",
//                    "pick3Payoff", "pick3Pool", "pick4Payoff", "pick4Pool", "pick5Payoff",
// "pick5Pool");

            WagerPayoffPools wagerPayoffPools = raceResult.getWagerPayoffPools();
            WagerPayoffPools.WinPlaceShowPayoffPool wpsPayoffPools =
                    wagerPayoffPools.getWinPlaceShowPayoffPools();
            List<WagerPayoffPools.ExoticPayoffPool> exoticPayoffPools =
                    wagerPayoffPools.getExoticPayoffPools();

            Starter winner = raceResult.getWinners().get(0);
            // winPayoff
            asNumber(raceRow, raceColNum++,
                    (winner.getWinPlaceShowPayoff() != null &&
                            winner.getWinPlaceShowPayoff().getWin() != null ?
                            winner.getWinPlaceShowPayoff().getWin().getPayoff() : null))
                    .setCellStyle(twoDigitCommaFormat);

            // placePayoff
            asNumber(raceRow, raceColNum++,
                    (winner.getWinPlaceShowPayoff() != null &&
                            winner.getWinPlaceShowPayoff().getPlace() != null ?
                            winner.getWinPlaceShowPayoff().getPlace().getPayoff() : null))
                    .setCellStyle(twoDigitCommaFormat);

            // showPayoff
            asNumber(raceRow, raceColNum++,
                    (winner.getWinPlaceShowPayoff() != null &&
                            winner.getWinPlaceShowPayoff().getShow() != null ?
                            winner.getWinPlaceShowPayoff().getShow().getPayoff() : null))
                    .setCellStyle(twoDigitCommaFormat);

            // totalWpsPool
            asNumber(raceRow, raceColNum++, wpsPayoffPools.getTotalWinPlaceShowPool())
                    .setCellStyle(commaNumberFormat);

            List<String> horizontals = Arrays.asList("Daily Double", "Exacta", "Trifecta",
                    "Superfecta", "Pick 3", "Pick 4", "Pick 5");
            for (String horizontal : horizontals) {
                Optional<WagerPayoffPools.ExoticPayoffPool> wager = exoticPayoffPools.stream()
                        .filter(exoticPayoffPool -> exoticPayoffPool.getName()
                                .equalsIgnoreCase(horizontal)).findFirst();

                // payoff
                asNumber(raceRow, raceColNum++,
                        (wager.isPresent() ? ((double) wager.get().getPayoff() / wager.get()
                                .getUnit()) * 2 : null))
                        .setCellStyle(twoDigitCommaFormat);

                // pool
                asNumber(raceRow, raceColNum++,
                        (wager.isPresent() ? wager.get().getPool() : null))
                        .setCellStyle(commaNumberFormat);
            }
        }
    }

    public static XSSFRow createWageringHeaderRow(XSSFSheet sheet, int row, int col) {
        XSSFRow headerRow = sheet.createRow(row++);

        col = seedCols(col, headerRow);

        List<String> columns = Arrays.asList("winPayoff", "placePayoff",
                "showPayoff", "totalWpsPool", "doublePayoff", "doublePool", "exactaPayoff",
                "exactaPool", "trifectaPayoff", "trifectaPool", "superfectaPayoff",
                "superfectaPool", "pick3Payoff", "pick3Pool", "pick4Payoff", "pick4Pool",
                "pick5Payoff", "pick5Pool");
        for (String column : columns) {
            CellUtil.createCell(headerRow, col++, column);
        }

        return headerRow;
    }

    static int seedCols(int col, XSSFRow headerRow) {
        List<String> columns = Arrays.asList("date", "track", "raceNumber", "type", "raceName",
                "grade", "purse", "dist f", "dist", "surface");
        for (String column : columns) {
            CellUtil.createCell(headerRow, col++, column);
        }
        return col;
    }

    static int standardCols(int col, XSSFRow headerRow) {
        col = seedCols(col, headerRow);
        List<String> columns = Arrays.asList("name", "pp", "odds", "favorite", "choice",
                "position", "trainer", "jockey", "final time", "seconds");
        for (String column : columns) {
            CellUtil.createCell(headerRow, col++, column);
        }
        return col;
    }

    public static XSSFWorkbook create(List<RaceResult> raceResults) {
        XSSFWorkbook workbook = new XSSFWorkbook();

        helper = workbook.getCreationHelper();

        dateFormat = workbook.createCellStyle();
        dateFormat.setDataFormat(helper.createDataFormat().getFormat("m/d/yy"));

        twoDigitFormat = workbook.createCellStyle();
        twoDigitFormat.setDataFormat(helper.createDataFormat().getFormat("0.00"));

        threeDigitFormat = workbook.createCellStyle();
        threeDigitFormat.setDataFormat(helper.createDataFormat().getFormat("0.000"));

        commaNumberFormat = workbook.createCellStyle();
        commaNumberFormat.setDataFormat(helper.createDataFormat().getFormat("#,##0"));

        twoDigitCommaFormat = workbook.createCellStyle();
        twoDigitCommaFormat.setDataFormat(helper.createDataFormat().getFormat("#,##0.00"));

        createResultsSheets(raceResults, workbook);
        createBreedingSheet(raceResults, workbook);
        createWageringSheet(raceResults, workbook);

        return workbook;
    }

    private static void validate(RaceResult raceResult) {
        if (raceResult == null || raceResult.getStarters() == null) {
            throw new RuntimeException("RaceResult instance or Starters List is null");
        }

        if (raceResult.getCancellation().isCancelled()) {
            throw new RuntimeException("Race was cancelled - spreadsheet will not be created");
        }
    }

}
