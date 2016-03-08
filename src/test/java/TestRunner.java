package com.interset.DataIntegrationTask;

import org.junit.*;
import static org.junit.Assert.*;

public class TestRunner {
	private static Record record;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	    String jsonFile = System.getProperty("jsonFile");
	    String csvFile = System.getProperty("csvFile");
	    //log.info("Reading config file : " + fileName);

	    record = new Record();
	}

	@Test
	public void testTimestamp() {
		String timestamp = "01/14/2016 07:37:36PM";
		String timeOffset = "-08:00";

		//Record record = new Record();

		record.setTimestamp(timestamp, timeOffset);

		assertEquals("2016-01-14T19:37:36-08:00", record.getTimestamp());
	}

	@Test
	public void testTimestampUTC() {
		String timestamp = "01/14/2016 09:16:54PM";
		String timeOffset = "";

		//Record record = new Record();

		record.setTimestamp(timestamp, timeOffset);

		assertEquals("2016-01-14T21:16:54Z", record.getTimestamp());
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
}