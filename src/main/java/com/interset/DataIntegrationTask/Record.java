package com.interset.DataIntegrationTask;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;

public class Record {
	private String timestamp, action, user, folder, fileName, ip;

	public Record() {
		this.timestamp = "";
		this.action = "";
		this.user = "";
		this.folder = "";
		this.fileName = "";
		this.ip = "";
	}

	public Record(Metadata metadata) {
		this();
		setTimestamp(metadata.getTimestamp(), metadata.getTimeOffset());
		setAction(metadata.getActivity());
		setUser(metadata.getUser());
		setFolder(metadata.getFile());
		setFileName(metadata.getFile());
		setIP(metadata.getIpAddr());
	}

/*
	public Record(String timestamp, String action, String user, 
		String folder, String fileName, String ip) {
		this.timestamp = timestamp;
		this.action = action;
		this.user = user;
		this.folder = folder;
		this.fileName = fileName;
		this.ip = ip;
	}
*/
	// Use Joda to obtain the string and save it
	public void setTimestamp(String timestamp, String timeOffset) {
		if (timeOffset == null || timeOffset.equals("")) {
			//System.out.println("zulu");
			timeOffset = "+00:00";
		}

        DateTimeFormatter dtf = DateTimeFormat.forPattern("MM/dd/yyyy hh:mm:ssaZZ");
        DateTime temp = dtf.withOffsetParsed().parseDateTime(timestamp + timeOffset);
        //DateTime temp = dtf.withOffsetParsed().parseDateTime("01/14/2016 09:16:54PM" + "Z");
        DateTimeZone dtz = temp.getZone();

        DateTime dateTime = new DateTime(temp.toDate());
        //DateTimeFormatter dtf2 = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZ"); // From before
        DateTimeFormatter dtf2 = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZZ"); // No ms
        DateTimeFormatter dtf3 = dtf2.withZone(dtz); // Correct'un

        //System.out.println(dateTime.toString(dtf3).replaceAll("\\+00:00", "Z")); // Replaces ending with Z for UTC 

        this.timestamp = dateTime.toString(dtf3).replaceAll("\\+00:00", "Z");
	}

	// Decide this Record to be one of three actions based on given activity
	// If activity does not map, do not include in the CSV
	public void setAction(String activity) {
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
			default:
				action = "";
				break;
		}
	}

	// Get the username from the email address or SYSTEM or ADMIN
	public void setUser(String user) {
		if (user.contains("@")) {
			// If email
			this.user = user.substring(0, user.indexOf("@"));
		} else {
			// For SYSTEM or ADMIN
			this.user = user;
		}
	}

	// Get the directory from the filepath
	public void setFolder(String file) {
		this.folder = file.substring(0, file.lastIndexOf("/") + 1);
	}

	// Get the filename from the filepath
	public void setFileName(String file) {
		this.fileName = file.substring(file.lastIndexOf("/") + 1);
	}

	// Get the IP
	public void setIP(String ipAddr) {
		this.ip = ipAddr;
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
		return "\"" + timestamp + "\",\"" + action + "\",\"" + user + "\",\"" + folder + "\",\"" + fileName + "\",\"" + ip + "\"";
	}
}