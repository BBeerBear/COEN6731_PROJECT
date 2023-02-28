package org.example.model;

public class Resort {
	private String resortName;
	private int resortID;

	public Resort() {
	}

	public Resort(String resortName, int resortID) {
		this.resortName = resortName;
		this.resortID = resortID;
	}

	public String getResortName() {
		return resortName;
	}

	public void setResortName(String resortName) {
		this.resortName = resortName;
	}

	public int getResortID() {
		return resortID;
	}

	public void setResortID(int resortID) {
		this.resortID = resortID;
	}

	@Override
	public String toString() {
		return "Resort [" + (resortName != null ? "resortName=" + resortName + ", " : "") + "resortID=" + resortID
				+ "]";
	}
	
}
