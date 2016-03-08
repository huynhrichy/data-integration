package com.interset.DataIntegrationTask;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.nio.charset.StandardCharsets;
import java.nio.file.StandardOpenOption;
import java.io.IOException;

import java.io.File;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import java.util.Iterator;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

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

            // Convert Metadata into Record objects for CSV-formatted output
            while (iterator.hasNext()) {
                Record record = new Record(iterator.next());

                // If Record fits exclusion criteria (duplicate eventID or invalid activity), do not write it
                if (!record.getAction().equals("") && !eventIDs.contains(record.getEventID())) {

                    Files.write(csvFile, record.toString().concat("\n")
                        .getBytes(StandardCharsets.US_ASCII), StandardOpenOption.CREATE, 
                        StandardOpenOption.WRITE, StandardOpenOption.APPEND);
                    
                    eventIDs.add(record.getEventID());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
