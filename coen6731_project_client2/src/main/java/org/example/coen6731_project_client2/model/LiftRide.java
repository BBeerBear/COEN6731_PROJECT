package org.example.coen6731_project_client2.model;

public class LiftRide {
	private short time; //217
	private short liftID; //21
	
	public LiftRide() {
	}
	
	public LiftRide(short time, short liftID) {
		this.time = time;
		this.liftID = liftID;
	}

	public short getTime() {
		return time;
	}
	public void setTime(short time) {
		this.time = time;
	}
	public short getLiftID() {
		return liftID;
	}
	public void setLiftID(short liftID) {
		this.liftID = liftID;
	}
	@Override
	public String toString() {
		return "LiftRide [time=" + time + ", liftID=" + liftID + "]";
	}
}
