package com.interset.DataIntegrationTask;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.io.IOException;
import java.io.File;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Filter {
	private Metrics metrics;
	private Path jsonFile;
	private Iterator<Metadata> metadataIterator;
	private List<Record> records;

	public Filter(Path jsonFile) {
		this.metrics = new Metrics();
		this.jsonFile = jsonFile;
		this.metadataIterator = null;
		this.records = new ArrayList<Record>();
	}

	public void parseJSONToMetadataObjectIterator() {
        try {
            // Use Jackson's ObjectMapper to create Metadata POJOs from JSON objects in jsonFile
            ObjectMapper mapper = new ObjectMapper();
            metadataIterator = mapper.reader(Metadata.class).readValues(jsonFile.toFile());
        } catch (IOException e) {
            System.err.println("Could not read from JSON.");
            System.exit(1);
        }
	}

	public void convertMetadataToRecordObjects() {
		// Check for event duplicates with a set
        Set<Long> eventIDs = new HashSet<Long>();

        // Convert Metadata into Record objects which are formatted for CSVs
		while (metadataIterator.hasNext()) {
            Record record = new Record(metadataIterator.next());
			boolean newValidRecord = true;

			// Add Record only if not within exclusion criteria
			if (isNewValidRecord(record, eventIDs)) {
	            records.add(record);
	            eventIDs.add(record.getEventID());
	            metrics.update(record);
        	}

            metrics.setLinesRead(metrics.getLinesRead() + 1);
		}
	}

	private boolean isNewValidRecord(Record record, Set<Long> eventIDs) {
		boolean newValidRecord = true;

        if (record.getAction().equals("")) {
            newValidRecord = false;
            metrics.setNoActionMapping(metrics.getNoActionMapping() + 1);
        }

        if (eventIDs.contains(record.getEventID())) {
            newValidRecord = false;
            metrics.setDuplicates(metrics.getDuplicates() + 1);
        }

        return newValidRecord;
	}

	public Metrics getMetrics() { return metrics; }
	public List<Record> getRecords() { return records; }
}