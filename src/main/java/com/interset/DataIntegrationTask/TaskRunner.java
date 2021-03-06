package com.interset.DataIntegrationTask;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardOpenOption;
import java.io.IOException;

public class TaskRunner {

    public static void main(String args[]) {

        // Check arguments
        if (args.length != 2) {
            System.out.println("We currently only expect 2 arguments! A path to a JSON file to read, and a path for a CSV file to write.");
            System.exit(1);
        }

        // Read arguments
        Path jsonFile = null;

        try {
            jsonFile = Paths.get(args[0]);
        } catch (InvalidPathException e) {
            System.err.println("Couldn't convert JSON file argument [" + args[0] + "] into a path!");
            throw e;
        }

        Path csvFile = null;

        try {
            csvFile = Paths.get(args[1]);
        } catch (InvalidPathException e) {
            System.err.println("Couldn't convert CSV file argument [" + args[1] + "] into a path!");
            throw e;
        }

        // Validate arguments
        if (!Files.exists(jsonFile)) {
            System.err.println("JSON file [" + jsonFile.toString() + "] doesn't exist!");
            System.exit(1);
        }

        if (!Files.isWritable(csvFile.getParent())) {
            System.err.println("Can't write to the directory [" + csvFile.getParent().toString() + "] to create the CSV file! Does directory exist?");
            System.exit(1);
        }

        // Create the CSV file
        System.out.println("Reading file [" + jsonFile.toString() + "], and writing to file [" + csvFile.toString() + "].");

        parseJsonFileAndCreateCsvFile(jsonFile, csvFile);

    }

    public static void parseJsonFileAndCreateCsvFile(Path jsonFile, Path csvFile) {
        // Create a Filter object which will handle all the processing
        Filter filter = new Filter(jsonFile);
        filter.parseJSONToMetadataObjectIterator();
        filter.convertMetadataToRecordObjects();

        try {
            // Begin writing to the CSV file; write headers based on schema for behaviour
            String headers = "TIMESTAMP,ACTION,USER,FOLDER,FILENAME,IP\n";
            Files.write(csvFile, headers.getBytes(StandardCharsets.US_ASCII));

            // Get list of Records from Filter to write out to the CSV
            for (Record record : filter.getRecords()) {
                Files.write(csvFile, record.toString().concat("\n")
                    .getBytes(StandardCharsets.US_ASCII), StandardOpenOption.CREATE, 
                    StandardOpenOption.WRITE, StandardOpenOption.APPEND);
            }
        } catch (IOException e) {
            System.err.println("Could not write to CSV.");
            System.exit(1);
        }

        // Output the metrics that were crunched from the processing
        System.out.println("---");
        filter.getMetrics().printMetricsAsJSON();
    }
}
