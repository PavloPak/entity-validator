package org.example;

import org.javatuples.Pair;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.example.RecordStatusConstants.*;

public class FilesComparator {

    static Map<String, Map<String, List<String>>> compareFiles(Map<String, List<Pair<String, Float>>> jsonMap, Map<String, List<Pair<String, Float>>> csvMap, String filePath) {

        final Map<String, Map<String, List<String>>> compareResult = new HashMap<>();

        final Map<String, List<String>> comparisonDetails = new HashMap<>();
        comparisonDetails.put(AS_OF_DATES_MISSING, new ArrayList<>());
        comparisonDetails.put(OBSERVATION_VALUES_INCORRECT, new ArrayList<>());

        String entityUUID = extractUUID(filePath);

        if(csvMap.isEmpty()) {

            comparisonDetails.put(IS_EMPTY, new ArrayList<>(Collections.singletonList(CSV_IS_EMPTY)));
            compareResult.put(entityUUID, comparisonDetails);
            return compareResult;
        }

        jsonMap.forEach((jsonAsOfDate, jsonObservations) -> {

            if (!csvMap.containsKey(jsonAsOfDate)) {
                comparisonDetails.get(AS_OF_DATES_MISSING).add(jsonAsOfDate);
            } else {
                List<Pair<String, Float>> csvObservations = csvMap.get(jsonAsOfDate);

                Collections.sort(jsonObservations);
                Collections.sort(csvObservations);

                boolean observationsMatch = jsonObservations.equals(csvObservations);

                if (!observationsMatch) {
                    comparisonDetails.get(OBSERVATION_VALUES_INCORRECT).add(jsonAsOfDate);
                }
            }
        });
        compareResult.put(entityUUID, comparisonDetails);
        return compareResult;
    }

    private static String extractUUID(String path) {

        String uuidRegex = "[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}";

        Pattern pattern = Pattern.compile(uuidRegex);
        Matcher matcher = pattern.matcher(path);

        if (matcher.find()) {
            return matcher.group();
        } else {
            return EMPTY_STRING;
        }
    }
}
