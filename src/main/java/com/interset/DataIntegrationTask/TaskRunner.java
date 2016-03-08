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

import org.junit.*;

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

            ObjectMapper mapper = new ObjectMapper();

            Iterator<Metadata> iterator = mapper.reader(Metadata.class).readValues(jsonFile.toFile());

            List<Record> records = new ArrayList<Record>();

            // Convert Metadata into Record objects for use as CSV output
            while (iterator.hasNext()) {
                records.add(new Record(iterator.next()));
            }

            System.out.println("---");
            System.out.println(records.size() + " Record objects");
            System.out.println("First Record: " + records.get(0));
            System.out.println("Last Record: " + records.get(records.size() - 1));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Get the test data, try with the first element
    private static void testMetadataToRecord(Path jsonFile) {
        try {

            ObjectMapper mapper = new ObjectMapper();

            Iterator<Metadata> iterator = mapper.reader(Metadata.class).readValues(jsonFile.toFile());

            for (int i = 0; i < 4; i++) {
                iterator.next();
            }

            Metadata metadata = iterator.next();

            Record record = new Record(metadata);

            //System.out.println("Metadata timestamp: " + metadata.getTimestamp() + "; Metadata time offset: " + metadata.getTimeOffset()); 
            //System.out.println("Record timestamp: " + record.getTimestamp());
            System.out.println(metadata.getIpAddr());
            System.out.println(record);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doJodaStuff() {
        DateTimeFormatter dtf = DateTimeFormat.forPattern("MM/dd/yyyy hh:mm:ssaZZ");
        DateTime temp = dtf.withOffsetParsed().parseDateTime("01/14/2016 07:37:36PM" + "-08:00");
        //DateTime temp = dtf.withOffsetParsed().parseDateTime("01/14/2016 09:16:54PM" + "Z");
        DateTimeZone dtz = temp.getZone();

        DateTime dateTime = new DateTime(temp.toDate());
        DateTimeFormatter dtf2 = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ"); // From before
        DateTimeFormatter dtf3 = dtf2.withZone(dtz); // Correct'un

        System.out.println(dateTime.toString(dtf2)); 
        System.out.println(dateTime.toString(dtf3).replaceAll("\\+00:00", "Z")); // Replaces ending with Z for UTC 
    }

    private void doCSVStuff() {
        // Make a fresh CSV file        
        /*
        try {

            String data = "TIMESTP,ACTION,USER,FOLDER,FILENE,IP";

            Files.write(csvFile, data.getBytes(StandardCharsets.US_ASCII), StandardOpenOption.CREATE, StandardOpenOption.WRITE);

        } catch (IOException e) {
            e.printStackTrace();
        }
        */
    }

    private void doIteratorStuff() {

            //while (iterator.hasNext()) {
                //System.out.println(iterator.next().getEventId());
            //}

            //List<Metadata> metadataList = mapper.readValue(jsonFile.toFile(), Metadata.class);

            //mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

            //String string = "{\"eventId\" : 111111}";

            //Metadata metadata = mapper.readValue(jsonFile.toFile(), Metadata.class);

            //Metadata metadata = mapper.readValue(string, Metadata.class);

            //Iterator iterator = mapper.readValues(jsonFile.toFile(), Metadata.class);

            //Metadata metadata = (Metadata) iterator.next();

            //System.out.println(metadata.getEventId());

            //while (iterator.hasNext()) {
            //    iterator.next()
            //}
    }
}
