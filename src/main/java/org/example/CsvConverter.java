package org.example;
import org.apache.commons.lang3.math.NumberUtils;
import org.javatuples.Pair;
import java.io.BufferedReader;
import java.io.FileReader;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.example.RecordStatusConstants.NULL_VALUE;

public class CsvConverter {

    private static final String CSV_LINE_PATTERN = "^\\d+,\\d+,(?:\\d*|null| )$";
    private static final String LINE_DELIMITER = ",";
    private static final int OBSERVATION_DATE_IDX = 0;
    private static final int AS_OF_DATE_IDX = 1;
    private static final int PRICE_IDX = 2;

    static Map<String, List<Pair<String, Float>>> convertCsv(FileReader fileReader) {

        Map<String, List<Pair<String, Float>>> csvMap = new HashMap<>();

        BufferedReader csvReader = new BufferedReader(fileReader);

        csvReader.lines().forEach(line -> {
            Pattern pattern = Pattern.compile(CSV_LINE_PATTERN);
            Matcher matcher = pattern.matcher(line);

            if(matcher.matches()) {

                String[] lineSplit = line.split(LINE_DELIMITER);
                String observationDate = Instant.ofEpochSecond(Long.parseLong(lineSplit[OBSERVATION_DATE_IDX])).toString();
                String asOfDate = Instant.ofEpochSecond(Long.parseLong(lineSplit[AS_OF_DATE_IDX])).toString();
                String priceValueCandidate = lineSplit[PRICE_IDX];
                float price = NumberUtils.isCreatable(priceValueCandidate) ? Float.parseFloat(priceValueCandidate) : NULL_VALUE;

                if(!csvMap.containsKey(asOfDate)) {
                    List<Pair<String, Float>> observations = new ArrayList<>();
                    observations.add(new Pair<>(observationDate, price));
                    csvMap.put(asOfDate, observations);
                }
                else {
                    csvMap.get(asOfDate).add(new Pair<>(observationDate, price));
                }
            }
        });

        return csvMap;
    }
}
