package org.example.model;

public class LiftRideEvent {
	private int resortID;
	private String seasonID;
	private String dayID;
	private int skierID;
	private LiftRide liftRide;
	
	public LiftRideEvent() {
	}
	
	public LiftRideEvent(int resortID, String seasonID, String dayID, int skierID, LiftRide liftRide) {
		super();
		this.resortID = resortID;
		this.seasonID = seasonID;
		this.dayID = dayID;
		this.skierID = skierID;
		this.liftRide = liftRide;
	}

	public int getSkierID() {
		return skierID;
	}

	public void setSkierID(int skierID) {
		this.skierID = skierID;
	}

	public int getResortID() {
		return resortID;
	}

	public void setResortID(int resortID) {
		this.resortID = resortID;
	}

	public String getSeasonID() {
		return seasonID;
	}

	public void setSeasonID(String seasonID) {
		this.seasonID = seasonID;
	}

	public String getDayID() {
		return dayID;
	}

	public void setDayID(String dayID) {
		this.dayID = dayID;
	}

	public LiftRide getLiftRide() {
		return liftRide;
	}

	public void setLiftRide(LiftRide liftRide) {
		this.liftRide = liftRide;
	}


	@Override
	public String toString() {
		return "LiftRideEvent [resortID=" + resortID + ", " + (seasonID != null ? "seasonID=" + seasonID + ", " : "")
				+ (dayID != null ? "dayID=" + dayID + ", " : "") + "skierID=" + skierID + ", "
				+ (liftRide != null ? "liftRide=" + liftRide : "") + "]";
	}
	
}
