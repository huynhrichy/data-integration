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

import org.junit.*;
import static org.junit.Assert.*;

public class TestRunner {
	private static Record record;
	private static Metadata metadata;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	    //String jsonFile = System.getProperty("jsonFile");
	    //String csvFile = System.getProperty("csvFile");

	    record = new Record();
	    metadata = new Metadata();
	}

	@Test
	public void testEventID() {
		record.setEventID(865423732);
		assertEquals(865423732, record.getEventID());
	}

	@Test
	public void testTimeStamp() {
		String timestamp = "01/14/2016 07:37:36PM";
		String timeOffset = "-08:00";

		record.setDateTime(timestamp, timeOffset);
		record.setTimestamp(record.getDateTime(), record.getTimeZone());

		assertEquals("2016-01-14T19:37:36.000-08:00", record.getTimestamp());
	}

	@Test
	public void testTimestampUTC() {
		String timestamp = "01/14/2016 09:16:54PM";
		String timeOffset = "";

		record.setDateTime(timestamp, timeOffset);
		record.setTimestamp(record.getDateTime(), record.getTimeZone());

		assertEquals("2016-01-14T21:16:54.000Z", record.getTimestamp());
	}

	@Test
	public void testActionAdd() {
		record.setAction("createdDoc");
		assertEquals("ADD", record.getAction());
		record.setAction("addedText");
		assertEquals("ADD", record.getAction());
		record.setAction("changedText");
		assertEquals("ADD", record.getAction());
		record.setAction("restored");
		assertEquals("ADD", record.getAction());
	}

	@Test
	public void testActionRemove() {
		record.setAction("deletedDoc");
		assertEquals("REMOVE", record.getAction());
		record.setAction("deletedText");
		assertEquals("REMOVE", record.getAction());
		record.setAction("archived");
		assertEquals("REMOVE", record.getAction());
	}

	@Test
	public void testActionAccessed() {
		record.setAction("viewedDoc");
		assertEquals("ACCESSED", record.getAction());
	}

	@Test
	public void testActionNone() {
		record.setAction("");
		assertEquals("", record.getAction());
		record.setAction("hashed");
		assertEquals("", record.getAction());
	}

	@Test
	public void testUserEmail() {
		record.setUser("rGarcia@company.com");
		assertEquals("rGarcia", record.getUser());
	}

	@Test
	public void testUserSystem() {
		record.setUser("SYSTEM");
		assertEquals("SYSTEM", record.getUser());
	}

	@Test
	public void testUserAdmin() {
		record.setUser("ADMIN");
		assertEquals("ADMIN", record.getUser());
	}

	@Test
	public void testFolder() {
		record.setFolder("/data/onlineDocs/2015/Q2/expences-May-2015.doc");
		assertEquals("/data/onlineDocs/2015/Q2/", record.getFolder());
	}

	@Test
	public void testFileName() {
		record.setFileName("/data/onlineDocs/2015/Q2/expences-May-2015.doc");
		assertEquals("expences-May-2015.doc", record.getFileName());
	}

	@Test
	public void testIP() {
		record.setIP("10.10.10.107");
		assertEquals("10.10.10.107", record.getIP());
	}

	@Test
	public void testJSONtoMetadata() {
		initMetadataFromJSON();

        assertEquals(865423732, metadata.getEventId());
        assertEquals("01/14/2016 07:37:36PM", metadata.getTimestamp());
        assertEquals("10.10.10.107", metadata.getIpAddr());
        assertEquals("rGarcia@company.com", metadata.getUser());
        assertEquals("/data/onlineDocs/2015/Q2/expences-May-2015.doc", metadata.getFile());
        assertEquals("addedText", metadata.getActivity());
        assertEquals("-08:00", metadata.getTimeOffset());
	}

	private void initMetadataFromJSON() {
		try {
			String jsonObject = "{\"eventId\": 865423732, \"timestamp\": \"01/14/2016 07:37:36PM\", \"ipAddr\": \"10.10.10.107\", \"user\": \"rGarcia@company.com\", \"file\": \"/data/onlineDocs/2015/Q2/expences-May-2015.doc\", \"activity\": \"addedText\", \"timeOffset\": \"-08:00\"}";

	        ObjectMapper mapper = new ObjectMapper();

	        metadata = mapper.readValue(jsonObject, Metadata.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testMetadataToRecord() {
		initMetadataFromJSON();
		record = new Record(metadata);

		assertEquals(865423732, record.getEventID());
		assertEquals("2016-01-14T19:37:36.000-08:00", record.getTimestamp());
		assertEquals("ADD", record.getAction());
		assertEquals("rGarcia", record.getUser());
		assertEquals("/data/onlineDocs/2015/Q2/", record.getFolder());
		assertEquals("expences-May-2015.doc", record.getFileName());
		assertEquals("10.10.10.107", record.getIP());
	}

	@Test
	public void testRecordToCSV() {
		initMetadataFromJSON();
		record = new Record(metadata);

		assertEquals("\"2016-01-14T19:37:36.000-08:00\",\"ADD\",\"rGarcia\",\"/data/onlineDocs/2015/Q2/\",\"expences-May-2015.doc\",\"10.10.10.107\"", record.toString());
	}

	@Test
	public void testNumberOfValidRecordsWithMetadataObjects5() {
		Path jsonFile = null;

        try {
            jsonFile = Paths.get("src/test/resources/metadataObjects5.json");

            ObjectMapper mapper = new ObjectMapper();
            Iterator<Metadata> iterator = mapper.reader(Metadata.class).readValues(jsonFile.toFile());

            Set<Long> eventIDs = new HashSet<Long>();

            int linesRead = 0;

            Set<Record> records = new HashSet<Record>();

            while (iterator.hasNext()) {
                Record record = new Record(iterator.next());

            	if (!eventIDs.contains(record.getEventID()) && !record.getAction().equals("")) {
            		records.add(record);
            		eventIDs.add(record.getEventID());
            	}

            	linesRead++;
            }

            assertEquals(5, linesRead);
            assertEquals(3, records.size());

        } catch (Exception e) {
        	e.printStackTrace();
        }
	}

	@Test
	public void testMetricsWithMetaDataObjects10() {
		Metrics metrics = new Metrics();
	}
}