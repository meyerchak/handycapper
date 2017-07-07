package com.robinhowlett.handycapper.storage;

import com.robinhowlett.chartparser.ChartParser;
import com.robinhowlett.chartparser.charts.pdf.RaceResult;
import com.robinhowlett.handycapper.examples.csv.Splits;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class FileSystemStorageService implements StorageService {

    private final Path rootLocation;
    private ChartParser chartParser;

    @Autowired
    public FileSystemStorageService(StorageProperties properties, ChartParser chartParser) {
        this.rootLocation = Paths.get(properties.getLocation());
        this.chartParser = chartParser;
    }

    @Override
    public void store(MultipartFile chart, String action) {
        try {
            if (chart.isEmpty()) {
                throw new StorageException("Failed to store empty chart " + chart
                        .getOriginalFilename());
            }

            List<RaceResult> raceResults = chartParser.parse(convertMultipartFileToFile(chart));

            for (RaceResult raceResult : raceResults) {
                String type = raceResult.getRaceConditions().getRaceTypeNameBlackTypeBreed()
                        .getType().replaceAll("\\s+", "-").toLowerCase();

                String raceName =
                        raceResult.getRaceConditions().getRaceTypeNameBlackTypeBreed().getName();
                if (raceName != null) {
                    type = String.format("%s-%s", type.toLowerCase(),
                            raceName.replaceAll("\\s+", "-")
                                    .replaceAll("\\.", "").toLowerCase());
                }

                String fileName = String.format("%s_%s_result-r%d_%.2ff_%s_%s.%s",
                        raceResult.getTrack().getCode(),
                        raceResult.getRaceDate(),
                        raceResult.getRaceNumber(),
                        ((double) raceResult.getDistanceSurfaceTrackRecord().getRaceDistance()
                                .getValue() / 660),
                        raceResult.getDistanceSurfaceTrackRecord().getSurface().toLowerCase(),
                        type,
                        action.toLowerCase());

                File file = new File(this.rootLocation.resolve(fileName).toUri());

                if (action.equalsIgnoreCase("json")) {
                    ChartParser.getObjectMapper()
                            .writerWithDefaultPrettyPrinter().writeValue(file, raceResult);
                } else if (action.equalsIgnoreCase("csv")) {
                    String csv = Splits.createCSV(ChartParser.getCsvMapper(), raceResult);
                    Files.write(Paths.get(file.toURI()), csv.getBytes(UTF_8));
                }
            }
        } catch (IOException e) {
            throw new StorageException("Failed to store chart " + chart.getOriginalFilename(), e);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(path -> this.rootLocation.relativize(path));
        } catch (IOException e) {
            throw new StorageException("Failed to read stored charts", e);
        }

    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new StorageFileNotFoundException("Could not read chart: " + filename);

            }
        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read chart: " + filename, e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    @Override
    public void init() {
        try {
            Files.createDirectory(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }

    private File convertMultipartFileToFile(@RequestParam("file") MultipartFile file) {
        File convFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return convFile;
    }
}
