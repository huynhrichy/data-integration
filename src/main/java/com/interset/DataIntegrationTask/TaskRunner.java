package com.interset.DataIntegrationTask;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.nio.charset.StandardCharsets;
import java.nio.file.StandardOpenOption;
import java.io.IOException;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;

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
        try {
            // Use Jackson's ObjectMapper to create Metadata POJOs from JSON objects in jsonFile
            ObjectMapper mapper = new ObjectMapper();
            Iterator<Metadata> iterator = mapper.reader(Metadata.class).readValues(jsonFile.toFile());
            
            // Check for event duplicates with a set
            Set<Long> eventIDs = new HashSet<Long>();

            // Write headers based on schema for behaviour
            String headers = "TIMESTAMP,ACTION,USER,FOLDER,FILENAME,IP\n";
            Files.write(csvFile, headers.getBytes(StandardCharsets.US_ASCII));

            // Metrics object for tracking numbers
            Metrics metrics = new Metrics();
            Record startRecord = null;
            Record endRecord = null;

            // Convert Metadata into Record objects for CSV-formatted output
            while (iterator.hasNext()) {
                Record record = new Record(iterator.next());

                // If Record fits exclusion criteria (duplicate eventID or invalid activity), do not write it
                boolean newValidRecord = true;

                if (record.getAction().equals("")) {
                    newValidRecord = false;
                    metrics.setNoActionMapping(metrics.getNoActionMapping() + 1);
                }

                if (eventIDs.contains(record.getEventID())) {
                    newValidRecord = false;
                    metrics.setDuplicates(metrics.getDuplicates() + 1);
                }

                if (newValidRecord) {
                    Files.write(csvFile, record.toString().concat("\n")
                        .getBytes(StandardCharsets.US_ASCII), StandardOpenOption.CREATE, 
                        StandardOpenOption.WRITE, StandardOpenOption.APPEND);

                    eventIDs.add(record.getEventID());

                    // Add action numbers to Metrics
                    switch (record.getAction()) {
                        case "ADD":
                            metrics.setAdd(metrics.getAdd() + 1);
                            break;
                        case "REMOVE":
                            metrics.setRemove(metrics.getRemove() + 1);
                            break;
                        case "ACCESSED":
                            metrics.setAccessed(metrics.getAccessed() + 1);
                            break;
                        default:
                            break;
                    }

                    // Add unique users and files
                    metrics.getUniqueUsers().add(record.getUser());
                    metrics.getUniqueFiles().add(record.getFileName());

                    // Determine start and end dates
                    if (startRecord == null && endRecord == null) {
                        startRecord = record;
                        endRecord = record;
                    } else if (record.getDateTime().isBefore(startRecord.getDateTime())) {
                        startRecord = record;
                    } else if (record.getDateTime().isAfter(endRecord.getDateTime())) {
                        endRecord = record;
                    }
                }

                // Set Metrics for lines read and time frame
                metrics.setLinesRead(metrics.getLinesRead() + 1);
                metrics.setStartDate(startRecord.getTimestamp());
                metrics.setEndDate(endRecord.getTimestamp());
            }

            System.out.println("---");

            // Aggregate and print stats as a JSON object
            metrics.printMetricsAsJSON();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void printMetricsAsJSON() {
        try {
            int linesRead = 0, droppedEventCounts = 0, uniqueUsers = 0, uniqueFiles = 0, noActionMapping = 0, duplicates = 0, add = 0, remove = 0, accessed = 0;
            String startDate = "", endDate = "";

            JsonNodeFactory factory = new JsonNodeFactory(false);

            JsonFactory jsonFactory = new JsonFactory();
            JsonGenerator generator = jsonFactory.createGenerator(System.out);

            ObjectMapper mapper = new ObjectMapper();

            ObjectNode metrics = factory.objectNode();

            metrics.put("linesRead", linesRead);
            metrics.put("droppedEventCounts", droppedEventCounts);

            ObjectNode droppedEvents = factory.objectNode();
            droppedEvents.put("No action mapping", noActionMapping);
            droppedEvents.put("Duplicates", duplicates);
            metrics.put("droppedEvents", droppedEvents);

            metrics.put("uniqueUsers", uniqueUsers);
            metrics.put("uniqueFiles", uniqueFiles);
            metrics.put("startDate", startDate);
            metrics.put("endDate", endDate);

            ObjectNode actions = factory.objectNode();
            actions.put("ADD", add);
            actions.put("REMOVE", remove);
            actions.put("ACCESSED", accessed);
            metrics.put("actions", actions);

            mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            mapper.writeValue(System.out, metrics);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
