package de.twt.client.modbus.common;

import java.util.ArrayList;

public class OntologyChangedEvent {
	private String filename;
	private ArrayList<String> systemNames;
	
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public ArrayList<String> getSystemNames() {
		return systemNames;
	}
	public void setSystemNames(ArrayList<String> systemNames) {
		this.systemNames = systemNames;
	}
	
}
