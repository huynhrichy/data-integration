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
	private int linesRead, /*droppedEventsCounts, uniqueUsers, uniqueFiles,*/ noActionMapping, duplicates, add, remove, accessed;
	private String startDate, endDate;
	private Set<String> uniqueUsers, uniqueFiles;

	public Metrics() {
		this.linesRead = 0;
		//this.droppedEventsCounts = 0;
		this.noActionMapping = 0;
		this.duplicates = 0;
		this.startDate = "";
		this.endDate = "";
		this.add = 0;
		this.remove = 0;
		this.accessed = 0;
		uniqueUsers = new HashSet<String>();
		uniqueFiles = new HashSet<String>();
	}

	public void setLinesRead(int lr) { linesRead = lr; }
	//public void setDroppedEventsCounts(int dec) { droppedEventsCounts = dec; }
	public void setNoActionMapping(int nam) { noActionMapping = nam; }
	public void setDuplicates(int d) { duplicates = d; }
	public void setStartDate(String sd) { startDate = sd; }
	public void setEndDate(String ed) { endDate = ed; }
	public void setAdd(int a) { add = a; }
	public void setRemove(int r) { remove = r; }
	public void setAccessed(int a) { accessed = a; }

	public int getLinesRead() { return linesRead; }
	public int getNoActionMapping() { return noActionMapping; }
	public int getDuplicates() { return duplicates; }
	public Set getUniqueUsers() { return uniqueUsers; }
	public Set getUniqueFiles() { return uniqueFiles; }
	public int getAdd() { return add; }
	public int getRemove() { return remove; }
	public int getAccessed() { return accessed; }

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

	/*
	// Public for brevity
	public int linesRead, droppedEventCounts, uniqueUsers, uniqueFiles;
	public String startDate, endDate;
	public DroppedEventMetrics droppedEvents; 
	public ActionMetrics actions;

	public Metrics() {
		this.linesRead = 0;
		this.droppedEventCounts = 0;
		this.uniqueUsers = 0;
		this.uniqueFiles = 0;
		this.startDate = "";
		this.endDate = "";
		this.droppedEvents = new DroppedEventMetrics();
		this.actions = new ActionMetrics();
	}
	*/
}