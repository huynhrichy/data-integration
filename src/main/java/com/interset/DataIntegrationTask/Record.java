package com.interset.DataIntegrationTask;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;

public class Record {
	private long eventID;
	private String timestamp, action, user, folder, fileName, ip;

	public Record() {
		this.eventID = 0;
		this.timestamp = "";
		this.action = "";
		this.user = "";
		this.folder = "";
		this.fileName = "";
		this.ip = "";
	}

	public Record(Metadata metadata) {
		this();
		setEventID(metadata.getEventId());
		setTimestamp(metadata.getTimestamp(), metadata.getTimeOffset());
		setAction(metadata.getActivity());
		setUser(metadata.getUser());
		setFolder(metadata.getFile());
		setFileName(metadata.getFile());
		setIP(metadata.getIpAddr());
	}

	public void setEventID(long eventId) {
		this.eventID = eventId;
	}

	public void setTimestamp(String timestamp, String timeOffset) {
		// Set to UTC (Zulu) time if no offset given
		if (timeOffset == null || timeOffset.equals("")) {
			timeOffset = "+00:00";
		}

		// Use Joda to parse the time and time zone
        DateTimeFormatter dtf = DateTimeFormat.forPattern("MM/dd/yyyy hh:mm:ssaZZ");
        DateTime temp = dtf.withOffsetParsed().parseDateTime(timestamp + timeOffset);
        DateTimeZone dtz = temp.getZone();

        // Format to ISO 8601 compliant string, with milliseconds
        DateTime dateTime = new DateTime(temp.toDate());
        DateTimeFormatter dtf2 = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
        DateTimeFormatter dtf3 = dtf2.withZone(dtz);

        // Set timestamp; replace UTC with Z
        this.timestamp = dateTime.toString(dtf3).replaceAll("\\+00:00", "Z");
	}

	public void setAction(String activity) {
		// Decide this Record to be one of three actions based on given activity
		switch (activity) {
			case "createdDoc":
			case "addedText":
			case "changedText":
			case "restored":
				action = "ADD";
				break;
			case "deletedDoc":
			case "deletedText":
			case "archived":
				action = "REMOVE";
				break;
			case "viewedDoc":
				action = "ACCESSED";
				break;
			// If activity does not map, Record will not be written to CSV
			default:
				action = "";
				break;
		}
	}

	public void setUser(String user) {
		// Get the username from the email address or SYSTEM or ADMIN
		if (user.contains("@")) {
			// If email
			this.user = user.substring(0, user.indexOf("@"));
		} else {
			// For SYSTEM or ADMIN
			this.user = user;
		}
	}

	public void setFolder(String file) {
		// Get the directory from the filepath
		this.folder = file.substring(0, file.lastIndexOf("/") + 1);
	}

	public void setFileName(String file) {
		// Get the filename from the filepath
		this.fileName = file.substring(file.lastIndexOf("/") + 1);
	}

	public void setIP(String ipAddr) {
		// Get the IP
		this.ip = ipAddr;
	}

	public long getEventID() {
		return eventID;
	}

	public String getUser() {
		return user;
	}

	public String getIP() {
		return ip;
	}

	public String getFileName() {
		return fileName;
	}

	public String getFolder() {
		return folder;
	}

	public String getAction() {
		return action;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public String toString() {
		// Format string for CSV row
		return "\"" + timestamp + "\",\"" + action + "\",\"" + user + "\",\"" + folder + "\",\"" + fileName + "\",\"" + ip + "\"";
	}
}