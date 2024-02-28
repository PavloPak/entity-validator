package org.example;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.example.RecordStatusConstants.*;

public class ReportGenerator {

    private final List<String> emptyV2UUIDS;
    private final List<String> missingAsOfDatesV2UUIDS;
    private final List<String> incorrectObservationsV2UUIDS;
    private final int totalEntities;
    private final ConcurrentLinkedQueue<Map<String, Map<String, List<String>>>> reportsStack;

    public ReportGenerator(ConcurrentLinkedQueue<Map<String, Map<String, List<String>>>> reportsStack, int totalEntities) {
        this.reportsStack = reportsStack;
        this.totalEntities = totalEntities;
        this.emptyV2UUIDS = new ArrayList<>();
        this.missingAsOfDatesV2UUIDS = new ArrayList<>();
        this.incorrectObservationsV2UUIDS = new ArrayList<>();
    }

    public void generateReport() {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm");
        LocalDateTime now = LocalDateTime.now();
        String today = dtf.format(now);

        String emptyV2Name = "Empty files uuids.txt";
        String missingRecordsV2ReportName = "Missing AsOfDates uuids.txt";
        String incorrectRecordsV2Name = "Incorrect Observations uuids.txt";
        String summaryReportName = "Summary execution report.txt";

        this.traverseReports();
        this.writeEmptyV2UUIDS(emptyV2Name);
        this.writeMissingAsOfDatesV2UUIDS(missingRecordsV2ReportName);
        this.writeIncorrectObservationsV2UUIDS(incorrectRecordsV2Name);
        this.writeSummaryReport(summaryReportName, today);

    }

    private void traverseReports() {
        this.reportsStack.forEach(report -> {
            report.forEach((uuidKey, valueMap) -> {
                if (valueMap.containsKey(IS_EMPTY) && !valueMap.get(IS_EMPTY).isEmpty()) {
                    emptyV2UUIDS.add(uuidKey);
                }
                if (valueMap.containsKey(AS_OF_DATES_MISSING) && !valueMap.get(AS_OF_DATES_MISSING).isEmpty()) {
                    missingAsOfDatesV2UUIDS.add(uuidKey);
                }
                if (valueMap.containsKey(OBSERVATION_VALUES_INCORRECT) && !valueMap.get(OBSERVATION_VALUES_INCORRECT).isEmpty()) {
                    incorrectObservationsV2UUIDS.add(uuidKey);
                }
            });
        });
    }

    private void writeEmptyV2UUIDS(String reportName) {

        try (PrintWriter printWriter = new PrintWriter(reportName)) {

            printWriter.println("Empty V2 files");
            printWriter.println("============================");
            printWriter.println();
            this.emptyV2UUIDS.forEach(printWriter::println);

        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
        }
    }

    private void writeMissingAsOfDatesV2UUIDS(String reportName) {
        try (PrintWriter printWriter = new PrintWriter(reportName)) {

            printWriter.println("Missing V2 As Of Dates");
            printWriter.println("============================");
            printWriter.println();
            this.missingAsOfDatesV2UUIDS.forEach(printWriter::println);

        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
        }
    }

    private void writeIncorrectObservationsV2UUIDS(String reportName) {
        try (PrintWriter printWriter = new PrintWriter(reportName)) {

            printWriter.println("Incorrect V2 Observations");
            printWriter.println("============================");
            printWriter.println();
            this.incorrectObservationsV2UUIDS.forEach(printWriter::println);

        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
        }
    }

    private void writeSummaryReport(String reportName, String date) {
        try (PrintWriter printWriter = new PrintWriter(reportName)) {

            printWriter.println("Execution summary");
            printWriter.println("Run date " + date);
            printWriter.println("============================");
            printWriter.println();
            printWriter.println("Total number of entities verified (UUID count): " + this.totalEntities);
            printWriter.println("============================");
            printWriter.println();
            printWriter.println("Total number of empty V2 entities: " + this.emptyV2UUIDS.size());
            printWriter.println("============================");
            printWriter.println();
            printWriter.println("Total number of missing As Of Date records of V2 entities: " + this.missingAsOfDatesV2UUIDS.size());
            printWriter.println("============================");
            printWriter.println();
            printWriter.println("Total number of incorrect or incomplete Observation records of V2 entities: " + this.incorrectObservationsV2UUIDS.size());
            printWriter.println("============================");

        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
        }
    }
}
