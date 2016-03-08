package com.interset.DataIntegrationTask;

public class Metadata {
	private long eventId;
	private String user, ipAddr, file, activity, timestamp, timeOffset;

	public Metadata() {
		this.eventId = 0;
		this.user = "";
		this.ipAddr = "";
		this.file = "";
		this.activity = "";
		this.timestamp = "";
		this.timeOffset = "";
	}

	public Metadata(long eventId, String user, String ipAddr, String file, 
		String activity, String timestamp, String timeOffset) {
		this();
		this.eventId = eventId;
		this.user = user;
		this.ipAddr = ipAddr;
		this.file = file;
		this.activity = activity;
		this.timestamp = timestamp;
		this.timeOffset = timeOffset;
	}

	public long getEventId() {
		return eventId;
	}

	public String getUser() {
		return user;
	}

	public String getIpAddr() {
		return ipAddr;
	}

	public String getFile() {
		return file;
	}

	public String getActivity() {
		return activity;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public String getTimeOffset() {
		return timeOffset;
	}

	public void setEventId(long eventId) {
		this.eventId = eventId;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public void setActivity(String activity) {
		this.activity = activity;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public void setTimeOffset(String timeOffset) {
		this.timeOffset = timeOffset;
	}

	/*
	public Metadata(long eventId, String user, String ipAddr, String file, 
		String activity, String timestamp, String timeOffset) {
		this.eventId = eventId;
		this.user = user;
		this.ipAddr = ipAddr;
		this.file = file;
		this.activity = activity;
		this.timestamp = timestamp;
		this.timeOffset = timeOffset;
	}


	public long getEventId() {
		return eventId;
	}

	public String toString() {
		return "";
	}
	*/
}