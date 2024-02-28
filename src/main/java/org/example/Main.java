package org.example;

import org.javatuples.Pair;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Main {

    private static final ConcurrentLinkedQueue<Map<String, Map<String, List<String>>>> REPORT_MAPS = new ConcurrentLinkedQueue<>();
    private static final String V1_FOLDER = "<Absolute path to V1 directory>\\v1";
    private static final String V2_FOLDER = "<Absolute path to V2 directory>\\v2";

    public static void main(String[] args)  {

        List<Path[]> filePairs = getFilePairs();

        filePairs.parallelStream().forEach(filePair -> {
            try {
                Path jsonPath = filePair[0];
                Path csvPath = filePair[1];

                final Map<String, List<Pair<String, Float>>> jsonMap;
                final Map<String, List<Pair<String, Float>>> csvMap;

                jsonMap = JsonConverter.convertJson(new FileReader(String.valueOf(jsonPath)));
                csvMap = CsvConverter.convertCsv(new FileReader(String.valueOf(csvPath)));

                Map<String, Map<String, List<String>>> reportMap = FilesComparator.compareFiles(jsonMap, csvMap, String.valueOf(jsonPath));
                if(!reportMap.isEmpty()) REPORT_MAPS.add(reportMap);
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        });
        ReportGenerator rg = new ReportGenerator(REPORT_MAPS, filePairs.size());
        rg.generateReport();
    }

    private static List<Path[]> getFilePairs()  {

        try (Stream<Path> paths = Files.walk(Paths.get(V1_FOLDER))) {
            return paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".json"))
                    .map(path -> {
                        String baseName = path.getFileName().toString().replaceAll("\\.json$", "");
                        Path csvPath = Paths.get(V2_FOLDER, baseName + ".csv");
                        return new Path[]{path, csvPath};
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
