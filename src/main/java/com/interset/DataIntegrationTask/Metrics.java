package com.interset.DataIntegrationTask;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;

import java.util.Set;
import java.util.HashSet;

public class Metrics {
	private int linesRead, noActionMapping, duplicates, add, remove, accessed;
	private Record startRecord, endRecord;
	private Set<String> uniqueUsers, uniqueFiles;

	public Metrics() {
		this.linesRead = 0;
		this.noActionMapping = 0;
		this.duplicates = 0;
		this.startRecord = null;
		this.endRecord = null;
		this.add = 0;
		this.remove = 0;
		this.accessed = 0;
		uniqueUsers = new HashSet<String>();
		uniqueFiles = new HashSet<String>();
	}

	public void incrementAction(Record record) {
        switch (record.getAction()) {
            case "ADD":
                setAdd(getAdd() + 1);
                break;
            case "REMOVE":
                setRemove(getRemove() + 1);
                break;
            case "ACCESSED":
                setAccessed(getAccessed() + 1);
                break;
            default:
                break;
		}
	}

	public void determineTimeRange(Record record) {
		if (startRecord == null && endRecord == null) {
            startRecord = record;
            endRecord = record;
        } else if (record.getDateTime().isBefore(startRecord.getDateTime())) {
            startRecord = record;
        } else if (record.getDateTime().isAfter(endRecord.getDateTime())) {
            endRecord = record;
        }
	}

	public void update(Record record) {
        incrementAction(record);
        getUniqueUsers().add(record.getUser());
        getUniqueFiles().add(record.getFolder() + record.getFileName());
        determineTimeRange(record);
	}

	public void printMetricsAsJSON() {
        try {
        	JsonNodeFactory factory = new JsonNodeFactory(false);

            JsonFactory jsonFactory = new JsonFactory();
            JsonGenerator generator = jsonFactory.createGenerator(System.out);

            ObjectMapper mapper = new ObjectMapper();

            ObjectNode metrics = factory.objectNode();

            metrics.put("linesRead", linesRead);
            metrics.put("droppedEventsCounts", noActionMapping + duplicates);

            ObjectNode droppedEvents = factory.objectNode();
            droppedEvents.put("No action mapping", noActionMapping);
            droppedEvents.put("Duplicates", duplicates);
            metrics.put("droppedEvents", droppedEvents);

            metrics.put("uniqueUsers", uniqueUsers.size());
            metrics.put("uniqueFiles", uniqueFiles.size());
            metrics.put("startDate", startRecord.getTimestamp());
            metrics.put("endDate", endRecord.getTimestamp());

            ObjectNode actions = factory.objectNode();
            actions.put("ADD", add);
            actions.put("REMOVE", remove);
            actions.put("ACCESSED", accessed);
            metrics.put("actions", actions);

            mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            mapper.writeValue(System.out, metrics);
        } catch (IOException e) {
            System.err.println("Could not work with JsonGenerator or ObjectMapper.");
            System.exit(1);
        }
	}

	public void setLinesRead(int lr) { linesRead = lr; }
	public void setNoActionMapping(int nam) { noActionMapping = nam; }
	public void setDuplicates(int d) { duplicates = d; }
	public void setAdd(int a) { add = a; }
	public void setRemove(int r) { remove = r; }
	public void setAccessed(int a) { accessed = a; }

	public int getLinesRead() { return linesRead; }
	public int getNoActionMapping() { return noActionMapping; }
	public int getDuplicates() { return duplicates; }
	public Set getUniqueUsers() { return uniqueUsers; }
	public Set getUniqueFiles() { return uniqueFiles; }
	public Record getStartRecord() { return startRecord; }
	public Record getEndRecord() { return endRecord; }
	public int getAdd() { return add; }
	public int getRemove() { return remove; }
	public int getAccessed() { return accessed; }
}